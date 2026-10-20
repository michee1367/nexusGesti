#!/usr/bin/env bash
# NexusGesti - smoke test Lot 3 boulangerie (boulangerie-approvisionnement-service, port 8143).
# Prerequis : infra Docker up, passerelle 8080 + boulangerie-approvisionnement-service 8143 a jour.
# Usage : scripts/test-boulangerie-lot3-approvisionnement.sh
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

info "2. Fournisseur (ADMIN)"
FRCODE="FRN-$(rand)"
req POST "$GW/boul/fournisseurs" "$TOKEN" "$ADMIN_TENANT" "{\"code\":\"$FRCODE\",\"raisonSociale\":\"Moulin du Fleuve\",\"delaiLivraisonJours\":2,\"categorieProduits\":\"Farines\"}"
FID="$(jget "$BODY" idFournisseur)"
if [[ "$CODE" == "201" && -n "$FID" ]]; then ok "POST /boul/fournisseurs -> 201 (id=$FID)"; else ko "POST fournisseur ($CODE) : $BODY"; fi
if [[ "$(jget "$BODY" actif)" == "true" ]]; then ok "fournisseur actif = true"; else ko "actif = $(jget "$BODY" actif)"; fi
req GET "$GW/boul/fournisseurs/$FID" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" code)" == "$FRCODE" ]]; then ok "GET /boul/fournisseurs/{id} -> 200"; else ko "GET fournisseur ($CODE) : $BODY"; fi

info "3. Besoins (calcul + sous-seuil)"
req POST "$GW/boul/besoins/calcule" "$TOKEN" "$ADMIN_TENANT" "[{\"articleCode\":\"ART-FARINE-001\",\"libelle\":\"Farine T55\",\"quantiteDisponible\":20,\"seuil\":50,\"consommationPrevue\":30,\"delaiReapproJours\":3},{\"articleCode\":\"ART-SEL-001\",\"libelle\":\"Sel\",\"quantiteDisponible\":100,\"seuil\":10,\"consommationPrevue\":0}]"
N="$(printf '%s' "$BODY" | node -pe "try{JSON.parse(require('fs').readFileSync(0)).length}catch(e){-1}")"
if [[ "$CODE" == "200" && "$N" == "2" ]]; then ok "POST /boul/besoins/calcule -> 200 (2 besoins)"; else ko "besoins calcule ($CODE) : $BODY"; fi
SQ="$(printf '%s' "$BODY" | node -pe "try{const a=JSON.parse(require('fs').readFileSync(0));String(a.find(x=>x.articleCode==='ART-FARINE-001').quantiteSuggeree)}catch(e){''}")"
if [[ "$SQ" == "60.0000" || "$SQ" == "60" || "$SQ" == "60.0" ]]; then ok "suggestion farine = $SQ (50+30-20)"; else ko "suggestion = $SQ (attendu 60)"; fi
UR="$(printf '%s' "$BODY" | node -pe "try{const a=JSON.parse(require('fs').readFileSync(0));String(a.find(x=>x.articleCode==='ART-FARINE-001').urgence)}catch(e){''}")"
if [[ "$UR" == "HAUTE" ]]; then ok "urgence farine = HAUTE"; else ko "urgence = $UR"; fi
req POST "$GW/boul/besoins/sous-seuil" "$TOKEN" "$ADMIN_TENANT" "[{\"articleCode\":\"ART-FARINE-001\",\"quantiteDisponible\":20,\"seuil\":50},{\"articleCode\":\"ART-SEL-001\",\"quantiteDisponible\":100,\"seuil\":10}]"
N="$(printf '%s' "$BODY" | node -pe "try{JSON.parse(require('fs').readFileSync(0)).length}catch(e){-1}")"
if [[ "$CODE" == "200" && "$N" == "1" ]]; then ok "sous-seuil -> 1 alerte (farine)"; else ko "sous-seuil = $N : $BODY"; fi

