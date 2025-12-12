# Payment Service Documentation

## Overview

The Nexus Payment Service is a Node.js-based webhook receiver that processes payments from multiple sources and automatically updates player balances in the Minecraft server database. It supports both automated PIX payments via Mercado Pago and manual payment confirmations through a secure API.

## Architecture

```
┌─────────────────────┐
│   Mercado Pago      │
│   (PIX Payments)    │
└──────────┬──────────┘
           │ Webhook
           ▼
┌─────────────────────┐      ┌─────────────────────┐
│  Payment API Server │◄─────┤  PowerShell Script  │
│  (Node.js/Express)  │      │  (Manual Payments)  │
└──────────┬──────────┘      └─────────────────────┘
           │
           │ MySQL Connection
           ▼
┌─────────────────────┐      ┌─────────────────────┐
│   MySQL Database    │◄────►│  Minecraft Plugin   │
│  (Shared Database)  │      │  (Spigot/Paper)     │
└─────────────────────┘      └─────────────────────┘
```

## Features

### 🔐 Security
- **HMAC-SHA256 Signature Verification**: All requests must include valid cryptographic signatures
- **Rate Limiting**: 100 requests per 15 minutes per IP address
- **CORS Protection**: Configurable cross-origin resource sharing
- **Helmet.js**: Security headers and XSS protection
- **IP Whitelisting**: Optional IP-based access control

### 💳 Payment Methods
- **Mercado Pago PIX**: Automated webhook processing for Brazilian PIX payments
- **Manual Confirmations**: Admin-initiated payments via PowerShell or API
- **Custom Payment Methods**: Extensible architecture for additional payment processors

### 📊 Features
- **Automatic Balance Updates**: Direct database integration with Minecraft plugin
- **Transaction Logging**: Complete audit trail in `nexus_transactions` table
- **Pending Payments**: Support for payment confirmation workflows
- **Real-time Processing**: Instant balance updates upon payment approval
- **Comprehensive Logging**: Winston-based logging with file rotation

---

## Installation & Setup

### Prerequisites

- **Node.js 16+** (check with `node --version`)
- **MySQL Database** (same database used by Minecraft plugin)
- **npm** (comes with Node.js)

### Step 1: Install Dependencies

```powershell
cd backend\payment-api
npm install
```

This installs:
- Express (web framework)
- MySQL2 (database driver)
- Winston (logging)
- Helmet (security)
- And other dependencies

### Step 2: Configuration

1. **Create environment file:**
   ```powershell
   Copy-Item .env.example .env
   ```

2. **Edit `.env` with your settings:**

   ```env
   # Server Configuration
   PORT=3000
   NODE_ENV=production

   # Database Configuration (MUST match Minecraft plugin)
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=nexus_db
   DB_USER=nexus_user
   DB_PASSWORD=your_secure_password

   # Mercado Pago Configuration
   MP_ACCESS_TOKEN=your_mercado_pago_access_token
   MP_WEBHOOK_SECRET=your_webhook_secret
   MP_PUBLIC_KEY=your_public_key

   # Custom Payment Method
   CUSTOM_PAYMENT_SECRET=generate_random_secret_here
   CUSTOM_PAYMENT_ENABLED=true

   # Security
   API_SECRET_KEY=generate_random_secret_key_here
   ALLOWED_IPS=127.0.0.1,your_minecraft_server_ip

   # Logging
   LOG_LEVEL=info
   LOG_FILE=./logs/payment-api.log
   ```

3. **Generate secure secrets:**
   ```powershell
   # Generate random secret (PowerShell)
   -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 32 | ForEach-Object {[char]$_})
   ```

### Step 3: Database Setup

The payment API uses the **same database** as the Minecraft plugin. Ensure these tables exist:

- `nexus_players` - Player data with UUID, username, and balance
- `nexus_transactions` - Transaction history

**Note:** These tables are automatically created by the Minecraft plugin's DatabaseService on first run.

### Step 4: Start the Server

