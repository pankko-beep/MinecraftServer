/**
 * Winston Logger Configuration
 */

const winston = require('winston');
const path = require('path');

const logLevel = process.env.LOG_LEVEL || 'info';
const logFile = process.env.LOG_FILE || './logs/payment-api.log';

// Create logger instance
const logger = winston.createLogger({
    level: logLevel,
    format: winston.format.combine(
        winston.format.timestamp({
            format: 'YYYY-MM-DD HH:mm:ss'
        }),
        winston.format.errors({ stack: true }),
        winston.format.splat(),
        winston.format.json()
    ),
    defaultMeta: { service: 'payment-api' },
    transports: [
        // Write all logs to file
        new winston.transports.File({ 
            filename: logFile,
            maxsize: 5242880, // 5MB
            maxFiles: 5,
        }),
        // Write errors to separate file
        new winston.transports.File({ 
            filename: path.join(path.dirname(logFile), 'error.log'),
            level: 'error',
            maxsize: 5242880,
            maxFiles: 5,
        })
    ]
});

// Console output in development
if (process.env.NODE_ENV !== 'production') {
    logger.add(new winston.transports.Console({
        format: winston.format.combine(
            winston.format.colorize(),
            winston.format.simple()
        )
    }));
}

module.exports = logger;