info "4. Commande : creation -> validation -> envoi"
req POST "$GW/boul/commandes" "$TOKEN" "$ADMIN_TENANT" "{\"fournisseurTiersCode\":\"$FRCODE\",\"date\":\"2026-10-21\",\"dateLivraisonPrevue\":\"2026-10-25\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"designation\":\"Farine T55\",\"quantiteCommandee\":100,\"prixUnitaire\":0.5,\"tauxTva\":16},{\"articleCode\":\"ART-SEL-001\",\"designation\":\"Sel fin\",\"quantiteCommandee\":25,\"prixUnitaire\":0.8,\"tauxTva\":16}]}"
CID="$(jget "$BODY" idCommande)"
if [[ "$CODE" == "201" && -n "$CID" ]]; then ok "POST /boul/commandes -> 201 (numero=$(jget "$BODY" numero))"; else ko "POST commande ($CODE) : $BODY"; fi
if [[ "$(jget "$BODY" statut)" == "BROUILLON" ]]; then ok "statut initial = BROUILLON"; else ko "statut = $(jget "$BODY" statut)"; fi
req POST "$GW/boul/commandes/$CID/envoyer" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "409" ]]; then ok "envoi depuis BROUILLON -> 409"; else ko "envoi premature ($CODE) : $BODY"; fi
req POST "$GW/boul/commandes/$CID/valider" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" statut)" == "VALIDEE" ]]; then ok "POST valider -> 200 VALIDEE"; else ko "valider ($CODE) : $BODY"; fi
TT="$(jget "$BODY" montantTTC)"
if [[ "$TT" == "81.20" || "$TT" == "81.2" ]]; then ok "montantTTC = $TT (70 HT + 11.20 TVA)"; else ko "montantTTC = $TT : $BODY"; fi
req POST "$GW/boul/commandes/$CID/envoyer" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" statut)" == "ENVOYEE" ]]; then ok "POST envoyer -> 200 ENVOYEE"; else ko "envoyer ($CODE) : $BODY"; fi

info "5. Reception complete -> commande receptionnee"
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID\",\"date\":\"2026-10-25\",\"bonLivraison\":\"BL-$(rand)\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"quantiteRecue\":100,\"prixAchat\":0.5,\"lotNumero\":\"LOT-FAR-$(rand)\"},{\"articleCode\":\"ART-SEL-001\",\"quantiteRecue\":25,\"prixAchat\":0.8,\"lotNumero\":\"LOT-SEL-$(rand)\"}]}"
RID="$(jget "$BODY" idReception)"
if [[ "$CODE" == "201" && -n "$RID" ]]; then ok "POST /boul/receptions -> 201 (numero=$(jget "$BODY" numero))"; else ko "POST reception ($CODE) : $BODY"; fi
if [[ "$(jget "$BODY" statut)" == "ATTENDUE" ]]; then ok "statut reception = ATTENDUE"; else ko "statut reception = $(jget "$BODY" statut)"; fi
req POST "$GW/boul/receptions/$RID/accepter" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "409" ]]; then ok "acceptation sans controle -> 409"; else ko "acceptation prematuree ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions/$RID/controle" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" statut)" == "EN_CONTROLE" ]]; then ok "POST controle -> 200 EN_CONTROLE"; else ko "controle ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions/$RID/accepter" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" statut)" == "ACCEPTEE" ]]; then ok "POST accepter -> 200 ACCEPTEE"; else ko "accepter ($CODE) : $BODY"; fi
req GET "$GW/boul/commandes/$CID" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$(jget "$BODY" statut)" == "RECEPTIONNEE" ]]; then ok "commande -> RECEPTIONNEE"; else ko "statut commande = $(jget "$BODY" statut)"; fi
QR="$(printf '%s' "$BODY" | node -pe "try{String(JSON.parse(require('fs').readFileSync(0)).lignes.find(x=>x.articleCode==='ART-FARINE-001').quantiteRecue)}catch(e){''}")"
if [[ "$QR" == "100" || "$QR" == "100.0000" ]]; then ok "quantiteRecue farine = $QR"; else ko "quantiteRecue = $QR"; fi
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID\",\"date\":\"2026-10-26\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"quantiteRecue\":10,\"prixAchat\":0.5}]}"
if [[ "$CODE" == "409" ]]; then ok "reception sur commande deja reçue -> 409"; else ko "reception commande reçue ($CODE) : $BODY"; fi

