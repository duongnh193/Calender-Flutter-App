# Horoscope Module Documentation

## Overview

The Horoscope module provides Vietnamese zodiac-based horoscope predictions including:
- **Lifetime horoscope**: Based on Can-Chi (60-year cycle) and gender
- **Yearly horoscope**: Based on zodiac and year
- **Monthly horoscope**: Based on zodiac, year, and month
- **Daily horoscope**: Based on zodiac and date

## API Endpoints

Base URL: `/api/v1/horoscope`

### 1. Lifetime Horoscope

**GET** `/api/v1/horoscope/lifetime`

Returns lifetime predictions based on Can-Chi combination and gender.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| canChi | string | Yes | Can-Chi combination (e.g., "Giáp Tý", "Ất Sửu") |
| gender | string | Yes | Gender ("male" or "female") |

**Example:**
```bash
curl "http://localhost:8080/api/v1/horoscope/lifetime?canChi=Gi%C3%A1p%20T%C3%BD&gender=male"
```

**Response:**
```json
{
  "zodiacId": 1,
  "zodiacCode": "ti",
  "zodiacName": "Tý",
  "canChi": "Giáp Tý",
  "gender": "male",
  "overview": "...",
  "career": "...",
  "love": "...",
  "health": "...",
  "family": "...",
  "fortune": "...",
  "unlucky": "...",
  "advice": "...",
  "metadata": {}
}
```

### 2. Yearly Horoscope

**GET** `/api/v1/horoscope/yearly`

Returns yearly predictions for a zodiac and year.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| zodiacId | long | One required | Zodiac ID (1-12) |
| zodiacCode | string | One required | Zodiac code (ti, suu, dan...) |
| year | int | Yes | Year (1900-2100) |

**Example:**
```bash
curl "http://localhost:8080/api/v1/horoscope/yearly?zodiacCode=ti&year=2025"
curl "http://localhost:8080/api/v1/horoscope/yearly?zodiacId=1&year=2025"
```

**Response:**
```json
{
  "zodiac": {
    "code": "ti",
    "nameVi": "Tý"
  },
  "year": 2025,
  "summary": "...",
  "love": "...",
  "career": "...",
  "fortune": "...",
  "health": "...",
  "warnings": "...",
  "metadata": {}
}
```

### 3. Monthly Horoscope

**GET** `/api/v1/horoscope/monthly`

Returns monthly predictions for a zodiac, year, and month.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| zodiacId | long | One required | Zodiac ID (1-12) |
| zodiacCode | string | One required | Zodiac code |
| year | int | Yes | Year (1900-2100) |
| month | int | Yes | Month (1-12) |

**Example:**
```bash
curl "http://localhost:8080/api/v1/horoscope/monthly?zodiacCode=ti&year=2025&month=12"
```

### 4. Daily Horoscope

**GET** `/api/v1/horoscope/daily`

Returns daily predictions for a zodiac and date.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| zodiacId | long | One required | Zodiac ID (1-12) |
| zodiacCode | string | One required | Zodiac code |
| date | date | No | Date (YYYY-MM-DD). Defaults to today (UTC+7) |

**Example:**
```bash
curl "http://localhost:8080/api/v1/horoscope/daily?zodiacId=1&date=2025-12-09"
curl "http://localhost:8080/api/v1/horoscope/daily?zodiacCode=ti"  # Uses today
```

### 5. Can-Chi Calculator

**GET** `/api/v1/horoscope/can-chi`

Calculates Can-Chi from a birth date.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| birthDate | date | Yes | Birth date (YYYY-MM-DD) |

**Example:**
```bash
curl "http://localhost:8080/api/v1/horoscope/can-chi?birthDate=1984-03-15"
```

**Response:**
```json
{
  "canYear": "Giáp",
  "chiYear": "Tý",
  "canChiYear": "Giáp Tý",
  "zodiacId": 1,
  "zodiacCode": "ti",
  "canDay": "...",
  "chiDay": "...",
  "canChiDay": "...",
  "canMonth": "...",
  "chiMonth": "...",
  "canChiMonth": "..."
}
```

## Zodiac Reference

| ID | Code | Vietnamese |
|----|------|------------|
| 1 | ti | Tý |
| 2 | suu | Sửu |
| 3 | dan | Dần |
| 4 | mao | Mão |
| 5 | thin | Thìn |
| 6 | ty | Tỵ |
| 7 | ngo | Ngọ |
| 8 | mui | Mùi |
| 9 | than | Thân |
| 10 | dau | Dậu |
| 11 | tuat | Tuất |
| 12 | hoi | Hợi |

## Caching

The module uses Redis caching with the following TTL settings:

| Type | Cache Key Pattern | TTL |
|------|-------------------|-----|
| Daily | `hr:daily:{zid}:{YYYY-MM-DD}` | 24 hours |
| Monthly | `hr:monthly:{zid}:{YYYY-MM}` | 12 hours |
| Yearly | `hr:yearly:{zid}:{YYYY}` | 7 days |
| Lifetime | `hr:lifetime:{canChiNorm}:{gender}` | 30 days |