**Development mode (with auto-reload):**
```powershell
npm run dev
```

**Production mode:**
```powershell
npm start
```

The server will start on port 3000 (or your configured `PORT`).

**Verify it's running:**
```powershell
Invoke-RestMethod -Uri "http://localhost:3000/api/health"
```

Expected response:
```json
{
  "status": "healthy",
  "timestamp": "2025-12-11T18:00:00.000Z",
  "uptime": 42.5,
  "database": "connected",
  "version": "1.0.0"
}
```

---

## API Reference

### Base URL
```
http://localhost:3000
```

### Endpoints

#### 1. Health Check
```http
GET /api/health
```

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "2025-12-11T18:00:00.000Z",
  "uptime": 123.45,
  "database": "connected",
  "version": "1.0.0"
}
```

---

#### 2. Mercado Pago Webhook
```http
POST /api/webhooks/mercadopago
```

**Headers:**
- `Content-Type: application/json`
- `X-Signature: ts=<timestamp>,v1=<signature>` (automatically sent by Mercado Pago)
- `X-Request-Id: <request_id>` (automatically sent by Mercado Pago)

**Body:** (automatically sent by Mercado Pago)
```json
{
  "type": "payment",
  "data": {
    "id": "1234567890"
  }
}
```

**Process Flow:**
1. Mercado Pago sends webhook when payment status changes
2. Server verifies webhook signature
3. Fetches payment details from Mercado Pago API
4. Processes payment based on status (approved/pending/rejected)
5. Updates player balance if approved
6. Records transaction in database

**Note:** This endpoint is called automatically by Mercado Pago. You must configure the webhook URL in your Mercado Pago dashboard.

---

#### 3. Custom Payment - Confirm
```http
POST /api/webhooks/custom/confirm
```

**Headers:**
- `Content-Type: application/json`
- `X-Payment-Signature: <hmac_sha256_signature>` (required)

**Body:**
```json
{
  "username": "Player123",
  "amount": 100.0,
  "method": "MANUAL",
  "metadata": {
    "note": "VIP purchase",
    "admin": "AdminUser"
  }
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Payment confirmed",
  "data": {
    "player": "Player123",
    "amount": 100.0,
    "newBalance": 1100.0,
    "transactionId": 42,
    "externalId": "CUSTOM_1702325678_a1b2c3d4"
  }
}
```

**Response (Error):**
```json
{
  "error": "Invalid signature"
}
```

---

#### 4. Custom Payment - Create Pending
```http
POST /api/webhooks/custom/pending
```

**Headers:**
- `Content-Type: application/json`
- `X-Payment-Signature: <hmac_sha256_signature>` (required)

**Body:**
```json
{
  "username": "Player123",
  "amount": 100.0,
  "externalId": "PAYMENT_12345",
  "method": "BANK_TRANSFER"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Pending payment created",
  "data": {
    "player": "Player123",
    "amount": 100.0,
    "externalId": "PAYMENT_12345",
    "status": "PENDING"
  }
}
```

**Use Case:** Create a pending transaction that will be confirmed later when payment is verified.

---

#### 5. Generate Signature Helper
```http
GET /api/webhooks/custom/generate-signature?username=Player123&amount=100.0&method=MANUAL
```

**Response:**
```json
{
  "body": "{\"username\":\"Player123\",\"amount\":100.0,\"method\":\"MANUAL\"}",
  "signature": "a1b2c3d4e5f6...",
  "headers": {
    "Content-Type": "application/json",
    "X-Payment-Signature": "a1b2c3d4e5f6..."
  },
  "curlExample": "curl -X POST http://localhost:3000/api/webhooks/custom/confirm ...",
  "powershellExample": "$headers = @{...}"
}
```

**Use Case:** Helper endpoint to generate valid signatures for testing or manual integration.

---

## PowerShell Integration

### Using the Provided Script

The easiest way to send manual payments is using the included PowerShell script:

```powershell
cd backend\payment-api\scripts

