K8S CI/CD DEMO
==============

This is a work-in-progress demo of a CI/CD workflow based on Kubernetes and Jenkins.
This demo makes use of the following projects:

- Kubernetes as a container orchestrator
- Kustomize for templating
- Jenkins as a pipeline job runner
- SonarQube for code quality and coverage measurements
- Nexus for storing artifacts
- JUnit for unit testing
- JaCoCo for code coverage
- Quarkus as an application runtime
- PostgreSQL for the database backend

It is not a lightweight setup, as at least 4 vCPUs and 8GB of RAM are needed for the minikube vm.
Also, since this demo *is still WIP*, this documentation is far from being finished and is to be considered **INCOMPLETE** for the time being.

PREREQUISITES
-------------

1) Install Minikube from the official distribution (or from the official repos of your distro of choice) and provision a VM:

.. code:: bash

  minikube start --cpus=4 --memory=8GB --disk-size=20G

If you are planning to run everything locally (eg. no secure Ingresses in place), add the url of the registry as an `insecure`_ source:

.. code:: bash

  minikube start --cpus=4 --memory=8GB --disk-size=20G --insecure-registry registry.apps.kubernetes.local 

2) Enable all needed k8s addons (istio, helm are optional for the time being)

.. code:: bash

  for item in registry ingress istio-provisioner istio dashboard helm-tiller; do
    minikube addons enable $item
  done

Remember to add the url of the insecure registry to the registries.conf file on the host machine (/etc/containers/registries.conf)
to add registry url to insecure registries on the local host machine

3) Create the base namespaces that will be cosumed by the demo

.. code:: bash

  for i in jenkins dev preprod; do
    kubectl create ns $i
  done

These namespaces are used as follows:

- jenkins: this namespace is used to run builds, pipelines and to host CI/CD components such as Sonar and Nexus
- dev: the mock development environment
- preprod: the mock preproduction environment

Create the configmap insecure-registry-config from configmaps/components/docker-in-docker-insecure-registry-cm.yaml in the jenkins namespace

.. code:: bash

  kubectl apply -f configmaps/components/docker-in-docker-insecure-registry-cm.yaml -n jenkins

4) Add all needed urls to a local DNS resolver.

The demo uses the '*.apps.kubernetes.local' DNS domain. All the following names must resolve to the IP of the minikube VM or to the IP of the
ingress controller deployed in the environment:

- jenkins.apps.kubernetes.local
- sonar.apps.kubernetes.local
- registry.apps.kubernetes.local
- nexus.apps.kubernetes.local

If running in a local VM, setting them in /etc/hosts is sufficient.

5) Expose the registry with an Ingress Route:

.. code:: bash

  kubectl apply -f k8s/components/registry-ingress-kubernetes.yaml -n kube-system

DEMO SETUP
==========

ACCESS TO NAMESPACES
--------------------

Pipelines are run in the 'jenkins' namespace, and some pipelines will need to deploy manifests in other namespaces too.
A dedicated service account needs to be deployed on kubernetes in all the relevant namespaces (jenkins, dev, preprod).

.. code:: bash

  kubectl create sa ci-jenkins -n jenkins
  kubectl create sa ci-jenkins -n dev
  kubectl create sa ci-jenkins -n preprod

A custom role is also needed for the ci-jenkins service account to let the automation work. This role grants:

- Ability to monitor, deploy, undeploy manifests in the jenkins namespace
- Ability to monitor, deploy, undeploy manifests in the dev namespace
- Ability to monitor, deploy, undeploy manifests in the preprod namespace

A matching role binding also must be created in these namespaces:

#) Create jenkins role

.. code:: bash

  kubectl apply -f k8s/jenkins-role.yaml

#) Create jenkins rolebinding

.. code:: bash

  kubectl apply -f k8s/jenkins-rolebinding.yaml

DEPLOY COMPONENTS
-----------------

In the 'jenkins' namespace, deploy:

- The jenkins master server
- A SonarQube instance
- A Nexus Repository instance

Deployment manifests are stored in the 'k8s/components' folder.

.. code:: bash

  for component in jenkins-master-deployment-kubernetes nexus-deployment-kubernetes sonarqube-deployment-kubernetes;
  do
    kubectl apply -f k8s/components/$component.yaml -n jenkins
  done

Once deployed, check that everything is running

.. image:: img/jenkins_ns.png

Required Jenkins Plugins installed in the Master Node:

- Kubernetes plugin
- Kubernetes credentials
- Nexus Artifact Uploader
- SonarQube Scanner
- JaCoCo plugin
- JUnit plugin
- Pipeline Utility Steps plugin
- (optional) BlueOcean

CONFIGURE JENKINS TO USE THE 'JENKINS' NAMESPACE FOR BUILDS
-----------------------------------------------------------

The Kubernetes plugin in jenkins needs to be configured in order to use the 'jenkins' namespace for builds.

1) Retrieve the Service Account login token from kubernetes (store the token in a Jenkins 'secret text' credentials object):

.. code:: bash

  kubectl get secret $(kubectl get sa ci-jenkins -n jenkins -o jsonpath={.secrets[0].name}) -n jenkins -o jsonpath={.data.token} | base64 --decode

2) Retrieve the API server CA certificate:

.. code:: bash

  kubectl get secret $(kubectl get sa ci-jenkins -n jenkins -o jsonpath={.secrets[0].name}) -n jenkins -o jsonpath={.data.'.ca\.crt'} | base64 --decode

3) Configure Jenkins to use the previous data to manage the 'jenkins' namespace:

.. image:: img/jenkins_k8s.png

4) get the jenkins service IP from kubernetes and add this to the plugin config

.. code:: bash

  kubectl get svc -n jenkins jenkins-master-service -o jsonpath='{.spec.clusterIP}'

.. image:: img/jenkins_svc.png

CONFIGURE NEXUS
---------------

On the Nexus web console:

- Create a new 'jenkins' user on the Nexus Repository manager console and make it an admin of the 'maven-releases' repo
- Create a credentias object (type username with password) on Jenkins with the username and password of the newly created account

.. image:: img/nexus.png

On the Jenkins web console:

- Configure a remote repository and point that to the local Nexus Instance

.. image:: img/jenkins_nexus.png

SONARQUBE CONFIG
----------------

1) On Sonar web console, create a 'jenkins' user

.. image:: img/sonar_user.png

Required plugins:

- java and javascanner plugins
- git scm  plugins
- jacoco plugins 

2) Configure the plugin to search for jacoco report in the correct path (for reference, see pom.xml)

.. image:: img/sonar_jacoco.png

3) create a project on Sonar web console

- Get the project token and save it into Jenkins as a credentials object (type secret text)

.. image:: img/sonar_token.png

4) Configure Jenkins to use the token in conjunction with SonarScanner

.. image:: img/sonar_jenkins.png

5) Create a webhook on the project page on Sonar Console to let Sonar push the Quality Gate measurements back to the Pipeline Job.

.. image:: img/sonar_webhook.png

TODO
====

#) Adapt the demo to run natively on Openshift
#) Implement tests in the dev and preprod pipelines
#) Complete documenting the whole demo workflow

.. _insecure: https://minikube.sigs.k8s.io/docs/tasks/registry/