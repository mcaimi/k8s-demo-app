NOTES ABOUT OPENSHIFT
=====================

In OpenShift, some test containers must be run with specific UIDs, therefore in this case the ci-jenkins service account
must be granted the 'anyuid' SCC:

.. code:: bash

  $ oc adm policy add-scc-to-user anyuid system:serviceaccount:jenkins:ci-jenkins

for more information about SCCs, see official documentation here_

.. _here: https://docs.openshift.com/container-platform/3.11/admin_guide/manage_scc.html