.\send-payment.ps1 -Username "Player123" -Amount 100.0 -Secret "your_custom_payment_secret"
```

**Parameters:**
- `-Username`: Minecraft player username (required)
- `-Amount`: Payment amount in server currency (required)
- `-Secret`: HMAC secret from `.env` file (required)
- `-Method`: Payment method label (optional, default: "MANUAL")
- `-ApiUrl`: Custom API URL (optional, default: http://localhost:3000/api/webhooks/custom/confirm)

**Example Output:**
```
==================================
Nexus Payment Confirmation
==================================

Player: Player123
Amount: 100.0
Method: MANUAL

Generating signature...
Sending payment confirmation...

==================================
✓ Payment Confirmed Successfully!
==================================

Transaction ID: 42
External ID: CUSTOM_1702325678_a1b2c3d4
New Balance: 1100.0
```

### Manual PowerShell Code

If you prefer to write your own integration:

```powershell
# Configuration
$apiUrl = "http://localhost:3000/api/webhooks/custom/confirm"
$secret = "your_custom_payment_secret"

# Payment data
$paymentData = @{
    username = "Player123"
    amount = 100.0
    method = "MANUAL"
} | ConvertTo-Json -Compress

# Generate HMAC signature
$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = [Text.Encoding]::UTF8.GetBytes($secret)
$signature = [BitConverter]::ToString(
    $hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($paymentData))
).Replace("-","").ToLower()

# Send request
$headers = @{
    "Content-Type" = "application/json"
    "X-Payment-Signature" = $signature
}

$response = Invoke-RestMethod -Uri $apiUrl -Method Post -Headers $headers -Body $paymentData

Write-Host "Success! Transaction ID: $($response.data.transactionId)"
```

---

## Mercado Pago Integration

### Setup Steps

1. **Create Mercado Pago Account:**
   - Go to https://www.mercadopago.com.br
   - Create business account
   - Complete verification process

2. **Get Credentials:**
   - Navigate to https://www.mercadopago.com.br/developers/panel/app
   - Create new application
   - Copy **Access Token** (production credentials)
   - Copy **Public Key**

3. **Configure Webhook:**
   - In Mercado Pago dashboard, go to Webhooks
   - Add new webhook URL: `https://your-domain.com/api/webhooks/mercadopago`
   - Select event: "Payments" (`payment`)
   - Save webhook configuration
   - Copy **Webhook Secret** for signature verification

4. **Update `.env` File:**
   ```env
   MP_ACCESS_TOKEN=APP-1234567890ABCDEF-121212-a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6-123456789
   MP_WEBHOOK_SECRET=abcdef1234567890abcdef1234567890
   MP_PUBLIC_KEY=APP_USR-1234abcd-5678-ef90-gh12-ijkl34567890
   ```

### Payment Flow

1. **Player initiates payment:**
   - Frontend/website creates payment request
   - Includes player username in `metadata` or `external_reference`
   - Shows QR Code for PIX payment

2. **Player pays via PIX:**
   - Scans QR Code with banking app
   - Completes PIX payment

3. **Mercado Pago sends webhook:**
   - Payment status changes to "approved"
   - Webhook sent to `/api/webhooks/mercadopago`

4. **Payment API processes:**
   - Verifies webhook signature
   - Fetches payment details
   - Finds player by username
   - Creates transaction record
   - Updates player balance

5. **Player receives balance:**
   - Balance updated in database
   - Player sees new balance on next login (or immediately if online)

### Payment Metadata Format

When creating Mercado Pago payment, include player username:

```json
{
  "transaction_amount": 10.0,
  "description": "VIP Guerreiro - 30 dias",
  "payment_method_id": "pix",
  "payer": {
    "email": "player@example.com"
  },
  "metadata": {
    "username": "Player123"
  },
  "external_reference": "Player123"
}
```

**Important:** Either `metadata.username` or `external_reference` must contain the exact Minecraft username.

---

