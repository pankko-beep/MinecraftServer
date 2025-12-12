# Nexus Payment API

Backend server for receiving and processing payment webhooks for the Nexus Minecraft Server.

> 📌 **Quick Start?** See [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for a condensed guide!

## Features

- **Mercado Pago Integration**: Receive PIX payment notifications
- **Custom Payment Methods**: Manual confirmations via API or PowerShell
- **ngrok Integration**: Use ngrok as webhook receiver for testing and production
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

## ngrok Setup (Required for Webhooks)

Since we don't have a public website yet, we use **ngrok** to expose the local server to receive webhooks from Mercado Pago.

### 1. Install ngrok
- Download from https://ngrok.com/download
- Or via chocolatey: `choco install ngrok`

### 2. Authenticate ngrok (free account)
```powershell
ngrok config add-authtoken YOUR_NGROK_AUTH_TOKEN
```

### 3. Start ngrok tunnel
```powershell
ngrok http 3000
```

This will output something like:
```
Forwarding  https://abc123.ngrok-free.app -> http://localhost:3000
```

### 4. Update Mercado Pago Webhook URL
Copy the ngrok URL (e.g., `https://abc123.ngrok-free.app`) and configure it in Mercado Pago (see Mercado Pago Configuration section below).

**Important Notes:**
- Free ngrok URLs change every time you restart ngrok
- You'll need to update the webhook URL in Mercado Pago each time
- For production, consider ngrok paid plan for static URLs or deploy to a cloud service

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

**Complete Startup Process:**
1. Start the payment API server: `npm run dev`
2. In another terminal, start ngrok: `ngrok http 3000`
3. Copy the ngrok URL and update Mercado Pago webhook configuration
4. Test the connection using the health check endpoint

## API Endpoints

### Health Check
```
GET /api/health
GET https://YOUR_NGROK_URL.ngrok-free.app/api/health
```
Check if the server is running and healthy. Use this to verify your ngrok tunnel is working.

**Test with curl:**
```bash
curl https://YOUR_NGROK_URL.ngrok-free.app/api/health
```

### Mercado Pago Webhook
```
POST /api/webhooks/mercadopago
POST https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/mercadopago
```
Automatically receives notifications from Mercado Pago when PIX payments are made.

**Mercado Pago Configuration:**
1. Go to https://www.mercadopago.com.br/developers/panel/app
2. Select your application
3. Go to "Webhooks" or "Notificaciones"
4. Configure webhook URL: `https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/mercadopago`
5. Enable payment notifications (payment.created, payment.updated)
6. Save and copy the webhook secret to your `.env` file as `MP_WEBHOOK_SECRET`

**Important:** Update this URL every time you restart ngrok (unless using paid static URL).

### Custom Payment - Confirm
```
POST /api/webhooks/custom/confirm
POST https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/confirm
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
POST https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/pending
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
GET https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/generate-signature?username=Player123&amount=100.0
```
Returns signature and example code for PowerShell/curl. Helpful for testing.

## Creating Payment Instances

### Method 1: Mercado Pago PIX Payment (Production)

This is the main payment method for real transactions.

**Player Flow:**
1. Player uses `/vip buy` command in-game
2. Plugin generates PIX payment via Mercado Pago API
3. Player scans QR code and pays
4. Mercado Pago sends webhook to ngrok URL
5. Payment API receives webhook and confirms payment
6. Player balance is updated automatically

**Setup Requirements:**
- Mercado Pago account with API credentials
- ngrok tunnel running
- Webhook URL configured in Mercado Pago dashboard
- MP_ACCESS_TOKEN and MP_WEBHOOK_SECRET in .env file

**Testing Mercado Pago:**
```powershell
# 1. Start the server
npm run dev

# 2. Start ngrok
ngrok http 3000

# 3. Update webhook URL in Mercado Pago with ngrok URL

# 4. Create a real test payment through Mercado Pago test account
# (Use test credentials from Mercado Pago developers panel)
```

### Method 2: Custom Payment (Testing/Manual)

For testing, manual payments, or alternative payment methods.

#### Option A: Using PowerShell Script (Recommended)

```powershell
# Navigate to scripts folder
cd backend\payment-api\scripts

# Run the payment script
.\send-payment.ps1 -Username "Player123" -Amount 100.0 -Secret "your_custom_payment_secret"

# With ngrok URL
.\send-payment.ps1 -Username "Player123" -Amount 100.0 -ApiUrl "https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/confirm" -Secret "your_custom_payment_secret"

# With custom method
.\send-payment.ps1 -Username "Player123" -Amount 50.0 -Method "PIX_MANUAL" -Secret "your_custom_payment_secret"
```

#### Option B: Using curl

```bash
# 1. Get signature helper
curl "https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/generate-signature?username=Player123&amount=100.0"

# 2. Use the signature from step 1
curl -X POST https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/confirm \
  -H "Content-Type: application/json" \
  -H "X-Payment-Signature: YOUR_GENERATED_SIGNATURE" \
  -d '{"username":"Player123","amount":100.0,"method":"MANUAL"}'
```

#### Option C: Manual PowerShell (for learning)

```powershell
# Configuration
$apiUrl = "https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/confirm"
$secret = "your_custom_payment_secret"  # From .env file

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

### Method 3: Create Pending Payment (Advanced)

Create a payment that requires later confirmation (e.g., bank transfer verification).

```powershell
# Use the signature helper first
$signatureResponse = Invoke-RestMethod -Uri "https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/generate-signature?username=Player123&amount=100.0"

# Create pending payment
$pendingBody = @{
    username = "Player123"
    amount = 100.0
    externalId = "PAYMENT_$(Get-Date -Format 'yyyyMMddHHmmss')"
    method = "BANK_TRANSFER"
} | ConvertTo-Json -Compress

# Calculate signature for pending payment
$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = [Text.Encoding]::UTF8.GetBytes($secret)
$signature = [BitConverter]::ToString(
    $hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($pendingBody))
).Replace("-","").ToLower()