### Cache Admin Endpoints

**DELETE** `/api/v1/admin/horoscope/cache/all` - Clear all caches
**DELETE** `/api/v1/admin/horoscope/cache/daily` - Clear daily cache
**DELETE** `/api/v1/admin/horoscope/cache/monthly` - Clear monthly cache
**DELETE** `/api/v1/admin/horoscope/cache/yearly` - Clear yearly cache
**DELETE** `/api/v1/admin/horoscope/cache/lifetime` - Clear lifetime cache
**DELETE** `/api/v1/admin/horoscope/cache/zodiac/{code}` - Clear by zodiac
**GET** `/api/v1/admin/horoscope/cache/stats` - Get cache statistics

## Database Schema

### horoscope_lifetime
```sql
CREATE TABLE horoscope_lifetime (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    zodiac_id BIGINT NOT NULL,
    can_chi VARCHAR(64) NOT NULL,
    gender ENUM('male','female') NOT NULL,
    overview TEXT,
    career TEXT,
    love TEXT,
    health TEXT,
    family TEXT,
    fortune TEXT,
    unlucky TEXT,
    advice TEXT,
    metadata JSON,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uq_horoscope_lifetime (zodiac_id, can_chi, gender),
    FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
);
```

### horoscope_yearly
```sql
CREATE TABLE horoscope_yearly (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    zodiac_id BIGINT NOT NULL,
    year INT NOT NULL,
    summary TEXT,
    career TEXT,
    love TEXT,
    health TEXT,
    fortune TEXT,
    warnings TEXT,
    metadata JSON,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uq_hy_zodiac_year (zodiac_id, year),
    FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
);
```

### horoscope_monthly
```sql
CREATE TABLE horoscope_monthly (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    zodiac_id BIGINT NOT NULL,
    year INT NOT NULL,
    month TINYINT UNSIGNED NOT NULL,
    summary TEXT,
    career TEXT,
    love TEXT,
    health TEXT,
    fortune TEXT,
    metadata JSON,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uq_hm_zodiac_year_month (zodiac_id, year, month),
    FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
);
```

### horoscope_daily
```sql
CREATE TABLE horoscope_daily (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    zodiac_id BIGINT NOT NULL,
    solar_date DATE NOT NULL,
    summary TEXT,
    career TEXT,
    love TEXT,
    health TEXT,
    fortune TEXT,
    lucky_color VARCHAR(64),
    lucky_number VARCHAR(64),
    metadata JSON,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uq_hd_zodiac_date (zodiac_id, solar_date),
    FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
);
```

## Data Import

### Prerequisites

1. MySQL database running
2. Database schema created (run `db/schema.sql` and `db/horoscope.sql`)
3. Zodiac data seeded

### Import Steps

1. **Validate data** (recommended):
   ```bash
   cd db
   python3 validate_horoscope_data.py --dir ./generated_horoscope.sql --verbose
   ```

2. **Run import script**:
   ```bash
   cd db
   chmod +x import_horoscopes.sh
   ./import_horoscopes.sh -u root -p your_password -d lich_van_nien
   ```

3. **Verify import**:
   ```sql
   SELECT 'lifetime' as type, COUNT(*) as count FROM horoscope_lifetime
   UNION ALL
   SELECT 'yearly', COUNT(*) FROM horoscope_yearly
   UNION ALL
   SELECT 'monthly', COUNT(*) FROM horoscope_monthly
   UNION ALL
   SELECT 'daily', COUNT(*) FROM horoscope_daily;
   ```

### Import Options

| Option | Description |
|--------|-------------|
| `-h, --host` | MySQL host (default: localhost) |
| `-P, --port` | MySQL port (default: 3306) |
| `-u, --user` | MySQL user (default: root) |
| `-p, --password` | MySQL password |
| `-d, --database` | Database name (default: lich_van_nien) |
| `--skip-schema` | Skip schema creation |
| `--skip-validate` | Skip validation after import |
| `--dry-run` | Show what would be done |

## Configuration

### application.yml

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
  cache:
    type: redis
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `REDIS_HOST` | Redis server host | localhost |
| `REDIS_PORT` | Redis server port | 6379 |
| `REDIS_PASSWORD` | Redis password | (empty) |

## Error Responses

All errors return a standard format:

```json
{
  "timestamp": "2025-12-09T10:30:00+07:00",
  "status": 404,
  "code": "NOT_FOUND",
  "message": "Record not found",
  "path": "/api/v1/horoscope/lifetime"
}
```

### Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `NOT_FOUND` | 404 | Horoscope data not found |
| `INVALID_CAN_CHI` | 400 | Invalid Can-Chi format |
| `INVALID_GENDER` | 400 | Gender must be male/female |
| `INVALID_PARAMS` | 400 | Missing required parameters |
| `INVALID_MONTH` | 400 | Month must be 1-12 |
| `ZODIAC_NOT_FOUND` | 404 | Zodiac not found |

## Metrics

Prometheus metrics are exposed at `/actuator/prometheus`:

