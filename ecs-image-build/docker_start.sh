#!/bin/bash
#
# Start script for acsp-api

PORT=18642

exec java -jar -Dserver.port="${PORT}" "acsp-api.jar"