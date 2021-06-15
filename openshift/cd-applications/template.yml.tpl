apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: nfs-provisioner
  namespace: argocd
spec:
  destination:
    namespace: nfs-provisioner
    server: 'https://kubernetes.default.svc'
  source:
#     repoURL: 'https://kubernetes-sigs.github.io/nfs-subdir-external-provisioner'
#     targetRevision: 4.0.4
#     chart: nfs-subdir-external-provisioner
    path: charts/nfs-subdir-external-provisioner
    repoURL: 'https://github.com/thekoma/nfs-subdir-external-provisioner.git'
    targetRevision: HEAD
    helm:
      parameters:
        - name: nfs.server
          value: nitsa.ipa.gpslab.club
        - name: nfs.path
          value: /mnt/data/nfs
        - name: storageClass.accessModes
          value: ReadWriteMany
        - name: storageClass.name
          value: nfs
        - name: podSecurityPolicy.enabled
          value: 'true'
        - name: rbac.enabled
          value: 'true'
        - name: scc.enabled
          value: 'true'

  project: ocp-day2
  syncPolicy:
    syncOptions:
      - CreateNamespace=true
    automated:
      selfHeal: true
      prune: false


