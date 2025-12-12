# Payment Flow Diagrams

## System Architecture with ngrok

```
┌─────────────────┐
│  Mercado Pago   │
│   (PIX System)  │
└────────┬────────┘
         │ HTTPS Webhook
         │ (payment.created, payment.updated)
         ▼
┌─────────────────────────────────────┐
│          Internet / Cloud           │
└────────────┬────────────────────────┘
             │
             ▼
      ┌─────────────┐
      │    ngrok    │ ← Public URL: https://abc123.ngrok-free.app
      │   Tunnel    │
      └──────┬──────┘
             │ Forwards to localhost:3000
             ▼
┌────────────────────────────────────────┐
│       Payment API Server               │
│       (Node.js + Express)              │
│                                        │
│  Endpoints:                            │
│  • /api/health                         │
│  • /api/webhooks/mercadopago          │
│  • /api/webhooks/custom/confirm       │
│  • /api/webhooks/custom/pending       │
└────────┬───────────────────┬──────────┘
         │                   │
         │ Updates           │ Logs
         ▼                   ▼
┌─────────────────┐   ┌──────────────┐
│  MySQL Database │   │  Log Files   │
│  (nexus_db)     │   │  (Winston)   │
│                 │   └──────────────┘
│  Tables:        │
│  • nexus_players│
│  • nexus_trans. │
└────────┬────────┘
         │ Read by
         ▼
┌─────────────────┐
│  Minecraft      │
│  Server Plugin  │
│  (Spigot/Paper) │
└─────────────────┘
```

## Payment Flow: Mercado Pago (PIX)

```
Player                Plugin              Mercado Pago         Payment API           Database
  │                     │                       │                    │                    │
  │ /vip buy gold       │                       │                    │                    │
  ├────────────────────>│                       │                    │                    │
  │                     │                       │                    │                    │
  │                     │ Create PIX Payment    │                    │                    │
  │                     ├──────────────────────>│                    │                    │
  │                     │                       │                    │                    │
  │                     │ Return QR Code        │                    │                    │
  │                     │<──────────────────────┤                    │                    │
  │                     │                       │                    │                    │
  │ Display QR Code     │                       │                    │                    │
  │<────────────────────┤                       │                    │                    │
  │                     │                       │                    │                    │
  │ [Scan & Pay via     │                       │                    │                    │
  │  Banking App]       │                       │                    │                    │
  │─────────────────────┼──────────────────────>│                    │                    │
  │                     │                       │                    │                    │
  │                     │                       │ Send Webhook       │                    │
  │                     │                       ├───────────────────>│                    │
  │                     │                       │ (via ngrok tunnel) │                    │
  │                     │                       │                    │                    │
  │                     │                       │                    │ Verify Signature   │
  │                     │                       │                    │───────────┐        │
  │                     │                       │                    │           │        │
  │                     │                       │                    │<──────────┘        │
  │                     │                       │                    │                    │
  │                     │                       │                    │ Get Payment Details│
  │                     │                       │ Payment Info       │                    │
  │                     │                       │<───────────────────┤                    │
  │                     │                       │                    │                    │
  │                     │                       │                    │ Update Transaction │
  │                     │                       │                    ├───────────────────>│
  │                     │                       │                    │                    │
  │                     │                       │                    │ Update Balance     │
  │                     │                       │                    ├───────────────────>│
  │                     │                       │                    │                    │
  │                     │                       │ 200 OK             │                    │
  │                     │                       │<───────────────────┤                    │
  │                     │                       │                    │                    │
  │                     │ [Plugin detects balance change on next check]                   │
  │                     │<────────────────────────────────────────────────────────────────┤
  │                     │                       │                    │                    │
  │ VIP activated!      │                       │                    │                    │
  │<────────────────────┤                       │                    │                    │
```

## Payment Flow: Custom Payment (Testing)

```
Admin/Tester         PowerShell Script      Payment API          Database
     │                      │                     │                   │
     │ Run script           │                     │                   │
     │ send-payment.ps1     │                     │                   │
     ├─────────────────────>│                     │                   │
     │                      │                     │                   │
     │                      │ Test Connection     │                   │
     │                      ├────────────────────>│                   │
     │                      │ (if -TestConnection)│                   │
     │                      │                     │                   │
     │                      │ Server OK           │                   │
     │                      │<────────────────────┤                   │
     │                      │                     │                   │
     │                      │ Generate HMAC       │                   │
     │                      │ Signature           │                   │
     │                      │─────────┐           │                   │
     │                      │         │           │                   │
     │                      │<────────┘           │                   │
     │                      │                     │                   │
     │                      │ POST /custom/confirm│                   │
     │                      │ with signature      │                   │
     │                      ├────────────────────>│                   │
     │                      │                     │                   │
     │                      │                     │ Verify Signature  │
     │                      │                     │─────────┐         │
     │                      │                     │         │         │
     │                      │                     │<────────┘         │
     │                      │                     │                   │
     │                      │                     │ Get Player        │
     │                      │                     ├──────────────────>│
     │                      │                     │                   │
     │                      │                     │ Create Transaction│
     │                      │                     ├──────────────────>│
     │                      │                     │                   │
     │                      │                     │ Update Balance    │
     │                      │                     ├──────────────────>│
     │                      │                     │                   │
     │                      │ Success Response    │                   │
     │                      │<────────────────────┤                   │
     │                      │                     │                   │
     │ Display Success      │                     │                   │
     │<─────────────────────┤                     │                   │
     │ - Transaction ID     │                     │                   │
     │ - New Balance        │                     │                   │
```

