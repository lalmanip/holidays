# Kubernetes manifests (dev)

| File | Purpose |
|------|---------|
| `configmap.yaml` | Spring external config (`holidays-api.yaml`) for this REST API |
| `deployment.yaml` | `vivance-holidays-api` Deployment (port **8095**) |
| `service.yaml` | NodePort `30095` → pod `8095` |

## Apply (once per cluster)

```bash
kubectl apply -f configmap.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

## CI/CD updates

GitHub Actions (`.github/workflows/deploy-dev.yml`) only changes the container image on each push to **`dev`**. ConfigMap changes are applied manually when needed:

```bash
kubectl apply -f configmap.yaml
kubectl rollout restart deployment/vivance-holidays-api
```

## Fix if your live Deployment still has `containerPort: 8090`

The Service expects **8095**. Patch or re-apply `deployment.yaml` so container and Service ports match.
