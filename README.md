# ACSP API

## Overview
The ACSP API is a microservice for the ACSP (Authorised Corporate Service Provider) workstream, with features for application management related to ACSP operations.

## Development Requirements
In order to build and run the service locally you will need:

- Java 21
- Maven
- Git

## Developer Testing with Docker
This assumes you have already set up your Docker CHS Development environment, if not do this one time task:

1. Clone Docker CHS Development and follow the steps in the repository README

Then:

1. Open new Terminal and change to the root directory of "Docker CHS Development".
   ```bash
   chs-dev services enable acsp-api
   chs-dev development enable acsp-api
   ```
3. Run docker using `chs-dev up` in the docker-chs-development directory, or `docker_chs up` from any directory

To view logs you can choose one of the following methods:
- Docker desktop dashboard (works best when bounced after a chs-dev up)
- Run `docker_chs logs -f acsp-api` to view logs
- Run `docker_chs reload acsp-api` to reload after making code changes

Test the health check endpoint when running locally `http://api.chs.local:4001/acsp-api/healthcheck`.

Use Postman to test this health check endpoint.

Please be aware that you will also be required to provide a valid bearer token in the Authorization header to call this endpoint.

## Environmental Variables
Please see below for the following environmental variables used within this repository:

| Name | Description                                      
|------|--------------------------------------------------|
| CHS_API_KEY | API Key for ERIC authorization
| API_URL | URL of API
| ERIC_API_URL | ERIC API URL
| ERIC_PORT | ERIC Port
| PAYMENTS_API_URL | Payments API URL
| DOCUMENT_API_LOCAL_URL | Document API URL
| MONGODB_URL | URL to MongoDB
| MONGODB_DATABASE | Name of database
| ACSP01_COST | Cost for ACSP01 (ACSP Registration) applications
| ACSP_APPLICATION_FILING_DESCRIPTION_IDENTIFIER | Filing description identifier
| ACSP_APPLICATION_FILING_DESCRIPTION | ACSP Registration filing description
| ACSP_UPDATE_FILING_DESCRIPTION | Update ACSP filing description
| ACSP_CLOSE_FILING_DESCRIPTION | Close ACSP filing description
| KAFKA_BROKER_ADDR | Kafka broker address for email functionality
| KAFKA_CONFIG_ACKS | Kafka acknowledgment configuration
| KAFKA_CONFIG_RETRIES | Kafka retry configuration
| KAFKA_CONFIG_IS_ROUND_ROBIN | Kafka round robin configuration
| LOG_LEVEL | Used for Structured Logging (minimum level to log)

## API Routes
The API provides the following endpoints organized by functionality:

### ACSP Application Management
These endpoints handle the core ACSP application lifecycle:

| HTTP Method | Path | Description
|-------------|------|-------------------------------------|
| POST | `/transactions/{transaction_id}/authorised-corporate-service-provider-applications` | Post a new ACSP application
| PUT | `/transactions/{transaction_id}/authorised-corporate-service-provider-applications/{acsp_application_id}` | Update an existing ACSP application
| GET | `/transactions/{transaction_id}/authorised-corporate-service-provider-applications/{acsp_application_id}` | Retrieve an ACSP application
| DELETE | `/transactions/{transaction_id}/authorised-corporate-service-provider-applications/{acsp_application_id}` | Delete an ACSP application

### Cost Information
| HTTP Method | Path | Description                           |
|-------------|------|---------------------------------------|
| GET | `/transactions/{transaction_id}/authorised-corporate-service-provider-applications/{acsp_application_id}/costs` | Get cost information for Register as an ACSP application

### Filing Operations
| HTTP Method | Path | Description                             |
|-------------|------|-----------------------------------------|
| GET | `/private/transactions/{transaction_id}/authorised-corporate-service-provider-applications/{acsp_application_id}/filings` | Get filing data for an ACSP application

### Client Identity Verification and Reverification Emails
| HTTP Method | Path                                                                                   | Description                                                  |
|-------------|----------------------------------------------------------------------------------------|--------------------------------------------------------------|
| POST | `/acsp-api/verify-client-identity/send-identity-verification-email/{application_type}` | Send identity verification or reverification email to client |

**Query Parameters for Identity Verification/Reverification Emails:**
- `application_type` (optional): Type of application - "verification" (default) or "reverification"

### Email Functionality

The following ACSP web applications trigger the email sender functionality within this repository upon a successful application submission:
- Verify a client's identity for Companies House (/tell-companies-house-you-have-verified-someones-identity)
- Reverify a client's identity for Companies House (/reverify-someones-identity-for-companies-house)

### Health Check
| HTTP Method | Path | Description |
|-------------|------|-------------|
| GET | `/acsp-api/healthcheck` | Health check endpoint |

### Path Parameters used within applicable endpoints
- `{transaction_id}`: The unique identifier for the transaction
- `{acsp_application_id}`: The unique identifier for the ACSP application

### Request Headers
For all endpoints, the following headers are required:
- **Authorization**: Bearer token for authentication
- **ERIC-Identity**: ERIC identity header (automatically handled by ERIC framework)
- **ERIC-Identity-Type**: Type of ERIC identity (automatically handled by ERIC framework)

### Response Codes
- **200 OK**: Successful operation
- **400 Bad Request**: Invalid request
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error


### Testing
- Unit tests: `mvn test`
- Integration tests: Run via Maven or IntelliJ
- Use Postman to test API endpoints

### Build Command
To build this repo from the pom.xml file, run the following command:
- Build: `mvn clean build`
