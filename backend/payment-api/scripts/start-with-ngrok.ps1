<#
.SYNOPSIS
    Helper script to start payment API with ngrok

.DESCRIPTION
    This script helps set up and start the payment API with ngrok tunnel.
    It will start the server in one terminal and ngrok in another.

.PARAMETER Port
    Port to run the payment API on (default: 3000)

.PARAMETER CheckHealth
    Check if the server is healthy after starting

.EXAMPLE
    .\start-with-ngrok.ps1
    Start server and ngrok with default settings

.EXAMPLE
    .\start-with-ngrok.ps1 -Port 3001 -CheckHealth
    Start on port 3001 and verify health
#>

param(
    [Parameter(Mandatory=$false)]
    [int]$Port = 3000,
    
    [Parameter(Mandatory=$false)]
    [switch]$CheckHealth
)

function Write-StepHeader($message) {
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host " $message" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
}

function Write-Success($message) {
    Write-Host "✓ $message" -ForegroundColor Green
}

function Write-Info($message) {
    Write-Host "ℹ $message" -ForegroundColor Yellow
}

function Write-Error($message) {
    Write-Host "✗ $message" -ForegroundColor Red
}

Clear-Host
Write-Host ""
Write-Host "╔════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║   Nexus Payment API - ngrok Setup Helper  ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

# Check if in correct directory
$currentPath = Get-Location
if (-not (Test-Path "package.json")) {
    Write-Error "Please run this script from the payment-api directory"
    Write-Info "Expected: backend\payment-api"
    Write-Info "Current:  $currentPath"
    exit 1
}

# Check if .env exists
Write-StepHeader "Step 1: Checking Configuration"
if (-not (Test-Path ".env")) {
    Write-Error ".env file not found"
    Write-Info "Creating .env from .env.example..."
    Copy-Item ".env.example" ".env"
    Write-Success ".env file created"
    Write-Info "Please edit .env and configure your settings before continuing"
    Write-Info "Required: DB credentials, MP credentials, CUSTOM_PAYMENT_SECRET"
    Write-Host ""
    Write-Host "Press Enter after editing .env to continue..."
    Read-Host
}
Write-Success "Configuration file exists"

# Check if ngrok is installed
Write-StepHeader "Step 2: Checking ngrok Installation"
try {
    $ngrokVersion = ngrok version
    Write-Success "ngrok is installed: $ngrokVersion"
} catch {
    Write-Error "ngrok is not installed or not in PATH"
    Write-Info "Download from: https://ngrok.com/download"
    Write-Info "Or install with Chocolatey: choco install ngrok"
    Write-Host ""
    Write-Host "Install ngrok and press Enter to continue..."
    Read-Host
}

# Check if node modules are installed
Write-StepHeader "Step 3: Checking Dependencies"
if (-not (Test-Path "node_modules")) {
    Write-Info "Node modules not found. Installing..."
    npm install
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Dependencies installed"
    } else {
        Write-Error "Failed to install dependencies"
        exit 1
    }
} else {
    Write-Success "Dependencies are installed"
}

# Start the payment API in a new window
Write-StepHeader "Step 4: Starting Payment API"
Write-Info "Starting server on port $Port..."

$serverProcess = Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$currentPath'; npm run dev" -PassThru
Start-Sleep -Seconds 3

Write-Success "Payment API started in new window"
Write-Info "Process ID: $($serverProcess.Id)"

# Wait a bit for server to start
Write-Info "Waiting for server to initialize..."
Start-Sleep -Seconds 3

# Test if server is running
try {
    $testResponse = Invoke-WebRequest -Uri "http://localhost:$Port/api/health" -UseBasicParsing -TimeoutSec 5
    Write-Success "Server is responding on http://localhost:$Port"
} catch {
    Write-Error "Server may not be running properly"
    Write-Info "Check the server window for errors"
}

# Start ngrok in a new window
Write-StepHeader "Step 5: Starting ngrok Tunnel"
Write-Info "Starting ngrok tunnel..."