- `horoscope_lifetime_get_seconds` - Lifetime API latency
- `horoscope_yearly_get_seconds` - Yearly API latency
- `horoscope_monthly_get_seconds` - Monthly API latency
- `horoscope_daily_get_seconds` - Daily API latency

## Testing

### Run Unit Tests
```bash
mvn test -Dtest=*Horoscope*
```

### Run Integration Tests
```bash
mvn test -Dtest=HoroscopeControllerIntegrationTest
```

### Sample Curl Tests
```bash
# Daily horoscope
curl "http://localhost:8080/api/v1/horoscope/daily?zodiacId=1&date=2025-12-09"

# Lifetime horoscope
curl "http://localhost:8080/api/v1/horoscope/lifetime?canChi=Giáp%20Tý&gender=male"

# Monthly horoscope
curl "http://localhost:8080/api/v1/horoscope/monthly?zodiacCode=ti&year=2025&month=12"

# Yearly horoscope
curl "http://localhost:8080/api/v1/horoscope/yearly?zodiacId=1&year=2025"

# Can-Chi calculator
curl "http://localhost:8080/api/v1/horoscope/can-chi?birthDate=1984-03-15"
```

## Lifetime Horoscope by Birth (New Feature)

### POST /api/v1/horoscope/lifetime/by-birth

Computes Can-Chi from birth data and returns the corresponding lifetime horoscope.

**Request Body:**
```json
{
  "date": "1990-02-15",
  "hour": 23,
  "minute": 30,
  "isLunar": false,
  "isLeapMonth": false,
  "gender": "male"
}
```

**Parameters:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| date | string | Yes | Birth date in ISO format (yyyy-MM-dd) |
| hour | int | Yes | Birth hour (0-23) |
| minute | int | No | Birth minute (0-59), defaults to 0 |
| isLunar | boolean | No | Whether date is lunar calendar, defaults to false |
| isLeapMonth | boolean | No | Whether it's a leap month (when isLunar=true) |
| gender | string | Yes | "male" or "female" |

**Response (200 - Exact Match):**
```json
{
  "zodiacId": 11,
  "zodiacCode": "tuat",
  "zodiacName": "Tuất",
  "canChi": "Canh Tuất",
  "gender": "male",
  "hourBranch": "ti",
  "hourBranchName": "Tý",
  "computed": true,
  "isFallback": false,
  "overview": "...",
  "career": "...",
  "love": "...",
  "health": "...",
  "metadata": {"source": "db", "computed": true}
}
```

**Response (200 - Fallback):**
```json
{
  "zodiacId": 11,
  "zodiacCode": "tuat",
  "canChi": null,
  "gender": "male",
  "message": "Lifetime data not found for computed Can-Chi; returning zodiac-level default.",
  "computed": true,
  "isFallback": true,
  "overview": "..."
}
```

### Hour Branch Mapping (12 Canh)

| Branch | Code | Time Range |
|--------|------|------------|
| Tý | ti | 23:00 - 00:59 |
| Sửu | suu | 01:00 - 02:59 |
| Dần | dan | 03:00 - 04:59 |
| Mão | mao | 05:00 - 06:59 |
| Thìn | thin | 07:00 - 08:59 |
| Tỵ | ty | 09:00 - 10:59 |
| Ngọ | ngo | 11:00 - 12:59 |
| Mùi | mui | 13:00 - 14:59 |
| Thân | than | 15:00 - 16:59 |
| Dậu | dau | 17:00 - 18:59 |
| Tuất | tuat | 19:00 - 20:59 |
| Hợi | hoi | 21:00 - 22:59 |

### Timezone Handling

- All dates/times are interpreted as **Asia/Bangkok (UTC+7)**
- If `isLunar=true`, the lunar date is converted to solar date using the Vietnamese lunar calendar library

### Sample Curl Commands

```bash
# Get lifetime horoscope by birth (solar date)
curl -X POST http://localhost:8080/api/v1/horoscope/lifetime/by-birth \
  -H "Content-Type: application/json" \
  -d '{"date":"1990-02-15","hour":23,"minute":30,"isLunar":false,"gender":"male"}'

# Get lifetime horoscope by birth (lunar date)
curl -X POST http://localhost:8080/api/v1/horoscope/lifetime/by-birth \
  -H "Content-Type: application/json" \
  -d '{"date":"1990-01-20","hour":8,"minute":0,"isLunar":true,"isLeapMonth":false,"gender":"female"}'
```

## Migration Scripts

### Add Indexes for Lifetime Lookup

```bash
mysql -u root -p lich_van_nien < db/migrations/V2__add_lifetime_indexes.sql
```

### Backfill User Can-Chi

```bash
cd db/scripts
python3 backfill_user_canchi.py --db-host localhost --db-user root --db-name lich_van_nien --dry-run
python3 backfill_user_canchi.py --db-host localhost --db-user root --db-name lich_van_nien --batch-size 100
```

### Validate SQL Before Import

```bash
cd db/scripts
python3 validate_sql_uniques.py --dir ../generated_horoscope.sql --verbose
```

## OpenAPI Documentation

Interactive API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

