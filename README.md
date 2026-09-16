# Valkey + Spring Boot

A comprehensive Spring Boot project demonstrating all **Valkey** (Redis alternative) data structures and operations through REST APIs. Includes PostgreSQL for audit logging and Spring Cache abstraction to showcase the performance difference between Valkey (cache) and PostgreSQL (database). All database operations include a **2-second artificial delay** to simulate real-world query latency, making the caching benefit immediately visible.

## Tech Stack

- **Java 25** + **Spring Boot 4.1.1**
- **Valkey 9.1.0** (via Docker) - Redis-compatible in-memory store
- **PostgreSQL 18** (via Docker) - Relational database for audit logs
- **Spring Data Valkey** (with Valkey Glide driver)
- **Spring Data JPA** + Hibernate
- **Spring Cache Abstraction** backed by Valkey
- **Lombok** for boilerplate reduction

## Prerequisites

- Java 25+
- Maven 3.9+
- Docker & Docker Compose

## Quick Start

```bash
# 1. Start infrastructure (Valkey + PostgreSQL)
docker compose up -d

# 2. Verify services are running
docker compose ps

# 3. Run the application
./mvnw spring-boot:run

# 4. Test an endpoint
curl http://localhost:8080/api/server/info
```

The application starts on **http://localhost:8080**.

## Architecture

```mermaid
graph TB
    Client[HTTP Client] --> Controller[Controllers<br/>12 classes]
    Controller --> Service[Services<br/>14 classes]
    Service --> ValkeyTemplate[StringValkeyTemplate]
    Service --> CacheService[CacheService]
    Service --> DatabaseService[DatabaseService]
    ValkeyTemplate --> Valkey[(Valkey 9.1.0<br/>In-Memory Store)]
    CacheService --> ValkeyCacheManager[ValkeyCacheManager]
    ValkeyCacheManager --> Valkey
    DatabaseService --> AuditLogRepository[AuditLogRepository]
    AuditLogRepository --> JPA[Spring Data JPA]
    JPA --> PostgreSQL[(PostgreSQL 18<br/>Audit Logs)]

    style Valkey fill:#dc382c,color:#fff
    style PostgreSQL fill:#336791,color:#fff
    style Controller fill:#6db33f,color:#fff
    style Service fill:#f0ad4e,color:#000
```

## Project Structure

```
src/main/java/com/sawmik/valkey/
├── ValkeySpringbootApplication.java    # Entry point with @EnableCaching
├── config/
│   ├── CacheConfig.java                # ValkeyCacheManager with JSON serialization
│   └── ValkeyConfig.java              # ValkeyTemplate with Jackson serializers
├── controller/
│   ├── CacheController.java            # Spring cache operations
│   ├── GeoController.java              # Geospatial operations
│   ├── HashController.java             # Hash data type operations
│   ├── HyperLogLogController.java      # Probabilistic counting
│   ├── KeyController.java              # Key management operations
│   ├── ListController.java             # List data type operations
│   ├── ScriptController.java           # Lua script execution
│   ├── ServerController.java           # Server info + audit logs (DB)
│   ├── SetController.java              # Set data type operations
│   ├── SortedSetController.java        # Sorted set operations
│   ├── StreamController.java           # Stream operations
│   └── StringController.java           # String data type operations
├── dto/
│   └── ApiResponse.java                # Uniform API response wrapper
├── entity/
│   └── AuditLog.java                   # JPA entity for audit_log table
├── repository/
│   └── AuditLogRepository.java         # Spring Data JPA repository
└── service/
    ├── CacheService.java               # @Cacheable/@CacheEvict demo
    ├── DatabaseService.java            # Simulated slow DB queries (2s)
    ├── GeoService.java                 # GEOADD, GEODIST, GEOPOS, GEORADIUS
    ├── HashService.java                # HSET, HGET, HGETALL, HINCRBY, etc.
    ├── HyperLogLogService.java         # PFADD, PFCOUNT, PFMERGE
    ├── KeyService.java                 # DEL, EXISTS, EXPIRE, TTL, RENAME, etc.
    ├── ListService.java                # LPUSH, RPUSH, LPOP, RPOP, LRANGE, etc.
    ├── ScriptService.java              # Lua scripts for atomic operations
    ├── SetService.java                 # SADD, SREM, SMEMBERS, SINTER, SUNION, etc.
    ├── SortedSetService.java           # ZADD, ZRANGE, ZRANK, ZSCORE, ZINCRBY
    ├── StreamService.java              # XADD, XREAD, XREADGROUP, XACK
    ├── StringService.java              # SET, GET, APPEND, INCR, STRLEN, etc.
    └── TransactionService.java         # MULTI/EXEC transactions + pipelines
```

