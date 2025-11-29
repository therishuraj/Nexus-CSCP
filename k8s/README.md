# Nexus Kubernetes Deployment

## What's Included

Complete Kubernetes deployment with **ALL services including Kafka**.

**Services:**
- ✅ user-service (2 replicas)
- ✅ product-service (2 replicas)
- ✅ investment-service (2 replicas)
- ✅ payment-service (2 replicas)
- ✅ order-service (2 replicas)
- ✅ api-gateway (2 replicas, NodePort 30080)
- ✅ kafka (1 replica, Apache Kafka 3.7.0 in KRaft mode)
- ✅ notification-service (1 replica)
- ✅ producer (1 replica)

**Total: 15 pods running**

## Prerequisites

```bash
# 1. Start Minikube with sufficient resources
minikube start --cpus=4 --memory=7168

# 2. Build all Docker images in Minikube context
eval $(minikube docker-env)

# Build service images
docker build -t user-service:latest ./user-service
docker build -t product-service:latest ./product-service
docker build -t investment-service:latest ./investment-service
docker build -t payment-service:latest ./payment-service
docker build -t order-service:latest ./order-service
docker build -t api-gateway:latest ./API-Gateway
docker build -t notification-service:latest ./notification-service
docker build -t producer:latest ./producer

# 3. Verify images
docker images | grep -E "user-service|product-service|investment-service|payment-service|order-service|api-gateway|notification-service|producer"
```

## Deployment Steps

### 1. Create Namespace and Deploy Core Services

```bash
cd k8s

# Create namespace
kubectl apply -f namespace.yaml

# Deploy microservices
kubectl apply -f user-service-deployment.yaml
kubectl apply -f product-service-deployment.yaml
kubectl apply -f investment-service-deployment.yaml
kubectl apply -f payment-service-deployment.yaml
kubectl apply -f order-service-deployment.yaml

# Wait for services to be ready
sleep 90
```

### 2. Deploy API Gateway

```bash
kubectl apply -f api-gateway-deployment.yaml
sleep 60
```

### 3. Deploy Kafka Stack

```bash
# Deploy Kafka (KRaft mode - no Zookeeper needed)
kubectl apply -f kafka-deployment.yaml
sleep 90

# Deploy consumer and producer
kubectl apply -f notification-service-deployment.yaml
kubectl apply -f producer-deployment.yaml
sleep 60
```

### 4. Verify Deployment

```bash
# Check all pods are running
kubectl get pods -n nexus

# Expected output: All 15 pods in Running state
# - 2x user-service
# - 2x product-service
# - 2x investment-service
# - 2x payment-service
# - 2x order-service
# - 2x api-gateway
# - 1x kafka
# - 1x notification-service
# - 1x producer

# Check services
kubectl get svc -n nexus
```

## Access Application

### Get API Gateway URL

```bash
minikube service api-gateway -n nexus --url
```

This will output a URL like: `http://127.0.0.1:xxxxx`

**Keep this terminal open** - it creates a tunnel to the service.

### Test Endpoints

```bash
# Replace <URL> with the URL from above command

# Get all users
curl <URL>/nexus/api/v1/users

# Get all products
curl <URL>/nexus/api/v1/products

# Get all orders
curl <URL>/nexus/api/v1/orders

# Health check
curl <URL>/actuator/health
```

### Use in Bruno/Postman

1. Run: `minikube service api-gateway -n nexus --url`
2. Copy the URL (e.g., `http://127.0.0.1:52844`)
3. Update your Bruno environment base URL to this URL
4. Keep the terminal running while testing

## Architecture Details

### Kafka Configuration
- **Mode**: KRaft (Kafka Raft) - no Zookeeper dependency
- **Image**: apache/kafka:3.7.0
- **Resources**: 512Mi memory, 250m CPU
- **Port**: 9092

### Resource Allocation
- **Microservices**: 512Mi request, 1Gi limit
- **API Gateway**: 512Mi request, 1Gi limit
- **Kafka**: 512Mi request, 1Gi limit
- **Consumer/Producer**: 256Mi request, 512Mi limit

### Why KRaft Mode?
- Simpler deployment (no Zookeeper needed)
- Better resource efficiency
- Production-ready since Kafka 3.3+

## Troubleshooting

### Pods Not Starting

```bash
# Check pod status
kubectl get pods -n nexus

# Check specific pod logs
kubectl logs -n nexus <pod-name>

# Describe pod for events
kubectl describe pod -n nexus <pod-name>
```

### Out of Memory

```bash
# Delete and restart Minikube with more memory
minikube delete
minikube start --cpus=4 --memory=8192

# Rebuild images and redeploy
```

### Kafka Issues

```bash
# Check Kafka logs
kubectl logs -n nexus -l app=kafka

# Verify Kafka is running
kubectl get pods -n nexus -l app=kafka

# Test Kafka connection from consumer
kubectl logs -n nexus -l app=notification-service
```

## Cleanup

```bash
# Delete entire namespace (removes all resources)
kubectl delete namespace nexus

# Or delete individual components
kubectl delete -f notification-service-deployment.yaml
kubectl delete -f producer-deployment.yaml
kubectl delete -f kafka-deployment.yaml
kubectl delete -f api-gateway-deployment.yaml
kubectl delete -f order-service-deployment.yaml
kubectl delete -f payment-service-deployment.yaml
kubectl delete -f investment-service-deployment.yaml
kubectl delete -f product-service-deployment.yaml
kubectl delete -f user-service-deployment.yaml
kubectl delete -f namespace.yaml
```

## Notes

- All services connect to MongoDB Atlas (cloud-hosted)
- Kafka runs in single-node mode for development
- API Gateway uses Spring Cloud Gateway for routing
- All inter-service communication uses ClusterIP services
- External access via NodePort on API Gateway (30080)
