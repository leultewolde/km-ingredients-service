#!/bin/sh
set -e

if [ -z "$NEW_RELIC_LICENSE_KEY" ]; then
    echo "Error: NEW_RELIC_LICENSE_KEY is not set. Exiting." >&2
    exit 1
fi
export NEW_RELIC_LICENSE_KEY="$NEW_RELIC_LICENSE_KEY"
exec java -javaagent:/newrelic/newrelic.jar -jar app.jar

