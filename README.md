# KM - Ingredients Service

This service manages ingredients and prepared foods. After starting the application, interactive API documentation is available at `/swagger-ui.html`.

## New Relic configuration

The application uses the New Relic Java agent located in the `newrelic` directory. Provide your license key via the `NEW_RELIC_LICENSE_KEY` environment variable or `.env` file. The included `newrelic.yml` loads the key from that variable.

## Production secrets

When running with the `prod` profile, secret values are retrieved from the Hashicorp Vault instance at `https://vault.leultewolde.com`. The application expects a `VAULT_TOKEN` environment variable for authentication. Secrets such as database credentials and MinIO access keys must be stored in Vault with the same property names used in `application-prod.properties`.

## Splunk logging

When the `prod` profile is active, logs are also sent to Splunk using the HTTP Event Collector. The Splunk instance is available at `http://10.0.0.222:8000/`. Store the `SPLUNK_TOKEN` in Vault so it can be injected at runtime.
