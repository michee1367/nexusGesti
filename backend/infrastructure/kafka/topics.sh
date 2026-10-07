#!/usr/bin/env bash
# Cree les 5 topics d'evenements NexusGesti.
# Un domaine = un topic. hopital.events porte notamment hopital.acte.facturable,
# consomme par fin-compta-core pour produire l'ecriture D 411 / C 706.
#
# Bootstrap : kafka:29092 (listener INTERNAL). Depuis un conteneur,
# "localhost" y resoudrait le conteneur lui-meme, pas le broker.
set -euo pipefail

BOOTSTRAP="${KAFKA_BOOTSTRAP:-kafka:29092}"
PARTITIONS="${KAFKA_PARTITIONS:-6}"
REPLICATION="${KAFKA_REPLICATION:-1}"
ATTEMPTS="${KAFKA_ATTEMPTS:-45}"

TOPICS=(
  "tenant.events"
  "ped.events"
  "hopital.events"
  "fin.events"
  "audit.events"
)

echo "Bootstrap Kafka : ${BOOTSTRAP}"

for attempt in $(seq 1 "${ATTEMPTS}"); do
  if timeout 10 kafka-topics --bootstrap-server "${BOOTSTRAP}" --list >/dev/null 2>&1; then
    echo "Kafka disponible (tentative ${attempt})."
    break
  fi
  if [ "${attempt}" -eq "${ATTEMPTS}" ]; then
    echo "Kafka injoignable sur ${BOOTSTRAP} apres ${ATTEMPTS} tentatives" >&2
    exit 1
  fi
  echo "Attente Kafka (${attempt}/${ATTEMPTS})..." >&2
  sleep 2
done

for topic in "${TOPICS[@]}"; do
  if timeout 10 kafka-topics --bootstrap-server "${BOOTSTRAP}" --list | grep -qx "${topic}"; then
    echo "Deja present : ${topic}"
    continue
  fi
  timeout 30 kafka-topics --bootstrap-server "${BOOTSTRAP}" \
    --create \
    --topic "${topic}" \
    --partitions "${PARTITIONS}" \
    --replication-factor "${REPLICATION}" \
    --config cleanup.policy=delete \
    --config retention.ms=2592000000 \
    >/dev/null
  echo "Cree : ${topic}"
done

echo "--- Topics finaux ---"
timeout 10 kafka-topics --bootstrap-server "${BOOTSTRAP}" --list | sort