## Component Details

### Config Layer

| Class | Purpose |
|-------|---------|
| `CacheConfig` | Creates `ValkeyCacheManager` bean with `JavaTimeModule` for `LocalDateTime` serialization, String key serializer, 10-minute default TTL |
| `ValkeyConfig` | Creates `ValkeyTemplate<String, Object>` with `StringValkeySerializer` for keys and `Jackson2JsonValkeySerializer` for values |

### DTO & Entity Layer

| Class | Purpose |
|-------|---------|
| `ApiResponse` | Uniform response wrapper: `{success, message, data, timestamp}`. Factory methods: `success()`, `error()` |
| `AuditLog` | JPA entity mapped to `audit_log` table. Fields: `id`, `operation`, `entityType`, `entityId`, `details` (TEXT), `createdAt` (auto-generated) |

### Repository Layer

| Class | Purpose |
|-------|---------|
| `AuditLogRepository` | Extends `JpaRepository<AuditLog, Long>`. Provides standard CRUD: `save()`, `findById()`, `findAll()`, `deleteById()`, `count()`, `existsById()` |

### Service Layer

| Class | Valkey Commands | Purpose |
|-------|----------------|---------|
| `StringService` | SET, GET, APPEND, INCR, DECR, SETNX, MGET, STRLEN, BITCOUNT, SETRANGE | String key-value operations |
| `ListService` | LPUSH, RPUSH, LPOP, RPOP, LLEN, LRANGE, LINDEX, LSET, LREM, LTRIM | Linked list operations |
| `HashService` | HSET, HGET, HDEL, HEXISTS, HINCRBY, HGETALL, HKEYS, HVALS, HLEN, HMSET, HSETNX | Field-value map operations |
| `SetService` | SADD, SREM, SMEMBERS, SISMEMBER, SCARD, SRANDMEMBER, SPOP, SINTER, SUNION, SDIFF, SMOVE | Unordered unique set operations |
| `SortedSetService` | ZADD, ZRANGE, ZRANGEBYSCORE, ZRANK, ZSCORE, ZINCRBY, ZREM, ZCARD | Score-ordered set operations |
| `GeoService` | GEOADD, GEODIST, GEOPOS, GEORADIUS, GEOHASH | Geospatial location operations |
| `StreamService` | XADD, XREAD, XGROUP CREATE, XREADGROUP, XACK, XPENDING, XTRIM, XINFO | Append-only log / message queue |
| `HyperLogLogService` | PFADD, PFCOUNT, PFMERGE | Probabilistic cardinality estimation |
| `KeyService` | DEL, EXISTS, EXPIRE, TTL, PERSIST, RENAME, TYPE, KEYS, RANDOMKEY, DUMP, RESTORE | Key-level management |
| `CacheService` | `@Cacheable`, `@CacheEvict` | Spring Cache abstraction demo |
| `ScriptService` | EVAL (Lua scripts) | Atomic operations: increment, compare-and-swap |
| `TransactionService` | MULTI, EXEC, PIPELINE | Transactional and batched commands |
| `DatabaseService` | JPA (PostgreSQL) | Simulated slow queries (2s delay) for audit logging |

### Controller Layer