## ngrok Tunnel Details

```
┌───────────────────────────────────────────────────────────────┐
│                    ngrok Tunnel Process                       │
└───────────────────────────────────────────────────────────────┘

Local Server                     ngrok Service                Internet
     │                                  │                          │
     │ Listening on                     │                          │
     │ localhost:3000                   │                          │
     │                                  │                          │
     │<─────────────────────────────────┤ Establishes secure       │
     │     Persistent connection        │ tunnel connection        │
     │                                  │                          │
     │                                  │<─────────────────────────┤
     │                                  │  Incoming HTTPS request  │
     │                                  │  https://abc.ngrok.app   │
     │                                  │                          │
     │<─────────────────────────────────┤                          │
     │  Forwards as HTTP to localhost   │                          │
     │                                  │                          │
     │  Processes request               │                          │
     │──────────┐                       │                          │
     │          │                       │                          │
     │<─────────┘                       │                          │
     │                                  │                          │
     │──────────────────────────────────>                          │
     │     Returns response             ├─────────────────────────>
     │                                  │   Sends HTTPS response   │
     │                                  │                          │

Advantages:
• No firewall configuration needed
• HTTPS encryption automatic
• Request inspection via http://127.0.0.1:4040
• Easy to restart with new URL (free tier)

Considerations:
• Free URLs change on restart
• Paid plan offers static URLs
• Adds ~50-100ms latency
• Suitable for development and small production
```

## Security Flow: HMAC Signature Verification

```
┌────────────────────────────────────────────────────────────────┐
│              Custom Payment Signature Verification              │
└────────────────────────────────────────────────────────────────┘

Client (PowerShell)                           Server (Payment API)
        │                                              │
        │ 1. Create JSON body                          │
        │    {"username":"Player","amount":100}        │
        │                                              │
        │ 2. Generate HMAC-SHA256                      │
        │    using CUSTOM_PAYMENT_SECRET               │
        │────────────┐                                 │
        │            │                                 │
        │<───────────┘                                 │
        │ signature = abc123...                        │
        │                                              │
        │ 3. Send POST request                         │
        │    Header: X-Payment-Signature: abc123...    │
        │    Body: {"username":"Player","amount":100}  │
        ├─────────────────────────────────────────────>│
        │                                              │
        │                                              │ 4. Read signature
        │                                              │    from header
        │                                              │
        │                                              │ 5. Read body
        │                                              │
        │                                              │ 6. Generate HMAC-SHA256
        │                                              │    using same secret
        │                                              │────────────┐
        │                                              │            │
        │                                              │<───────────┘
        │                                              │
        │                                              │ 7. Compare signatures
        │                                              │────────────┐
        │                                              │            │
        │                                              │<───────────┘
        │                                              │
        │                                              │ Match? → Process
        │                                              │ No match? → 401 Error
        │                                              │
        │ 8. Response (success or error)               │
        │<─────────────────────────────────────────────┤
        │                                              │

Why HMAC?
• Prevents tampering with payment amounts
• Prevents unauthorized payment creation
• Ensures request authenticity
• No sensitive data in transit (only hash)
```

## Startup Sequence

```
┌────────────────────────────────────────────────────────────┐
│                   Recommended Startup Order                 │
└────────────────────────────────────────────────────────────┘

Step 1: Start Database
   └─> MySQL Server running on port 3306
       • Database: nexus_db
       • Tables created by plugin

Step 2: Start Payment API
   └─> npm run dev
       • Server starts on port 3000
       • Connects to database
       • Loads environment variables
       • Ready to receive requests
       ✓ Test: curl http://localhost:3000/api/health

Step 3: Start ngrok
   └─> ngrok http 3000
       • Creates tunnel to port 3000
       • Provides public HTTPS URL
       • Dashboard at http://127.0.0.1:4040
       ✓ Test: curl https://YOUR_URL.ngrok-free.app/api/health

Step 4: Configure Mercado Pago
   └─> Update webhook URL in dashboard
       • URL: https://YOUR_URL.ngrok-free.app/api/webhooks/mercadopago
       • Enable payment notifications
       ✓ Test: Create test payment

Step 5: Start Minecraft Server
   └─> Start server with Nexus plugin
       • Plugin connects to same database
       • Players can now purchase VIP
       ✓ Test: /vip buy command

Optional: Monitor Logs
   └─> Get-Content logs\payment-api.log -Wait -Tail 50
```

---

**Note:** These diagrams show the complete payment flow with ngrok integration. For production deployment without ngrok, replace the ngrok tunnel with direct HTTPS access to your cloud-hosted server.
