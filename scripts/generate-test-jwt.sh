#!/bin/bash
# Ajouts de Aziz — generate-test-jwt.sh — 2026-02-21
# WARNING: DEV ONLY. Generates a valid HS256 JWT using the config server's Base64 secret.

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <subject_id>"
    echo "Example: $0 john.doe"
    exit 1
fi

SUBJECT=$1

# Header: {"alg":"HS256","typ":"JWT"}
HEADER_B64="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"

# Payload: {"sub":"$SUBJECT","exp": <timestamp+1hour>}
EXP=$(($(date +%s) + 3600))
PAYLOAD="{\"sub\":\"$SUBJECT\",\"exp\":$EXP}"
PAYLOAD_B64=$(echo -n "$PAYLOAD" | base64 | tr -d '=' | tr '/+' '_-')

# Make sure this matches the gateway.security.jwt.secret exactly!
SECRET_B64="U21hcnRNb2JpbGl0eVNlY3JldEtleUZvckp3dDIwMjYhQW5kQmV5b25k"
# We decode base64, then hmac sha256
SIGNATURE=$(echo -n "${HEADER_B64}.${PAYLOAD_B64}" | openssl dgst -sha256 -mac HMAC -macopt hexkey:$(echo -n "$SECRET_B64" | base64 -D | xxd -p | tr -d '\n') -binary | base64 | tr -d '=' | tr '/+' '_-')

echo "Generated JWT Token (Valid for 1 Hour):"
echo "${HEADER_B64}.${PAYLOAD_B64}.${SIGNATURE}"
