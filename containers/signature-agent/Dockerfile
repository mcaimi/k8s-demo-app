FROM docker.io/library/centos:latest
LABEL maintainer=mcaimi@redhat.com
LABEL agent=gpg-signature-agent

ARG VERSION=4.7
ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000

USER root
RUN groupadd -g ${gid} ${group}
RUN useradd -c "Jenkins user" -d /home/${user} -u ${uid} -g ${gid} -m ${user}

RUN dnf update -y && dnf install -y java-11-openjdk-devel git git-lfs python3 python3-requests skopeo pinentry && dnf clean all

RUN curl --create-dirs -fsSLo /usr/share/jenkins/agent.jar https://repo.jenkins-ci.org/public/org/jenkins-ci/main/remoting/${VERSION}/remoting-${VERSION}.jar \
  && chmod 755 /usr/share/jenkins \
  && chmod 644 /usr/share/jenkins/agent.jar \
  && ln -sf /usr/share/jenkins/agent.jar /usr/share/jenkins/slave.jar

RUN git clone https://github.com/mcaimi/ocp4-signature-verification /tmp/gpg-sig && \
    cp /tmp/gpg-sig/clients/signature-upload.py /usr/local/bin/signature-upload.py && \
    chmod +x /usr/local/bin/signature-upload.py

COPY scripts/jenkins-agent /usr/local/bin/jenkins-agent
COPY config/default.yaml /etc/containers/registries.d/default.yaml
RUN chmod +x /usr/local/bin/jenkins-agent &&\
    ln -s /usr/local/bin/jenkins-agent /usr/local/bin/jenkins-slave

ARG AGENT_WORKDIR=/home/${user}/agent
USER ${user}
ENV AGENT_WORKDIR=${AGENT_WORKDIR}
COPY scripts/bashrc /home/${user}/.bashrc
RUN mkdir /home/${user}/.jenkins && mkdir -p ${AGENT_WORKDIR} && mkdir -p /home/${user}/keyring && mkdir -p /home/${user}/sigstore

VOLUME /home/${user}/.jenkins
VOLUME /home/${user}/keyring
VOLUME ${AGENT_WORKDIR}
WORKDIR /home/${user}

ENTRYPOINT ["jenkins-agent"]
