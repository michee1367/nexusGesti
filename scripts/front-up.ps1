# NexusGesti - demarre la console socle (Next.js) sur le port 3001
# (Grafana occupe deja 3000). Usage : scripts/front-up.ps1 [-Port 3001]
[CmdletBinding()]
param(
    [int]$Port = 3001
)

$ErrorActionPreference = 'Stop'

$Root  = Split-Path -Parent $PSScriptRoot
$Front = Join-Path $Root 'frontend'

try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -UseBasicParsing -TimeoutSec 3
    if ($r.StatusCode -ne 200) { throw 'gateway' }
}
catch {
    Write-Host "Attention: la passerelle (8080) ne repond pas. Lance d abord scripts/backend-up.ps1." -ForegroundColor Yellow
}

Set-Location $Front
if (-not (Test-Path 'node_modules')) {
    Write-Host "==> npm install"
    npm install
    if ($LASTEXITCODE -ne 0) { exit 1 }
}

$env:PORT = "$Port"
Write-Host "==> Console sur http://localhost:$Port"
npm run dev
