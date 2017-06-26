#!/usr/bin/env bash

set -euf -o pipefail

readonly IS_PR_BUILD=${CDP_PULL_REQUEST_NUMBER+true}

echo "Build repository root."

if [ "$IS_PR_BUILD" = true ]; then
  echo "We're in a pull request, aborting."
  exit 1
fi

echo "Build all sub-projects."
./.delivery/build-crawler.sh
./.delivery/build-storage.sh
./.delivery/build-swagger-ui-3.sh
