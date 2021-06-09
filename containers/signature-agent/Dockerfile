FROM docker.io/library/centos:latest
LABEL maintainer=mcaimi@redhat.com
LABEL agent=gpg-signature-agent

USER root
RUN dnf update -y && dnf install -y maven git python3 python3-requests && \
    git clone https://github.com/mcaimi/ocp4-signature-verification /tmp/gpg-sig && \
    cp /tmp/gpg-sig/clients/signature-upload.py /usr/local/bin/signature-upload.py && chmod +x /usr/local/bin/signature-upload.py

