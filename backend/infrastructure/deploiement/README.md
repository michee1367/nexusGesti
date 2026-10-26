# Déploiement production — VPS unique

Ce guide décrit une installation **mono-hôte** de NexusGesti sur un VPS Linux :
infrastructure Docker (PostgreSQL, Redis, Kafka, Keycloak), trois applications
Java (`socle-service`, `pedagogie-service`, `boulangerie-service`), la passerelle
`api-gateway`, et la console Next.js derrière Nginx.

> Cette cible n'est pas une architecture haute disponibilité. Elle convient à une
> mise en production contrôlée, avec sauvegardes et fenêtre de maintenance. Pour
> une charge réelle, ajouter PgBouncer, un Kafka géré ou redondant, des répliques
> PostgreSQL, et un stockage de secrets dédié.

---

## 1. Architecture cible

| Composant | Processus | Port local | Exposition |
|---|---|---:|---|
| `api-gateway` | JVM | 8080 | Nginx → `https://gesti.mink67.com/api/` |
| `socle-service` | JVM | 8101 | interne |
| `pedagogie-service` | JVM | 8111 | interne |
| `boulangerie-service` | JVM | 8141 | interne |
| Console Next.js | Node.js | 3001 | Nginx → `https://gesti.mink67.com/` |
| PostgreSQL 16 | Docker | 5433 → 5432 | localhost uniquement |
| Redis 7 | Docker | 6379 | localhost uniquement |
| Kafka 7.6 | Docker | 9092 (hôte), 29092 (conteneurs) | localhost uniquement |
| Keycloak 25 | Docker | 8180 | Nginx → `https://auth.gesti.mink67.com/` |
| Prometheus / Grafana | Docker | 9090 / 3000 | localhost ou tunnel SSH |
| Vault dev | Docker | 8200 | à retirer en production |

Flux public :

```text
Navigateur
  ├─ https://gesti.mink67.com/          → Nginx → Next.js :3001
  ├─ https://gesti.mink67.com/api/v1/*  → Nginx → api-gateway :8080
  │                                             ├─ /socle/* → socle-service :8101
  │                                             ├─ /ped/*   → pedagogie-service :8111
  │                                             └─ /boul/*  → boulangerie-service :8141
  └─ https://auth.gesti.mink67.com/*    → Nginx → Keycloak :8180
```

La passerelle réécrit `/api/v1/socle/...` en `/api/v1/...` pour le service cible.
Nginx doit donc transmettre le chemin **sans le réécrire** : `proxy_pass
http://127.0.0.1:8080;` sans slash final.

---

## 2. Prérequis

### Serveur

- Ubuntu 24.04 LTS ou Debian 12, x86_64.
- Minimum conseillé : **4 vCPU, 8 Go RAM, 80 Go SSD**. Un build Maven + Next.js
  peut consommer temporairement 3 à 4 Go.
- Accès SSH par clé, utilisateur avec `sudo`, pare-feu.
- Nom de domaine `mink67.com` géré chez le registrar/DNS.

### DNS

Créer deux enregistrements `A` (et `AAAA` si IPv6 est correctement configuré) :

| Nom | Valeur |
|---|---|
| `gesti.mink67.com` | IP publique du VPS |
| `auth.gesti.mink67.com` | IP publique du VPS |

Vérifier avant Certbot :

```bash
dig +short gesti.mink67.com
dig +short auth.gesti.mink67.com
```

### Logiciels

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y ca-certificates curl gnupg git nginx certbot python3-certbot-nginx jq rsync
```

Installer Docker Engine et le plugin Compose depuis le dépôt officiel Docker,
puis Java 21, Maven et Node.js 22 LTS :

```bash
java -version
mvn -version
node --version
npm --version
docker --version
docker compose version
```

Versions attendues : Java 21, Node.js ≥ 20.9 (22 LTS recommandé), Docker Compose v2.

---

## 3. Comptes, répertoires et permissions

```bash
sudo useradd --system --create-home --home-dir /opt/nexusgesti \
  --shell /usr/sbin/nologin nexusgesti

