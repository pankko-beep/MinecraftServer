# Payment API Troubleshooting Checklist

## Pre-Flight Checklist

Before starting development, verify:

- [ ] **MySQL Database**
  - [ ] Server is running
  - [ ] Database `nexus_db` exists
  - [ ] User has proper permissions
  - [ ] Can connect: `mysql -h localhost -u nexus_user -p`

- [ ] **Node.js & npm**
  - [ ] Node.js installed (v16 or higher)
  - [ ] npm available: `npm --version`
  - [ ] Dependencies installed: `npm install`

- [ ] **ngrok**
  - [ ] Installed: `ngrok version`
  - [ ] In PATH or accessible
  - [ ] Authenticated (for free tier): `ngrok config add-authtoken YOUR_TOKEN`

- [ ] **Environment Configuration**
  - [ ] `.env` file exists
  - [ ] Database credentials configured
  - [ ] `CUSTOM_PAYMENT_SECRET` set
  - [ ] Mercado Pago credentials (if using MP)

## Startup Checklist

Follow this order:

1. [ ] Start MySQL database
2. [ ] Start Payment API: `npm run dev`
3. [ ] Verify local server: `curl http://localhost:3000/api/health`
4. [ ] Start ngrok: `ngrok http 3000`
5. [ ] Copy ngrok URL
6. [ ] Test through ngrok: `curl https://YOUR_URL.ngrok-free.app/api/health`
7. [ ] Update Mercado Pago webhook (if using)

## Common Issues & Solutions

### Issue: Server won't start

**Symptoms:**
- Error message when running `npm run dev`
- "Port already in use" error

**Solutions:**
- [ ] Check if port 3000 is already in use
  ```powershell
  netstat -ano | findstr :3000
  ```
- [ ] Kill existing process
  ```powershell
  Stop-Process -Id PROCESS_ID
  ```
- [ ] Change port in `.env` file
- [ ] Check for syntax errors in `.env`

### Issue: Database connection fails

**Symptoms:**
- "ECONNREFUSED" error
- "Access denied for user" error
- Can't connect to database

**Solutions:**
- [ ] Verify MySQL is running
  ```powershell
  Get-Service -Name MySQL*
  ```
- [ ] Check database credentials in `.env`
- [ ] Test manual connection
  ```powershell
  mysql -h localhost -u nexus_user -p nexus_db
  ```
- [ ] Verify user has permissions
  ```sql
  SHOW GRANTS FOR 'nexus_user'@'localhost';
  ```
- [ ] Check firewall rules

### Issue: ngrok not found

**Symptoms:**
- "ngrok is not recognized" error
- Command not found

**Solutions:**
- [ ] Install ngrok
  - Download: https://ngrok.com/download
  - Or: `choco install ngrok`
- [ ] Add to PATH
  ```powershell
  $env:PATH += ";C:\path\to\ngrok"
  ```
- [ ] Restart terminal after installation
- [ ] Verify installation: `ngrok version`

### Issue: ngrok tunnel not working

**Symptoms:**
- Can access `localhost:3000` but not ngrok URL
- "502 Bad Gateway" from ngrok
- ngrok dashboard shows no traffic

**Solutions:**
- [ ] Verify server is running first
- [ ] Check correct port in ngrok: `ngrok http 3000`
- [ ] Check ngrok dashboard: http://127.0.0.1:4040
- [ ] Restart ngrok
- [ ] Try different ngrok region
- [ ] Check free tier limits

### Issue: Mercado Pago webhook not received

**Symptoms:**
- Payment created but webhook never arrives
- No logs in payment API
- Payment shows in MP dashboard but not processed

