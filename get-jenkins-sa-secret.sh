#!/bin/bash
# little helper script for dealing with service accounts
# and associated secrets and tokens
#

if [[ "z"$1 == "z" ]]; then
    echo "Usage: $0 <service-account name>"
    exit
fi

CA_FILE_DUMP="/tmp/kube_ca.crt"

# get secret name from service-account
SA_SECRET_NAME=$(kubectl get sa $1 -o jsonpath={.secrets[].name})
SA_TOKEN=$(kubectl get secret $SA_SECRET_NAME -o jsonpath={.data.token} | base64 -d)

# get the Kubernetes CA for API endpoints
KUBE_API_CA_CERT=$(kubectl get secret $SA_SECRET_NAME -o jsonpath='{.data.ca\.crt}' | base64 -d)
echo -e "$KUBE_API_CA_CERT" > $CA_FILE_DUMP

# get current context
CONTEXT=$(kubectl config current-context)

# get cluster name of context
K8S_CLUSTER=$(kubectl config get-contexts $CONTEXT | awk '{print $3}' | tail -n 1)

# get endpoint of current context 
API_ENDPOINT=$(kubectl config view -o jsonpath="{.clusters[?(@.name == \"$K8S_CLUSTER\")].cluster.server}")

# print info
echo "SERVICE ACCOUNT: $1 (associated secret: $SA_SECRET_NAME)"
echo "SERVICE ACCOUNT TOKEN: $SA_TOKEN"
echo "CLUSTER NAME: $K8S_CLUSTER"
echo "ENDPOINT: $API_ENDPOINT"
echo -e "K8S API CERT (stored in $CA_FILE_DUMP): \n$KUBE_API_CA_CERT"