sudo mkdir -p /opt/nexusgesti/{app,frontend,backups,src}
sudo mkdir -p /etc/nexusgesti
sudo chown -R nexusgesti:nexusgesti /opt/nexusgesti
sudo chmod 750 /etc/nexusgesti
```

Le code source sera cloné dans `/opt/nexusgesti/src`, les jars stables dans
`/opt/nexusgesti/app`, la console compilée dans `/opt/nexusgesti/frontend`.

---

## 4. Pare-feu

N'exposer que SSH, HTTP et HTTPS :

```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
sudo ufw status verbose
```

Les ports 3000, 3001, 5433, 6379, 8080, 8101, 8111, 8141, 8180, 9090 et 8200 ne
doivent **pas** être ouverts publiquement. Next.js écoute sur `127.0.0.1` (voir
§10). Les JVM Spring Boot écoutent par défaut sur toutes les interfaces ; UFW
bloque les ports 8080/8101/8111/8141 depuis l'extérieur. Ne pas définir
`SERVER_ADDRESS=127.0.0.1` : Prometheus scrape les JVM via
`host.docker.internal`. Les conteneurs d'infrastructure sont liés à `127.0.0.1`
(voir §6).

---

## 5. Récupérer et compiler le projet

```bash
sudo -u nexusgesti git clone <URL_DU_DEPOT> /opt/nexusgesti/src
```

Si le dépôt est privé, utiliser une clé de déploiement en lecture seule.

### Backend

```bash
sudo -u nexusgesti mvn -f /opt/nexusgesti/src/backend/pom.xml test
sudo -u nexusgesti mvn -f /opt/nexusgesti/src/backend/pom.xml -DskipTests package
cd /opt/nexusgesti/src/backend
```

Les jars produits sont :

```text
services/socle-service/target/socle-service-1.0.0-SNAPSHOT.jar
services/pedagogie-service/target/pedagogie-service-1.0.0-SNAPSHOT.jar
services/boulangerie-service/target/boulangerie-service-1.0.0-SNAPSHOT.jar
services/api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar
```

Les copier sous des noms stables :

```bash
sudo install -o nexusgesti -g nexusgesti -m 640 \
  services/socle-service/target/socle-service-1.0.0-SNAPSHOT.jar \
  /opt/nexusgesti/app/socle-service.jar
sudo install -o nexusgesti -g nexusgesti -m 640 \
  services/pedagogie-service/target/pedagogie-service-1.0.0-SNAPSHOT.jar \
  /opt/nexusgesti/app/pedagogie-service.jar
sudo install -o nexusgesti -g nexusgesti -m 640 \
  services/boulangerie-service/target/boulangerie-service-1.0.0-SNAPSHOT.jar \
  /opt/nexusgesti/app/boulangerie-service.jar
sudo install -o nexusgesti -g nexusgesti -m 640 \
  services/api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar \
  /opt/nexusgesti/app/api-gateway.jar
```

### Console

Copier le frontend sans les dépendances ni le build local, puis compiler avec
l'URL publique de l'API :

```bash
sudo rsync -a --delete \
  --exclude node_modules --exclude .next \
  /opt/nexusgesti/src/frontend/ /opt/nexusgesti/frontend/

cd /opt/nexusgesti/frontend
sudo -u nexusgesti npm ci
sudo -u nexusgesti sh -c \
  'NEXT_PUBLIC_API_URL=https://gesti.mink67.com/api/v1 npm run build'
```

`NEXT_PUBLIC_API_URL` est **figée à la compilation**. Si l'URL change, il faut
recompiler et redémarrer la console. Le fichier `frontend/.env` contient les
valeurs de développement ; ne pas s'en servir en production.

---

## 6. Configurer l'infrastructure Docker

Le fichier de référence est `/opt/nexusgesti/src/backend/docker-compose.yml`.
Ses volumes relatifs supposent que la commande est lancée depuis
`/opt/nexusgesti/src/backend`.

Créer une copie de production **non versionnée** :

```bash
cd /opt/nexusgesti/src/backend
sudo cp docker-compose.yml docker-compose.prod.yml
sudo chown nexusgesti:nexusgesti docker-compose.prod.yml
sudo chmod 600 docker-compose.prod.yml
```

Modifier `docker-compose.prod.yml` :

1. Remplacer tous les mots de passe par des secrets générés.
2. Bindings `ports` sur `127.0.0.1` uniquement.
3. Keycloak en mode production derrière Nginx.
4. Grafana : mot de passe fort, pas `admin`.
5. Vault : retirer le service ou le remplacer par un Vault réel.
6. Prometheus : accès à `host.docker.internal` sous Linux.

Exemple de bindings :

```yaml
services:
  postgres:
    ports:
      - "127.0.0.1:5433:5432"
  redis:
    ports:
      - "127.0.0.1:6379:6379"
  kafka:
    ports:
      - "127.0.0.1:9092:9092"
  keycloak:
    ports:
      - "127.0.0.1:8180:8080"
  prometheus:
    ports:
      - "127.0.0.1:9090:9090"
  grafana:
    ports:
      - "127.0.0.1:3000:3000"
