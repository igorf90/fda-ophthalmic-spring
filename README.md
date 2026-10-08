# FDA Ophthalmic RI — backend-only Phase 1

Java 21 + Spring Boot 3.5 + MapStruct 1.6.3. **No PostgreSQL, JPA, Flyway or Angular yet.**

## Architecture
`Controller → Service → External Repository → official API` (response flows back through typed external record → MapStruct mapper → DTO → controller).

`HttpExternalRepository` centralizes timeouts, identifying User-Agent, HTTP errors, 4 MB response cap and JSON content validation. `FdaExternalRepository` contains endpoint-specific queries and deserialization. `FdaMapper` maps typed external models to DTOs. Services return `ApiResult<DTO>` and never return JPA entities.

Source status is distinct: `AVAILABLE`, `NOT_FOUND`, `BLOCKED`, `RATE_LIMITED`, `ERROR`. HTTP 200 from this API means the wrapper request succeeded; check `status` and `upstreamHttpStatus` before interpreting data. The external URL is included for debugging/provenance.

## Run
Requires Java 21 + Maven 3.9:

```powershell
mvn spring-boot:run
```

Or Docker Desktop:

```powershell
docker compose up --build
```

Postman: import `postman/FDA-Ophthalmic-Backend.postman_collection.json`. Default `baseUrl=http://localhost:8080`.

## Requests
- `GET /api/fda/510k/K251848`
- `GET /api/fda/pma/P170019`
- `GET /api/fda/classification/HQT`
- `GET /api/fda/udi/product-codes/K251848`
- `GET /api/fda/bulk/manifest`
- `GET /api/sources/federal-register`
- `GET /api/sources/ecfr`
- `GET /api/sources/guidance` (blocked source; returns `BLOCKED` if 403/401)
- `GET /api/sources/fda-510k-page` (HTML source; will return `ERROR` if 200 HTML; current generic probe expects JSON)
- `GET /actuator/health`

## Scope / limitations
This phase deliberately focuses on **five typed FDA API workflows** and a few source diagnostics. It is NOT a migration of every endpoint of the original Python project yet: De Novo, standards product-code lookup, CDRH documents, QMSR page-watchers, full eCFR documents and GovInfo retrieval are outstanding. HTML/PDF endpoints require separate document/download response handling instead of JSON deserialization. Not all sample IDs are guaranteed to exist in openFDA. No bypassing Akamai; 401/403 is reported, not retried via proxies.

No database is created or changed. The existing Python app and its PostgreSQL data stay untouched.

## FDA 510(k) legacy page HTTP diagnostics

`GET /api/sources/fda-510k-http?id=K251848` checks the FDA HTML page (not JSON),
records redirect hops/status/content-type, detects the Akamai apology redirect, and
returns a JSON diagnostic payload. `AVAILABLE` means HTML is reachable, not that
any particular clearance data was extracted. The older `/api/sources/fda-510k-page`
endpoint still expects JSON and is retained for compatibility.

### Hetzner VPS (Ubuntu, Docker Compose)

Install Docker Engine and the Docker Compose plugin on your server, copy this project
there, then run `docker compose up -d --build`. By default the container is bound
to `127.0.0.1:8080` on the VPS, **not exposed publicly**. SSH tunnel from your PC:

```bash
ssh -L 8080:127.0.0.1:8080 root@YOUR_HETZNER_IP
```

In another terminal, check `http://localhost:8080/actuator/health` and
`http://localhost:8080/api/sources/fda-510k-http?id=K251848`. To test the
FDA URL directly on the VPS as well, run:

```bash
curl -sS -L -D - -o /dev/null --max-time 30 'https://www.accessdata.fda.gov/scripts/cdrh/cfdocs/cfpmn/pmn.cfm?ID=K251848'
```

A Hetzner IP may still be blocked by FDA/Akamai; moving servers is not a guarantee.