$headers = @{
    "Content-Type" = "application/json"
    "X-Payment-Signature" = $signature
}

Invoke-RestMethod -Uri "https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/pending" -Method Post -Headers $headers -Body $pendingBody
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

## Quick Start Guide

### Automated Setup (Recommended for Beginners)

Use the helper script to automatically set up everything:

```powershell
cd backend\payment-api
.\scripts\start-with-ngrok.ps1 -CheckHealth
```

This script will:
- ✅ Check if .env is configured
- ✅ Verify ngrok is installed
- ✅ Install dependencies if needed
- ✅ Start the payment API server
- ✅ Start ngrok tunnel
- ✅ Display your ngrok URL
- ✅ Provide next steps

### Manual Setup (For Advanced Users)

1. **Start the Payment API:**
   ```powershell
   cd backend\payment-api
   npm run dev
   ```

2. **Start ngrok in another terminal:**
   ```powershell
   ngrok http 3000
   ```
   Copy the HTTPS URL (e.g., `https://abc123.ngrok-free.app`)

3. **Test the connection:**
   ```powershell
   curl https://abc123.ngrok-free.app/api/health
   ```

4. **Send a test payment:**
   ```powershell
   cd backend\payment-api\scripts
   .\send-payment.ps1 -Username "TestPlayer" -Amount 100.0 -ApiUrl "https://abc123.ngrok-free.app/api/webhooks/custom/confirm" -Secret "your_secret_from_env" -TestConnection
   ```

### For Production (with ngrok)

1. **Get ngrok static domain (paid plan):**
   - Sign up for ngrok paid plan
   - Reserve a static domain (e.g., `yourserver.ngrok.io`)

2. **Configure Mercado Pago webhook:**
   - Go to Mercado Pago developer panel
   - Set webhook URL: `https://yourserver.ngrok.io/api/webhooks/mercadopago`

3. **Run with PM2:**
   ```bash
   npm install -g pm2
   pm2 start server.js --name nexus-payment-api
   pm2 save
   pm2 startup
   ```

4. **Run ngrok with static domain:**
   ```powershell
   ngrok http 3000 --domain=yourserver.ngrok.io
   ```

### Alternative: Deploy to Cloud (Future)

When ready for full production, deploy to:
- **Azure Web Apps**
- **AWS EC2/Lambda**
- **Google Cloud Run**
- **Heroku**
- **DigitalOcean**