```

Keycloak :

```yaml
  keycloak:
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: <MOT_DE_PASSE_ADMIN_FORT>
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/nexusgesti
      KC_DB_USERNAME: nexusgesti
      KC_DB_PASSWORD: <MOT_DE_PASSE_POSTGRES>
      KC_PROXY_HEADERS: xforwarded
      KC_HTTP_ENABLED: "true"
      KC_HOSTNAME: https://auth.gesti.mink67.com
      KC_HOSTNAME_STRICT: "true"
      KC_HOSTNAME_STRICT_HTTPS: "true"
```

Le hostname Keycloak doit correspondre **exactement** à `KEYCLOAK_ISSUER` des
services. Un écart produit des jetons dont l'`iss` ne correspond pas, donc des
401 sur toutes les routes protégées.

Prometheus sous Linux :

```yaml
  prometheus:
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

Le fichier `backend/infrastructure/docker/prometheus/prometheus.yml` cible
`host.docker.internal` pour joindre les JVM lancées sur l'hôte.

> Ne pas modifier `docker-compose.yml` du dépôt pour y mettre des secrets. La
> copie `docker-compose.prod.yml` reste locale au serveur et doit être sauvegardée
> dans le coffre de secrets, pas dans Git.

### Démarrer l'infrastructure

```bash
cd /opt/nexusgesti/src/backend
sudo docker compose -f docker-compose.prod.yml up -d postgres redis kafka
sudo docker compose -f docker-compose.prod.yml run --rm kafka-init
sudo docker compose -f docker-compose.prod.yml up -d keycloak prometheus grafana
```

`kafka-init` ne s'exécute qu'à la création de son conteneur. Si Kafka tourne déjà
et qu'un topic a été ajouté depuis, rejouer la commande `run --rm kafka-init`.

Vérifications :

```bash
sudo docker compose -f docker-compose.prod.yml ps
sudo docker compose -f docker-compose.prod.yml logs --tail=100 kafka-init
curl -fsS http://127.0.0.1:8180/realms/nexusgesti/.well-known/openid-configuration | jq -r .issuer
```

L'issuer renvoyé doit être l'URL publique
`https://auth.gesti.mink67.com/realms/nexusgesti`, même lorsque la requête part
de localhost. Il doit être identique à `KEYCLOAK_ISSUER`.

### Mots de passe PostgreSQL

`POSTGRES_PASSWORD` n'est utilisé qu'à la **création** du volume. Sur une base
existante, changer le mot de passe avec `ALTER ROLE` ; modifier le compose ne
suffit pas.

Créer le rôle applicatif avec le mot de passe de production **avant le premier
démarrage** des services, afin que `V3__app_role.sql` le réutilise au lieu de
poser son mot de passe de développement :

```bash
sudo docker compose -f docker-compose.prod.yml exec -T postgres \
  psql -U nexusgesti -d nexusgesti <<'SQL'
ALTER ROLE nexusgesti WITH PASSWORD '<MOT_DE_PASSE_POSTGRES>';
DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'nexusgesti_app') THEN
    CREATE ROLE nexusgesti_app LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE
      NOINHERIT NOBYPASSRLS PASSWORD '<MOT_DE_PASSE_APP>';
  ELSE
    ALTER ROLE nexusgesti_app PASSWORD '<MOT_DE_PASSE_APP>';
  END IF;
END
$$;
SQL
```

Les migrations Flyway utilisent le rôle propriétaire `nexusgesti`, tandis que
les applications utilisent `nexusgesti_app`, soumis au RLS. Ne jamais faire
tourner les applications avec le rôle propriétaire : un superutilisateur
contourne les politiques RLS.

