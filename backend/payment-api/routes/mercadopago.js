/**
 * Mercado Pago Webhook Routes
 * Handles PIX payment notifications
 */

const express = require('express');
const router = express.Router();
const crypto = require('crypto');
const axios = require('axios');
const logger = require('../utils/logger');
const db = require('../utils/database');

const MP_ACCESS_TOKEN = process.env.MP_ACCESS_TOKEN;
const MP_WEBHOOK_SECRET = process.env.MP_WEBHOOK_SECRET;

/**
 * Verify Mercado Pago webhook signature
 */
function verifyWebhookSignature(req) {
    const signature = req.headers['x-signature'];
    const requestId = req.headers['x-request-id'];
    
    if (!signature || !MP_WEBHOOK_SECRET) {
        return false;
    }
    
    const parts = signature.split(',');
    let ts, hash;
    
    parts.forEach(part => {
        const [key, value] = part.split('=');
        if (key.trim() === 'ts') ts = value;
        if (key.trim() === 'v1') hash = value;
    });
    
    const manifest = `id:${req.body.data?.id};request-id:${requestId};ts:${ts};`;
    const hmac = crypto.createHmac('sha256', MP_WEBHOOK_SECRET);
    hmac.update(manifest);
    const expectedHash = hmac.digest('hex');
    
    return hash === expectedHash;
}

/**
 * POST /api/webhooks/mercadopago
 * Receive payment notifications from Mercado Pago
 */
router.post('/', async (req, res) => {
    try {
        logger.info('Mercado Pago webhook received', {
            body: req.body,
            headers: req.headers
        });
        
        // Verify webhook signature
        if (!verifyWebhookSignature(req)) {
            logger.warn('Invalid Mercado Pago webhook signature');
            return res.status(401).json({ error: 'Invalid signature' });
        }
        
        const { type, data } = req.body;
        
        // Only process payment notifications
        if (type === 'payment') {
            const paymentId = data.id;
            
            // Get payment details from Mercado Pago API
            const paymentResponse = await axios.get(
                `https://api.mercadopago.com/v1/payments/${paymentId}`,
                {
                    headers: {
                        'Authorization': `Bearer ${MP_ACCESS_TOKEN}`
                    }
                }
            );
            
            const payment = paymentResponse.data;
            
            logger.info('Payment details retrieved', {
                id: payment.id,
                status: payment.status,
                amount: payment.transaction_amount
            });
            
            // Process payment based on status
            if (payment.status === 'approved') {
                await processApprovedPayment(payment);
            } else if (payment.status === 'pending') {
                await processPendingPayment(payment);
            } else if (payment.status === 'rejected' || payment.status === 'cancelled') {
                await processRejectedPayment(payment);
            }
        }
        
        // Always return 200 to acknowledge receipt
        res.status(200).json({ received: true });
        
    } catch (error) {
        logger.error('Error processing Mercado Pago webhook:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});

/**
 * Process approved payment
 */
async function processApprovedPayment(payment) {
    try {
        const playerUsername = payment.metadata?.username || payment.external_reference;
        const amount = payment.transaction_amount;
        const externalId = `MP_${payment.id}`;
        
        if (!playerUsername) {
            logger.error('No player username in payment metadata');
            return;
        }
        
        // Get player from database
        const player = await db.getPlayerByUsername(playerUsername);
        
        if (!player) {
            logger.error(`Player not found: ${playerUsername}`);
            return;
        }
        
        // Complete transaction and update balance
        const transaction = await db.completeTransaction(externalId, {
            mercadoPagoId: payment.id,
            status: payment.status,
            paymentType: payment.payment_type_id,
            approvedAt: payment.date_approved
        });
        
        if (transaction) {
            logger.info(`Payment approved for player ${playerUsername}`, {
                amount,
                transactionId: transaction.id
            });
        } else {
            // Create new transaction if not exists
            await db.recordTransaction({
                playerUuid: player.uuid,
                amount,
                type: 'VIP_PURCHASE',
                status: 'COMPLETED',
                description: `Pagamento PIX aprovado`,
                paymentMethod: 'MERCADO_PAGO_PIX',
                externalId,
                metadata: {
                    mercadoPagoId: payment.id,
                    paymentType: payment.payment_type_id
                }
            });
            
            await db.updatePlayerBalance(player.uuid, amount);
            
            logger.info(`New payment processed for player ${playerUsername}`, { amount });
        }
        
    } catch (error) {
        logger.error('Error processing approved payment:', error);
    }
}

/**
 * Process pending payment
 */
async function processPendingPayment(payment) {
    try {
        const playerUsername = payment.metadata?.username || payment.external_reference;
        const amount = payment.transaction_amount;
        const externalId = `MP_${payment.id}`;
        
        if (!playerUsername) {
            logger.error('No player username in payment metadata');
            return;
        }
        
        const player = await db.getPlayerByUsername(playerUsername);
        
        if (!player) {
            logger.error(`Player not found: ${playerUsername}`);
            return;
        }
        
        await db.createPendingTransaction(
            player.uuid,
            amount,
            externalId,
            'MERCADO_PAGO_PIX'
        );
        
        logger.info(`Pending payment created for player ${playerUsername}`, { amount });
        
    } catch (error) {
        logger.error('Error processing pending payment:', error);
    }
}

/**
 * Process rejected payment
 */
async function processRejectedPayment(payment) {
    try {
        const externalId = `MP_${payment.id}`;
        
        await db.query(
            'UPDATE nexus_transactions SET status = ? WHERE external_id = ?',
            ['FAILED', externalId]
        );
        
        logger.info(`Payment rejected: ${externalId}`, {
            status: payment.status,
            statusDetail: payment.status_detail
        });
        
    } catch (error) {
        logger.error('Error processing rejected payment:', error);
    }
}

module.exports = router;
