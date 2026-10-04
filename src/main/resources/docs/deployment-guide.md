# Deployment Guide

Payment Service deployments can modify application configuration.

Database configuration must be verified after deployment.

Important database configuration includes:

DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

After deployment, verify:

1. Service health
2. Database connectivity
3. Kafka connectivity
4. Application logs

If the new deployment introduces database configuration errors,
the service may fail its health checks and return HTTP 503.