---

## 7. Variables d'environnement des applications

Créer `/etc/nexusgesti/common.env` :

```bash
sudo install -o root -g nexusgesti -m 640 /dev/null /etc/nexusgesti/common.env
```

Contenu (remplacer chaque valeur) :

```dotenv
SPRING_PROFILES_ACTIVE=prod

DB_HOST=127.0.0.1
DB_PORT=5433
DB_NAME=nexusgesti
DB_USER=nexusgesti_app
DB_PASSWORD=<MOT_DE_PASSE_APP>

FLYWAY_URL=jdbc:postgresql://127.0.0.1:5433/nexusgesti
FLYWAY_USER=nexusgesti
FLYWAY_PASSWORD=<MOT_DE_PASSE_POSTGRES>

REDIS_HOST=127.0.0.1
REDIS_PORT=6379
REDIS_PASSWORD=<MOT_DE_PASSE_REDIS>

KAFKA_BOOTSTRAP=localhost:9092

KEYCLOAK_ISSUER=https://auth.gesti.mink67.com/realms/nexusgesti
INTERNAL_SERVICE_TOKEN=<JETON_INTERNE_LONG_ALEATOIRE>

JAVA_OPTS=-Xms256m -Xmx512m -XX:MaxMetaspaceSize=192m -XX:ActiveProcessorCount=2
```

Créer `/etc/nexusgesti/socle-service.env` pour les secrets Keycloak :

```dotenv
KEYCLOAK_SERVER_URL=https://auth.gesti.mink67.com
KEYCLOAK_REALM=nexusgesti
KEYCLOAK_CLIENT_ID=nexusgesti-api
KEYCLOAK_ADMIN_CLIENT_ID=nexusgesti-service
KEYCLOAK_ADMIN_CLIENT_SECRET=<SECRET_CLIENT_SERVICE>
MFA_ENCRYPTION_KEY=<CLE_32_CARACTERES_MINIMUM>
```

Créer `/etc/nexusgesti/api-gateway.env` :

```dotenv
JAVA_OPTS=-Xms128m -Xmx256m -XX:MaxMetaspaceSize=160m -XX:ActiveProcessorCount=2
NEXUSGESTI_CORS_ALLOWED_ORIGINS=https://gesti.mink67.com
```

Générer les secrets :

```bash
openssl rand -hex 32
```

Utiliser une valeur différente pour chaque secret. Ne jamais réutiliser le mot de
passe PostgreSQL comme secret Keycloak, Redis ou jeton interne.

Permissions :

```bash
sudo chown root:nexusgesti /etc/nexusgesti/*.env
sudo chmod 640 /etc/nexusgesti/*.env
```

Le jeton `INTERNAL_SERVICE_TOKEN` doit être **identique** dans les trois
applications. Sa valeur par défaut `dev-internal-token` ne doit jamais atteindre
la production.

---

## 8. Initialiser Keycloak

Le realm `nexusgesti` est importé au premier démarrage depuis
`realm-nexusgesti.json`. Keycloak 25 n'importe pas les rôles de service account :
il faut les accorder après l'import, sinon `POST /api/v1/socle/auth/register`
répond **403**.

```bash
KC_ADMIN_PASSWORD='<MOT_DE_PASSE_ADMIN_FORT>'

sudo docker exec -i nexusgesti-keycloak-1 /opt/keycloak/bin/kcadm.sh \
  config credentials \
  --server http://localhost:8080 --realm master \
  --user admin --password "$KC_ADMIN_PASSWORD"

sudo docker exec -i nexusgesti-keycloak-1 /opt/keycloak/bin/kcadm.sh \
  add-roles -r nexusgesti \
  --uusername service-account-nexusgesti-service \
  --cclientid realm-management \
  --rolename manage-users \
  --rolename view-users \
  --rolename query-users \
  --rolename view-realm
```

Vérifier avec un jeton de service :

