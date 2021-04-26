FROM jenkins/slave:latest
LABEL maintainer=mcaimi@redhat.com
LABEL agent=jenkins-slave-maven

USER root
RUN apt update && apt install -y maven
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl \
  && chmod +x ./kubectl && mv ./kubectl /usr/local/bin/kubectl
RUN curl -LO https://mirror.openshift.com/pub/openshift-v4/clients/ocp/4.7.0/openshift-install-linux-4.7.0.tar.gz -o /tmp/oc.tar.gz && tar xzvf oc.tar.gz -C /tmp/ && mv /tmp/oc /usr/local/bin/oc

COPY scripts/jenkins-agent /usr/local/bin/jenkins-agent
RUN chmod +x /usr/local/bin/jenkins-agent &&\
    ln -s /usr/local/bin/jenkins-agent /usr/local/bin/jenkins-slave

USER jenkins
ENTRYPOINT ["jenkins-agent"]
