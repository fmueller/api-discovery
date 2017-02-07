#!/bin/bash
# Script to load test data to the storage service via its API.

set -e
set -o xtrace

base_url=${1:-localhost:8010}

curl -H "Content-Type: application/json" -X PUT --data @src/test/resources/petstore-full.json ${base_url}/apps/petstore
curl -H "Content-Type: application/json" -X PUT --data @src/test/resources/uber.json ${base_url}/apps/uber
