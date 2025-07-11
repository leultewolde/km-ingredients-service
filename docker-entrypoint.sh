#!/bin/sh
set -e

#export NEW_RELIC_LICENSE_KEY="$NEW_RELIC_LICENSE_KEY"

#exec java -javaagent:/newrelic/newrelic.jar -jar app.jar
exec java -jar app.jar