info "6. Reception partielle + refus non conforme"
req POST "$GW/boul/commandes" "$TOKEN" "$ADMIN_TENANT" "{\"fournisseurTiersCode\":\"$FRCODE\",\"date\":\"2026-10-21\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"designation\":\"Farine T55\",\"quantiteCommandee\":40,\"prixUnitaire\":0.5,\"tauxTva\":16}]}"
CID2="$(jget "$BODY" idCommande)"
req POST "$GW/boul/commandes/$CID2/valider" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req POST "$GW/boul/commandes/$CID2/envoyer" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID2\",\"date\":\"2026-10-25\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"quantiteRecue\":15,\"prixAchat\":0.5}]}"
RID2="$(jget "$BODY" idReception)"
if [[ "$CODE" == "201" && -n "$RID2" ]]; then ok "2e commande envoyee + reception partielle creee"; else ko "reception partielle ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions/$RID2/controle" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req POST "$GW/boul/receptions/$RID2/accepter" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" statut)" == "PARTIELLE" ]]; then ok "reception partielle -> 200 PARTIELLE"; else ko "reception partielle ($CODE) : $BODY"; fi
req GET "$GW/boul/commandes/$CID2" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$(jget "$BODY" statut)" == "PARTIELLE" ]]; then ok "commande -> PARTIELLE"; else ko "statut commande 2 = $(jget "$BODY" statut)"; fi
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID2\",\"date\":\"2026-10-26\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"quantiteRecue\":50,\"prixAchat\":0.5}]}"
if [[ "$CODE" == "400" ]]; then ok "quantite recue > reste a recevoir -> 400"; else ko "sur-reception ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID2\",\"date\":\"2026-10-26\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"quantiteRecue\":5,\"prixAchat\":0.5,\"conforme\":false}]}"
RID3="$(jget "$BODY" idReception)"
if [[ "$CODE" == "400" ]]; then ok "ligne non conforme sans motif -> 400"; else ko "ligne non conforme ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID2\",\"date\":\"2026-10-26\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"quantiteRecue\":5,\"prixAchat\":0.5,\"conforme\":false,\"motifNonConformite\":\"DLC trop courte\"}]}"
RID3="$(jget "$BODY" idReception)"
if [[ "$CODE" == "201" && -n "$RID3" ]]; then ok "ligne non conforme avec motif -> 201"; else ko "reception non conforme ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions/$RID3/controle" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req POST "$GW/boul/receptions/$RID3/accepter" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "409" ]]; then ok "acceptation d une reception non conforme -> 409"; else ko "acceptation non conforme ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions/$RID3/refuser" "$TOKEN" "$ADMIN_TENANT" "{\"motif\":\"Marchandise abimee\"}"
if [[ "$CODE" == "200" && "$(jget "$BODY" statut)" == "REFUSEE" ]]; then ok "POST refuser -> 200 REFUSEE"; else ko "refuser ($CODE) : $BODY"; fi

info "7. Annulation de commande"
req POST "$GW/boul/commandes" "$TOKEN" "$ADMIN_TENANT" "{\"fournisseurTiersCode\":\"$FRCODE\",\"date\":\"2026-10-21\",\"lignes\":[{\"articleCode\":\"ART-SEL-001\",\"designation\":\"Sel\",\"quantiteCommandee\":5,\"prixUnitaire\":0.8}]}"
CID3="$(jget "$BODY" idCommande)"
req POST "$GW/boul/commandes/$CID3/annuler" "$TOKEN" "$ADMIN_TENANT" "{}"
if [[ "$CODE" == "400" ]]; then ok "annulation sans motif -> 400"; else ko "annulation sans motif ($CODE) : $BODY"; fi
req POST "$GW/boul/commandes/$CID3/annuler" "$TOKEN" "$ADMIN_TENANT" "{\"motif\":\"Doublon de commande\"}"
if [[ "$CODE" == "200" && "$(jget "$BODY" statut)" == "ANNULEE" ]]; then ok "POST annuler -> 200 ANNULEE"; else ko "annuler ($CODE) : $BODY"; fi
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID3\",\"date\":\"2026-10-25\",\"lignes\":[{\"articleCode\":\"ART-SEL-001\",\"quantiteRecue\":1,\"prixAchat\":0.8}]}"
if [[ "$CODE" == "409" ]]; then ok "reception sur commande annulee -> 409"; else ko "reception commande annulee ($CODE) : $BODY"; fi

info "8. Rapprochement commande <-> receptions"
req GET "$GW/boul/commandes/$CID/rapprochement" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" concordant)" == "true" ]]; then ok "rapprochement commande 1 -> concordant = true"; else ko "rapprochement ($CODE) : $BODY"; fi
req POST "$GW/boul/commandes" "$TOKEN" "$ADMIN_TENANT" "{\"fournisseurTiersCode\":\"$FRCODE\",\"date\":\"2026-10-21\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"designation\":\"Farine\",\"quantiteCommandee\":10,\"prixUnitaire\":0.5,\"tauxTva\":16}]}"
CID4="$(jget "$BODY" idCommande)"
req POST "$GW/boul/commandes/$CID4/valider" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req POST "$GW/boul/commandes/$CID4/envoyer" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req POST "$GW/boul/receptions" "$TOKEN" "$ADMIN_TENANT" "{\"commandeId\":\"$CID4\",\"date\":\"2026-10-25\",\"lignes\":[{\"articleCode\":\"ART-FARINE-001\",\"quantiteRecue\":10,\"prixAchat\":0.75}]}"
RID4="$(jget "$BODY" idReception)"
req POST "$GW/boul/receptions/$RID4/controle" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req POST "$GW/boul/receptions/$RID4/accepter" "$TOKEN" "$ADMIN_TENANT" "" >/dev/null
req GET "$GW/boul/commandes/$CID4/rapprochement" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" concordant)" == "false" ]]; then ok "rapprochement avec ecart de prix -> concordant = false"; else ko "rapprochement ecart ($CODE) : $BODY"; fi
EC="$(printf '%s' "$BODY" | node -pe "try{String(JSON.parse(require('fs').readFileSync(0)).ecarts[0].ecartPrixAchat)}catch(e){''}")"
if [[ "$EC" == "0.25" ]]; then ok "ecartPrixAchat = $EC"; else ko "ecartPrixAchat = $EC (attendu 0.25)"; fi

