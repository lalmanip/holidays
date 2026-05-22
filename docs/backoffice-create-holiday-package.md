# Backoffice — Create Holiday Package API

## Endpoint

| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/v1/holidays/admin/packages` |
| **Content-Type** | `application/json` |
| **Success** | `201 Created` |

**Base URL (local):** `http://localhost:8095`  
**Base URL (dev):** `http://<your-host>:30095` (NodePort) or your ingress URL

---

## What it does

Creates rows in one database transaction (IDs are auto-generated; do not send primary keys):

1. `holidays_destinations` — **new** destination, **or** link to `existingDestinationSlug`
2. `holidays_tour_packages`
3. `holidays_package_inclusions`
4. `holidays_package_itinerary_days` + `holidays_package_itinerary_highlights`
5. `holidays_package_detail_sections`
6. `holidays_package_hotels`
7. `holidays_package_terms`
8. `holidays_package_pricing_config`

**Category** is referenced by `categoryCode` (e.g. `senior`, `best-seller`) — must already exist in `holidays_package_categories`.

---

## Sample request (matches your USA / Senior Citizen example)

```http
POST /api/v1/holidays/admin/packages HTTP/1.1
Host: localhost:8095
Content-Type: application/json
```

```json
{
  "destination": {
    "slug": "america-tour-packages",
    "name": "USA",
    "region": "international",
    "description": "Statue of Liberty, white house",
    "heroImageUrl": "/statueofliberty.jpg",
    "startingPrice": 99000.00,
    "active": true,
    "sortOrder": 1
  },
  "tourPackage": {
    "pkgId": "PKG-USA-CLASSIC-001",
    "slug": "america-classic-package",
    "categoryCode": "senior",
    "title": "America Classic Package",
    "imageUrl": "/usa/usa.jpg",
    "price": 99900.00,
    "days": 5,
    "nights": 4,
    "rating": 4.7,
    "reviewCount": 289,
    "badge": "Recommended",
    "hasDetailPage": true,
    "sortOrder": 1,
    "active": true,
    "inclusions": [
      { "label": "Hotel", "sortOrder": 1 },
      { "label": "Flight", "sortOrder": 2 }
    ],
    "itinerary": [
      {
        "dayNumber": 1,
        "title": "Arrive in Mauritius",
        "description": "Welcome to Mauritius. Transfer to your beach resort. Evening at leisure by the lagoon.",
        "meals": "Dinner",
        "accommodation": "4★ Beach Resort — Port Louis area",
        "sortOrder": 1,
        "highlights": [
          { "highlight": "Beach leisure", "sortOrder": 1 }
        ]
      },
      {
        "dayNumber": 2,
        "title": "North Island Tour",
        "description": "Full-day sightseeing covering Port Louis, Caudan Waterfront, and northern coastal views.",
        "meals": "Breakfast, Lunch",
        "accommodation": "4★ Beach Resort",
        "sortOrder": 2,
        "highlights": [
          { "highlight": "Optional activities", "sortOrder": 1 }
        ]
      },
      {
        "dayNumber": 3,
        "title": "South & Chamarel",
        "description": "Explore the south — Chamarel coloured earth, waterfalls, and scenic viewpoints.",
        "meals": "Breakfast, Lunch",
        "accommodation": "4★ Beach Resort",
        "sortOrder": 3,
        "highlights": [
          { "highlight": "Sunset views", "sortOrder": 1 }
        ]
      }
    ],
    "detailSections": [
      {
        "sectionType": "highlights",
        "content": "Dedicated tour coordinator support",
        "sortOrder": 5
      },
      {
        "sectionType": "inclusions",
        "content": "Accommodation on twin-sharing basis",
        "sortOrder": 1
      },
      {
        "sectionType": "exclusions",
        "content": "International airfare (unless flight add-on selected)",
        "sortOrder": 1
      },
      {
        "sectionType": "flights_note",
        "content": "Flights can be added during Calculate Price. Round-trip economy seats from Mumbai/Delhi/Bengaluru subject to availability.",
        "sortOrder": 1
      },
      {
        "sectionType": "visa_note",
        "content": "Mauritius offers visa-on-arrival for Indian passport holders. Valid passport (6+ months) and return ticket required.",
        "sortOrder": 1
      }
    ],
    "hotels": [
      {
        "name": "Holiday Inn (or similar)",
        "nights": "4 Nights",
        "mealPlan": "Breakfast + selected lunches/dinners",
        "tourType": null,
        "sortOrder": 1
      }
    ],
    "terms": [
      {
        "termText": "Prices are per person on twin-sharing basis and subject to availability.",
        "sortOrder": 1
      },
      {
        "termText": "Rates may change based on travel dates, flight fares, and hotel inventory.",
        "sortOrder": 2
      }
    ],
    "pricing": {
      "basePrice": 99990.00,
      "currency": "INR",
      "allowsFlights": true,
      "tourTypes": ["Standard", "Value", "Premium"]
    }
  }
}
```

