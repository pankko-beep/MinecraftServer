/**
 * Database Connection Utility
 * Connects to the same MySQL database used by the Minecraft plugin
 */

const mysql = require('mysql2/promise');
const logger = require('./logger');

let pool;

const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT) || 3306,
    user: process.env.DB_USER || 'nexus_user',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'nexus_db',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
    enableKeepAlive: true,
    keepAliveInitialDelay: 0
};

/**
 * Connect to database
 */
async function connect() {
    try {
        pool = mysql.createPool(dbConfig);
        
        // Test connection
        const connection = await pool.getConnection();
        logger.info('Database connection successful');
        connection.release();
        
        return pool;
    } catch (error) {
        logger.error('Database connection failed:', error);
        throw error;
    }
}

/**
 * Get database connection from pool
 */
function getPool() {
    if (!pool) {
        throw new Error('Database not connected. Call connect() first.');
    }
    return pool;
}

/**
 * Execute query
 */
async function query(sql, params = []) {
    try {
        const [results] = await pool.execute(sql, params);
        return results;
    } catch (error) {
        logger.error('Query error:', { sql, error: error.message });
        throw error;
    }
}

/**
 * Record transaction
 */
async function recordTransaction(data) {
    const sql = `
        INSERT INTO nexus_transactions 
        (player_uuid, amount, type, status, description, payment_method, external_id, metadata, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
    `;
    
    const params = [
        data.playerUuid,
        data.amount,
        data.type,
        data.status,
        data.description,
        data.paymentMethod,
        data.externalId,
        JSON.stringify(data.metadata || {})
    ];
    
    return await query(sql, params);
}

/**
 * Update player balance
 */
async function updatePlayerBalance(playerUuid, amount) {
    const sql = `
        UPDATE nexus_players 
        SET money = money + ?, 
            last_transaction = NOW()
        WHERE uuid = ?
    `;
    
    return await query(sql, [amount, playerUuid]);
}

/**
 * Get player by username
 */
async function getPlayerByUsername(username) {
    const sql = 'SELECT * FROM nexus_players WHERE username = ? LIMIT 1';
    const results = await query(sql, [username]);
    return results.length > 0 ? results[0] : null;
}

/**
 * Create pending transaction
 */
async function createPendingTransaction(playerUuid, amount, externalId, paymentMethod) {
    return await recordTransaction({
        playerUuid,
        amount,
        type: 'VIP_PURCHASE',
        status: 'PENDING',
        description: `Pagamento PIX pendente - ${paymentMethod}`,
        paymentMethod,
        externalId,
        metadata: { pending: true }
    });
}

/**
 * Complete transaction
 */
async function completeTransaction(externalId, metadata = {}) {
    const updateSql = `
        UPDATE nexus_transactions 
        SET status = 'COMPLETED', 
            metadata = ?,
            updated_at = NOW()
        WHERE external_id = ? AND status = 'PENDING'
    `;
    
    const result = await query(updateSql, [JSON.stringify(metadata), externalId]);
    
    if (result.affectedRows > 0) {
        // Get transaction details
        const getSql = 'SELECT * FROM nexus_transactions WHERE external_id = ? LIMIT 1';
        const transactions = await query(getSql, [externalId]);
        
        if (transactions.length > 0) {
            const transaction = transactions[0];
            // Update player balance
            await updatePlayerBalance(transaction.player_uuid, transaction.amount);
            return transaction;
        }
    }
    
    return null;
}

/**
 * Disconnect from database
 */
async function disconnect() {
    if (pool) {
        await pool.end();
        logger.info('Database connection closed');
    }
}

module.exports = {
    connect,
    disconnect,
    getPool,
    query,
    recordTransaction,
    updatePlayerBalance,
    getPlayerByUsername,
    createPendingTransaction,
    completeTransaction
};
