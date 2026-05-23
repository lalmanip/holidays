# Vivance Holidays API

Production-ready Spring Boot 3.x REST API for **Vivance Travel** holiday packages. Reads from the shared MySQL database (`holidays_*` tables) and serves the [vivance-ui](https://github.com/vivance/vivance-ui) Next.js frontend.

## Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8.0+ with schema and seed applied:

```bash
mysql -u root -p vivance < ../vivance-ui/docs/database/holiday-flow-ddl.sql
mysql -u root -p vivance < ../vivance-ui/docs/database/holiday-flow-seed.sql
```

## Configuration

Environment variables (or `application-local.yml`):

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `vivance` | Database name |
| `DB_USER` | `root` | MySQL user |
| `DB_PASSWORD` | *(empty)* | MySQL password |
| `SERVER_PORT` | `8095` | HTTP port |
| `SPRING_DATASOURCE_URL` | *(see application.yml)* | Full JDBC URL (used in Kubernetes) |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | — | DB credentials (Kubernetes Secret) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Comma-separated Next.js origins |

```bash
export DB_USER=root DB_PASSWORD=secret
mvn spring-boot:run
```

- Swagger UI: [http://localhost:8095/swagger-ui.html](http://localhost:8095/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8095/api-docs](http://localhost:8095/api-docs)

## API endpoints (`/api/v1/holidays`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/hero` | Hero slides + ticker |
| GET | `/destinations/trending?region=international\|india` | Trending destination tiles |
| GET | `/destinations/{slug}` | Destination listing header |
| GET | `/categories` | Active package categories |
| GET | `/destinations/{slug}/packages?categoryCode=best-seller` | Package cards by category |
| GET | `/packages/{pkgId}` | Full package detail (itinerary, details, pricing, terms) |

### Backoffice (admin)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/admin/packages` | Create destination + package + all child tables (one transaction) |
| PUT | `/admin/packages/{pkgId}` | Update package + replace all child rows (same JSON body as create) |

See [docs/backoffice-create-holiday-package.md](docs/backoffice-create-holiday-package.md) for sample request/response JSON.

Public reads filter `is_active = 1` and order by `sort_order` ascending.

## Project layout

```
src/main/java/com/vivance/holidays/
  domain/entity/     # JPA entities (1:1 with holidays_* tables)
  domain/repository/
  service/
  web/controller/    # REST + OpenAPI
  web/dto/
  web/mapper/
  config/
```

## Frontend integration

Point vivance-ui fetches at `NEXT_PUBLIC_HOLIDAYS_API_URL=http://localhost:8095/api/v1/holidays` (example).

Detail response field `comments` maps from `review_count`; `pricing` supplies Calculate Price tab options from `holidays_package_pricing_config` and `holidays_departure_cities`.

## Build

```bash
mvn -q clean package
java -jar target/holidays-api-1.0.0-SNAPSHOT.jar
```

## CI/CD (GitHub Actions → Dev Kubernetes)

| Workflow | Trigger | Action |
|----------|---------|--------|
| `ci.yml` | PRs to `dev`/`main`, pushes to `main` | Maven build + tests only |
| `deploy-dev.yml` | Push to **`dev`** only | Build image, push Docker Hub, `kubectl set image` on VPS |

**`main` is not deployed** (no production pipeline yet).

### Required GitHub secrets

| Secret | Purpose |
|--------|---------|
| `DOCKERHUB_USERNAME` | Docker Hub user (e.g. `vivance`) |
| `DOCKERHUB_TOKEN` | Docker Hub access token |
| `VPS_HOST` | VPS IP/hostname with Kubernetes |
| `VPS_USER` | SSH user |
| `VPS_SSH_KEY` | Private key for SSH (kubectl on VPS) |

### Optional GitHub variables

| Variable | Default |
|----------|---------|
| `K8S_NAMESPACE` | `default` |
| `K8S_DEPLOYMENT_NAME` | `vivance-holidays-api` |
| `K8S_CONTAINER_NAME` | `vivance-holidays-api` |

Create a GitHub **Environment** named `dev` (Settings → Environments) if you use protection rules.

Image tags: `{DOCKERHUB_USERNAME}/holidays-api:{version}-{shortSha}` and `:dev-latest`.

### First-time Kubernetes setup (on VPS)

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Ensure `java-mysql-secret` exists with keys `username`, `password`. The Deployment mounts `holidays-api-config` at `/config/holidays-api.yaml` and listens on **8095** (Service NodePort **30095**).

After pushing to `dev`, the workflow runs:

```bash
kubectl set image deployment/vivance-holidays-api \
  vivance-holidays-api=vivance/holidays-api:1.0.0-<sha>
```

### Docker (local)

```bash
mvn clean package -DskipTests
docker build -t vivance/holidays-api:local .
docker run --rm -p 8095:8095 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/vivance_java \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vivance/holidays-api:local
```
