#!/usr/bin/env bash

set -euf -o pipefail

readonly DOCKER_HOST="pierone.stups.zalan.do"
readonly DOCKER_TEAM="architecture"
readonly DOCKER_NAME="api-disovery"
readonly DOCKER_VERSION="$CDP_BUILD_VERSION"
readonly DOCKER_IMAGE="$DOCKER_HOST/$DOCKER_TEAM/$DOCKER_NAME:$DOCKER_VERSION"
readonly IS_PR_BUILD=${CDP_PULL_REQUEST_NUMBER+true}

echo "Build api-discovery."

if [ "$IS_PR_BUILD" = true ]; then
  echo "We're in a pull request, aborting."
  exit 0
fi

echo "Nothing to do for api-discovery."