### Use an existing destination (no new destination row)

```json
{
  "existingDestinationSlug": "mauritius-tour-packages",
  "tourPackage": {
    "pkgId": "PKG-MRU-SENIOR-001",
    "slug": "mauritius-senior-package",
    "categoryCode": "senior",
    "title": "Mauritius Senior Special",
    "imageUrl": "https://example.com/image.jpg",
    "price": 45000,
    "days": 5,
    "nights": 4,
    "rating": 4.5,
    "reviewCount": 10,
    "hasDetailPage": false,
    "sortOrder": 1,
    "inclusions": [
      { "label": "Hotel", "sortOrder": 1 }
    ]
  }
}
```

---

## Sample response (`201 Created`)

```json
{
  "message": "Holiday package created successfully",
  "destinationId": 15,
  "destinationSlug": "america-tour-packages",
  "destinationName": "USA",
  "packageInternalId": 4,
  "pkgId": "PKG-USA-CLASSIC-001",
  "packageSlug": "america-classic-package",
  "categoryCode": "senior",
  "detailUrl": "/international-tour-packages/america-tour-packages/america-classic-package?pkgId=PKG-USA-CLASSIC-001",
  "listingUrl": "/international-tour-packages/america-tour-packages",
  "counts": {
    "inclusions": 2,
    "itineraryDays": 3,
    "itineraryHighlights": 3,
    "detailSections": 5,
    "hotels": 1,
    "terms": 2,
    "pricingConfig": 1
  },
  "createdAt": "2026-05-22T20:15:30.123Z"
}
```

| Field | Meaning |
|-------|---------|
| `destinationId` | `holidays_destinations.id` (auto-generated) |
| `packageInternalId` | `holidays_tour_packages.id` — used as FK in child tables |
| `pkgId` | Business id shown on website (`?pkgId=`) |
| `detailUrl` | Frontend detail page path when `hasDetailPage` is true |
| `listingUrl` | International listing path (null for `india` region) |
| `counts` | Number of child rows inserted |

---

## Error responses

| HTTP | When |
|------|------|
| `400` | Validation failed, missing destination, or both `destination` and `existingDestinationSlug` |
| `404` | Unknown `categoryCode` or `existingDestinationSlug` |
| `409` | Duplicate destination `slug`, duplicate `pkgId`, or duplicate package `slug` per destination |

**Validation error example (`400`):**

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed",
  "errors": {
    "tourPackage.categoryCode": "must not be blank"
  },
  "timestamp": "2026-05-22T20:10:00.123Z"
}
```

**Conflict example (`409`):**

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Package pkgId already exists: PKG-USA-CLASSIC-001"
}
```

---

## Field reference

### `destination` (required unless `existingDestinationSlug` is set)

| Field | Required | Notes |
|-------|----------|-------|
| `slug` | Yes | Unique, e.g. `america-tour-packages` |
| `name` | Yes | Display name |
| `region` | Yes | `international` or `india` |
| `description` | No | |
| `heroImageUrl` | No | |
| `startingPrice` | Yes | Decimal |
| `active` | No | Default `true` |
| `sortOrder` | No | Default `0` |

### `tourPackage`

| Field | Required | DB column / notes |
|-------|----------|-------------------|
| `pkgId` | Yes | `pkg_id` — unique |
| `slug` | Yes | Unique per destination |
| `categoryCode` | Yes | Maps to `holidays_package_categories.code` |
| `title`, `imageUrl`, `price`, `days`, `nights`, `rating`, `reviewCount` | Yes | |
| `badge` | No | |
| `hasDetailPage` | Yes | `0` / `1` in DB |
| `sortOrder`, `active` | Yes / No | |
| `inclusions[]` | No | `label`, `sortOrder` |
| `itinerary[]` | No | `dayNumber`, `title`, `description`, `meals`, `accommodation`, `sortOrder`, `highlights[]` |
| `detailSections[]` | No | `sectionType`: `highlights`, `inclusions`, `exclusions`, `flights_note`, `visa_note` |
| `hotels[]` | No | `name`, `nights`, `mealPlan`, `tourType`, `sortOrder` |
| `terms[]` | No | `termText`, `sortOrder` |
| `pricing` | No | `basePrice`, `currency` (3 chars), `allowsFlights`, `tourTypes` array → stored as CSV |

---

## Verify after create

```http
GET /api/v1/holidays/packages/PKG-USA-CLASSIC-001
```

```http
GET /api/v1/holidays/destinations/america-tour-packages/packages?categoryCode=senior
```

---

## Swagger

Open `http://localhost:8095/swagger-ui.html` → **Holidays Admin** → **POST /api/v1/holidays/admin/packages**.
