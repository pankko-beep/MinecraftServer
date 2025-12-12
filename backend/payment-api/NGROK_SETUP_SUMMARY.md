# Payment API Configuration Summary - ngrok Setup

## ✅ Configuration Complete!

The payment system has been reconfigured to use **ngrok** as a webhook receiver, allowing you to receive Mercado Pago webhooks without a public website.

## 📁 Updated Files

### Configuration Files
- ✅ **[.env.example](backend/payment-api/.env.example)** - Added ngrok URL placeholder and setup instructions
- ✅ **[README.md](backend/payment-api/README.md)** - Complete guide with ngrok integration

### Documentation
- ✅ **[QUICK_REFERENCE.md](backend/payment-api/QUICK_REFERENCE.md)** - Quick lookup guide for common tasks
- ✅ **[TROUBLESHOOTING.md](backend/payment-api/TROUBLESHOOTING.md)** - Comprehensive troubleshooting checklist
- ✅ **[PAYMENT_FLOW_DIAGRAMS.md](docs/PAYMENT_FLOW_DIAGRAMS.md)** - Visual system architecture

### Helper Scripts
- ✅ **[start-with-ngrok.ps1](backend/payment-api/scripts/start-with-ngrok.ps1)** - Automated setup script (NEW!)
- ✅ **[send-payment.ps1](backend/payment-api/scripts/send-payment.ps1)** - Enhanced with ngrok support and connection testing

## 🚀 Quick Start (3 Steps)

### For Absolute Beginners:
```powershell
cd backend\payment-api
.\scripts\start-with-ngrok.ps1 -CheckHealth
```
Follow the on-screen instructions!

### For Experienced Users:
```powershell
# Terminal 1: Start API
cd backend\payment-api
npm run dev

# Terminal 2: Start ngrok
ngrok http 3000

# Copy ngrok URL and update Mercado Pago webhook
```

## 📝 What You Need to Do Next

### 1. Set Up Environment File
```powershell
cd backend\payment-api
Copy-Item .env.example .env
# Edit .env and fill in:
# - Database credentials
# - Mercado Pago credentials (MP_ACCESS_TOKEN, MP_WEBHOOK_SECRET, MP_PUBLIC_KEY)
# - CUSTOM_PAYMENT_SECRET (generate random string)
```

### 2. Install ngrok
If not already installed:
- Download from: https://ngrok.com/download
- Or via Chocolatey: `choco install ngrok`
- Authenticate: `ngrok config add-authtoken YOUR_TOKEN`

### 3. Start Everything
Use the helper script (recommended):
```powershell
.\scripts\start-with-ngrok.ps1 -CheckHealth
```

Or manually:
```powershell
# Terminal 1
npm run dev

# Terminal 2
ngrok http 3000
```

### 4. Configure Mercado Pago
1. Go to https://www.mercadopago.com.br/developers/panel/app
2. Select your application
3. Navigate to Webhooks section
4. Set webhook URL: `https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/mercadopago`
5. Enable payment notifications
6. Save webhook secret to `.env` as `MP_WEBHOOK_SECRET`

### 5. Test the System

**Test Health Check:**
```powershell
curl https://YOUR_NGROK_URL.ngrok-free.app/api/health
```

**Test Custom Payment:**
```powershell
cd scripts
.\send-payment.ps1 -Username "TestPlayer" -Amount 100.0 -ApiUrl "https://YOUR_NGROK_URL.ngrok-free.app/api/webhooks/custom/confirm" -Secret "your_secret" -TestConnection
```

**Test Mercado Pago:**
- Create a test payment using Mercado Pago test credentials
- Verify webhook is received in logs

## 📚 Documentation Structure

```
backend/payment-api/
├── README.md                    # Complete setup and usage guide
├── QUICK_REFERENCE.md          # Fast lookup for common tasks
├── TROUBLESHOOTING.md          # Detailed troubleshooting checklist
├── .env.example                # Environment template with ngrok notes
└── scripts/
    ├── start-with-ngrok.ps1    # Automated setup helper
    └── send-payment.ps1        # Payment testing script

docs/
└── PAYMENT_FLOW_DIAGRAMS.md    # Visual architecture diagrams
```

## 💡 Key Features Added

### 1. ngrok Integration
- Complete setup instructions
- URL management tips
- Webhook configuration guide
- Free vs paid plan considerations

### 2. Payment Creation Methods