info "9. Evaluation fournisseur"
req GET "$GW/boul/fournisseurs/$FID/evaluation" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && -n "$(jget "$BODY" nbCommandes)" ]]; then ok "GET evaluation -> 200 (commandes=$(jget "$BODY" nbCommandes), refusees=$(jget "$BODY" nbReceptionsRefusees))"; else ko "evaluation ($CODE) : $BODY"; fi
req POST "$GW/boul/fournisseurs/$FID/desactiver" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" actif)" == "false" ]]; then ok "desactivation fournisseur -> 200 actif=false"; else ko "desactivation ($CODE) : $BODY"; fi
req POST "$GW/boul/fournisseurs/$FID/activer" "$TOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" actif)" == "true" ]]; then ok "reactivation fournisseur -> 200 actif=true"; else ko "reactivation ($CODE) : $BODY"; fi

info "10. Evenements persistes par audit-service"
sleep 5
req GET "$GW/socle/audit/search?eventType=commande.validee" "$TOKEN" "$ADMIN_TENANT" ""
N="$(printf '%s' "$BODY" | node -pe "try{const o=JSON.parse(require('fs').readFileSync(0));String(o.length ?? o.totalElements ?? -1)}catch(e){-1}")"
if [[ "$CODE" == "200" && "$N" != "0" && "$N" != "-1" ]]; then ok "audit commande.validee -> $N entree(s)"; else ko "audit commande.validee ($CODE) : $BODY"; fi
req GET "$GW/socle/audit/search?eventType=reception.acceptee" "$TOKEN" "$ADMIN_TENANT" ""
N="$(printf '%s' "$BODY" | node -pe "try{const o=JSON.parse(require('fs').readFileSync(0));String(o.length ?? o.totalElements ?? -1)}catch(e){-1}")"
if [[ "$CODE" == "200" && "$N" != "0" && "$N" != "-1" ]]; then ok "audit reception.acceptee -> $N entree(s)"; else ko "audit reception.acceptee ($CODE) : $BODY"; fi

info "11. Isolation + securite"
TB="$(uuid)"
req GET "$GW/boul/commandes" "$TOKEN" "$TB" ""
if [[ "$CODE" == "200" && "$(jget "$BODY" length)" == "0" ]]; then ok "isolation tenant B -> /boul/commandes = []"; else ko "isolation B ($CODE) : $BODY"; fi
req GET "$GW/boul/fournisseurs" "" "$ADMIN_TENANT" ""
if [[ "$CODE" == "401" ]]; then ok "sans token -> 401"; else ko "sans token ($CODE)"; fi
req GET "$GW/boul/fournisseurs" "$TOKEN" "" ""
if [[ "$CODE" == "400" ]]; then ok "sans X-Tenant-Id -> 400"; else ko "sans tenant ($CODE)"; fi

info "12. CUSTOMER : lecture OK / ecriture 403"
req POST "$GW/socle/auth/login" "" "$ADMIN_TENANT" "{\"email\":\"demo.customer@nexusgesti.com\",\"password\":\"demo_secret_customer\"}"
CTOKEN="$(jget "$BODY" accessToken)"
if [[ "$CODE" == "200" && -n "$CTOKEN" ]]; then ok "login CUSTOMER 200"; else ko "login CUSTOMER ($CODE) : $BODY"; fi
req GET "$GW/boul/commandes" "$CTOKEN" "$ADMIN_TENANT" ""
if [[ "$CODE" == "200" ]]; then ok "CUSTOMER lecture /boul/commandes -> 200"; else ko "CUSTOMER lecture ($CODE)"; fi
req POST "$GW/boul/commandes" "$CTOKEN" "$ADMIN_TENANT" "{\"fournisseurTiersCode\":\"X\",\"date\":\"2026-10-21\",\"lignes\":[]}"
if [[ "$CODE" == "403" ]]; then ok "CUSTOMER ecriture /boul/commandes -> 403"; else ko "CUSTOMER ecriture ($CODE)"; fi
req POST "$GW/boul/receptions/$RID/refuser" "$CTOKEN" "$ADMIN_TENANT" "{\"motif\":\"x\"}"
if [[ "$CODE" == "403" ]]; then ok "CUSTOMER transition reception -> 403"; else ko "CUSTOMER transition ($CODE)"; fi

printf '\n===============================\n'
printf 'RESULTAT : %d OK / %d KO\n' "$PASS" "$FAIL"
printf '===============================\n'
[[ "$FAIL" == "0" ]]
