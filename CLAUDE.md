# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project shape

This repo is a Maven multi-module flash-sale system plus a separate React frontend workspace in `flash-sale-web/`.

- Backend root: `pom.xml`
- Shared backend module: `flash-sale-common`
- Edge service: `flash-sale-gateway`
- Business services: `flash-sale-user`, `flash-sale-goods`, `flash-sale-seckill`, `flash-sale-order`, `flash-sale-pay`, `flash-sale-admin`
- Frontend app: `flash-sale-web/` (Vite + React + TypeScript)
- Local infrastructure: `docker-compose.yml`
- Database bootstrap: `sql/schema.sql`

## Architecture notes

### Backend

- The backend uses Spring Boot 3.2, Spring Cloud Alibaba, Nacos, Redis, RocketMQ, MyBatis-Plus, Sa-Token, and Redisson.
- Each business area is split into `*-api` and `*-service` modules.
- Service code follows a layered, DDD-style layout with packages such as `adapter/web`, `application`, `domain`, and `infrastructure`.
- `flash-sale-common` holds shared result/error types, validation, annotations, and common exceptions.
- `flash-sale-gateway` handles request entry, auth, IP blacklist checks, trace propagation, and rate limiting.
- The seckill flow is async: request -> gateway auth -> token/captcha checks -> Redis stock decrement -> RocketMQ message -> order creation.
- Database tables are created from `sql/schema.sql`; the sample data in that file is used by the local compose stack.

### Frontend

- `flash-sale-web` is a Vite app with React Router, Axios, Zustand, and Ant Design.
- Current source implements the C-side flows: login, register, home list, goods detail, order detail, pay, and profile.
- `src/utils/request.ts` centralizes token injection and `Result<T>` handling.
- Route protection is done with `AuthGuard` and `CLayout` in `src/App.tsx`.
- The frontend PRD in `docs/frontend-prd.md` also describes admin screens, but those are not present in the current `src/` tree.

## Common commands

### Backend

From the repo root:

```bash
mvn clean install -DskipTests
```

Build a single module and its dependencies:

```bash
mvn -pl flash-sale-seckill/flash-sale-seckill-service -am clean package -DskipTests
```

Run all tests:

```bash
mvn test
```

Run one test class:

```bash
mvn -pl flash-sale-seckill/flash-sale-seckill-service -Dtest=SeckillExecutionServiceTest test
```

Run one test method:

```bash
mvn -pl flash-sale-seckill/flash-sale-seckill-service -Dtest=SeckillExecutionServiceTest#shouldRejectDuplicateSeckill test
```

Run a service locally:

```bash
mvn -pl flash-sale-gateway spring-boot:run
```

Or run the built jar:

```bash
java -jar flash-sale-user/flash-sale-user-service/target/flash-sale-user-service-1.0.0-SNAPSHOT.jar
```

### Frontend

From `flash-sale-web/`:

```bash
npm install
npm run dev
npm run build
npm run lint
npm run preview
```

There is no frontend test script in `package.json`.

### Infrastructure

Start local dependencies:

```bash
docker compose up -d
```

Stop them:

```bash
docker compose down
```

Reset data and reinitialize the schema:

```bash
docker compose down -v && docker compose up -d
```

## Repo-specific pointers

- Gateway auth is implemented in `flash-sale-gateway/src/main/java/com/flashsale/gateway/filter/AuthGlobalFilter.java`.
- IP blocking is implemented in `flash-sale-gateway/src/main/java/com/flashsale/gateway/filter/IpBlacklistFilter.java`.
- Seckill execution starts at `flash-sale-seckill/flash-sale-seckill-service/src/main/java/com/flashsale/seckill/execution/adapter/web/SeckillController.java` and flows into `SeckillExecutionService`.
- User entrypoints are in `flash-sale-user/flash-sale-user-service/src/main/java/com/flashsale/user/adapter/web/UserController.java`.
- Frontend routing starts in `flash-sale-web/src/App.tsx` and shared request behavior is in `flash-sale-web/src/utils/request.ts`.