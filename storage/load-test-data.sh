#!/bin/bash
# Script to load test data to the storage service via its API.
# Example: ./load-test-data.sh localhost:8010 ~/api-discovery/storage

set -e

base_url=${1:-localhost:8010}
storage_project_path=${2:-.}

curl -H "Content-Type: application/json" -X PUT --data @${storage_project_path}/src/test/resources/petstore-full.json ${base_url}/apps/petstore
curl -H "Content-Type: application/json" -X PUT --data @${storage_project_path}/src/test/resources/uber.json ${base_url}/apps/uber