Configure SSL/TLS certificate and update Mercado Pago webhook URL accordingly.

## Troubleshooting

> 📋 **Detailed Guide:** See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for a complete checklist and solutions!

### ngrok Issues

**Problem: ngrok URL changes every restart**
- Solution: Use ngrok paid plan for static URLs, or update Mercado Pago webhook URL each time
- Quick fix: Create a script to auto-update webhook URL via Mercado Pago API

**Problem: "ngrok not found" error**
- Solution: Install ngrok from https://ngrok.com/download or via `choco install ngrok`
- Add ngrok to PATH: `$env:PATH += ";C:\path\to\ngrok"`

**Problem: ngrok tunnel not working**
- Check if port 3000 is already in use: `netstat -ano | findstr :3000`
- Verify server is running before starting ngrok
- Test local server first: `curl http://localhost:3000/api/health`

### Database Connection Issues
- Verify MySQL credentials in `.env`
- Ensure database is accessible from payment API server
- Check firewall rules
- Test connection: `mysql -h localhost -u nexus_user -p nexus_db`

### Mercado Pago Webhook Not Receiving

**Problem: Webhook not triggering**
- Verify ngrok tunnel is running and URL is correct
- Check webhook URL in Mercado Pago dashboard matches current ngrok URL
- Test webhook URL: `curl https://YOUR_NGROK_URL.ngrok-free.app/api/health`
- Check logs: `backend\payment-api\logs\payment-api.log`

**Problem: Signature verification failed**
- Ensure MP_WEBHOOK_SECRET in `.env` matches Mercado Pago webhook secret
- Check Mercado Pago developer panel for correct secret
- Review request headers in logs for debugging

**Problem: Payment not confirmed in game**
- Verify database connection is working
- Check player username exists in database
- Review transaction logs in database
- Check plugin is reading from correct database

### Custom Payment Issues

**Problem: PowerShell Signature Mismatch**
- Ensure exact same JSON body (no extra spaces/formatting)
- Verify secret key matches `.env` configuration (`CUSTOM_PAYMENT_SECRET`)
- Check signature encoding (should be lowercase hex)
- Use the signature helper endpoint to debug

**Problem: "Invalid signature" error**
- Secret in PowerShell script must match `.env` file exactly
- JSON body must be compressed (no whitespace)
- Use `ConvertTo-Json -Compress` in PowerShell

**Problem: "Player not found" error**
- Player must exist in database (join server at least once)
- Check spelling of username (case-sensitive)
- Query database: `SELECT * FROM nexus_players WHERE username = 'Player123';`

### Testing Tips

1. **Test locally first:**
   ```powershell
   curl http://localhost:3000/api/health
   ```

2. **Test through ngrok:**
   ```powershell
   curl https://YOUR_NGROK_URL.ngrok-free.app/api/health
   ```

3. **Check logs in real-time:**
   ```powershell
   Get-Content backend\payment-api\logs\payment-api.log -Wait -Tail 50
   ```

4. **Test signature generation:**
   ```powershell
   curl "https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/generate-signature?username=Player123&amount=100.0"
   ```

5. **Monitor ngrok requests:**
   - Open http://127.0.0.1:4040 in browser
   - View all HTTP requests through ngrok tunnel
   - Inspect request/response details

## Common Workflows

### Daily Development Workflow

```powershell
# Terminal 1: Start Payment API
cd backend\payment-api
npm run dev

# Terminal 2: Start ngrok
ngrok http 3000

# Terminal 3: Monitor logs (optional)
Get-Content backend\payment-api\logs\payment-api.log -Wait -Tail 50
```

**When ngrok URL changes:**
1. Copy new ngrok URL from terminal
2. Update Mercado Pago webhook configuration
3. Update any scripts using the old URL

### Testing a Payment End-to-End

**Mercado Pago (Real Payment):**
1. Ensure server and ngrok are running
2. Player runs `/vip buy <package>` in-game
3. Plugin creates payment and returns QR code
4. Player scans and pays
5. Mercado Pago webhook → ngrok → Payment API
6. Payment confirmed automatically

