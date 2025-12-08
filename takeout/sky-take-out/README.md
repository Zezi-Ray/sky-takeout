# YOUR PROJECT TITLE

#### Description:
Sky Take Out is a Spring Boot 2.7 backend for take-out and delivery with separate admin and consumer APIs. It covers menu browsing, shopping cart, ordering, payment simulation, and tracking for users, plus staff onboarding, menu authoring, shop scheduling, and analytics for admins. Modules keep shared contracts isolated from HTTP and persistence. Knife4j docs at `/doc.html` and WebSocket `/ws/{sid}` endpoints make the system easy to explore.

Users log in through a WeChat-style code exchange that issues JWTs, browse cached categories and dishes, manage carts, and submit orders. Each submission validates address books, checks delivery distance through Baidu Map geocoding and routing, saves orders and details, and clears the cart. Payments keep the WeChat Pay contract but mock the remote call for local use; on success the backend pushes WebSocket notifications so operators see new tickets immediately. Users can repeat or cancel orders, send reminders, and view history without re-entering data.

Admins log in with their own secret, manage employees, and toggle status. Menu governance covers category CRUD, dishes with flavors, and set meals, with images uploaded via Aliyun OSS. Menu endpoints cache responses in Redis (manual for dishes, Spring Cache for set meals) and evictions trigger on edits. Shop open/closed flags live in Redis. Order consoles let operators accept, reject, cancel with refunds, dispatch, and complete orders with status validation.

Operations and analytics include scheduled tasks, reporting endpoints, and exports. A minute-level task auto-cancels orders stuck in pending payment beyond 15 minutes, and a nightly task completes deliveries older than an hour. Reports return turnover, user growth, order funnel, and top-10 sales for any date range, and an Excel export from `src/main/resources/template` summarizes daily operations. MyBatis XML mappers keep SQL explicit, and PageHelper keeps large queries paged and fast.

Platform configuration handles JWT interceptors for `/admin/**` and `/user/**`, Jackson converters, Knife4j grouping, Redis-backed caching and feature flags, WebSocket beans, conditional Aliyun OSS utilities, and Druid-pooled MySQL connections. Domain-specific exceptions, result wrappers, and DTO/VO contracts under `sky-common` and `sky-pojo` keep controllers focused on orchestration.

Configuration is driven by `application.yml` with the `dev` profile active. Database, Redis, OSS, Baidu Map, and WeChat credentials live in `application-dev.yml`; replace sample values before real deployments. The server listens on port `8088` against a `sky_take_out` schema. External payment and map calls need outbound access or stubs, but API docs and WebSocket endpoints work locally without extras.

## Project Layout
- `sky-common`: cross-cutting pieces such as constants, result wrappers, JWT and OSS properties, and domain exceptions.
- `sky-pojo`: entities, DTOs, and VOs defining persistence models, request contracts, and response shapes shared across modules.
- `sky-server`: Spring Boot application with controllers, services, MyBatis mappers, scheduled tasks, WebSocket server, cache configuration, and resource templates.

## Tech Stack Highlights
- Spring Boot 2.7 with declarative transaction management and scheduling enabled.
- MyBatis with XML mappers, PageHelper for pagination, and Druid for connection pooling.
- Redis for caching dishes/set meals and storing shop status flags; Spring Cache annotations and manual RedisTemplate usage coexist.
- JWT-based auth split between admin and user realms, enforced by dedicated interceptors and per-realm secrets/TTLs.
- Third-party hooks: Aliyun OSS for media uploads, WeChat login and mocked WeChat Pay client, Baidu Map APIs for delivery radius validation, Knife4j for interactive API docs, and WebSocket for live ops alerts.

## Running Locally
1) Install JDK 8+ and Maven, then provision MySQL and Redis; create a database named `sky_take_out`.
2) Update `sky-server/src/main/resources/application-dev.yml` with database, Redis, OSS, WeChat, and Baidu Map credentials (or keep defaults for mocked integrations).
3) Start the app: `mvn -pl sky-server -am clean spring-boot:run`, then open `http://localhost:8088/doc.html` and connect to `ws://localhost:8088/ws/{sid}` for live pushes.
4) Seed baseline categories, dishes, and employees through the admin APIs or SQL so user flows have data to browse and order.

## Core API Flows
- User app: WeChat-style login issues a user JWT; cached listings for categories, dishes, and set meals; shopping cart add/subtract/clear; address book before submission; payments simulate WeChat Pay and emit WebSocket notifications; repeat, cancel, remind, history, and detail views are supported.
- Admin app: Employee login issues an admin JWT; employee accounts can be created, paged, and toggled; categories, dishes with flavors, and set meals support full CRUD with OSS-backed media and cache eviction; shop status toggles globally; orders can be filtered, confirmed, rejected, cancelled, dispatched, or completed; dashboards expose turnover, user growth, order funnel, and top-selling items, plus Excel export.

## Operations & Observability
- Scheduled tasks auto-cancel unpaid orders and auto-complete long-running deliveries.
- WebSocket pushes send “new order” and “reminder” events for real-time dashboards.
- Layered logging is tuned in `application.yml`, and result envelopes standardize error handling for clients.

## License
This project is distributed under the Apache License 2.0 (see `LICENSE` for details).
