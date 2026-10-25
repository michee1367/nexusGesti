# NexusGesti - demarre l'infra Docker puis les 3 applications par domaine + la passerelle.
# Usage : scripts/backend-up.ps1 [-NoBuild]
[CmdletBinding()]
param(
    [switch]$NoBuild
)

$ErrorActionPreference = 'Stop'

$Root    = Split-Path -Parent $PSScriptRoot
$Backend = Join-Path $Root 'backend'
$LogDir  = Join-Path $Backend 'logs'
$Mvn     = if ($env:MVN) { $env:MVN } else { 'mvn' }
$JavaOpts = if ($env:JAVA_OPTS) { $env:JAVA_OPTS } else { '-Xms256m -Xmx512m -XX:MaxMetaspaceSize=192m -XX:ActiveProcessorCount=2' }
$GatewayJavaOpts = if ($env:GATEWAY_JAVA_OPTS) { $env:GATEWAY_JAVA_OPTS } else { '-Xms128m -Xmx256m -XX:MaxMetaspaceSize=160m -XX:ActiveProcessorCount=2' }

$Services = [ordered]@{
    'socle-service'        = 8101
    'pedagogie-service'    = 8111
    'boulangerie-service' = 8141
    'api-gateway'          = 8080
}

function Info($m) { Write-Host $m }
function Die($m)  { Write-Host "ERREUR: $m" -ForegroundColor Red; exit 1 }

# 1. Preflight
foreach ($c in 'docker', 'java', 'mvn') {
    if (-not (Get-Command $c -ErrorAction SilentlyContinue)) { Die "$c introuvable dans le PATH" }
}
docker info *> $null
if ($LASTEXITCODE -ne 0) { Die "Docker n'est pas demarre (lance Docker Desktop)" }

New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

# 2. Infra Docker
Info "==> Infra : docker compose up -d"
Push-Location $Backend
try {
    docker compose up -d
    if ($LASTEXITCODE -ne 0) { Die "docker compose up a echoue" }
}
finally { Pop-Location }

function Wait-Healthy([string]$Name, [int]$Tries = 30) {
    for ($i = 0; $i -lt $Tries; $i++) {
        $s = (docker inspect -f '{{.State.Health.Status}}' "nexusgesti-$Name-1" 2>$null)
        if ($s -eq 'healthy') { Info "    $Name healthy"; return $true }
        Start-Sleep -Seconds 4
    }
    return $false
}
if (-not (Wait-Healthy 'postgres' 30)) { Die "postgres non healthy" }
if (-not (Wait-Healthy 'redis' 15))    { Die "redis non healthy" }
if (-not (Wait-Healthy 'kafka' 40))    { Die "kafka non healthy" }

# kafka-init ne s'execute qu'a la creation du conteneur : sur une infra deja
# lancee, un nouveau topic (ex. boulangerie.events) n'existe jamais et le
# producteur bloque sur UNKNOWN_TOPIC_OR_PARTITION. On le rejoue (idempotent).
Info "==> Kafka : creation idempotente des topics"
Push-Location $Backend
try {
    docker compose run --rm kafka-init
    if ($LASTEXITCODE -ne 0) { Write-Host "    kafka-init a echoue (topics a verifier manuellement)" -ForegroundColor Yellow }
}
finally { Pop-Location }

Info "==> Attente Keycloak 8180"
$kc = $false
for ($i = 0; $i -lt 40; $i++) {
    try {
        $r = Invoke-WebRequest -Uri 'http://localhost:8180/realms/nexusgesti' -UseBasicParsing -TimeoutSec 3
        if ($r.StatusCode -eq 200) { Info "    keycloak 200"; $kc = $true; break }
    }
    catch { }
    Start-Sleep -Seconds 3
}
if (-not $kc) { Write-Host "    Keycloak indisponible pour l'instant (on continue)" -ForegroundColor Yellow }

# 3. Arret des services deja presents (port ou ligne de commande) -> evite verrou .jar
Info "==> Arret des services existants"
Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
    Where-Object { $_.CommandLine -match 'nexusgesti' -and $_.CommandLine -match 'SNAPSHOT\.jar' } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
Start-Sleep -Seconds 2

# 4. Build
if (-not $NoBuild) {
    Info "==> Build (mvn -DskipTests package)"
    Push-Location $Backend
    try {
        & $Mvn -q -DskipTests package
        if ($LASTEXITCODE -ne 0) { Die "build Maven echoue" }
    }
    finally { Pop-Location }
}
else { Info "==> Build ignore (-NoBuild)" }

# 5. Demarrage des services
Info "==> Demarrage des services"
foreach ($svc in $Services.Keys) {
    $jar = Join-Path $Backend "services/$svc/target/$svc-1.0.0-SNAPSHOT.jar"
    if (-not (Test-Path $jar)) { Die "jar absent : $jar (build ?)" }
    $out = Join-Path $LogDir "$svc.log"
    $err = Join-Path $LogDir "$svc.err.log"
    $opts = if ($svc -eq 'api-gateway') { $GatewayJavaOpts } else { $JavaOpts }
    $p = Start-Process -FilePath 'java' -ArgumentList (@($opts -split '\s+' | Where-Object { $_ }) + @('-jar', $jar)) -NoNewWindow -PassThru `
        -RedirectStandardOutput $out -RedirectStandardError $err
    Set-Content -Path (Join-Path $LogDir "$svc.pid") -Value $p.Id
    Info "    $svc (PID $($p.Id)) -> logs/$svc.log"
}

# 6. Attente sante
Info "==> Attente /actuator/health"
$up = 0
foreach ($svc in $Services.Keys) {
    $port = $Services[$svc]
    $ok = $false
    for ($i = 0; $i -lt 60; $i++) {
        try {
            $r = Invoke-WebRequest -Uri "http://localhost:$port/actuator/health" -UseBasicParsing -TimeoutSec 3
            if ($r.StatusCode -eq 200) { $ok = $true; break }
        }
        catch { }
        Start-Sleep -Seconds 3
    }
    if ($ok) {
        Write-Host ("    {0,-22} {1}  OK" -f $svc, $port) -ForegroundColor Green
        $up++
    }
    else {
        Write-Host ("    {0,-22} {1}  KO (voir logs/{0}.log)" -f $svc, $port) -ForegroundColor Red
    }
}
Info "==> $up/$($Services.Count) services UP"