| Controller | Base Path | Endpoints | Description |
|------------|-----------|-----------|-------------|
| `StringController` | `/api/strings` | 7 | SET, GET, APPEND, INCR, DELETE, LENGTH, CHECK-ABSENT |
| `ListController` | `/api/lists` | 8 | LPUSH, RPUSH, LPOP, RPOP, GET ALL, SIZE, INDEX, DELETE |
| `HashController` | `/api/hashes` | 7 | PUT, GET, ENTRIES, KEYS, SIZE, DELETE, INCREMENT |
| `SetController` | `/api/sets` | 8 | ADD, GET ALL, SIZE, MEMBER CHECK, REMOVE, RANDOM, INTERSECT, UNION |
| `SortedSetController` | `/api/sorted-sets` | 7 | ADD, GET ALL, RANK, SCORE, INCREMENT, RANGE, REMOVE |
| `GeoController` | `/api/geo` | 4 | ADD, POSITION, DISTANCE, RADIUS |
| `StreamController` | `/api/streams` | 5 | ADD, READ, CREATE GROUP, READ GROUP, ACK |
| `HyperLogLogController` | `/api/hyperloglog` | 3 | ADD, COUNT, MERGE |
| `KeyController` | `/api/keys` | 8 | DELETE, EXISTS, EXPIRE, TTL, PERSIST, RENAME, TYPE, SEARCH |
| `ScriptController` | `/api/scripts` | 2 | ATOMIC-INCREMENT, CONDITIONAL-SET |
| `CacheController` | `/api/cache` | 3 | GET (with cache), EVICT, CLEAR ALL |
| `ServerController` | `/api/server` | 5 | INFO, DB-SIZE, FLUSH, AUDIT-LOGS, AUDIT-LOG BY ID |

**Total: 67 endpoints**

## HTTP API Reference

### String Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/strings` | `{"key":"k","value":"v","ttlSeconds":60}` | Set key (optional TTL) |
| `GET` | `/api/strings/{key}` | - | Get value |
| `PUT` | `/api/strings/{key}/append` | `{"value":"v"}` | Append to value |
| `POST` | `/api/strings/{key}/increment` | - | Increment numeric value |
| `DELETE` | `/api/strings/{key}` | - | Delete key |
| `GET` | `/api/strings/{key}/length` | - | Get string length |
| `POST` | `/api/strings/check-absent` | `{"key":"k","value":"v"}` | Set if absent (SETNX) |

### Hash Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/hashes/{key}` | `{"field":"f","value":"v"}` | Set hash field |
| `GET` | `/api/hashes/{key}/{field}` | - | Get field value |
| `GET` | `/api/hashes/{key}/entries` | - | Get all field-value pairs |
| `GET` | `/api/hashes/{key}/keys` | - | Get all field names |
| `GET` | `/api/hashes/{key}/size` | - | Get hash size |
| `DELETE` | `/api/hashes/{key}/{field}` | - | Delete field |
| `PUT` | `/api/hashes/{key}/{field}/increment` | `{"delta":5}` | Increment field value |

### List Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/lists/{key}/left` | `{"value":"v"}` | Push to left (LPUSH) |
| `POST` | `/api/lists/{key}/right` | `{"value":"v"}` | Push to right (RPUSH) |
| `GET` | `/api/lists/{key}/left` | - | Pop from left (LPOP) |
| `GET` | `/api/lists/{key}/right` | - | Pop from right (RPOP) |
| `GET` | `/api/lists/{key}` | - | Get all elements |
| `GET` | `/api/lists/{key}/size` | - | Get list size |
| `GET` | `/api/lists/{key}/{index}` | - | Get element at index |
| `DELETE` | `/api/lists/{key}` | - | Delete entire list |

### Set Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/sets/{key}` | `{"values":["a","b"]}` | Add members |
| `GET` | `/api/sets/{key}` | - | Get all members |
| `GET` | `/api/sets/{key}/size` | - | Get set size |
| `GET` | `/api/sets/{key}/member/{value}` | - | Check membership |
| `DELETE` | `/api/sets/{key}/member/{value}` | - | Remove member |
| `POST` | `/api/sets/{key}/random` | - | Pop random member |
| `POST` | `/api/sets/intersect` | `{"keys":["s1","s2"]}` | Intersection |
| `POST` | `/api/sets/union` | `{"keys":["s1","s2"]}` | Union |

### Sorted Set Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/sorted-sets/{key}` | `{"member":"a","score":10}` | Add with score |
| `GET` | `/api/sorted-sets/{key}` | - | Get all members |
| `GET` | `/api/sorted-sets/{key}/rank/{member}` | - | Get rank |
| `GET` | `/api/sorted-sets/{key}/score/{member}` | - | Get score |
| `POST` | `/api/sorted-sets/{key}/increment/{member}` | `{"delta":5}` | Increment score |
| `GET` | `/api/sorted-sets/{key}/range?min=0&max=100` | - | Range by score |
| `DELETE` | `/api/sorted-sets/{key}/member/{member}` | - | Remove member |

