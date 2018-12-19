#!/usr/bin/env bash

# Exit immediately if there is an error
set -e

# Cause a pipeline (for example, curl -s http://sipb.mit.edu/ | grep foo) to produce a failure return code if any command errors not just the last command of the pipeline.
set -o pipefail

# Print shell input lines as they are read.
set -v

login() {
  if [[ -z "$CF_ORG" ]]; then
    echo "CF env vars not found, assuming you are already logged in to cf"
    return
  fi

  if [[ "${CIRCLE_BRANCH}" = "deploy" ]]; then
    cf api $CF_PROD_API
    cf auth "$CF_USER" "$CF_PASSWORD_PROD"
  else
    echo "Incorrect branch for deployment to prod"
    exit 1
  fi

  cf target -o $CF_ORG
  cf target -s $CF_SPACE
}

main() {
  login

  apiguid=`cf app staging-api-gov-au --guid`
  [ -f /tmp/droplet_$apiguid.tgz] && rm /tmp/droplet_$apiguid.tgz 
  echo "Downloading droplet for guid $apiguid"
  cf curl /v2/apps/$apiguid/droplet/download --output /tmp/droplet_$apiguid.tgz
  cf push --droplet /tmp/droplet_$apiguid.tgz -f manifest-prod-blue.yml
  cf push --droplet /tmp/droplet_$apiguid.tgz -f manifest-prod-green.yml
}

main $@
