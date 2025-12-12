/**
 * Health Check Routes
 */

const express = require('express');
const router = express.Router();
const db = require('../utils/database');

/**
 * GET /api/health
 * Basic health check
 */
router.get('/', async (req, res) => {
    try {
        // Test database connection
        await db.query('SELECT 1');
        
        res.status(200).json({
            status: 'healthy',
            timestamp: new Date().toISOString(),
            uptime: process.uptime(),
            database: 'connected',
            version: '1.0.0'
        });
    } catch (error) {
        res.status(503).json({
            status: 'unhealthy',
            timestamp: new Date().toISOString(),
            database: 'disconnected',
            error: error.message
        });
    }
});

module.exports = router;
