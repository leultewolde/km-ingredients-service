# KM - Ingredients Service

This service manages ingredients and prepared foods. After starting the application, interactive API documentation is available at `/swagger-ui.html`.

## New Relic configuration

The application uses the New Relic Java agent located in the `newrelic` directory. Provide your license key via the `NEW_RELIC_LICENSE_KEY` environment variable or `.env` file. The included `newrelic.yml` loads the key from that variable.
