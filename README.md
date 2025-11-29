# Nexus – Open Source Microservices Platform

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

Nexus is an open-source microservices platform built with Spring Boot (Java 17+), MongoDB Atlas, Kafka, and Kubernetes. It provides domain services for users, products, orders, payments, and investments, frontend by an API Gateway.

## What the Project Does

### Intro
Supply chain fragmentation creates significant barriers for SMEs, hindering efficient operations and coordination. This challenge leads to increased costs and delays, affecting overall competitiveness.

### Problem Statement
The lack of real-time visibility and coordination in supply chains leads to inefficiencies, increased costs, and missed opportunities for SMEs to optimize their operations.

### Nexus Solution (High-Level)
Nexus unifies disparate supply chain actors through a modular event-driven microservices architecture. It enables:
- Real-time collaboration across suppliers, funders, investors, and fulfillment partners.
- Transparent tracking of user, product, order, payment, and investment lifecycle events.
- Extensible integration points (Kafka topics, REST APIs) for adding analytical, financing, or notification services.
- A gateway entry point enforcing consistent security, routing, and resilience.

This foundation reduces friction for SMEs by providing a pluggable digital backbone to experiment, scale, and interoperate without adopting a monolithic platform.

- API Gateway as single entry point
- Stateless Spring Boot microservices
- MongoDB Atlas persistence
- Optional Kafka producer/consumer
- Kubernetes-first deployment (Minikube)

---

## Architecture Overview

- API Gateway (entry point, NodePort by default)
- Microservices (ClusterIP):
  - user-service (port 3000)
  - product-service (port 3002)
  - investment-service (port 3003)
  - payment-service (port 3004)
  - order-service (port 3005)
- Messaging (optional):
  - Kafka (single broker; KRaft or Zookeeper mode)
  - producer (publishes events)
  - consumer (e.g., notifications)
- Persistence:
  - MongoDB Atlas (configured via env vars)

Flow:
<img width="5878" height="3244" alt="Mermaid Chart - Create complex, visual diagrams with text -2025-11-29-113759" src="https://github.com/user-attachments/assets/104be7e0-4249-4f3e-8880-990dbc21e44b" />

---

## Features

- REST APIs per domain routed via API Gateway
- JWT filters and authorization at gateway
- Circuit breaker-ready (gateway fallbacks included)
- Bruno collections for API testing (NexusApis/, ApiGatewayBruno/, bruno-* folders)
- Kubernetes deployments and scripts in k8s/

---

## How to Run Locally (Kubernetes)

Prerequisites:
- Docker
- Minikube, kubectl
- Java 17, Maven (for local runs)

1) Start Minikube (7GB RAM):
```bash
minikube start --cpus=4 --memory=7168 --driver=docker
```

2) Build Docker images inside Minikube (example):
```bash
eval $(minikube docker-env)

# Build core services
docker build -t nexus/user-service:latest ./user-service
docker build -t nexus/product-service:latest ./product-service
docker build -t nexus/investment-service:latest ./investment-service
docker build -t nexus/payment-service:latest ./payment-service
docker build -t nexus/order-service:latest ./order-service
docker build -t nexus/api-gateway:latest ./API-Gateway

# Optional Kafka apps
docker build -t nexus/producer:latest ./producer
docker build -t nexus/consumer:latest ./consumer
```

3) Deploy to Kubernetes:
```bash
cd k8s
chmod +x deploy.sh
./deploy.sh
```

4) Get API Gateway URL (best for macOS):
```bash
minikube service api-gateway -n nexus --url
# Use the printed URL in Bruno/Postman
```

If NodePort isn’t reachable directly, use:
```bash
# Alternative: Port-forward
kubectl port-forward -n nexus svc/api-gateway 8080:8080
# Access http://localhost:8080
```

5) Verify pods:
```bash
kubectl get pods -n nexus
```

Notes:
- If using Kafka in KRaft mode, Zookeeper isn’t needed. If using Zookeeper-based Kafka, deploy Zookeeper first (k8s/zookeeper-deployment.yaml).
- On macOS with Docker driver, prefer “minikube service … --url” or port-forward due to NodePort networking constraints.

---

## Run Services Without Kubernetes (Optional)

Each service can run standalone:
```bash
# Example: user-service
cd user-service
./mvnw spring-boot:run
```
Configure MongoDB Atlas via application.yml or env vars. Gateway defaults to port 8080.

---

## Example Requests

With port-forwarding:
```bash
curl http://localhost:8080/nexus/api/v1/users
curl http://localhost:8080/nexus/api/v1/products
curl http://localhost:8080/nexus/api/v1/orders
```

Bruno collections:
- NexusApis/
- API-Gateway/ApiGatewayBruno/
- user-service/bruno-user-service/
- product-service/bruno-product-service/

---

## Screenshots (add later)

Place images under docs/screenshots/ and reference here:
- Kubernetes Dashboard pods/services
- K9s view of nexus namespace
- Bruno calling API Gateway endpoints

---

## Contribution Guide

We welcome contributions:
- Fork, branch, and open PRs
- Keep changes focused and add tests when possible
- For new services: include Dockerfile, k8s YAML, actuator/probes if enabled, and docs

Issue labels:
- bug: steps to reproduce, logs
- enhancement: use case and proposal
- docs: README or deployment improvements

Coding:
- Spring Boot 3, Java 17
- Consistent package naming (com.nexus.*, org.razz.* as present)
- Use gateway filters for auth and fallbacks

---

## License

Licensed under the Apache License, Version 2.0. See LICENSE for details.

Badge:
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