### Geo Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/geo/{key}` | `{"member":"city","longitude":139.69,"latitude":35.69}` | Add location |
| `GET` | `/api/geo/{key}/position/{member}` | - | Get position |
| `GET` | `/api/geo/{key}/distance/{member1}/{member2}` | - | Distance between (km) |
| `GET` | `/api/geo/{key}/radius?longitude=...&latitude=...&radius=100&unit=km` | - | Radius search |

### Stream Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/streams/{name}` | `{"key":"val"}` | Add message |
| `GET` | `/api/streams/{name}` | - | Read all messages |
| `POST` | `/api/streams/{name}/group/{group}` | - | Create consumer group |
| `POST` | `/api/streams/{name}/group/{group}/read` | `{"consumer":"c1"}` | Read from group |
| `POST` | `/api/streams/{name}/group/{group}/ack` | `{"recordIds":["..."]}` | Acknowledge messages |

### HyperLogLog Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/hyperloglog/{key}/add` | `{"values":["a","b"]}` | Add elements |
| `GET` | `/api/hyperloglog/{key}/count` | - | Approximate count |
| `POST` | `/api/hyperloglog/merge` | `{"destKey":"d","sourceKeys":["s1","s2"]}` | Merge HLLs |

### Key Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `DELETE` | `/api/keys/{key}` | - | Delete key |
| `GET` | `/api/keys/{key}/exists` | - | Check existence |
| `POST` | `/api/keys/{key}/expire` | `{"seconds":60}` | Set expiry |
| `GET` | `/api/keys/{key}/ttl` | - | Get TTL |
| `POST` | `/api/keys/{key}/persist` | - | Remove TTL |
| `POST` | `/api/keys/rename` | `{"oldKey":"a","newKey":"b"}` | Rename key |
| `GET` | `/api/keys/{key}/type` | - | Get key type |
| `GET` | `/api/keys/search?pattern=*` | - | Search by pattern |

### Script Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `POST` | `/api/scripts/atomic-increment` | `{"key":"k"}` | Lua: atomic INCR+GET |
| `POST` | `/api/scripts/conditional-set` | `{"key":"k","expectedValue":"old","newValue":"new"}` | Lua: compare-and-swap |

### Cache Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `GET` | `/api/cache/{key}` | - | Get from cache (miss = 2s DB sim) |
| `DELETE` | `/api/cache/{key}` | - | Evict single entry |
| `DELETE` | `/api/cache/all` | - | Clear entire cache |

### Server Operations

| Method | Endpoint | Body | Description |
|--------|----------|------|-------------|
| `GET` | `/api/server/info` | - | Valkey server INFO |
| `GET` | `/api/server/db-size` | - | Number of keys |
| `POST` | `/api/server/flush` | - | FLUSHALL (dangerous) |
| `GET` | `/api/server/audit-logs` | - | All audit logs (2s delay) |
| `GET` | `/api/server/audit-logs/{id}` | - | Audit log by ID (2s delay) |

## Performance: Cache vs Database

The project demonstrates the dramatic performance difference between Valkey (in-memory cache) and PostgreSQL (disk-based database) by simulating a 2-second delay on all database operations.

| Operation | Source | Response Time | Notes |
|-----------|--------|--------------|-------|
| All Valkey data operations | **Valkey** | **8-50ms** | In-memory, sub-millisecond actual |
| Cache HIT | **Valkey** | **~10ms** | Cached value returned instantly |
| Cache MISS (first call) | **PostgreSQL** | **~2,000ms** | Simulated 2s DB delay |
| Audit logs query | **PostgreSQL** | **~2,000ms** | Simulated 2s DB delay |

### Response Time Breakdown

```
Valkey Operations (in-memory):  ~10-50ms total
├── Network round-trip:         ~5-10ms
├── Command execution:          ~1-5ms (actual)
└── Serialization:              ~2-5ms

PostgreSQL Operations (simulated):  ~2,000ms total
├── Network round-trip:              ~1-2ms
├── Simulated query delay:           ~2,000ms
└── Serialization:                   ~5-10ms
```

