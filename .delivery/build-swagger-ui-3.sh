#!/usr/bin/env bash

set -euf -o pipefail

readonly DOCKER_HOST="pierone.stups.zalan.do"
readonly DOCKER_TEAM="architecture"
readonly DOCKER_NAME="api-portal"
readonly DOCKER_VERSION="$CDP_BUILD_VERSION"
readonly DOCKER_IMAGE="$DOCKER_HOST/$DOCKER_TEAM/$DOCKER_NAME:$DOCKER_VERSION"
readonly IS_PR_BUILD=${CDP_PULL_REQUEST_NUMBER+true}

echo "Build api-portal."

if [ "$IS_PR_BUILD" = true ]; then
  echo "We're in a pull request, aborting."
  exit 0
fi

echo "Install dependencies..."
apt-get update
apt-get install --no-install-recommends -y apt-transport-https
curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add -
echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list
curl -sL https://deb.nodesource.com/setup_8.x | bash -
apt-get update
apt-get install --no-install-recommends -y \
  yarn \
  nodejs \
  build-essential

echo "Build project..."
cd swagger-ui-3/
yarn install
yarn run dist

echo "Build docker image..."
docker build -t ${DOCKER_IMAGE} .

echo "Push docker image..."
docker push ${DOCKER_IMAGE}