## Security Considerations

### HMAC Signature Verification

All custom payment requests **must** include a valid HMAC-SHA256 signature:

**How it works:**
1. Create JSON payload (exact body that will be sent)
2. Generate HMAC-SHA256 hash using secret key
3. Include hash in `X-Payment-Signature` header
4. Server recomputes hash and compares

**Why it's important:**
- Prevents unauthorized balance additions
- Ensures requests come from trusted sources
- Protects against replay attacks (combined with unique external IDs)

### Rate Limiting

Default configuration:
- **100 requests per 15 minutes** per IP address
- Applies to all `/api/*` endpoints
- Returns `429 Too Many Requests` when exceeded

Configure in `.env`:
```env
RATE_LIMIT_WINDOW_MS=900000    # 15 minutes in milliseconds
RATE_LIMIT_MAX_REQUESTS=100     # Maximum requests per window
```

### IP Whitelisting

Restrict API access to specific IP addresses:

```env
ALLOWED_IPS=127.0.0.1,192.168.1.100,203.0.113.42
```

**Recommendation:** Only allow:
- Localhost (for local testing)
- Your Minecraft server IP
- Your admin machine IPs

### HTTPS in Production

**⚠️ Critical:** Always use HTTPS in production!

1. **Get SSL Certificate:**
   - Use Let's Encrypt (free)
   - Or purchase from certificate authority

2. **Configure Reverse Proxy:**
   ```nginx
   # Nginx example
   server {
       listen 443 ssl;
       server_name api.yourserver.com;
       
       ssl_certificate /path/to/cert.pem;
       ssl_certificate_key /path/to/key.pem;
       
       location / {
           proxy_pass http://localhost:3000;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

3. **Update Mercado Pago webhook URL** to use `https://`

---

## Database Schema

### nexus_transactions Table

| Column | Type | Description |
|--------|------|-------------|
| `id` | INT AUTO_INCREMENT | Transaction ID |
| `player_uuid` | VARCHAR(36) | Player UUID |
| `amount` | DECIMAL(10,2) | Transaction amount |
| `type` | VARCHAR(50) | Transaction type (e.g., VIP_PURCHASE) |
| `status` | VARCHAR(20) | PENDING, COMPLETED, FAILED |
| `description` | TEXT | Human-readable description |
| `payment_method` | VARCHAR(50) | MERCADO_PAGO_PIX, MANUAL, etc. |
| `external_id` | VARCHAR(255) | Unique external reference |
| `metadata` | JSON | Additional data |
| `created_at` | TIMESTAMP | Creation timestamp |
| `updated_at` | TIMESTAMP | Last update timestamp |

### Transaction Types

- `VIP_PURCHASE` - VIP tier purchase
- `BALANCE_ADD` - Manual balance addition
- `TRANSFER_RECEIVED` - Money received from another player
- `SHOP_PURCHASE` - Item purchased from shop
- And more (see plugin configuration)

### Transaction Statuses

- `PENDING` - Awaiting confirmation
- `COMPLETED` - Successfully processed
- `FAILED` - Payment failed or rejected
- `CANCELLED` - Manually cancelled

---

## Logging

### Log Files

Located in `backend/payment-api/logs/`:

- `payment-api.log` - All logs (info, warn, error)
- `error.log` - Error logs only

### Log Rotation

- Maximum file size: **5 MB**
- Maximum files kept: **5**
- Oldest logs automatically deleted

### Log Levels

Configure in `.env`:
```env
LOG_LEVEL=info   # Options: error, warn, info, debug
```

### Log Format

```json
{
  "level": "info",
  "message": "Payment details retrieved",
  "timestamp": "2025-12-11 18:30:45",
  "service": "payment-api",
  "id": "1234567890",
  "status": "approved",
  "amount": 100.0
}
```

### Viewing Logs in Real-time

```powershell
# PowerShell
Get-Content logs\payment-api.log -Wait -Tail 50

# Or use the built-in tail equivalent
gc logs\payment-api.log -Tail 50 -Wait
```

