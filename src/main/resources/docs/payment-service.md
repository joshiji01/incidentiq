# Payment Service

Payment Service processes customer payment requests.

Dependencies:

- PostgreSQL
- Kafka
- API Gateway

PostgreSQL is used for transaction persistence.

Kafka is used for asynchronous payment events.

The Payment Service performs database connectivity checks during startup.

If PostgreSQL is unavailable, the service may fail health checks.

HTTP 503 can be returned when the service is unavailable or unhealthy.

Important configuration includes:

DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
KAFKA_BOOTSTRAP_SERVERS