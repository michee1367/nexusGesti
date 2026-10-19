#!/usr/bin/env bash
# NexusGesti - smoke test Etape 0 (profil BOULANGERIE, prerequis transverses).
# Prerequis : infra Docker up, passerelle 8080 + tenant/auth a jour.
# Usage : scripts/test-boulangerie-etape0.sh
set -uo pipefail

GW="${GATEWAY_URL:-http://localhost:8080/api/v1}"
ADMIN_TENANT="${ADMIN_TENANT:-11111111-2222-3333-4444-555555555570}"
ADMIN_USER="${ADMIN_USER:-demo.admin@nexusgesti.com}"
ADMIN_PASS="${ADMIN_PASS:-Admin_pass123!}"

PASS=0; FAIL=0
ok()  { printf '  [OK]   %s\n' "$1"; PASS=$((PASS+1)); }
ko()  { printf '  [KO]   %s\n' "$1"; FAIL=$((FAIL+1)); }
info(){ printf '\n== %s\n' "$1"; }

uuid() {
  if command -v uuidgen >/dev/null 2>&1; then uuidgen | tr 'A-Z' 'a-z'; \
  else powershell -NoProfile -Command "[guid]::NewGuid().ToString()" | tr -d '\r'; fi
}
rand() { uuid | tr -d '-' | cut -c1-8; }
jget() { printf '%s' "$1" | node -pe "try{const o=JSON.parse(require('fs').readFileSync(0));String(eval('o.'+process.argv[1])??'')}catch(e){''}" "$2" 2>/dev/null; }

# requete : methode url token tenant corps -> remplit CODE et BODY
req() {
  local method="$1" url="$2" token="$3" tenant="$4" data="$5"
  local args=(-s -w $'\n%{http_code}' -X "$method" "$url" -H 'Content-Type: application/json')
  [[ -n "$token"  ]] && args+=(-H "Authorization: Bearer $token")
  [[ -n "$tenant" ]] && args+=(-H "X-Tenant-Id: $tenant")
  [[ -n "$data"   ]] && args+=(-d "$data")
  local out; out="$(curl "${args[@]}")"
  CODE="${out##*$'\n'}"
  BODY="${out%$'\n'*}"
}

info "0. Passerelle 8080"
req GET "$GW/socle/auth/login" "" "" ""
if [[ "$CODE" =~ ^[0-9]+$ ]]; then ok "passerelle joignable (HTTP $CODE)"; else ko "passerelle injoignable"; exit 1; fi

info "1. Login ADMIN"
req POST "$GW/socle/auth/login" "" "$ADMIN_TENANT" "{\"email\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}"
TOKEN="$(jget "$BODY" accessToken)"
if [[ "$CODE" == "200" && -n "$TOKEN" ]]; then ok "login ADMIN 200 + accessToken"; else ko "login ADMIN ($CODE) : $BODY"; exit 1; fi

info "2. Creation tenant profil BOULANGERIE"
T1="$(uuid)"
req POST "$GW/socle/tenants" "$TOKEN" "$T1" "{\"denomination\":\"Boulangerie E2E\",\"code\":\"BOUL-E2E-$(rand)\",\"profil\":\"BOULANGERIE\"}"
if [[ "$CODE" == "201" ]]; then ok "POST /socle/tenants -> 201 (id=$T1)"; else ko "POST /socle/tenants ($CODE) : $BODY"; fi
if [[ "$(jget "$BODY" profil)" == "BOULANGERIE" ]]; then ok "profil renvoye = BOULANGERIE"; else ko "profil = $(jget "$BODY" profil)"; fi

info "3. Referentiels du tenant (attendu 23, dont 6 natures boulangerie)"
req GET "$GW/socle/referentiels" "$TOKEN" "$T1" ""
N="$(printf '%s' "$BODY" | node -pe "try{JSON.parse(require('fs').readFileSync(0)).length}catch(e){-1}")"
if [[ "$N" == "23" ]]; then ok "23 referentiels"; else ko "referentiels = $N (attendu 23)"; fi
printf '%s' "$BODY" | node -e "
let s='';process.stdin.on('data',d=>s+=d).on('end',()=>{
  const codes=JSON.parse(s).map(x=>x.code||x.nature);
  const want=['GAMME_PRODUIT','CATEGORIE_PRODUIT','ALLERGENE','TYPE_PERTE','MODE_PAIEMENT_CAISSE','POSTE_BOULANGERIE'];
  const miss=want.filter(w=>!codes.includes(w));
  console.log(miss.length===0 ? '  [OK]   6/6 natures boulangerie' : '  [KO]   natures manquantes: '+miss.join(','));
  process.exit(miss.length===0?0:1);
});"
if [[ $? -eq 0 ]]; then PASS=$((PASS+1)); else FAIL=$((FAIL+1)); fi

info "4. Register self-service profil BOULANGERIE"
T2="$(uuid)"
EMAIL="boul.$(rand)@nexusgesti.com"
body="{\"email\":\"$EMAIL\",\"phone\":\"+243810000000\",\"password\":\"Boul_pass123!x\",\"firstName\":\"Boul\",\"lastName\":\"E2E\",\"countryCode\":\"CD\",\"acceptedTerms\":true,\"etablissement\":{\"denomination\":\"Boulangerie Signup\",\"code\":\"BOUL-SU-$(rand)\",\"profil\":\"BOULANGERIE\"}}"
req POST "$GW/socle/auth/register" "" "$T2" "$body"
if [[ "$CODE" == "201" ]]; then ok "register BOULANGERIE -> 201"; else ko "register ($CODE) : $BODY"; fi

info "5. Register profil invalide -> 400"
bodybad="{\"email\":\"bad.$(rand)@nexusgesti.com\",\"phone\":\"+243810000001\",\"password\":\"Boul_pass123!x\",\"firstName\":\"A\",\"lastName\":\"B\",\"countryCode\":\"CD\",\"acceptedTerms\":true,\"etablissement\":{\"denomination\":\"X\",\"code\":\"X-$(rand)\",\"profil\":\"INVALID\"}}"
req POST "$GW/socle/auth/register" "" "$(uuid)" "$bodybad"
if [[ "$CODE" == "400" ]]; then ok "profil INVALID -> 400"; else ko "profil INVALID ($CODE) : $BODY"; fi

info "6. Consumer Kafka : tenant cree avec profil BOULANGERIE"
found=0
for i in $(seq 1 10); do
  req GET "$GW/socle/tenants/$T2/identite" "" "$T2" ""
  if [[ "$CODE" == "200" ]]; then found=1; break; fi
  sleep 2
done
if [[ "$found" == "1" && "$(jget "$BODY" profil)" == "BOULANGERIE" ]]; then
  ok "GET /socle/tenants/$T2/identite -> profil BOULANGERIE"
else
  ko "identite ($CODE) : $BODY"
fi

printf '\n===============================\n'
printf 'RESULTAT : %d OK / %d KO\n' "$PASS" "$FAIL"
printf '===============================\n'
[[ "$FAIL" == "0" ]]
