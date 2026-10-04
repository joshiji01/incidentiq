# IncidentIQ System Architecture

The IncidentIQ platform contains several backend services.

Payment Service is responsible for processing customer payments.

Payment Service depends on PostgreSQL for persistent transaction data.

Payment Service communicates with Kafka for asynchronous payment events.

The service exposes a health endpoint used to determine whether the service is healthy.

If the PostgreSQL connection cannot be established, the Payment Service may become unhealthy.

If Kafka connectivity fails, payment event processing may be affected.

The API Gateway routes client requests to backend services.