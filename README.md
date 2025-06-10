# 📦 Tracking Number Generator API

A Spring Boot-based reactive API to generate and persist tracking numbers with Redis rate-limiting and Oracle DB storage.

---

## 🚀 Features

- Generate unique tracking numbers
- Redis-based rate limiting per customer (per minute)
- Environment-specific rate limit configuration
- Oracle DB persistence via R2DBC
- RFC 3339 timestamp handling
- Input validation with descriptive error responses

---

## ⚙️ Prerequisites

- Java 17 or above
- Maven 3.6+
- Oracle XE (or any Oracle DB) running locally or via Docker
- Redis running locally (default: `localhost:6379`)

---

## 📋 Note
- For demo purposes, Oracle DB persistence is disabled. In production, tracking numbers are saved using R2DBC

## 🛠️ How to Run

- Start Oracle DB and Redis
- Oracle: Ensure Oracle DB is running and accessible.
- Redis: docker run -p 6379:6379 redis

---

## 📫 API Usage

- Endpoint : GET /v1/next-tracking-number
- Required Query Parameters

| Name                      | Type   | Description                                          |
| ------------------------- | ------ | ---------------------------------------------------- |
| `origin_country_id`       | String | Origin ISO country code (e.g. `"MY"`)                |
| `destination_country_id`  | String | Destination ISO code (e.g. `"ID"`)                   |
| `weight`                  | Double | Weight in KG (e.g. `"1.234"`)                        |
| `created_at` *(optional)* | String | RFC 3339 timestamp (e.g. `"2025-06-09T13:59:32Z"`)   |
| `customer_id`             | String | UUID (e.g. `"de619854-b59b-425e-9db4-943979e1bd49"`) |
| `customer_name`           | String | Customer name (e.g. `"RedBox Logistics"`)            |
| `customer_slug`           | String | Slug-case name (e.g. `"redbox-logistics"`)           |

---

## Sample Request
curl -G http://localhost:8080/v1/next-tracking-number \
--data-urlencode "origin_country_id=MY" \
--data-urlencode "destination_country_id=ID" \
--data-urlencode "weight=1.234" \
--data-urlencode "customer_id=de619854-b59b-425e-9db4-943979e1bd49" \
--data-urlencode "customer_name=RedBox Logistics" \
--data-urlencode "customer_slug=redbox-logistics"

## Sample Response

{
"tracking_number": "CHNIN00001O",
"created_at": "2025-06-09T13:59:32Z"
}

---
## 📝 Author
Made by Pritesh Bhate