**Mercado Pago (Production):**
- Player uses `/vip buy` in-game
- Plugin generates PIX payment
- Player pays via QR code
- Webhook automatically confirms payment

**Custom Payment (Testing):**
- PowerShell script with signature generation
- curl examples with signature helper
- Manual PowerShell code for learning
- Connection testing built-in

### 3. Helper Scripts

**start-with-ngrok.ps1:**
- Automated environment setup
- Checks prerequisites
- Starts server and ngrok
- Displays ngrok URL automatically
- Provides next steps

**send-payment.ps1 (Enhanced):**
- Added `-TestConnection` flag
- ngrok URL detection
- Better error messages
- Connection verification

### 4. Comprehensive Documentation

**README.md:**
- ngrok setup section
- Quick start guides (automated & manual)
- Payment creation examples
- Common workflows
- Environment variables reference
- Troubleshooting basics

**QUICK_REFERENCE.md:**
- One-page cheat sheet
- All important URLs
- Common commands
- Configuration checklist
- Testing workflow

**TROUBLESHOOTING.md:**
- Pre-flight checklist
- Startup checklist
- Issue-by-issue solutions
- Diagnostic commands
- Emergency recovery procedures

**PAYMENT_FLOW_DIAGRAMS.md:**
- System architecture diagram
- Mercado Pago flow diagram
- Custom payment flow diagram
- ngrok tunnel details
- Security flow (HMAC)
- Startup sequence

## 🔒 Security Notes

- All webhook requests are verified with HMAC signatures
- Custom payments require `CUSTOM_PAYMENT_SECRET`
- Mercado Pago webhooks verified with `MP_WEBHOOK_SECRET`
- Rate limiting enabled (100 req/15min)
- CORS protection configured
- Logs contain no sensitive data

## ⚙️ Environment Variables

Essential variables in `.env`:

| Variable | Purpose | Example |
|----------|---------|---------|
| `PORT` | Server port | `3000` |
| `DB_*` | Database connection | See .env.example |
| `NGROK_URL` | Current ngrok URL (reference) | `https://abc.ngrok-free.app` |
| `MP_ACCESS_TOKEN` | Mercado Pago auth | `APP_USR-...` |
| `MP_WEBHOOK_SECRET` | MP webhook verification | From MP dashboard |
| `CUSTOM_PAYMENT_SECRET` | Custom payment auth | Random 32+ chars |

## 🎯 Testing Checklist

- [ ] Local server responds: `curl http://localhost:3000/api/health`
- [ ] ngrok tunnel works: `curl https://NGROK_URL/api/health`
- [ ] Custom payment succeeds: `.\scripts\send-payment.ps1 ...`
- [ ] Database records transaction
- [ ] Player balance updates
- [ ] Mercado Pago webhook configured
- [ ] Test payment processes successfully

## 🆘 Getting Help

1. Check [TROUBLESHOOTING.md](backend/payment-api/TROUBLESHOOTING.md)
2. Review logs: `Get-Content backend\payment-api\logs\payment-api.log -Tail 50`
3. Check ngrok dashboard: http://127.0.0.1:4040
4. Verify configuration in `.env`
5. Test with minimal example

## 📞 Support Checklist

Before asking for help, verify:
- [ ] Followed setup steps in README
- [ ] Checked troubleshooting guide
- [ ] Tested with examples provided
- [ ] Reviewed relevant logs
- [ ] Can reproduce the issue

## 🎉 Success Indicators

You'll know everything is working when:
1. ✅ Server starts without errors
2. ✅ ngrok tunnel establishes successfully
3. ✅ Health check returns 200 OK
4. ✅ Custom payment creates transaction
5. ✅ Database shows new transaction
6. ✅ Player balance increases
7. ✅ Mercado Pago webhooks are received
8. ✅ In-game VIP activates after payment

## 🚦 Status

- **Configuration:** ✅ Complete
- **Documentation:** ✅ Complete
- **Scripts:** ✅ Ready to use
- **Testing:** ⏳ Awaiting your tests
- **Production:** ⏳ Deploy when ready

---

**Configuration Date:** December 12, 2025  
**Configured by:** GitHub Copilot  
**Status:** Ready for testing

**Next Actions:**
1. Set up `.env` file with your credentials
2. Install ngrok if not already installed
3. Run `.\scripts\start-with-ngrok.ps1`
4. Configure Mercado Pago webhook
5. Test with custom payment
6. Test with Mercado Pago test payment

Good luck! 🚀
