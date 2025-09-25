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
The API routes are defined based on the following patterns:

| Path Pattern                                                                                                         | Description                                                                                                        |
|----------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| `/private/transactions/{TRANSACTION_ID}/authorised-corporate-service-provider-applications/{APPLICATION_ID}/filings` | Filing endpoint for ACSP Registration applications (requires bearer token, a transaction id and an application id) |
| `/acsp-api/healthcheck`                                                                                              | Health check endpoint (Requires bearer token)                                                                      |

### Request Headers
For testing the endpoints of this service, a valid Bearer token is required, as is standard with all CH API services.

### Logging Events
The application uses structured logging with configurable log levels.

### MongoDB
The application uses MongoDB for data persistence with:
- Database: `acsp_application`
- Auto-index creation enabled
- Snake case field naming strategy

### Email Functionality

The following ACSP web applications trigger the email sender functionality within this repository:
- Verify a client's identity for Companies House (/tell-companies-house-you-have-verified-someones-identity)
- Reverify a client's identity for Companies House (/reverify-someones-identity-for-companies-house)

The above front facing web applications trigger the email sender functionality within this repository upon application completion.
### Testing
- Unit tests: `mvn test`
- Integration tests: Run via Maven or IntelliJ
- Use Postman to test API endpoints

### Build Commands
To build this repo from the pom.xml file, run the following command:
- Build: `mvn clean build`