**Custom Payment (Testing):**
```powershell
# Option 1: With local server
cd backend\payment-api\scripts
.\send-payment.ps1 -Username "TestPlayer" -Amount 100.0 -Secret "your_secret" -TestConnection

# Option 2: With ngrok
.\send-payment.ps1 -Username "TestPlayer" -Amount 100.0 -ApiUrl "https://abc123.ngrok-free.app/api/webhooks/custom/confirm" -Secret "your_secret" -TestConnection

# Option 3: Using signature helper
curl "https://abc123.ngrok-free.app/api/webhooks/custom/generate-signature?username=TestPlayer&amount=100.0"
# Then use the generated signature with curl or Postman
```

### Switching Between Environments

**Local (no internet required):**
```powershell
# Start server only
npm run dev

# Test locally
.\send-payment.ps1 -Username "Player" -Amount 100 -Secret "secret"
```

**ngrok (for Mercado Pago webhooks):**
```powershell
# Terminal 1
npm run dev

# Terminal 2
ngrok http 3000

# Update webhook in Mercado Pago
```

**Production (cloud deployment):**
- Deploy to Azure/AWS/GCP
- Configure DNS and SSL
- Update Mercado Pago webhook to production URL
- No ngrok needed

## Environment Variables Reference

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `PORT` | No | Server port | `3000` |
| `NODE_ENV` | No | Environment | `development` or `production` |
| `DB_HOST` | Yes | Database host | `localhost` |
| `DB_PORT` | Yes | Database port | `3306` |
| `DB_NAME` | Yes | Database name | `nexus_db` |
| `DB_USER` | Yes | Database user | `nexus_user` |
| `DB_PASSWORD` | Yes | Database password | `your_password` |
| `NGROK_URL` | No | Current ngrok URL (for reference) | `https://abc.ngrok-free.app` |
| `MP_ACCESS_TOKEN` | Yes* | Mercado Pago access token | `APP_USR-...` |
| `MP_WEBHOOK_SECRET` | Yes* | Mercado Pago webhook secret | Generated in MP panel |
| `MP_PUBLIC_KEY` | Yes* | Mercado Pago public key | `APP_USR-...` |
| `CUSTOM_PAYMENT_SECRET` | Yes | Secret for custom payments | Random 32+ char string |
| `CUSTOM_PAYMENT_ENABLED` | No | Enable custom payments | `true` or `false` |
| `API_SECRET_KEY` | Yes | General API secret | Random 32+ char string |
| `ALLOWED_IPS` | No | IP whitelist (comma-separated) | `127.0.0.1,192.168.1.1` |
| `ALLOWED_ORIGINS` | No | CORS origins | `http://localhost:3000` |
| `LOG_LEVEL` | No | Logging level | `info`, `debug`, `error` |
| `RATE_LIMIT_WINDOW_MS` | No | Rate limit window | `900000` (15 min) |
| `RATE_LIMIT_MAX_REQUESTS` | No | Max requests per window | `100` |

\* Required only if using Mercado Pago

## Support

For issues or questions:
1. Check the logs first: `./logs/payment-api.log`
2. Test the health endpoint: `curl https://YOUR_URL/api/health`
3. Check ngrok web interface: http://127.0.0.1:4040
4. Verify database connectivity
5. Review Mercado Pago webhook configuration
6. Check if player exists in database
7. Verify secrets match between script and `.env` file

## Additional Resources

### Documentation
- [Quick Reference Guide](QUICK_REFERENCE.md) - Fast lookup for common tasks
- [Payment Flow Diagrams](../../docs/PAYMENT_FLOW_DIAGRAMS.md) - Visual system architecture
- [Project Structure](../../docs/PROJECT_STRUCTURE.md) - Overall project layout

### External Resources
- [Mercado Pago Developer Docs](https://www.mercadopago.com.br/developers/en/docs)
- [ngrok Documentation](https://ngrok.com/docs)
- [Express.js Documentation](https://expressjs.com/)
- [Winston Logger](https://github.com/winstonjs/winston)

### Helper Scripts
- [start-with-ngrok.ps1](scripts/start-with-ngrok.ps1) - Automated setup script
- [send-payment.ps1](scripts/send-payment.ps1) - Test payment creation

---

**Last Updated:** December 2025  
**Version:** 1.0.0  
**ngrok Ready:** ✅ Configured for webhook reception
