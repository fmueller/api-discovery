#!/bin/bash
# This script builds the respective projects, starts the whole stack using docker-compose and loads test data.

set -e

script_path="$( cd "$( dirname "$0" )" && pwd )"
docker_host=${1:-localhost}
storage_project_path="${script_path}/storage"
storage_base_url=${docker_host}:8010
log_file=${script_path}/start-script.log

# Preparations
npm install gulp >> ${log_file} 2>&1

# Build
echo "Log file: ${log_file}"
echo "Building storage service.."
cd ${script_path}/storage && ./gradlew build >> ${log_file} 2>&1

echo "Building swagger-ui.."
cd ${script_path}/swagger-ui && npm install >> ${log_file} 2>&1
cd ${script_path}/swagger-ui/server && npm install >> ${log_file} 2>&1
cd ${script_path}/swagger-ui && gulp >> ${log_file} 2>&1

# Run docker-compose
echo "Starting services with docker-compose.."
export DOCKER_MACHINE_HOST=${docker_host}
docker-compose up --build >> ${log_file} 2>&1 &

# Load test data
echo "Waiting for services to come up.."
while ! printf "GET / HTTP/1.0\r\n\r\n" | nc ${docker_host} 8010; do sleep 5; done
sleep 20

echo "Loading test data to the storage service.."
/bin/bash ${script_path}/storage/load-test-data.sh ${storage_base_url} ${storage_project_path}

# Done
echo "Done. Swagger-UI is accessable over: http://${docker_host}:8080"
