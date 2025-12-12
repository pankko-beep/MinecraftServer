/**
 * Custom Payment Webhook Routes
 * Handles manual/PowerShell payment confirmations
 */

const express = require('express');
const router = express.Router();
const crypto = require('crypto');
const logger = require('../utils/logger');
const db = require('../utils/database');

const CUSTOM_PAYMENT_SECRET = process.env.CUSTOM_PAYMENT_SECRET;

/**
 * Verify custom payment signature
 */
function verifyCustomSignature(req) {
    const providedSignature = req.headers['x-payment-signature'];
    
    if (!providedSignature || !CUSTOM_PAYMENT_SECRET) {
        return false;
    }
    
    const payload = JSON.stringify(req.body);
    const hmac = crypto.createHmac('sha256', CUSTOM_PAYMENT_SECRET);
    hmac.update(payload);
    const expectedSignature = hmac.digest('hex');
    
    return providedSignature === expectedSignature;
}

/**
 * Generate signature for PowerShell client
 * Example PowerShell usage:
 * 
 * $secret = "your_secret"
 * $body = '{"username":"Player123","amount":100.0,"method":"MANUAL"}'
 * $hmac = New-Object System.Security.Cryptography.HMACSHA256
 * $hmac.Key = [Text.Encoding]::UTF8.GetBytes($secret)
 * $signature = [BitConverter]::ToString($hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($body))).Replace("-","").ToLower()
 */
router.get('/generate-signature', (req, res) => {
    const { username, amount, method = 'MANUAL' } = req.query;
    
    if (!username || !amount) {
        return res.status(400).json({
            error: 'Missing required parameters: username, amount'
        });
    }
    
    const body = JSON.stringify({ username, amount: parseFloat(amount), method });
    const hmac = crypto.createHmac('sha256', CUSTOM_PAYMENT_SECRET);
    hmac.update(body);
    const signature = hmac.digest('hex');
    
    res.json({
        body,
        signature,
        headers: {
            'Content-Type': 'application/json',
            'X-Payment-Signature': signature
        },
        curlExample: `curl -X POST http://localhost:${process.env.PORT || 3000}/api/webhooks/custom/confirm \\
  -H "Content-Type: application/json" \\
  -H "X-Payment-Signature: ${signature}" \\
  -d '${body}'`,
        powershellExample: `$headers = @{
    "Content-Type" = "application/json"
    "X-Payment-Signature" = "${signature}"
}
$body = '${body}'
Invoke-RestMethod -Uri "http://localhost:${process.env.PORT || 3000}/api/webhooks/custom/confirm" -Method Post -Headers $headers -Body $body`
    });
});

/**
 * POST /api/webhooks/custom/confirm
 * Confirm manual payment
 */
router.post('/confirm', async (req, res) => {
    try {
        logger.info('Custom payment webhook received', { body: req.body });
        
        // Verify signature
        if (!verifyCustomSignature(req)) {
            logger.warn('Invalid custom payment signature');
            return res.status(401).json({ error: 'Invalid signature' });
        }
        
        const { username, amount, method = 'MANUAL', metadata = {} } = req.body;
        
        if (!username || !amount) {
            return res.status(400).json({
                error: 'Missing required fields: username, amount'
            });
        }
        
        // Get player from database
        const player = await db.getPlayerByUsername(username);
        
        if (!player) {
            logger.error(`Player not found: ${username}`);
            return res.status(404).json({ error: 'Player not found' });
        }
        
        // Generate unique external ID
        const externalId = `CUSTOM_${Date.now()}_${crypto.randomBytes(4).toString('hex')}`;
        
        // Record transaction
        const result = await db.recordTransaction({
            playerUuid: player.uuid,
            amount: parseFloat(amount),
            type: 'VIP_PURCHASE',
            status: 'COMPLETED',
            description: `Pagamento manual confirmado - ${method}`,
            paymentMethod: method,
            externalId,
            metadata: {
                ...metadata,
                confirmedAt: new Date().toISOString(),
                source: 'custom_webhook'
            }
        });
        
        // Update player balance
        await db.updatePlayerBalance(player.uuid, parseFloat(amount));
        
        logger.info(`Custom payment processed for player ${username}`, {
            amount,
            method,
            transactionId: result.insertId
        });
        
        res.status(200).json({
            success: true,
            message: 'Payment confirmed',
            data: {
                player: username,
                amount: parseFloat(amount),
                newBalance: player.money + parseFloat(amount),
                transactionId: result.insertId,
                externalId
            }
        });
        
    } catch (error) {
        logger.error('Error processing custom payment:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});

/**
 * POST /api/webhooks/custom/pending
 * Create pending payment (to be confirmed later)
 */
router.post('/pending', async (req, res) => {
    try {
        logger.info('Custom pending payment received', { body: req.body });
        
        if (!verifyCustomSignature(req)) {
            logger.warn('Invalid custom payment signature');
            return res.status(401).json({ error: 'Invalid signature' });
        }
        
        const { username, amount, method = 'MANUAL', externalId } = req.body;
        
        if (!username || !amount || !externalId) {
            return res.status(400).json({
                error: 'Missing required fields: username, amount, externalId'
            });
        }
        
        const player = await db.getPlayerByUsername(username);
        
        if (!player) {
            return res.status(404).json({ error: 'Player not found' });
        }
        
        await db.createPendingTransaction(
            player.uuid,
            parseFloat(amount),
            externalId,
            method
        );
        
        logger.info(`Pending payment created for player ${username}`, {
            amount,
            externalId
        });
        
        res.status(200).json({
            success: true,
            message: 'Pending payment created',
            data: {
                player: username,
                amount: parseFloat(amount),
                externalId,
                status: 'PENDING'
            }
        });
        
    } catch (error) {
        logger.error('Error creating pending payment:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});

module.exports = router;
