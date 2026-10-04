# Database Recovery Runbook

When Payment Service reports database connectivity failures:

1. Check whether PostgreSQL is available.
2. Check the DATABASE_URL configuration.
3. Verify database credentials.
4. Check whether the application can reach PostgreSQL.
5. Inspect application logs for connection errors.
6. Compare the current deployment configuration with the previous version.

Common database errors include:

Connection refused
Authentication failed
Connection timeout
Unknown database host