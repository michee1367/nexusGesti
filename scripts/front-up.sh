#!/usr/bin/env bash
# NexusGesti - demarre la console socle (Next.js) sur le port 3001
# (Grafana occupe deja 3000). Usage : scripts/front-up.sh [port]
set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONT="$ROOT/frontend"
PORT="${1:-${PORT:-3001}}"

code="$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/actuator/health --max-time 3 || true)"
if [[ "$code" != "200" ]]; then
  printf 'Attention: la passerelle (8080) ne repond pas (code %s). Lance d abord scripts/backend-up.sh.\n' "$code"
fi

cd "$FRONT" || { echo "dossier frontend introuvable : $FRONT" >&2; exit 1; }
if [[ ! -d node_modules ]]; then
  echo "==> npm install"
  npm install || exit 1
fi

echo "==> Console sur http://localhost:${PORT}"
PORT="$PORT" npm run dev