---

## Production Deployment

### Using PM2 Process Manager

1. **Install PM2:**
   ```bash
   npm install -g pm2
   ```

2. **Start server:**
   ```bash
   pm2 start server.js --name nexus-payment-api
   ```

3. **Save configuration:**
   ```bash
   pm2 save
   ```

4. **Auto-start on reboot:**
   ```bash
   pm2 startup
   # Follow the instructions shown
   ```

5. **Monitoring:**
   ```bash
   pm2 status
   pm2 logs nexus-payment-api
   pm2 monit
   ```

### Environment Variables

Set `NODE_ENV=production` in `.env` for production optimizations.

### Firewall Configuration

**Open port 3000** (or your configured port):

```powershell
# Windows Firewall
New-NetFirewallRule -DisplayName "Nexus Payment API" -Direction Inbound -LocalPort 3000 -Protocol TCP -Action Allow
```

**Or use reverse proxy** (recommended) and only expose ports 80/443.

### Monitoring & Alerts

Consider setting up:
- **Uptime monitoring** (e.g., UptimeRobot)
- **Log aggregation** (e.g., Papertrail, Loggly)
- **Error tracking** (e.g., Sentry)
- **Performance monitoring** (e.g., New Relic)

---

## Troubleshooting

### Database Connection Failed

**Symptoms:**
```
Database connection failed: Error: Access denied for user...
```

**Solutions:**
1. Verify database credentials in `.env`
2. Check database server is running
3. Test connection manually:
   ```powershell
   mysql -h localhost -u nexus_user -p nexus_db
   ```
4. Ensure MySQL allows connections from payment API server IP
5. Check firewall rules

### Mercado Pago Webhook Not Receiving

**Symptoms:**
- Payments processed on Mercado Pago
- No webhook received by payment API
- No transaction created in database

**Solutions:**
1. **Verify webhook URL is publicly accessible:**
   ```powershell
   Invoke-RestMethod -Uri "https://your-domain.com/api/health"
   ```

2. **Check Mercado Pago webhook logs** in dashboard

3. **Verify webhook is configured** for "Payments" event

4. **Check signature verification:**
   - Ensure `MP_WEBHOOK_SECRET` matches Mercado Pago dashboard
   - Review logs for signature errors

5. **Test webhook locally with ngrok:**
   ```bash
   ngrok http 3000
   # Use ngrok URL in Mercado Pago dashboard for testing
   ```

### PowerShell Signature Mismatch

**Symptoms:**
```json
{ "error": "Invalid signature" }
```

**Solutions:**
1. **Verify secret matches `.env`:**
   - Check `CUSTOM_PAYMENT_SECRET` in `.env`
   - Ensure no extra spaces or newlines

2. **Check JSON formatting:**
   - Must use `-Compress` flag: `ConvertTo-Json -Compress`
   - No extra spaces or formatting
   - Order doesn't matter, but content must be exact

3. **Verify signature encoding:**
   - Must be lowercase hexadecimal
   - Use `.ToLower()` on signature

4. **Test with signature generator:**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:3000/api/webhooks/custom/generate-signature?username=Player123&amount=100.0"
   ```

### Player Not Found

**Symptoms:**
```json
{ "error": "Player not found" }
```

**Solutions:**
1. **Verify player exists in database:**
   ```sql
   SELECT * FROM nexus_players WHERE username = 'Player123';
   ```

2. **Check username spelling** (case-sensitive)

3. **Ensure player has joined server at least once** (so DatabaseService created their record)

4. **Check player UUID** if using UUID instead of username

### Rate Limit Exceeded

**Symptoms:**
```json
{ "error": "Too many requests from this IP, please try again later." }
```

**Solutions:**
1. **Wait for rate limit window to expire** (default: 15 minutes)

2. **Increase rate limits in `.env`:**
   ```env
   RATE_LIMIT_MAX_REQUESTS=200
   ```

3. **Whitelist your IP** in `ALLOWED_IPS`

4. **Check for request loops** or automated scripts hitting API too frequently

---

## Testing

### Manual Testing with cURL

**Test health endpoint:**
```bash
curl http://localhost:3000/api/health
```

**Test custom payment (generate signature first):**
```bash
# 1. Generate signature
curl "http://localhost:3000/api/webhooks/custom/generate-signature?username=Player123&amount=100.0"