```bash
TOKEN=$(curl -fsS -X POST \
  https://auth.gesti.mink67.com/realms/nexusgesti/protocol/openid-connect/token \
  -d grant_type=client_credentials \
  -d client_id=nexusgesti-service \
  -d client_secret='<SECRET_CLIENT_SERVICE>' | jq -r .access_token)

curl -s -o /dev/null -w '%{http_code}\n' \
  -H "Authorization: Bearer $TOKEN" \
  'https://auth.gesti.mink67.com/admin/realms/nexusgesti/users?max=1'
```

Attendu : `200`. Un `403` signifie que les rôles `realm-management` manquent.

### Réglages à faire dans la console Keycloak

- Changer le mot de passe administrateur initial (`KEYCLOAK_ADMIN_PASSWORD` ne
  s'applique qu'à la création du compte admin).
- Vérifier `Realm settings → Login → SSL required = external`.
- Configurer le SMTP pour la réinitialisation de mot de passe et les
  notifications.
- Client `nexusgesti-api` : ajouter `https://gesti.mink67.com/*` aux redirect
  URIs, retirer les URLs de démonstration. Garder le flux direct grant actif
  puisque la console passe par le backend.
- Client `nexusgesti-service` : régénérer le secret dans
  `Clients → nexusgesti-service → Credentials`, puis mettre à jour
  `KEYCLOAK_ADMIN_CLIENT_SECRET` et redémarrer `socle-service`.
- Ne pas exécuter `provision.sh` en production : il crée des utilisateurs de
  démonstration et un établissement de test.
- Créer les utilisateurs et établissements réels via le parcours d'inscription ou
  l'API, puis attribuer les rôles `ADMIN` / `CUSTOMER` selon les besoins.

---

## 9. Services systemd

Créer `/etc/systemd/system/nexusgesti@.service` :

```ini
[Unit]
Description=NexusGesti %i
After=network-online.target docker.service
Wants=network-online.target

[Service]
Type=simple
User=nexusgesti
Group=nexusgesti
WorkingDirectory=/opt/nexusgesti
EnvironmentFile=/etc/nexusgesti/common.env
EnvironmentFile=-/etc/nexusgesti/%i.env
ExecStart=/usr/bin/java $JAVA_OPTS -jar /opt/nexusgesti/app/%i.jar
Restart=on-failure
RestartSec=10
SuccessExitStatus=143
LimitNOFILE=65535
NoNewPrivileges=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

Activer les quatre applications :

```bash
sudo systemctl daemon-reload
sudo systemctl enable nexusgesti@socle-service
sudo systemctl enable nexusgesti@pedagogie-service
sudo systemctl enable nexusgesti@boulangerie-service
sudo systemctl enable nexusgesti@api-gateway

sudo systemctl start nexusgesti@socle-service
sudo systemctl start nexusgesti@pedagogie-service
sudo systemctl start nexusgesti@boulangerie-service
sudo systemctl start nexusgesti@api-gateway
```

Surveiller le premier démarrage :

```bash
sudo journalctl -u nexusgesti@socle-service -f
sudo journalctl -u nexusgesti@pedagogie-service -f
sudo journalctl -u nexusgesti@boulangerie-service -f
sudo journalctl -u nexusgesti@api-gateway -f
```

Health checks :

```bash
for p in 8101 8111 8141 8080; do
  printf '%s ' "$p"
  curl -fsS "http://127.0.0.1:$p/actuator/health" || echo KO
  echo
done
```

Les trois services domaine appliquent Flyway au premier démarrage. En cas
d'échec, consulter les journaux avant de redémarrer en boucle.

---

## 10. Console Next.js

Créer `/etc/systemd/system/nexusgesti-front.service` :

```ini
[Unit]
Description=NexusGesti frontend
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=nexusgesti
Group=nexusgesti
WorkingDirectory=/opt/nexusgesti/frontend
Environment=NODE_ENV=production
Environment=PORT=3001
ExecStart=/opt/nexusgesti/frontend/node_modules/.bin/next start -H 127.0.0.1 -p 3001
Restart=on-failure
RestartSec=10
LimitNOFILE=65535
NoNewPrivileges=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

Démarrer :

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now nexusgesti-front
curl -I http://127.0.0.1:3001/login
```

Le script `npm run start` du projet écoute sur `0.0.0.0` ; l'unité ci-dessus
appelle directement Next.js pour rester sur `127.0.0.1`.

---

## 11. Nginx et TLS

Créer `/etc/nginx/sites-available/nexusgesti` :

```nginx
map $http_upgrade $connection_upgrade {
    default upgrade;
    ''      close;
}

