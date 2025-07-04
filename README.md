# KM - Ingredients Service

This service manages ingredients and prepared foods. After starting the application, interactive API documentation is available at `/swagger-ui.html`.

## New Relic configuration

The application uses the New Relic Java agent located in the `newrelic` directory. Provide your license key via the `NEW_RELIC_LICENSE_KEY` environment variable or `.env` file. The included `newrelic.yml` loads the key from that variable.

## Production secrets

When running with the `prod` profile, secret values are retrieved from the Hashicorp Vault instance at `https://vault.leultewolde.com`. The application expects a `VAULT_TOKEN` environment variable for authentication. Secrets such as database credentials and MinIO access keys must be stored in Vault with the same property names used in `application-prod.properties`.
