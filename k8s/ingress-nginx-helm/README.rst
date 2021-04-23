Kubernetes Ingress Controller Deployment with Helm
==================================================

Deployment of an Ingress Controller in minikube can be also performed with an Helm Chart.
This gives much more headroom for customization, and also allows users to fully leverage all Ingress features.

An Helm Chart for the NGINX-based Ingress Controller can be found on ArtifactHUB:

.. code:: bash

  $ helm search hub ingress-nginx
  URL                                                     CHART VERSION   APP VERSION     DESCRIPTION
  https://artifacthub.io/packages/helm/ingress-ng...      3.29.0          0.45.0          Ingress controller for Kubernetes using NGINX a... 
  [...]

The provided values file (ingress-values.yaml) is configured to expose the ingress controller ports (80 & 443) on the host network:

.. code:: bash

  ## Use host ports 80 and 443
  ## Disabled by default
  ##
  hostPort:
    enabled: true
    ports:
      http: 80
      https: 443

Deployment is done with Helm in a dedicated namespace:

.. code:: bash

  $ kubectl create ns ingress-controller
  $ helm repo add ingress-nginx https://artifacthub.io/packages/helm/ingress-nginx && helm repo update
  $ helm upgrade -i -f ingress-values.yaml -n ingress-controller ingress-controller ingress-nginx/ingress-nginx

After a while, the ingress is deployed

.. code:: bash

  $ kubectl get all -n ingress-controller
  NAME                                                               READY   STATUS    RESTARTS   AGE
  pod/ingress-controller-ingress-nginx-controller-7dfcfd4c6d-cw2cj   1/1     Running   0          11m

  NAME                                                            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
  service/ingress-controller-ingress-nginx-controller             ClusterIP   10.97.230.11    <none>        80/TCP,443/TCP   22m
  service/ingress-controller-ingress-nginx-controller-admission   ClusterIP   10.109.198.96   <none>        443/TCP          22m

  NAME                                                          READY   UP-TO-DATE   AVAILABLE   AGE
  deployment.apps/ingress-controller-ingress-nginx-controller   1/1     1            1           22m

  NAME                                                                     DESIRED   CURRENT   READY   AGE
  replicaset.apps/ingress-controller-ingress-nginx-controller-6cff469dd7   0         0         0       22m
  replicaset.apps/ingress-controller-ingress-nginx-controller-7dfcfd4c6d   1         1         1       11m