server {
    listen 80;
    listen [::]:80;
    server_name gesti.mink67.com;

    client_max_body_size 20m;

    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }

    location / {
        proxy_pass http://127.0.0.1:3001;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }
}

server {
    listen 80;
    listen [::]:80;
    server_name auth.gesti.mink67.com;

    location / {
        proxy_pass http://127.0.0.1:8180;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port 443;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection $connection_upgrade;
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }
}
```

Activer et tester :

```bash
sudo ln -sf /etc/nginx/sites-available/nexusgesti /etc/nginx/sites-enabled/nexusgesti
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx
```

Obtenir les certificats :

```bash
sudo certbot --nginx -d gesti.mink67.com -d auth.gesti.mink67.com
```

Certbot ajoute les blocs TLS et configure le renouvellement. Vérifier :

```bash
sudo certbot renew --dry-run
curl -I https://gesti.mink67.com/
curl -fsS https://auth.gesti.mink67.com/realms/nexusgesti/.well-known/openid-configuration | jq -r .issuer
```

Une fois le HTTPS confirmé, ajouter si besoin les en-têtes de sécurité
(`Strict-Transport-Security`, `X-Content-Type-Options`, `Referrer-Policy`) et
redémarrer Nginx. Ne pas activer HSTS avant d'être certain que le HTTPS est
stable.

---

## 12. Vérification de bout en bout

### Infrastructure

```bash
cd /opt/nexusgesti/src/backend
sudo docker compose -f docker-compose.prod.yml ps
curl -fsS http://127.0.0.1:8080/actuator/health
curl -fsS http://127.0.0.1:8101/actuator/health
curl -fsS http://127.0.0.1:8111/actuator/health
curl -fsS http://127.0.0.1:8141/actuator/health
```

### Résolution de tenant

Endpoint public, sans `X-Tenant-Id` :

```bash
curl -i -X POST https://gesti.mink67.com/api/v1/socle/auth/resolve-tenant \
  -H 'Content-Type: application/json' \
  -d '{"email":"utilisateur@exemple.com"}'
```

Attendu : `200` avec `{"tenantId":"..."}`, ou `404` si l'email n'est pas
rattaché à un établissement.

### Connexion et isolation

Sur une installation neuve, créer le premier compte et son établissement via
`https://gesti.mink67.com/signup` avant de tester la connexion. Le compte reçoit
le rôle `ADMIN` et l'établissement est créé de façon asynchrone ; la console
attend sa disponibilité avant d'ouvrir le tableau de bord.

1. Ouvrir `https://gesti.mink67.com/login`.
2. Saisir l'email, laisser la console résoudre l'établissement.
3. Saisir le mot de passe.
4. Vérifier l'accès au tableau de bord et la dénomination de l'établissement.

Contrôles API :

```bash
curl -i https://gesti.mink67.com/api/v1/socle/annees
# Attendu : 400, en-tête X-Tenant-Id manquant

curl -i https://gesti.mink67.com/api/v1/socle/annees \
  -H 'X-Tenant-Id: <UUID_TENANT>'
# Attendu : 401 sans jeton
```

Pour un test authentifié, récupérer un jeton via le flux de connexion de la
console ou Keycloak, puis appeler l'API avec `Authorization: Bearer <token>` et
`X-Tenant-Id: <uuid>`.

### Topics Kafka

```bash
sudo docker exec -it nexusgesti-kafka-1 \
  kafka-topics --bootstrap-server kafka:29092 --list
```

Attendu : `tenant.events`, `ped.events`, `boulangerie.events`, `hopital.events`,
`fin.events`, `audit.events`.

### Prometheus

Ouvrir `http://127.0.0.1:9090/targets` via tunnel SSH, ou
`ssh -L 9090:127.0.0.1:9090 ...`. Les quatre JVM doivent être `UP`.

---

## 13. Sauvegardes

Sauvegarder PostgreSQL (données métier **et** Keycloak, qui partagent la même
base) :

