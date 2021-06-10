FROM docker.io/library/centos:latest
LABEL maintainer=mcaimi@redhat.com
LABEL agent=gpg-signature-agent

ENV user signer
ENV uid 1001

USER root
RUN dnf update -y
RUN dnf install -y maven git python3 python3-requests skopeo
RUN git clone https://github.com/mcaimi/ocp4-signature-verification /tmp/gpg-sig && \
    cp /tmp/gpg-sig/clients/signature-upload.py /usr/local/bin/signature-upload.py && \
    chmod +x /usr/local/bin/signature-upload.py

RUN useradd -u $uid $user
USER $user
VOLUME /home/$user/keyring

