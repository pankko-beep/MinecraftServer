# Nexus Payment API

Backend server for receiving and processing payment webhooks for the Nexus Minecraft Server.

## Features

- **Mercado Pago Integration**: Receive PIX payment notifications
- **Custom Payment Methods**: Manual confirmations via API or PowerShell
- **Security**: HMAC signature verification, rate limiting, CORS protection
- **Database Integration**: Direct connection to Minecraft plugin database
- **Logging**: Comprehensive logging with Winston
- **Health Monitoring**: Health check endpoints

## Installation

```powershell
cd backend\payment-api
npm install
```

## Configuration

1. Copy `.env.example` to `.env`:
   ```powershell
   Copy-Item .env.example .env
   ```

2. Edit `.env` and configure:
   - Database credentials (same as Minecraft plugin)
   - Mercado Pago credentials
   - API secrets
   - Security settings

## Running the Server

### Development Mode (with auto-reload)
```powershell
npm run dev
```

### Production Mode
```powershell
npm start
```

The server will start on port 3000 (or the port specified in `.env`).

## API Endpoints

### Health Check
```
GET /api/health
```

### Mercado Pago Webhook
```
POST /api/webhooks/mercadopago
```
Automatically receives notifications from Mercado Pago when PIX payments are made.

**Mercado Pago Configuration:**
1. Go to https://www.mercadopago.com.br/developers/panel/app
2. Configure webhook URL: `https://your-domain.com/api/webhooks/mercadopago`
3. Enable payment notifications

### Custom Payment - Confirm
```
POST /api/webhooks/custom/confirm
Headers:
  Content-Type: application/json
  X-Payment-Signature: <HMAC-SHA256 signature>
Body:
{
  "username": "Player123",
  "amount": 100.0,
  "method": "MANUAL",
  "metadata": {}
}
```

### Custom Payment - Create Pending
```
POST /api/webhooks/custom/pending
Headers:
  Content-Type: application/json
  X-Payment-Signature: <HMAC-SHA256 signature>
Body:
{
  "username": "Player123",
  "amount": 100.0,
  "externalId": "PAYMENT_12345",
  "method": "MANUAL"
}
```

### Generate Signature (Helper)
```
GET /api/webhooks/custom/generate-signature?username=Player123&amount=100.0
```
Returns signature and example code for PowerShell/curl.

## PowerShell Payment Example

```powershell
# Configuration
$apiUrl = "http://localhost:3000/api/webhooks/custom/confirm"
$secret = "your_custom_payment_secret"

# Payment data
$username = "Player123"
$amount = 100.0
$method = "MANUAL"

# Create JSON body
$bodyData = @{
    username = $username
    amount = $amount
    method = $method
} | ConvertTo-Json -Compress

# Generate HMAC signature
$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = [Text.Encoding]::UTF8.GetBytes($secret)
$signature = [BitConverter]::ToString(
    $hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($bodyData))
).Replace("-","").ToLower()

# Send request
$headers = @{
    "Content-Type" = "application/json"
    "X-Payment-Signature" = $signature
}

$response = Invoke-RestMethod -Uri $apiUrl -Method Post -Headers $headers -Body $bodyData

Write-Host "Payment processed successfully!" -ForegroundColor Green
Write-Host "Transaction ID: $($response.data.transactionId)"
Write-Host "New balance: $($response.data.newBalance)"
```

## Security

### HMAC Signature Verification

All custom payment requests must include a valid HMAC-SHA256 signature:

1. Create JSON payload
2. Generate HMAC signature using secret key
3. Include signature in `X-Payment-Signature` header

### Rate Limiting

- 100 requests per 15 minutes per IP (configurable in `.env`)
- Applies to all `/api/*` endpoints

### IP Whitelisting

Configure `ALLOWED_IPS` in `.env` to restrict access to specific IPs.

## Database Schema

The API uses the same database as the Minecraft plugin. Required tables:

- `nexus_players` - Player data and balances
- `nexus_transactions` - Transaction history

## Logs

Logs are stored in `./logs/`:
- `payment-api.log` - All logs
- `error.log` - Error logs only

## Production Deployment

1. Set `NODE_ENV=production` in `.env`
2. Configure reverse proxy (nginx/Apache)
3. Use PM2 or similar for process management:
   ```bash
   npm install -g pm2
   pm2 start server.js --name nexus-payment-api
   pm2 save
   pm2 startup
   ```

4. Configure SSL/TLS certificate
5. Set up firewall rules
6. Configure Mercado Pago webhook URL with your domain

## Troubleshooting

### Database Connection Issues
- Verify MySQL credentials in `.env`
- Ensure database is accessible from payment API server
- Check firewall rules

### Mercado Pago Webhook Not Receiving
- Verify webhook URL is publicly accessible
- Check webhook signature verification
- Review logs for errors

### PowerShell Signature Mismatch
- Ensure exact same JSON body (no extra spaces/formatting)
- Verify secret key matches `.env` configuration
- Check signature encoding (should be lowercase hex)

## Support

For issues or questions, check the logs first (`./logs/payment-api.log`).