$ngrokProcess = Start-Process powershell -ArgumentList "-NoExit", "-Command", "ngrok http $Port" -PassThru
Start-Sleep -Seconds 3

Write-Success "ngrok started in new window"
Write-Info "Process ID: $($ngrokProcess.Id)"

# Wait for ngrok to initialize
Write-Info "Waiting for ngrok to initialize..."
Start-Sleep -Seconds 3

# Get ngrok URL from API
Write-StepHeader "Step 6: Retrieving ngrok URL"
try {
    $ngrokApi = Invoke-RestMethod -Uri "http://127.0.0.1:4040/api/tunnels" -TimeoutSec 5
    $publicUrl = $ngrokApi.tunnels | Where-Object { $_.proto -eq "https" } | Select-Object -First 1 -ExpandProperty public_url
    
    if ($publicUrl) {
        Write-Success "ngrok tunnel established!"
        Write-Host ""
        Write-Host "╔════════════════════════════════════════════╗" -ForegroundColor Green
        Write-Host "║           🎉 Setup Complete!              ║" -ForegroundColor Green
        Write-Host "╚════════════════════════════════════════════╝" -ForegroundColor Green
        Write-Host ""
        Write-Host "Your ngrok URL: " -NoNewline
        Write-Host $publicUrl -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Important URLs:" -ForegroundColor Cyan
        Write-Host "  Local Server:      http://localhost:$Port" -ForegroundColor Gray
        Write-Host "  ngrok Dashboard:   http://127.0.0.1:4040" -ForegroundColor Gray
        Write-Host "  Health Check:      $publicUrl/api/health" -ForegroundColor Gray
        Write-Host ""
        Write-Host "Webhook URLs for Mercado Pago:" -ForegroundColor Cyan
        Write-Host "  $publicUrl/api/webhooks/mercadopago" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Custom Payment URL:" -ForegroundColor Cyan
        Write-Host "  $publicUrl/api/webhooks/custom/confirm" -ForegroundColor Yellow
        Write-Host ""
        
        # Test health if requested
        if ($CheckHealth) {
            Write-Info "Testing health endpoint through ngrok..."
            try {
                $healthResponse = Invoke-RestMethod -Uri "$publicUrl/api/health"
                Write-Success "Health check passed!"
                Write-Host "  Status: $($healthResponse.status)" -ForegroundColor Gray
            } catch {
                Write-Error "Health check failed through ngrok"
            }
        }
        
        Write-Host ""
        Write-Host "Next Steps:" -ForegroundColor Magenta
        Write-Host "  1. Copy the ngrok URL above" -ForegroundColor White
        Write-Host "  2. Update Mercado Pago webhook configuration" -ForegroundColor White
        Write-Host "  3. Test with: .\scripts\send-payment.ps1 -Username 'Player' -Amount 100 -ApiUrl '$publicUrl/api/webhooks/custom/confirm' -Secret 'your_secret' -TestConnection" -ForegroundColor White
        Write-Host ""
        Write-Host "To stop:" -ForegroundColor Cyan
        Write-Host "  - Close both PowerShell windows (server and ngrok)" -ForegroundColor Gray
        Write-Host "  - Or run: Stop-Process $($serverProcess.Id), $($ngrokProcess.Id)" -ForegroundColor Gray
        Write-Host ""
        
        # Save ngrok URL to a file for reference
        $publicUrl | Out-File -FilePath "ngrok-url.txt" -Encoding UTF8
        Write-Info "ngrok URL saved to: ngrok-url.txt"
        
    } else {
        Write-Error "Could not retrieve ngrok URL"
        Write-Info "Check ngrok window for errors"
    }
} catch {
    Write-Error "Could not connect to ngrok API"
    Write-Info "ngrok may still be starting. Check http://127.0.0.1:4040"
}

Write-Host ""
Write-Host "Press Enter to exit this helper (servers will keep running)..."
Read-Host
