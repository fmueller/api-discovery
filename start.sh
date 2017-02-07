#!/bin/bash
# This script builds the respective projects, starts the whole stack using docker-compose and loads test data.

set -e

script_path=`realpath $(dirname $0)`
docker_host=${1:-localhost}
storage_base_url=${docker_host}:8010
log_file=${script_path}/start.log

# Build
echo "Log file: ${log_file}"
echo "Building storage service.."
cd ${script_path}/storage && ./gradlew build > ${log_file} 2>&1
echo "Building swagger-ui.."
cd ${script_path}/swagger-ui/server && npm install > ${log_file} 2>&1
cd ${script_path}/swagger-ui && gulp > ${log_file} 2>&1

# Run docker-compose
echo "Starting services with docker-compose.."
export DOCKER_MACHINE_HOST=${docker_host}
docker-compose up --build > ${log_file} 2>&1 &

# Load test data
echo "Waiting for services to come up.."
sleep 45s # have to wait until storage service is up
echo "Loading test data to the storage service.."
/bin/bash ${script_path}/storage/load-test-data.sh ${storage_base_url} ${script_path}/storage

echo "Done. Swagger-UI is accessable over: http://${docker_host}:8080"
