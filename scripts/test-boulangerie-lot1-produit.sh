#!/usr/bin/env bash
# NexusGesti - smoke test Lot 1 boulangerie (boulangerie-produit-service, port 8141).
# Prerequis : infra Docker up, passerelle 8080 + boulangerie-produit-service 8141 a jour.
# Usage : scripts/test-boulangerie-lot1-produit.sh
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

info "2. Gamme + categorie (ADMIN)"
req POST "$GW/boul/gammes" "$TOKEN" "$ADMIN_TENANT" "{\"code\":\"GAM-PAIN-$(rand)\",\"libelle\":\"Pains\",\"ordre\":1}"
if [[ "$CODE" == "201" ]]; then ok "POST /boul/gammes -> 201"; else ko "POST /boul/gammes ($CODE) : $BODY"; fi
GAMME_CODE="$(jget "$BODY" code)"
req POST "$GW/boul/categories" "$TOKEN" "$ADMIN_TENANT" "{\"code\":\"CAT-TRAD-$(rand)\",\"libelle\":\"Tradition\",\"ordre\":1}"
if [[ "$CODE" == "201" ]]; then ok "POST /boul/categories -> 201"; else ko "POST /boul/categories ($CODE) : $BODY"; fi
CAT_CODE="$(jget "$BODY" code)"

info "3. Produit (ADMIN)"
PCODE="BAGUETTE-$(rand)"
req POST "$GW/boul/produits" "$TOKEN" "$ADMIN_TENANT" "{\"code\":\"$PCODE\",\"designation\":\"Baguette tradition\",\"gammeCode\":\"$GAMME_CODE\",\"categorieCode\":\"$CAT_CODE\",\"articleCode\":\"ART-FARINE-001\",\"uniteVente\":\"PIECE\",\"prixVenteHT\":0.80,\"tauxTva\":16,\"allergenes\":[\"GLUTEN\"]}"
PID="$(jget "$BODY" idProduit)"
if [[ "$CODE" == "201" && -n "$PID" ]]; then ok "POST /boul/produits -> 201 (id=$PID)"; else ko "POST /boul/produits ($CODE) : $BODY"; fi
if [[ "$(jget "$BODY" fabricable)" == "false" ]]; then ok "produit cree non fabricable (aucune recette active)"; else ko "fabricable initial = $(jget "$BODY" fabricable)"; fi
req GET "$GW/boul/produits/$PID" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" code)" == "$PCODE" ]]; then ok "GET /boul/produits/{id} -> 200"; else ko "GET produit ($CODE) : $BODY"; fi

info "4. Fiche recette versionnee (ADMIN)"
REC_CODE="REC-$PCODE"
req POST "$GW/boul/recettes" "$TOKEN" "$ADMIN_TENANT" "{\"code\":\"$REC_CODE\",\"produitCode\":\"$PCODE\",\"libelle\":\"Recette baguette\",\"rendementQuantite\":10,\"rendementUnite\":\"PIECE\",\"tempsPreparationMin\":30,\"tempsCuissonMin\":20,\"temperatureCuissonC\":250}"
RID="$(jget "$BODY" idRecette)"
if [[ "$CODE" == "201" && -n "$RID" ]]; then ok "POST /boul/recettes -> 201 (version=$(jget "$BODY" version))"; else ko "POST /boul/recettes ($CODE) : $BODY"; fi
if [[ "$(jget "$BODY" version)" == "1" ]]; then ok "premiere version = 1"; else ko "version initiale = $(jget "$BODY" version)"; fi

info "5. Ingredients (ADMIN)"
req POST "$GW/boul/recettes/$RID/ingredients" "$TOKEN" "$ADMIN_TENANT" "{\"articleCode\":\"ART-FARINE-001\",\"libelle\":\"Farine T55\",\"quantite\":6,\"unite\":\"KG\",\"tauxPerte\":0.02,\"ordre\":1}"
if [[ "$CODE" == "201" ]]; then ok "POST ingredient farine -> 201"; else ko "POST ingredient farine ($CODE) : $BODY"; fi
req POST "$GW/boul/recettes/$RID/ingredients" "$TOKEN" "$ADMIN_TENANT" "{\"articleCode\":\"ART-EAU-001\",\"libelle\":\"Eau\",\"quantite\":4,\"unite\":\"L\",\"tauxPerte\":0,\"ordre\":2}"
if [[ "$CODE" == "201" ]]; then ok "POST ingredient eau -> 201"; else ko "POST ingredient eau ($CODE) : $BODY"; fi

