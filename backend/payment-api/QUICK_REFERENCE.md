# Payment API Quick Reference

## 🚀 Starting the System

### Option 1: Automated (Recommended)
```powershell
cd backend\payment-api
.\scripts\start-with-ngrok.ps1 -CheckHealth
```

### Option 2: Manual
```powershell
# Terminal 1: Start API
cd backend\payment-api
npm run dev

# Terminal 2: Start ngrok
ngrok http 3000
```

## 🌐 Important URLs

| Service | Local | ngrok |
|---------|-------|-------|
| Health Check | http://localhost:3000/api/health | https://YOUR_URL.ngrok-free.app/api/health |
| Mercado Pago Webhook | http://localhost:3000/api/webhooks/mercadopago | https://YOUR_URL.ngrok-free.app/api/webhooks/mercadopago |
| Custom Payment | http://localhost:3000/api/webhooks/custom/confirm | https://YOUR_URL.ngrok-free.app/api/webhooks/custom/confirm |
| ngrok Dashboard | http://127.0.0.1:4040 | - |

## 💳 Creating Payments

### Mercado Pago (Real Payments)
1. Player uses `/vip buy <package>` in-game
2. Plugin creates payment and shows QR code
3. Player pays
4. Webhook automatically confirms payment

**Setup:**
- Configure webhook in Mercado Pago: `https://YOUR_URL.ngrok-free.app/api/webhooks/mercadopago`
- Add credentials to `.env`: MP_ACCESS_TOKEN, MP_WEBHOOK_SECRET

### Custom Payment (Testing/Manual)

**Using PowerShell Script:**
```powershell
cd backend\payment-api\scripts

# Local
.\send-payment.ps1 -Username "Player123" -Amount 100.0 -Secret "your_secret"

# With ngrok
.\send-payment.ps1 -Username "Player123" -Amount 100.0 -ApiUrl "https://YOUR_URL.ngrok-free.app/api/webhooks/custom/confirm" -Secret "your_secret" -TestConnection
```

**Using curl:**
```bash
# Get signature
curl "https://YOUR_URL.ngrok-free.app/api/webhooks/custom/generate-signature?username=Player123&amount=100.0"

# Send payment
curl -X POST https://YOUR_URL.ngrok-free.app/api/webhooks/custom/confirm \
  -H "Content-Type: application/json" \
  -H "X-Payment-Signature: SIGNATURE_FROM_ABOVE" \
  -d '{"username":"Player123","amount":100.0,"method":"MANUAL"}'
```

## 🔧 Configuration Checklist

### .env File
- [ ] `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` - Database connection
- [ ] `MP_ACCESS_TOKEN` - Mercado Pago access token
- [ ] `MP_WEBHOOK_SECRET` - Mercado Pago webhook secret
- [ ] `MP_PUBLIC_KEY` - Mercado Pago public key
- [ ] `CUSTOM_PAYMENT_SECRET` - Random secret for custom payments
- [ ] `API_SECRET_KEY` - Random secret for API

### Mercado Pago Dashboard
- [ ] Application created
- [ ] Webhook URL configured: `https://YOUR_URL.ngrok-free.app/api/webhooks/mercadopago`
- [ ] Payment notifications enabled
- [ ] Webhook secret copied to `.env`

### ngrok
- [ ] Installed and in PATH
- [ ] Authenticated (if using free plan)
- [ ] Running on port 3000
- [ ] URL copied and updated in Mercado Pago

## 🧪 Testing Workflow

1. **Test local server:**
   ```powershell
   curl http://localhost:3000/api/health
   ```

2. **Test ngrok tunnel:**
   ```powershell
   curl https://YOUR_URL.ngrok-free.app/api/health
   ```

3. **Test custom payment:**
   ```powershell
   .\scripts\send-payment.ps1 -Username "TestPlayer" -Amount 1.0 -Secret "your_secret" -TestConnection
   ```

4. **Test Mercado Pago webhook:**
   - Create test payment in Mercado Pago test environment
   - Check logs: `Get-Content logs\payment-api.log -Wait -Tail 50`

## 📝 Common Commands

```powershell
# Check if server is running
Get-Process -Name node

# View logs in real-time
Get-Content logs\payment-api.log -Wait -Tail 50

# Check ngrok status
curl http://127.0.0.1:4040/api/tunnels | ConvertFrom-Json

# Read saved ngrok URL
Get-Content ngrok-url.txt

# Stop all node processes (careful!)
Stop-Process -Name node

# Restart server
npm run dev
```

## 🐛 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Server won't start | Check port 3000 not in use: `netstat -ano | findstr :3000` |
| ngrok not found | Install: `choco install ngrok` or download from ngrok.com |
| Database connection error | Verify credentials in `.env`, check MySQL is running |
| Webhook not received | Verify ngrok URL in Mercado Pago, check logs |
| Signature mismatch | Ensure secret in script matches `.env` file |
| Player not found | Player must join server at least once |

## 📞 Support Checklist

Before asking for help:
- [ ] Checked `logs\payment-api.log`
- [ ] Verified server is running: `curl http://localhost:3000/api/health`
- [ ] Verified ngrok is running: `curl http://127.0.0.1:4040`
- [ ] Tested through ngrok: `curl https://YOUR_URL.ngrok-free.app/api/health`
- [ ] Verified database connection
- [ ] Checked Mercado Pago webhook configuration
- [ ] Confirmed player exists in database
- [ ] Verified secrets match in `.env` and scripts

## 📚 Documentation Links

- Full README: [README.md](README.md)
- PowerShell Script: [scripts/send-payment.ps1](scripts/send-payment.ps1)
- Setup Helper: [scripts/start-with-ngrok.ps1](scripts/start-with-ngrok.ps1)
- Environment Template: [.env.example](.env.example)

---

**Quick Access:** Save this file for quick reference during development!
