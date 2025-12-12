<#
.SYNOPSIS
    Send payment confirmation to Nexus Payment API

.DESCRIPTION
    PowerShell script to send manual payment confirmations to the payment API.
    Automatically generates HMAC signature for authentication.
    Supports both local and ngrok URLs.

.PARAMETER Username
    Minecraft player username

.PARAMETER Amount
    Payment amount (in server currency)

.PARAMETER Method
    Payment method (default: MANUAL)

.PARAMETER ApiUrl
    Payment API URL. Options:
    - Local: http://localhost:3000/api/webhooks/custom/confirm
    - ngrok: https://YOUR_URL.ngrok-free.app/api/webhooks/custom/confirm

.PARAMETER Secret
    HMAC secret key (must match CUSTOM_PAYMENT_SECRET in .env)

.PARAMETER TestConnection
    Test API connection before sending payment

.EXAMPLE
    .\send-payment.ps1 -Username "Player123" -Amount 100.0 -Secret "your_secret"
    Send payment to local server

.EXAMPLE
    .\send-payment.ps1 -Username "Player123" -Amount 100.0 -ApiUrl "https://abc123.ngrok-free.app/api/webhooks/custom/confirm" -Secret "your_secret"
    Send payment through ngrok tunnel

.EXAMPLE
    .\send-payment.ps1 -Username "Player123" -Amount 50.0 -Method "PIX_MANUAL" -Secret "your_secret" -TestConnection
    Send payment with connection test first
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$Username,
    
    [Parameter(Mandatory=$true)]
    [decimal]$Amount,
    
    [Parameter(Mandatory=$false)]
    [string]$Method = "MANUAL",
    
    [Parameter(Mandatory=$false)]
    [string]$ApiUrl = "http://localhost:3000/api/webhooks/custom/confirm",
    
    [Parameter(Mandatory=$true)]
    [string]$Secret,
    
    [Parameter(Mandatory=$false)]
    [switch]$TestConnection
)

try {
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host "Nexus Payment Confirmation" -ForegroundColor Cyan
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host ""
    
    # Determine if using ngrok
    $isNgrok = $ApiUrl -match "ngrok"
    $baseUrl = $ApiUrl -replace "/api/webhooks/custom/confirm", ""
    
    # Test connection if requested
    if ($TestConnection) {
        Write-Host "Testing connection..." -ForegroundColor Gray
        try {
            $healthUrl = "$baseUrl/api/health"
            $healthResponse = Invoke-RestMethod -Uri $healthUrl -TimeoutSec 5
            Write-Host "✓ Server is reachable" -ForegroundColor Green
            if ($isNgrok) {
                Write-Host "✓ ngrok tunnel is working" -ForegroundColor Green
            }
            Write-Host ""
        } catch {
            Write-Host "✗ Cannot reach server at $baseUrl" -ForegroundColor Red
            if ($isNgrok) {
                Write-Host "  Tip: Check if ngrok is running (ngrok http 3000)" -ForegroundColor Yellow
            } else {
                Write-Host "  Tip: Check if payment API is running (npm run dev)" -ForegroundColor Yellow
            }
            Write-Host ""
            return
        }
    }
    
    # Create payment data
    $paymentData = @{
        username = $Username
        amount = [decimal]$Amount
        method = $Method
    }
    
    # Convert to JSON (compact, no spaces)
    $jsonBody = $paymentData | ConvertTo-Json -Compress
    
    Write-Host "Payment Details:" -ForegroundColor Cyan
    Write-Host "  Player:    $Username" -ForegroundColor Yellow
    Write-Host "  Amount:    $Amount" -ForegroundColor Yellow
    Write-Host "  Method:    $Method" -ForegroundColor Yellow
    Write-Host "  Endpoint:  $ApiUrl" -ForegroundColor Gray
    if ($isNgrok) {
        Write-Host "  Via:       ngrok tunnel" -ForegroundColor Magenta
    }
    Write-Host ""
    
    # Generate HMAC signature
    Write-Host "Generating signature..." -ForegroundColor Gray
    $hmac = New-Object System.Security.Cryptography.HMACSHA256
    $hmac.Key = [Text.Encoding]::UTF8.GetBytes($Secret)
    $signature = [BitConverter]::ToString(
        $hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($jsonBody))
    ).Replace("-","").ToLower()
    
    # Prepare headers
    $headers = @{
        "Content-Type" = "application/json"
        "X-Payment-Signature" = $signature
    }
    
    # Send request
    Write-Host "Sending payment confirmation..." -ForegroundColor Gray
    $response = Invoke-RestMethod -Uri $ApiUrl -Method Post -Headers $headers -Body $jsonBody
    
    # Success
    Write-Host ""
    Write-Host "==================================" -ForegroundColor Green
    Write-Host "✓ Payment Confirmed Successfully!" -ForegroundColor Green
    Write-Host "==================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Transaction ID: $($response.data.transactionId)" -ForegroundColor Green
    Write-Host "External ID: $($response.data.externalId)" -ForegroundColor Green
    Write-Host "New Balance: $($response.data.newBalance)" -ForegroundColor Green
    Write-Host ""
    
} catch {
    Write-Host ""
    Write-Host "==================================" -ForegroundColor Red
    Write-Host "✗ Payment Failed" -ForegroundColor Red
    Write-Host "==================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody" -ForegroundColor Red
    }
    
    Write-Host ""
    exit 1
}