info "6. Besoins matieres proportionnels (rendement 10)"
req GET "$GW/boul/recettes/$RID/besoins?quantite=10" "$TOKEN" "$ADMIN_TENANT" ""
N="$(printf '%s' "$BODY" | node -pe "try{JSON.parse(require('fs').readFileSync(0)).length}catch(e){-1}")"
if [[ "$N" == "2" ]]; then ok "GET /recettes/{id}/besoins -> 2 lignes"; else ko "besoins = $N (attendu 2) : $BODY"; fi
req GET "$GW/boul/recettes/$RID/besoins?quantite=20" "$TOKEN" "$ADMIN_TENANT" ""
QB="$(printf '%s' "$BODY" | node -pe "try{const a=JSON.parse(require('fs').readFileSync(0));String(a.find(x=>x.articleCode==='ART-FARINE-001').quantiteBrute)}catch(e){''}")"
if [[ "$QB" == "12.24" || "$QB" == "12.2400" ]]; then ok "farine brute x2 (rendement double) = $QB"; else ko "farine brute = $QB (attendu 12.2400)"; fi

info "7. Cout de revient (prix moyens fournis)"
req GET "$GW/boul/recettes/$RID/cout?prix=ART-FARINE-001:2.00&prix=ART-EAU-001:0.10" "$TOKEN" "$ADMIN_TENANT" ""
CM="$(jget "$BODY" coutMatiere)"
if [[ "$CM" == "12.64" ]]; then ok "cout matiere = $CM (farine 6.12 + eau 5.20... selon brut)"; else ko "cout matiere = $CM : $BODY"; fi
if [[ "$(jget "$BODY" coutMatiereDisponible)" == "true" ]]; then ok "coutMatiereDisponible = true"; else ko "coutMatiereDisponible = $(jget "$BODY" coutMatiereDisponible)"; fi

info "8. Activer la recette -> produit fabricable"
req POST "$GW/boul/recettes/$RID/activer" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" active)" == "true" ]]; then ok "POST /recettes/{id}/activer -> 200"; else ko "activer recette ($CODE) : $BODY"; fi
req GET "$GW/boul/produits/$PID" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$(jget "$BODY" fabricable)" == "true" ]]; then ok "produit desormais fabricable = true"; else ko "fabricable = $(jget "$BODY" fabricable)"; fi

info "9. Figer la recette ; correction apres fige = nouvelle version"
req POST "$GW/boul/recettes/$RID/figer" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" modifiable)" == "false" ]]; then ok "POST /recettes/{id}/figer -> 200 (modifiable=false)"; else ko "figer recette ($CODE) : $BODY"; fi
req POST "$GW/boul/recettes/$RID/ingredients" "$TOKEN" "$ADMIN_TENANT" "{\"articleCode\":\"ART-SEL-001\",\"libelle\":\"Sel\",\"quantite\":0.1,\"unite\":\"KG\"}"
if [[ "$CODE" == "409" ]]; then ok "ajout ingredient sur recette figee -> 409"; else ko "ajout apres fige ($CODE) : $BODY"; fi
req POST "$GW/boul/recettes" "$TOKEN" "$ADMIN_TENANT" "{\"code\":\"$REC_CODE\",\"produitCode\":\"$PCODE\",\"libelle\":\"Recette baguette v2\",\"rendementQuantite\":10,\"rendementUnite\":\"PIECE\"}"
if [[ "$(jget "$BODY" version)" == "2" ]]; then ok "correction -> nouvelle version = 2"; else ko "version v2 = $(jget "$BODY" version)"; fi

info "10. Isolation + securite"
TB="$(uuid)"
req GET "$GW/boul/produits" "$TOKEN" "$TB" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" length)" == "0" ]]; then ok "isolation tenant B -> /boul/produits = []"; else ko "isolation B ($CODE) : $BODY"; fi
req GET "$GW/boul/produits" "" "$ADMIN_TENANT" ""
if [[ "$CODE" == "401" ]]; then ok "sans token -> 401"; else ko "sans token ($CODE)"; fi
req GET "$GW/boul/produits" "$TOKEN" "" ""
if [[ "$CODE" == "400" ]]; then ok "sans X-Tenant-Id -> 400"; else ko "sans tenant ($CODE)"; fi

# login CUSTOMER
info "11. CUSTOMER : lecture OK / ecriture 403"
req POST "$GW/socle/auth/login" "" "$ADMIN_TENANT" "{\"email\":\"demo.customer@nexusgesti.com\",\"password\":\"demo_secret_customer\"}"
CTOKEN="$(jget "$BODY" accessToken)"
if [[ "$CODE" == "200" && -n "$CTOKEN" ]]; then ok "login CUSTOMER 200"; else ko "login CUSTOMER ($CODE) : $BODY"; fi
req GET "$GW/boul/produits" "$CTOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" ]]; then ok "CUSTOMER lecture /boul/produits -> 200"; else ko "CUSTOMER lecture ($CODE)"; fi
req POST "$GW/boul/gammes" "$CTOKEN" "$ADMIN_TENANT" "{\"code\":\"X-$(rand)\",\"libelle\":\"X\"}"
if [[ "$CODE" == "403" ]]; then ok "CUSTOMER ecriture /boul/gammes -> 403"; else ko "CUSTOMER ecriture ($CODE)"; fi

printf '\n===============================\n'
printf 'RESULTAT : %d OK / %d KO\n' "$PASS" "$FAIL"
printf '===============================\n'
[[ "$FAIL" == "0" ]]