### Cache Flow

```mermaid
sequenceDiagram
    participant Client
    participant CacheController
    participant CacheService
    participant Valkey
    participant DatabaseService
    participant PostgreSQL

    Note over Client,PostgreSQL: Cache Miss (First Call)
    Client->>CacheController: GET /api/cache/{key}
    CacheController->>CacheService: getFromDatabase(key)
    CacheService->>Valkey: Check cache
    Valkey-->>CacheService: null (miss)
    CacheService->>DatabaseService: simulateSlowQuery()
    DatabaseService->>PostgreSQL: INSERT INTO audit_log
    PostgreSQL-->>DatabaseService: saved (2s delay)
    DatabaseService-->>CacheService: "Database value"
    CacheService->>Valkey: SET key value (cache for 10min)
    CacheService-->>CacheController: value
    CacheController-->>Client: 200 OK (~2,014ms)

    Note over Client,PostgreSQL: Cache Hit (Subsequent Calls)
    Client->>CacheController: GET /api/cache/{key}
    CacheController->>CacheService: getFromDatabase(key)
    CacheService->>Valkey: Check cache
    Valkey-->>CacheService: "Database value" (hit)
    CacheService-->>CacheController: value
    CacheController-->>Client: 200 OK (~10ms)
```

## Data Structure Flow Diagrams

### String Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST set| B[StringController]
    B -->|set key value| C[StringService]
    C -->|opsForValue.set| D[StringValkeyTemplate]
    D -->|SET key value| E[(Valkey)]

    A2[Client] -->|GET value| B2[StringController]
    B2 -->|get key| C2[StringService]
    C2 -->|opsForValue.get| D2[StringValkeyTemplate]
    D2 -->|GET key| E2[(Valkey)]
    E2 -->|value| D2
    D2 -->|value| C2
    C2 -->|value| B2
    B2 -->|200 OK| A2
```

### Hash Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST put field| B[HashController]
    B -->|put key field value| C[opsForHash]
    C -->|HSET key field value| D[(Valkey)]

    A2[Client] -->|GET entries| B2[HashController]
    B2 -->|entries key| C2[opsForHash]
    C2 -->|HGETALL key| D2[(Valkey)]
    D2 -->|Map| B2
    B2 -->|200 OK| A2
```

### List Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST left push| B[ListController]
    B -->|leftPush key value| C[opsForList]
    C -->|LPUSH key value| D[(Valkey)]

    A2[Client] -->|GET all| B2[ListController]
    B2 -->|range key 0 -1| C2[opsForList]
    C2 -->|LRANGE key 0 -1| D2[(Valkey)]
    D2 -->|List| B2
    B2 -->|200 OK| A2
```

### Set Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST add members| B[SetController]
    B -->|add key values| C[opsForSet]
    C -->|SADD key v1 v2| D[(Valkey)]

    A2[Client] -->|POST intersect| B2[SetController]
    B2 -->|intersect k1 k2| C2[opsForSet]
    C2 -->|SINTER k1 k2| D2[(Valkey)]
    D2 -->|Set| B2
    B2 -->|200 OK| A2
```

### Sorted Set Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST add with score| B[SortedSetController]
    B -->|add key member score| C[opsForZSet]
    C -->|ZADD key score member| D[(Valkey)]

    A2[Client] -->|GET rank| B2[SortedSetController]
    B2 -->|rank key member| C2[opsForZSet]
    C2 -->|ZRANK key member| D2[(Valkey)]
    D2 -->|rank| B2
    B2 -->|200 OK| A2
```

### Geo Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST add location| B[GeoController]
    B -->|addLocation key point member| C[opsForGeo]
    C -->|GEOADD key lng lat member| D[(Valkey)]

    A2[Client] -->|GET distance| B2[GeoController]
    B2 -->|distance key m1 m2| C2[opsForGeo]
    C2 -->|GEODIST key m1 m2| D2[(Valkey)]
    D2 -->|Distance| B2
    B2 -->|200 OK| A2
```

