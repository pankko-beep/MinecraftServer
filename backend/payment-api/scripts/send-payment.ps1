<#
.SYNOPSIS
    Send payment confirmation to Nexus Payment API

.DESCRIPTION
    PowerShell script to send manual payment confirmations to the payment API.
    Automatically generates HMAC signature for authentication.

.PARAMETER Username
    Minecraft player username

.PARAMETER Amount
    Payment amount (in server currency)

.PARAMETER Method
    Payment method (default: MANUAL)

.PARAMETER ApiUrl
    Payment API URL (default: http://localhost:3000/api/webhooks/custom/confirm)

.PARAMETER Secret
    HMAC secret key (must match CUSTOM_PAYMENT_SECRET in .env)

.EXAMPLE
    .\send-payment.ps1 -Username "Player123" -Amount 100.0 -Secret "your_secret"

.EXAMPLE
    .\send-payment.ps1 -Username "Player123" -Amount 50.0 -Method "PIX_MANUAL" -ApiUrl "https://api.myserver.com/api/webhooks/custom/confirm" -Secret "your_secret"
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
    [string]$Secret
)

try {
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host "Nexus Payment Confirmation" -ForegroundColor Cyan
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host ""
    
    # Create payment data
    $paymentData = @{
        username = $Username
        amount = [decimal]$Amount
        method = $Method
    }
    
    # Convert to JSON (compact, no spaces)
    $jsonBody = $paymentData | ConvertTo-Json -Compress
    
    Write-Host "Player: $Username" -ForegroundColor Yellow
    Write-Host "Amount: $Amount" -ForegroundColor Yellow
    Write-Host "Method: $Method" -ForegroundColor Yellow
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