```bash
sudo mkdir -p /opt/nexusgesti/backups
sudo docker exec nexusgesti-postgres-1 \
  pg_dump -U nexusgesti -Fc nexusgesti \
  | sudo tee /opt/nexusgesti/backups/nexusgesti-$(date +%F-%H%M).dump > /dev/null
```

Conserver aussi :

- `/etc/nexusgesti/*.env` et `docker-compose.prod.yml` dans un coffre de secrets ;
- les certificats et la configuration Nginx ;
- une copie des jars déployés, pour un retour arrière immédiat.

Fréquence minimale : quotidienne, avec rétention 7 jours / 4 semaines / 12 mois,
chiffrement et copie hors site. Tester une restauration au moins une fois par
trimestre.

Restauration dans une base de test :

```bash
sudo docker exec -T nexusgesti-postgres-1 \
  createdb -U nexusgesti nexusgesti_restore

sudo docker exec -i nexusgesti-postgres-1 \
  pg_restore -U nexusgesti -d nexusgesti_restore --clean --if-exists \
  < /opt/nexusgesti/backups/nexusgesti-YYYY-MM-DD-HHMM.dump
```

Ne jamais restaurer par-dessus la production sans arrêter les applications et
sans sauvegarde préalable.

---

## 14. Mise à jour et retour arrière

### Mise à jour

```bash
cd /opt/nexusgesti/src
sudo -u nexusgesti git fetch --all
sudo -u nexusgesti git checkout <TAG_OU_COMMIT>

# Comparer le compose du tag avec la copie de production locale, puis reporter
# les changements de ports, volumes, images et variables.
sudo diff -u /opt/nexusgesti/src/backend/docker-compose.yml \
  /opt/nexusgesti/src/backend/docker-compose.prod.yml || true

sudo -u nexusgesti mvn -f /opt/nexusgesti/src/backend/pom.xml test
sudo -u nexusgesti mvn -f /opt/nexusgesti/src/backend/pom.xml -DskipTests package
cd /opt/nexusgesti/src/backend

# Sauvegarde avant migration
sudo docker exec nexusgesti-postgres-1 \
  pg_dump -U nexusgesti -Fc nexusgesti \
  | sudo tee /opt/nexusgesti/backups/pre-update-$(date +%F-%H%M).dump > /dev/null

sudo systemctl stop nexusgesti@api-gateway \
  nexusgesti@boulangerie-service nexusgesti@pedagogie-service nexusgesti@socle-service

sudo install -o nexusgesti -g nexusgesti -m 640 \
  services/socle-service/target/socle-service-1.0.0-SNAPSHOT.jar \
  /opt/nexusgesti/app/socle-service.jar
# répéter pour les trois autres jars

sudo systemctl start nexusgesti@socle-service nexusgesti@pedagogie-service \
  nexusgesti@boulangerie-service nexusgesti@api-gateway
```

Pour le frontend, arrêter le service avant de remplacer les fichiers et de
recompiler :

```bash
sudo systemctl stop nexusgesti-front
sudo rsync -a --delete --exclude node_modules --exclude .next \
  /opt/nexusgesti/src/frontend/ /opt/nexusgesti/frontend/
cd /opt/nexusgesti/frontend
sudo -u nexusgesti npm ci
sudo -u nexusgesti sh -c \
  'NEXT_PUBLIC_API_URL=https://gesti.mink67.com/api/v1 npm run build'
sudo systemctl start nexusgesti-front
```

Flyway est **forward-only** : ne jamais modifier une migration déjà appliquée en
production. Une correction passe par une nouvelle migration `V4__...sql`.

### Retour arrière

1. Arrêter les applications.
2. Restaurer le dump PostgreSQL pris avant la mise à jour.
3. Remettre les anciens jars dans `/opt/nexusgesti/app`.
4. Recompiler/restaurer l'ancien frontend si nécessaire.
5. Redémarrer et vérifier les health checks.

Un retour arrière de code sans restauration de base n'est valide que si la mise à
jour n'a appliqué aucune migration incompatible.

---

## 15. Supervision et exploitation

### Journaux

```bash
sudo journalctl -u nexusgesti@socle-service -n 200 --no-pager
sudo journalctl -u nexusgesti@api-gateway -n 200 --no-pager
sudo journalctl -u nexusgesti-front -n 200 --no-pager
sudo docker compose -f /opt/nexusgesti/src/backend/docker-compose.prod.yml logs --tail=200
```