### Stream Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST add message| B[StreamController]
    B -->|addMessage stream data| C[opsForStream]
    C -->|XADD stream * k v| D[(Valkey)]

    A2[Client] -->|POST read group| B2[StreamController]
    B2 -->|readGroup stream group consumer| C2[opsForStream]
    C2 -->|XREADGROUP| D2[(Valkey)]
    D2 -->|Records| B2
    B2 -->|200 OK| A2
```

### HyperLogLog Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST add elements| B[HyperLogLogController]
    B -->|add key values| C[opsForHyperLogLog]
    C -->|PFADD key v1 v2| D[(Valkey)]

    A2[Client] -->|GET count| B2[HyperLogLogController]
    B2 -->|count key| C2[opsForHyperLogLog]
    C2 -->|PFCOUNT key| D2[(Valkey)]
    D2 -->|count| B2
    B2 -->|200 OK| A2
```

### Script Lua Operations Flow

```mermaid
flowchart LR
    A[Client] -->|POST atomic increment| B[ScriptController]
    B -->|execute script key| C[ScriptService]
    C -->|EVAL script 1 key| D[StringValkeyTemplate]
    D -->|EXEC| E[(Valkey)]
    E -->|result| D
    D -->|result| C
    C -->|count| B
    B -->|200 OK| A
```

### Cache Operations Flow

```mermaid
flowchart LR
    A[Client] -->|GET cache| B[CacheController]
    B -->|getFromDatabase key| C[CacheService]
    C -->|@Cacheable check| D{Valkey Cache}
    D -->|HIT| C
    D -->|MISS| E[DatabaseService]
    E -->|simulateSlowQuery 2s| F[(PostgreSQL)]
    F -->|AuditLog| E
    E -->|value| C
    C -->|cache value| D
    C -->|value| B
    B -->|200 OK| A
```

### Key Management Flow

```mermaid
flowchart LR
    A[Client] -->|DELETE key| B[KeyController]
    B -->|delete key| C[StringValkeyTemplate]
    C -->|DEL key| D[(Valkey)]

    A2[Client] -->|GET search| B2[KeyController]
    B2 -->|keys pattern| C2[StringValkeyTemplate]
    C2 -->|KEYS pattern| D2[(Valkey)]
    D2 -->|Set of keys| B2
    B2 -->|200 OK| A2
```

### Server and Audit Log Flow

```mermaid
flowchart LR
    A[Client] -->|GET info| B[ServerController]
    B -->|info| C[StringValkeyTemplate]
    C -->|INFO| D[(Valkey)]
    D -->|info map| B
    B -->|200 OK| A

    A2[Client] -->|GET audit-logs| B2[ServerController]
    B2 -->|findAll| E[DatabaseService]
    E -->|findAll 2s delay| F[AuditLogRepository]
    F -->|JPA query| G[(PostgreSQL)]
    G -->|List of AuditLog| B2
    B2 -->|200 OK| A2
```

## Full Request Lifecycle

```mermaid
flowchart TB
    Client[HTTP Client] -->|Request| Dispatcher[DispatcherServlet]
    Dispatcher -->|Route| Controller[Controller Layer]
    Controller -->|Validate & Transform| Service[Service Layer]
    Service -->|Data Operation| Template[StringValkeyTemplate]
    Template -->|Command| Valkey[(Valkey)]
    Valkey -->|Response| Template
    Template -->|Result| Service
    Service -->|Format| Controller
    Controller -->|ApiResponse| Dispatcher
    Dispatcher -->|JSON Response| Client

    Service2[DatabaseService] -->|JPA Query| Repository[AuditLogRepository]
    Repository -->|SQL| PostgreSQL[(PostgreSQL)]

    style Client fill:#e1f5fe
    style Valkey fill:#dc382c,color:#fff
    style PostgreSQL fill:#336791,color:#fff
```

## Docker Compose Services

| Service | Image | Port | Purpose |
|---------|-------|------|---------|
| `valkey` | `valkey/valkey:9.1.0` | `6379` | In-memory data store (Redis alternative) |
| `postgres` | `postgres:18` | `5432` | Relational DB for audit logs |

```bash
# Start all services
docker compose up -d

# Check status
docker compose ps

# View logs
docker compose logs -f valkey
docker compose logs -f postgres

# Stop all services
docker compose down

# Stop and remove volumes (data loss)
docker compose down -v
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
