/**
 * Nexus Payment API Server
 * 
 * Receives and processes payment webhooks from:
 * - Mercado Pago (PIX payments)
 * - Custom payment methods (PowerShell instances, manual confirmations)
 * 
 * @author Nexus Development Team
 */

require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const logger = require('./utils/logger');
const db = require('./utils/database');

// Import route handlers
const mercadoPagoRoutes = require('./routes/mercadopago');
const customPaymentRoutes = require('./routes/custom');
const healthRoutes = require('./routes/health');

// Initialize Express app
const app = express();
const PORT = process.env.PORT || 3000;

// Security Middleware
app.use(helmet());

// CORS Configuration
const corsOptions = {
    origin: function (origin, callback) {
        const allowedOrigins = process.env.ALLOWED_ORIGINS ? 
            process.env.ALLOWED_ORIGINS.split(',') : ['http://localhost:3000'];
        
        if (!origin || allowedOrigins.indexOf(origin) !== -1) {
            callback(null, true);
        } else {
            callback(new Error('Not allowed by CORS'));
        }
    },
    credentials: true
};
app.use(cors(corsOptions));

// Rate Limiting
const limiter = rateLimit({
    windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 15 * 60 * 1000, // 15 minutes
    max: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS) || 100,
    message: 'Too many requests from this IP, please try again later.',
    standardHeaders: true,
    legacyHeaders: false,
});
app.use('/api/', limiter);

// Body Parsers
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Request Logging Middleware
app.use((req, res, next) => {
    logger.info(`${req.method} ${req.path}`, {
        ip: req.ip,
        userAgent: req.get('user-agent')
    });
    next();
});

// Routes
app.use('/api/health', healthRoutes);
app.use('/api/webhooks/mercadopago', mercadoPagoRoutes);
app.use('/api/webhooks/custom', customPaymentRoutes);

// Root endpoint
app.get('/', (req, res) => {
    res.json({
        service: 'Nexus Payment API',
        version: '1.0.0',
        status: 'running',
        endpoints: {
            health: '/api/health',
            mercadoPago: '/api/webhooks/mercadopago',
            custom: '/api/webhooks/custom'
        }
    });
});

// 404 Handler
app.use((req, res) => {
    res.status(404).json({
        error: 'Endpoint not found',
        path: req.path
    });
});

// Global Error Handler
app.use((err, req, res, next) => {
    logger.error('Unhandled error:', {
        error: err.message,
        stack: err.stack,
        path: req.path
    });

    res.status(err.status || 500).json({
        error: process.env.NODE_ENV === 'production' ? 
            'Internal server error' : err.message
    });
});

// Database Connection
db.connect()
    .then(() => {
        logger.info('Database connection established');
        
        // Start Server
        app.listen(PORT, () => {
            logger.info(`Payment API server running on port ${PORT}`);
            logger.info(`Environment: ${process.env.NODE_ENV || 'development'}`);
        });
    })
    .catch((err) => {
        logger.error('Failed to connect to database:', err);
        process.exit(1);
    });

// Graceful Shutdown
process.on('SIGTERM', () => {
    logger.info('SIGTERM signal received: closing HTTP server');
    db.disconnect();
    process.exit(0);
});

process.on('SIGINT', () => {
    logger.info('SIGINT signal received: closing HTTP server');
    db.disconnect();
    process.exit(0);
});

module.exports = app;