# 2. Use the signature from response
curl -X POST http://localhost:3000/api/webhooks/custom/confirm \
  -H "Content-Type: application/json" \
  -H "X-Payment-Signature: YOUR_SIGNATURE_HERE" \
  -d '{"username":"Player123","amount":100.0,"method":"MANUAL"}'
```

### Testing with Postman

1. **Import endpoints** or create new request
2. Set method to `POST`
3. URL: `http://localhost:3000/api/webhooks/custom/confirm`
4. Headers:
   - `Content-Type: application/json`
   - `X-Payment-Signature: <get from generate-signature endpoint>`
5. Body (raw JSON):
   ```json
   {
     "username": "Player123",
     "amount": 100.0,
     "method": "MANUAL"
   }
   ```
6. Send request

### Automated Testing

Create test suite (optional):

```javascript
// test/payment.test.js
const request = require('supertest');
const app = require('../server');

describe('Payment API', () => {
  it('should return health status', async () => {
    const res = await request(app).get('/api/health');
    expect(res.statusCode).toBe(200);
    expect(res.body.status).toBe('healthy');
  });
  
  // Add more tests...
});
```

---

## Maintenance

### Regular Tasks

1. **Monitor logs daily:**
   ```powershell
   Get-Content logs\payment-api.log -Tail 100
   ```

2. **Check database transactions:**
   ```sql
   SELECT * FROM nexus_transactions 
   WHERE created_at > DATE_SUB(NOW(), INTERVAL 1 DAY);
   ```

3. **Review error logs weekly:**
   ```powershell
   Get-Content logs\error.log
   ```

4. **Update dependencies monthly:**
   ```powershell
   npm outdated
   npm update
   ```

### Backup Strategy

1. **Database backups** (same as Minecraft plugin database)
2. **Configuration backups** (`.env` file)
3. **Log archives** (optional, logs rotate automatically)

### Performance Monitoring

Monitor these metrics:
- Request response times
- Database query times
- Error rates
- Memory usage
- CPU usage

---

## API Changelog

### Version 1.0.0 (2025-12-11)
- Initial release
- Mercado Pago PIX webhook support
- Custom payment confirmations
- PowerShell integration script
- HMAC signature verification
- Rate limiting
- Transaction logging
- Health check endpoint

---

## Support & Resources

### Documentation
- Main README: `backend/payment-api/README.md`
- Environment example: `backend/payment-api/.env.example`
- PowerShell script: `backend/payment-api/scripts/send-payment.ps1`

### External Resources
- [Mercado Pago API Documentation](https://www.mercadopago.com.br/developers/en/reference)
- [Express.js Documentation](https://expressjs.com/)
- [Node.js Documentation](https://nodejs.org/docs/)
- [MySQL2 Documentation](https://github.com/sidorares/node-mysql2)

### Common Issues
- Check logs first: `logs/payment-api.log`
- Verify database connection
- Confirm signature generation
- Review Mercado Pago webhook logs

---

## Future Enhancements

Planned features for future versions:

- [ ] Additional payment processors (Stripe, PayPal)
- [ ] Subscription management
- [ ] Refund processing
- [ ] Payment analytics dashboard
- [ ] Webhook retry mechanism
- [ ] Multi-currency support
- [ ] Payment scheduling
- [ ] Discount codes/coupons
- [ ] Transaction exports (CSV/PDF)
- [ ] Email notifications

---

**Last Updated:** December 11, 2025  
**Version:** 1.0.0  
**Maintainer:** Nexus Development Team
