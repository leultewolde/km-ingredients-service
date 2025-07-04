#!/bin/sh
set -e

# Fetch New Relic license key from Hashicorp Vault if VAULT_TOKEN is provided
if [ -n "$VAULT_TOKEN" ]; then
  VAULT_URL="${VAULT_ADDR:-https://vault.leultewolde.com}"
  VAULT_PATH="${VAULT_NEW_RELIC_PATH:-secret/data/newrelic}"
  LICENSE=$(curl -sf -H "X-Vault-Token: $VAULT_TOKEN" "$VAULT_URL/v1/$VAULT_PATH" | jq -r '.data.data.NEW_RELIC_LICENSE_KEY // .data.data.license_key // empty')
  if [ -n "$LICENSE" ]; then
    export NEW_RELIC_LICENSE_KEY="$LICENSE"
  fi
fi

exec java -javaagent:/newrelic/newrelic.jar -jar app.jar

