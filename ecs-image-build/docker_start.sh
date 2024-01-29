#!/bin/bash
#
# Start script for acsp-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "acsp-api.jar"