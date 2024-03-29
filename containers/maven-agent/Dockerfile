FROM quay.io/centos/centos:stream8
LABEL maintainer=mcaimi@redhat.com
LABEL agent=jenkins-slave-maven

ARG VERSION=4.13
ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000

USER root
RUN groupadd -g ${gid} ${group}
RUN useradd -c "Jenkins user" -d /home/${user} -u ${uid} -g ${gid} -m ${user}

RUN dnf update -y && dnf install -y maven java-11-openjdk-devel git && dnf clean all
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl \
  && chmod +x ./kubectl && mv ./kubectl /usr/local/bin/kubectl
RUN curl -LO https://mirror.openshift.com/pub/openshift-v4/clients/ocp/4.9.11/openshift-client-linux.tar.gz && tar xzvf openshift-client-linux.tar.gz -C /tmp/ && mv /tmp/oc /usr/local/bin/oc
RUN curl -LO https://github.com/kubernetes-sigs/kustomize/releases/download/kustomize%2Fv4.5.2/kustomize_v4.5.2_linux_amd64.tar.gz && tar xzvf kustomize_v4.5.2_linux_amd64.tar.gz -C /tmp/ \
  && chmod +x /tmp/kustomize && mv /tmp/kustomize /usr/local/bin/kustomize

RUN curl --create-dirs -fsSLo /usr/share/jenkins/agent.jar https://repo.jenkins-ci.org/public/org/jenkins-ci/main/remoting/${VERSION}/remoting-${VERSION}.jar \
  && chmod 755 /usr/share/jenkins \
  && chmod 644 /usr/share/jenkins/agent.jar \
  && ln -sf /usr/share/jenkins/agent.jar /usr/share/jenkins/slave.jar

COPY scripts/jenkins-agent /usr/local/bin/jenkins-agent
RUN chmod +x /usr/local/bin/jenkins-agent &&\
    ln -s /usr/local/bin/jenkins-agent /usr/local/bin/jenkins-slave

ARG AGENT_WORKDIR=/home/${user}/agent
ENV AGENT_WORKDIR=${AGENT_WORKDIR}
COPY scripts/bashrc /home/${user}/.bashrc
RUN mkdir /home/${user}/.jenkins && mkdir -p ${AGENT_WORKDIR} && chown -Rv ${user}:${group} /home/${user}

USER ${user}
VOLUME /home/${user}/.jenkins
VOLUME ${AGENT_WORKDIR}
WORKDIR /home/${user}

ENTRYPOINT ["jenkins-agent"]

