FROM quay.io/quarkus/centos-quarkus-native-s2i
LABEL maintainer=mcaimi@redhat.com
LABEL runner=openjdk

ARG nexus_repo_url
ARG artifact_name
ARG nexus_auth

USER root
COPY scripts/nexus-fetch.py /usr/local/bin/nexus-fetch
RUN chmod +x /usr/local/bin/nexus-fetch
RUN yum install -y python3

RUN mkdir -p /var/lib/deploy && /usr/local/bin/nexus-fetch -r ${nexus_repo_url} -a ${artifact_name} -d /var/lib/deploy -c ${nexus_auth}
EXPOSE 8080

CMD [ "java", "-jar", "/var/lib/deploy/runner.jar" ]
