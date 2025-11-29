# API Documentation for Product Service

This document provides details about the API documentation for the `product-service` microservice.

---

## API Endpoints

### 1. Open Swagger UI
You can access the Swagger UI to explore and test the API endpoints interactively:

👉 [http://localhost:{portnumber}/swagger-ui/index.html](http://localhost:{portnumber}/swagger-ui/index.html)

---

### 2. Open API Docs (JSON)
The OpenAPI specification in JSON format is available at:

👉 [http://localhost:{portnumber}/v3/api-docs](http://localhost:{portnumber}/v3/api-docs)

---

### 3. Open API Docs (YAML)
The OpenAPI specification in YAML format is available at:

👉 [http://localhost:{portnumber}/v3/api-docs.yaml](http://localhost:{portnumber}/v3/api-docs.yaml)

---

## Notes
- Replace `{portnumber}` with the actual port number where the service is running.
- Ensure the service is running before accessing the above links.

---

## How to Start the Service
1. Navigate to the project directory.
2. Run the following command to start the service:
   ```bash
   mvn spring-boot:run