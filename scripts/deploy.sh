#!/usr/bin/env bash

# Exit immediately if there is an error
set -e

# Cause a pipeline (for example, curl -s http://sipb.mit.edu/ | grep foo) to produce a failure return code if any command errors not just the last command of the pipeline.
set -o pipefail

# Print shell input lines as they are read.
set -v

# Include build env vars
source "$(dirname "$0")/buildrc"

# login to cloud foundry if env vars are present
login() {
  if [[ -z "$CF_ORG" ]]; then
    echo "CF env vars not found, assuming you are already logged in to cf"
    return
  fi

  if [[ "${CIRCLE_BRANCH}" = "master" ]]; then
    cf api $CF_PROD_API
    cf auth "$CF_USER" "$CF_PASSWORD_PROD"
  else
    cf api $CF_STAGING_API
    cf auth "$CF_USER" "$CF_PASSWORD_STAGING"
  fi

  cf target -o $CF_ORG
  cf target -s $CF_SPACE
}

# main script function
#
main() {
  login
  if [[ "${CIRCLE_BRANCH}" = "master" ]]; then
    cf push staging-api-gov-au -f manifest-staging.yml
  else
    cf push staging-api-gov-au -f manifest-staging.yml
  fi
}

main $@