### Commandes utiles

```bash
sudo systemctl status nexusgesti@api-gateway
sudo systemctl restart nexusgesti@api-gateway
sudo docker stats --no-stream
df -h
free -h
```

### Pannes fréquentes

| Symptôme | Cause probable | Action |
|---|---|---|
| 401 sur toutes les routes protégées | `KEYCLOAK_ISSUER` différent de l'`iss` des jetons | Aligner `KC_HOSTNAME`, l'URL publique et `KEYCLOAK_ISSUER`, redémarrer |
| `register` renvoie 403 | rôles `realm-management` non accordés | Rejouer les commandes `kcadm.sh add-roles` |
| 503 sur les endpoints qui publient un événement | topic Kafka absent ou broker bloqué | Rejouer `kafka-init`, vérifier Kafka, recréer les topics |
| 400 « Tenant header required » | `X-Tenant-Id` absent | La console doit résoudre le tenant avant l'appel |
| Données invisibles | mauvais `X-Tenant-Id` ou RLS | Vérifier l'UUID et `app.tenant_id` |
| CORS bloqué | origine absente de `NEXUSGESTI_CORS_ALLOWED_ORIGINS` | Ajouter l'origine exacte, redémarrer la passerelle |
| 401 sur `/actuator/**` en direct | route non publique ou `/error` protégé | Tester sur localhost ; ne pas exposer actuator via Nginx |
| Prometheus `DOWN` | `host.docker.internal` non résolu | Ajouter `extra_hosts: host.docker.internal:host-gateway` |
| `remaining connection slots` | pools PostgreSQL saturés | Réduire Hikari, ajouter PgBouncer, ne pas monter `max_connections` à l'infini |
| Échec `mvn clean` sur un jar | JVM encore active / verrou Windows | Arrêter le service avant le build |
| Front appelle `localhost:8080` | build sans `NEXT_PUBLIC_API_URL` | Recompiler avec l'URL publique |

---

## 16. Durcissement production

- [ ] Mots de passe uniques et longs pour PostgreSQL, Redis, Keycloak, Grafana.
- [ ] `nexusgesti_app` avec un mot de passe fort, jamais le rôle propriétaire.
- [ ] `INTERNAL_SERVICE_TOKEN` et `MFA_ENCRYPTION_KEY` générés, jamais les valeurs par défaut.
- [ ] Secret du client `nexusgesti-service` régénéré et stocké hors du dépôt.
- [ ] Utilisateurs de démonstration supprimés ; `provision.sh` non exécuté en production.
- [ ] Keycloak : `sslRequired=external`, SMTP configuré, admin renommé si possible.
- [ ] Ports d'infrastructure liés à `127.0.0.1`, UFW actif.
- [ ] Vault dev supprimé ou remplacé ; jeton racine jamais exposé.
- [ ] Grafana : mot de passe fort, accès limité, pas d'utilisateur anonyme.
- [ ] Prometheus/Grafana accessibles uniquement par tunnel SSH ou VPN.
- [ ] Certificats renouvelés automatiquement, `certbot renew --dry-run` vert.
- [ ] Sauvegardes chiffrées, hors site, restauration testée.
- [ ] Journaux centralisés ou rotation configurée (`journald`, `logrotate`).
- [ ] Surveillance des health checks et de l'espace disque.
- [ ] Accès SSH par clé, root désactivé, `fail2ban` installé.
- [ ] Mises à jour de sécurité appliquées régulièrement.

### Limites connues à documenter auprès des exploitants

- L'en-tête `X-Tenant-Id` est fourni par le client. Le RLS isole les données,
  mais un ADMIN qui connaît l'UUID d'un autre tenant pourrait le cibler. Un
  durcissement (tenant déduit du jeton) reste à faire.
- La console couvre le socle, la pédagogie et les lots 1–3 boulangerie. L'API
  vente existe, mais l'écran de caisse/vente n'est pas livré.
- Hôpital et finance ne sont pas implémentés.
- Cette installation mono-hôte ne survit pas à la perte du serveur sans
  restauration de sauvegarde. Ne pas la présenter comme hautement disponible.
- Le build Next.js et les jars ne sont pas automatisés par une CI dans ce dépôt.
