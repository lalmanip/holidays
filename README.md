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
| `SERVER_PORT` | `8080` | HTTP port |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Comma-separated Next.js origins |

```bash
export DB_USER=root DB_PASSWORD=secret
mvn spring-boot:run
```

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## API endpoints (`/api/v1/holidays`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/hero` | Hero slides + ticker |
| GET | `/destinations/trending?region=international\|india` | Trending destination tiles |
| GET | `/destinations/{slug}` | Destination listing header |
| GET | `/categories` | Active package categories |
| GET | `/destinations/{slug}/packages?categoryCode=best-seller` | Package cards by category |
| GET | `/packages/{pkgId}` | Full package detail (itinerary, details, pricing, terms) |

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

Point vivance-ui fetches at `NEXT_PUBLIC_HOLIDAYS_API_URL=http://localhost:8080/api/v1/holidays` (example).

Detail response field `comments` maps from `review_count`; `pricing` supplies Calculate Price tab options from `holidays_package_pricing_config` and `holidays_departure_cities`.

## Build

```bash
mvn -q clean package
java -jar target/holidays-api-1.0.0-SNAPSHOT.jar
```
