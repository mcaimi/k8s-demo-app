NOTES ABOUT OPENSHIFT
=====================

In OpenShift, some test containers must be run with specific UIDs, therefore in this case the ci-jenkins service account
must be granted the 'anyuid' SCC:

.. code:: bash

  $ oc adm policy add-scc-to-user anyuid system:serviceaccount:jenkins:ci-jenkins

for more information about SCCs, see official documentation here_

Difference in Pipelines
-----------------------

Jenkins needs two additional plugins to manage OpenShift Clusters:

- Openshift Pipeline Plugin
- Openshift Client Plugin

Since Openshift offers the ability to run builds natively through the employment of BuildConfig objects, the Jenkins CI flow
differs slightly from the one that is run un K8S:

- Jenkinsfile.agent-builder and Jenkinsfile.java-runner have been replaced with Jenkinsfile.buildconfig. This pipeline runs and monitors
buildconfig runs through the use of the Openshift Pipeline Plugin in Jenkins
- Jenkinsfile.build-phase now runs the image generation stage at the end of the pipeline (instead of leveraging another phase and another pipeline)

The 'oc' binary has been added to the base maven-agent image.

Custom Templating
-----------------

Kustomize is a wonderful tool and it beats Templates hands down basically on every aspect. But as of now it does not support
Kubernetes extensions such as OCP3.11 Routes and DeploymentConfigs.

Fortunately it can be patched by adding Custom Resources Definitions (CRDs) to the templates and by writing custom transformer rules.
Look in the 'crds' folder in deployments/common and deployment/pgcommon.

More information about Kustomize and CRDs can be found a this_ link and in the official kubernetes fields_ docs on GitHub.

.. _here: https://docs.openshift.com/container-platform/3.11/admin_guide/manage_scc.html
.. _this: https://github.com/kubernetes-sigs/kustomize/blob/master/examples/transformerconfigs/crd/README.md
.. _fields: https://github.com/kubernetes-sigs/kustomize/blob/master/docs/fields.md
