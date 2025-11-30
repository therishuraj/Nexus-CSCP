# Nexus – Collaborative Supply Chain Platform

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Contributors](https://img.shields.io/github/contributors/therishuraj/Nexus-CSCP)](https://github.com/therishuraj/Nexus-CSCP/graphs/contributors)
[![Open Issues](https://img.shields.io/github/issues/therishuraj/Nexus-CSCP)](https://github.com/therishuraj/Nexus-CSCP/issues)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

Nexus is an open-source microservices platform built with Spring Boot (Java 17+), MongoDB Atlas, Kafka, and Kubernetes. It provides domain services for users, products, orders, payments, and investments, frontend by an API Gateway.

## Table of Contents
- [What the Project Does](#what-the-project-does)
- [Architecture Overview](#architecture-overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [How to Run Locally (Kubernetes)](#how-to-run-locally-kubernetes)
- [Run Services Without Kubernetes](#run-services-without-kubernetes-optional)
- [Example Requests](#example-requests)
- [Troubleshooting](#troubleshooting)
- [Contribution Guide](#contribution-guide)
- [Contributors](#contributors)
- [License](#license)

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
- Kafka producer/consumer
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
  - notification-service (e.g., notifications)
- Persistence:
  - MongoDB Atlas (configured via env vars)

Flow:
![Architecture Nexus](/images/architecture.jpeg)

---

## Features

- REST APIs per domain routed via API Gateway
- JWT filters and authorization at gateway
- Circuit breaker-ready (gateway fallbacks included)
- Bruno collections for API testing (NexusApis/, ApiGatewayBruno/, bruno-* folders)
- Kubernetes deployments and scripts in k8s/

---

## Prerequisites

Before running Nexus locally, ensure you have the following installed:

- **Docker Desktop** - Container runtime for building and running services
- **Minikube** - Local Kubernetes cluster (minimum 7GB RAM allocated)
- **kubectl** - Kubernetes command-line tool
- **Java 17+** - Required for local service development and Maven builds
- **Maven 3.8+** - Build automation tool
- **MongoDB Atlas Account** - Cloud database (free tier available at [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas))

---

## How to Run Locally (Kubernetes)

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

# Kafka apps
docker build -t nexus/notification:latest ./notification-service
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

## Troubleshooting

### Common Issues

**Pods not starting or stuck in Pending state?**
```bash
# Check pod status and events
kubectl describe pod <pod-name> -n nexus

# Verify resource allocation
kubectl top nodes
```
- Ensure Minikube has sufficient resources (minimum 7GB RAM)
- Check MongoDB connection strings are correctly configured
- Verify all Docker images built successfully

**Cannot access API Gateway?**

For **macOS users** (recommended):
```bash
# Use Minikube service URL
minikube service api-gateway -n nexus --url
```

**Alternative approach** (all platforms):
```bash
# Port-forward to localhost
kubectl port-forward -n nexus svc/api-gateway 8080:8080
# Access at http://localhost:8080
```

**Kafka/Messaging issues?**
```bash
# Check Kafka logs
kubectl logs -n nexus deployment/kafka

# Verify Zookeeper if using legacy mode
kubectl logs -n nexus deployment/zookeeper
```
- Ensure Kafka deployment matches your mode (KRaft vs Zookeeper)
- Verify notification-service can connect to Kafka brokers

**MongoDB connection failures?**
- Double-check connection string format in ConfigMaps/Secrets
- Ensure MongoDB Atlas IP whitelist includes `0.0.0.0/0` for testing
- Verify network policies allow outbound connections

**Need more help?**
- Check [existing issues](https://github.com/therishuraj/Nexus-CSCP/issues)
- Open a [new issue](https://github.com/therishuraj/Nexus-CSCP/issues/new/choose) with logs and details
- Join [Discussions](https://github.com/therishuraj/Nexus-CSCP/discussions) for questions

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

## Contributors

We are grateful to all the contributors who have helped build and improve Nexus:

| Name | Email | GitHub |
|------|-------|--------|
| Rishu Raj | rishurajsalarpur@gmail.com | [@therishuraj](https://github.com/therishuraj) |
| Mdataa Khan | trendsandfactss@gmail.com | [@mdataakhan](https://github.com/mdataakhan) |
| Dheeraj Salunkhe | 2024sl93042@wilp.bits-pilani.ac.in | [@SalunkheDheeraj](https://github.com/SalunkheDheeraj) |
| Sagar Dev | sagar.dev.lab@gmail.com | [@sagardevlab](https://github.com/sagardevlab) |
| Tushar Trivedi | tushar.trivedi@gmail.com | [@tushar-trivedi](https://github.com/tushar-trivedi) |

We welcome contributions from the community! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## License

Licensed under the Apache License, Version 2.0. See LICENSE for details.