**Solutions:**
- [ ] Verify webhook URL is correct in MP dashboard
- [ ] URL must be ngrok HTTPS URL
- [ ] Check webhook URL format: `https://YOUR_URL.ngrok-free.app/api/webhooks/mercadopago`
- [ ] Verify payment notifications enabled in MP
- [ ] Check ngrok is still running (didn't timeout)
- [ ] Look for webhook attempts in MP dashboard
- [ ] Check signature verification
- [ ] Review payment API logs
- [ ] Test with MP test environment first

### Issue: Signature verification fails

**Symptoms:**
- "Invalid signature" error
- 401 Unauthorized response
- Custom payment rejected

**Solutions:**
- [ ] Verify `CUSTOM_PAYMENT_SECRET` matches in:
  - `.env` file
  - PowerShell script
- [ ] Check JSON formatting (must be compact)
- [ ] Ensure using `ConvertTo-Json -Compress`
- [ ] Verify no extra whitespace in JSON
- [ ] Test with signature helper endpoint
  ```powershell
  curl "http://localhost:3000/api/webhooks/custom/generate-signature?username=Test&amount=100"
  ```
- [ ] Compare generated signature with script
- [ ] Check for special characters in username

### Issue: Player not found

**Symptoms:**
- "Player not found" error
- Payment succeeds but not applied

**Solutions:**
- [ ] Player must join server at least once
- [ ] Check username spelling (case-sensitive)
- [ ] Query database directly
  ```sql
  SELECT * FROM nexus_players WHERE username = 'Player123';
  ```
- [ ] Verify player table is populated
- [ ] Check plugin is writing to correct database

### Issue: Payment succeeds but balance not updated

**Symptoms:**
- Transaction appears in logs
- Database shows transaction
- Player balance unchanged in-game

**Solutions:**
- [ ] Check transaction status in database
  ```sql
  SELECT * FROM nexus_transactions ORDER BY created_at DESC LIMIT 10;
  ```
- [ ] Verify transaction is marked as COMPLETED
- [ ] Check player balance in database
  ```sql
  SELECT username, balance FROM nexus_players WHERE username = 'Player123';
  ```
- [ ] Plugin might be caching balance (player may need to rejoin)
- [ ] Check plugin is reading from correct database
- [ ] Verify database connection in plugin config

### Issue: Rate limiting / Too many requests

**Symptoms:**
- "Too many requests" error
- 429 status code
- Requests being blocked

**Solutions:**
- [ ] Wait for rate limit window to expire (15 minutes default)
- [ ] Adjust limits in `.env`
  - `RATE_LIMIT_WINDOW_MS`
  - `RATE_LIMIT_MAX_REQUESTS`
- [ ] Use IP whitelist in production
- [ ] Implement exponential backoff in client

### Issue: CORS errors

**Symptoms:**
- Browser console shows CORS error
- "Access-Control-Allow-Origin" missing
- Requests blocked by browser

**Solutions:**
- [ ] Configure `ALLOWED_ORIGINS` in `.env`
- [ ] Add ngrok URL to allowed origins
- [ ] Restart server after changing `.env`
- [ ] Check if using correct protocol (HTTP vs HTTPS)

## Diagnostic Commands

### Check Server Status
```powershell
# Test local server
curl http://localhost:3000/api/health

# Test through ngrok
curl https://YOUR_URL.ngrok-free.app/api/health

# Check if port is in use
netstat -ano | findstr :3000

# Check node processes
Get-Process -Name node
```

### Check Database
```powershell
# Connect to MySQL
mysql -h localhost -u nexus_user -p nexus_db

# In MySQL:
SHOW TABLES;
SELECT COUNT(*) FROM nexus_players;
SELECT COUNT(*) FROM nexus_transactions;
SELECT * FROM nexus_transactions ORDER BY created_at DESC LIMIT 5;
```

### Check ngrok
```powershell
# Get ngrok status
curl http://127.0.0.1:4040/api/tunnels | ConvertFrom-Json

# Read saved URL
Get-Content ngrok-url.txt

# Check ngrok version
ngrok version
```

### Check Logs
```powershell
# View logs in real-time
Get-Content logs\payment-api.log -Wait -Tail 50

# Search for errors
Select-String -Path logs\payment-api.log -Pattern "error"

# View last 100 lines
Get-Content logs\payment-api.log -Tail 100
```

### Test Payment
```powershell
# Using script (recommended)
.\scripts\send-payment.ps1 -Username "TestPlayer" -Amount 1.0 -Secret "your_secret" -TestConnection

# Generate signature
curl "http://localhost:3000/api/webhooks/custom/generate-signature?username=Test&amount=1"

# Manual test
$secret = "your_secret"
$body = '{"username":"Test","amount":1.0,"method":"MANUAL"}'
$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = [Text.Encoding]::UTF8.GetBytes($secret)
$signature = [BitConverter]::ToString($hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($body))).Replace("-","").ToLower()
Write-Host "Signature: $signature"
```

## Emergency Recovery

### Reset Everything
```powershell
# Stop all servers
Stop-Process -Name node -Force
Stop-Process -Name ngrok -Force

# Clear logs (optional)
Remove-Item logs\*.log

# Restart database (if needed)
Restart-Service -Name MySQL*

# Start fresh
npm run dev
ngrok http 3000
```

### Database Recovery
```sql
-- Check transaction counts
SELECT status, COUNT(*) FROM nexus_transactions GROUP BY status;

-- Find pending transactions
SELECT * FROM nexus_transactions WHERE status = 'PENDING' ORDER BY created_at DESC;

-- Check player balances
SELECT username, balance FROM nexus_players ORDER BY balance DESC LIMIT 10;

-- Manually complete a transaction (CAREFUL!)
UPDATE nexus_transactions SET status = 'COMPLETED' WHERE external_id = 'PAYMENT_ID';
UPDATE nexus_players SET balance = balance + AMOUNT WHERE uuid = 'PLAYER_UUID';
```

## When to Ask for Help

Contact support/developer if:
- [ ] Completed all checklist items above
- [ ] Verified all configurations
- [ ] Tested with minimal example
- [ ] Collected relevant logs
- [ ] Issue persists after restart
- [ ] Affecting production environment

**Provide this information:**
1. Error message (exact text)
2. Relevant log entries
3. Configuration (sanitized, no secrets!)
4. Steps to reproduce
5. Environment details (OS, Node version, etc.)
6. What you've already tried

---

**Tip:** Save this checklist and refer to it before asking for help!
