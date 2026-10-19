#!/usr/bin/env bash
# Provisionne le realm Keycloak nexusgesti pour le developpement.
#
# Deux trous du realm importe (realm-nexusgesti.json) sont combles ici car
# Keycloak 25 refuse les roles de service account dans un import JSON
# (unrecognized field "serviceAccountClientRoles"). Le passage par kcadm est
# la seule voie fiable.
#
# 1. role-mappings realm-management sur le service account nexusgesti-service
#    -> debloque POST /socle/auth/register (etait 403 Forbidden).
# 2. utilisateurs de test reproductibles et idempotents :
#      demo.customer@nexusgesti.com  CUSTOMER  / demo_secret_customer
#      demo.admin@nexusgesti.com     ADMIN     / Admin_pass123!
#
# Idempotent : se relancer sans crainte apres un recreate du conteneur keycloak.
set -euo pipefail

# Git-bash (Windows) convertit /opt/... en chemin Windows avant docker exec :
# sans ce flag, kcadm.sh est introuvable ("stat C:/Program Files/Git/opt/...").
export MSYS_NO_PATHCONV=1

CONTAINER="${KEYCLOAK_CONTAINER:-nexusgesti-keycloak-1}"
SERVER="${KEYCLOAK_SERVER:-http://localhost:8080}"
REALM="nexusgesti"
ADMIN_USER="${KEYCLOAK_ADMIN_USER:-admin}"
ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-admin_secret}"
# Etablissement de demonstration (id = X-Tenant-Id par defaut de la console).
DEMO_TENANT_ID="${NEXUSGESTI_DEMO_TENANT:-11111111-2222-3333-4444-555555555570}"

KCADM=(docker exec -i "${CONTAINER}" /opt/keycloak/bin/kcadm.sh)

echo "Connexion kcadm a ${CONTAINER} (${SERVER}, realm master)..."
"${KCADM[@]}" config credentials --server "${SERVER}" --realm master --user "${ADMIN_USER}" --password "${ADMIN_PASSWORD}"

echo "1. Role-mappings realm-management pour nexusgesti-service..."
"${KCADM[@]}" add-roles -r "${REALM}" --uusername service-account-nexusgesti-service \
  --cclientid realm-management \
  --rolename manage-users --rolename view-users --rolename query-users --rolename view-realm \
  || echo "   (roles deja presents ou partiels - on continue)"

create_user() {
  # $1 = email, $2 = mot de passe, $3+ = noms de roles realm
  local email="$1" pass="$2"; shift 2
  local username="${email}"
  local uid
  uid=$("${KCADM[@]}" get users -r "${REALM}" -q "email=${email}" --fields id --format csv --noquotes | tail -n 1)
  if [ -z "${uid}" ]; then
    uid=$("${KCADM[@]}" create users -r "${REALM}" \
      -s "username=${username}" -s "email=${email}" -s "emailVerified=true" -s "enabled=true" -i)
    "${KCADM[@]}" set-password -r "${REALM}" --userid "${uid}" --password "${pass}"
    echo "   cree ${email} (${uid})"
  else
    echo "   deja present : ${email}"
  fi
  # roles realm : CUSTOMER toujours, ADMIN ajoute si demande
  "${KCADM[@]}" add-roles -r "${REALM}" --uusername "${username}" --rolename CUSTOMER || true
  for role in "$@"; do
    "${KCADM[@]}" add-roles -r "${REALM}" --uusername "${username}" --rolename "${role}" || true
  done
  # attribut tenant_id : permet a POST /socle/auth/resolve-tenant de retrouver
  # l'etablissement a partir de l'email (meme mecanisme que /signup).
  "${KCADM[@]}" update "users/${uid}" -r "${REALM}" \
    -s "attributes.tenant_id=[\"${DEMO_TENANT_ID}\"]" || true
}

echo "2. Utilisateurs de test..."
create_user demo.customer@nexusgesti.com demo_secret_customer
create_user demo.admin@nexusgesti.com Admin_pass123! ADMIN

echo "--- Verification ---"
"${KCADM[@]}" get-roles -r "${REALM}" --uusername service-account-nexusgesti-service --cclientid realm-management \
  | grep -oE '"name" ?: ?"[^"]+"' | sed 's/.*": *"//; s/"//' | sort | tr '\n' ' '
echo
echo "Done."