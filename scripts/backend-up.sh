#!/usr/bin/env bash
# NexusGesti - demarre l'infra Docker puis les 3 applications par domaine + la passerelle.
# Usage : scripts/backend-up.sh [--no-build]
set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND="$ROOT/backend"
LOGDIR="$BACKEND/logs"
MVN="${MVN:-mvn}"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m -XX:MaxMetaspaceSize=192m -XX:ActiveProcessorCount=2}"
GATEWAY_JAVA_OPTS="${GATEWAY_JAVA_OPTS:--Xms128m -Xmx256m -XX:MaxMetaspaceSize=160m -XX:ActiveProcessorCount=2}"

SERVICES=(socle-service pedagogie-service boulangerie-service api-gateway)
declare -A PORTS=(
  [socle-service]=8101
  [pedagogie-service]=8111
  [boulangerie-service]=8141
  [api-gateway]=8080
)

BUILD=1
for arg in "$@"; do
  case "$arg" in
    --no-build) BUILD=0 ;;
    -h|--help) echo "Usage: backend-up.sh [--no-build]"; exit 0 ;;
    *) echo "Argument inconnu : $arg" >&2; exit 2 ;;
  esac
done

info() { printf '%s\n' "$*"; }
fail() { printf 'ERREUR: %s\n' "$*" >&2; exit 1; }

# 1. Preflight
command -v docker >/dev/null || fail "docker introuvable dans le PATH"
command -v java   >/dev/null || fail "java introuvable dans le PATH"
command -v "$MVN" >/dev/null || fail "mvn introuvable dans le PATH"
docker info >/dev/null 2>&1 || fail "Docker n'est pas demarre (lance Docker Desktop)"

mkdir -p "$LOGDIR"

# 2. Infra Docker
info "==> Infra : docker compose up -d"
( cd "$BACKEND" && docker compose up -d ) || fail "docker compose up a echoue"

wait_healthy() {
  local name="$1" tries="${2:-30}" status
  for ((i = 0; i < tries; i++)); do
    status="$(docker inspect -f '{{.State.Health.Status}}' "nexusgesti-${name}-1" 2>/dev/null || true)"
    if [[ "$status" == "healthy" ]]; then info "    ${name} healthy"; return 0; fi
    sleep 4
  done
  return 1
}
wait_healthy postgres 30 || fail "postgres non healthy"
wait_healthy redis    15 || fail "redis non healthy"
wait_healthy kafka    40 || fail "kafka non healthy"

# kafka-init ne s'execute qu'a la creation du conteneur : sur une infra deja
# lancee, un nouveau topic (ex. boulangerie.events) n'existe jamais et le
# producteur bloque sur UNKNOWN_TOPIC_OR_PARTITION. On le rejoue (idempotent).
info "==> Kafka : creation idempotente des topics"
( cd "$BACKEND" && docker compose run --rm kafka-init ) || info "    kafka-init a echoue (topics a verifier manuellement)"

info "==> Attente Keycloak 8180"
KC_OK=0
for ((i = 0; i < 40; i++)); do
  code="$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8180/realms/nexusgesti --max-time 3 || true)"
  if [[ "$code" == "200" ]]; then info "    keycloak 200"; KC_OK=1; break; fi
  sleep 3
done
[[ "$KC_OK" == "1" ]] || info "    Keycloak indisponible pour l'instant (on continue)"

# 3. Arret des services deja presents (port ou ligne de commande) -> evite verrou .jar
info "==> Arret des services existants"
for svc in "${SERVICES[@]}"; do
  port="${PORTS[$svc]:-}"
  [[ -z "$port" ]] && continue
  pid="$(netstat -ano 2>/dev/null | awk -v p=":${port}" '$0 ~ p && /LISTENING/ {print $NF; exit}')"
  if [[ -n "${pid:-}" ]]; then
    taskkill //F //PID "$pid" >/dev/null 2>&1 && info "    port ${port} libere (PID ${pid})"
  fi
done
powershell -NoProfile -Command "Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" | Where-Object { \$_.CommandLine -match 'nexusgesti' -and \$_.CommandLine -match 'SNAPSHOT\.jar' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }" >/dev/null 2>&1 || true
sleep 2

# 4. Build
if [[ "$BUILD" == "1" ]]; then
  info "==> Build (mvn -DskipTests package)"
  ( cd "$BACKEND" && "$MVN" -q -DskipTests package ) || fail "build Maven echoue"
else
  info "==> Build ignore (--no-build)"
fi

# 5. Demarrage des services
info "==> Demarrage des services"
for svc in "${SERVICES[@]}"; do
  jar="$BACKEND/services/${svc}/target/${svc}-1.0.0-SNAPSHOT.jar"
  [[ -f "$jar" ]] || fail "jar absent : $jar (build ?)"
  opts="$JAVA_OPTS"
  [[ "$svc" == "api-gateway" ]] && opts="$GATEWAY_JAVA_OPTS"
  nohup java $opts -jar "$jar" > "$LOGDIR/${svc}.log" 2>&1 &
  echo $! > "$LOGDIR/${svc}.pid"
  info "    ${svc} (PID $!) -> logs/${svc}.log"
done

# 6. Attente sante
info "==> Attente /actuator/health"
UP=0
for svc in "${SERVICES[@]}"; do
  port="${PORTS[$svc]:-}"
  [[ -z "$port" ]] && continue
  ok=0
  for ((i = 0; i < 60; i++)); do
    code="$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:${port}/actuator/health" --max-time 3 || true)"
    if [[ "$code" == "200" ]]; then ok=1; break; fi
    sleep 3
  done
  if [[ "$ok" == "1" ]]; then
    printf '    %-22s %s  OK\n' "$svc" "$port"
    UP=$((UP + 1))
  else
    printf '    %-22s %s  KO (voir logs/%s.log)\n' "$svc" "$port" "$svc"
  fi
done
info "==> ${UP}/${#SERVICES[@]} services UP"
