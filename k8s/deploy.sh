#!/bin/bash

echo "🚀 Deploying Nexus Platform to Kubernetes..."
echo ""

# Create namespace
echo "📦 Creating namespace..."
kubectl apply -f namespace.yaml
sleep 2

# Deploy services
echo ""
echo "🔧 Deploying microservices..."
kubectl apply -f user-service-deployment.yaml
kubectl apply -f product-service-deployment.yaml
kubectl apply -f investment-service-deployment.yaml
kubectl apply -f payment-service-deployment.yaml
kubectl apply -f order-service-deployment.yaml

echo ""
echo "⏳ Waiting for services to be ready (90 seconds)..."
sleep 90

# Deploy API Gateway
echo ""
echo "🌐 Deploying API Gateway..."
kubectl apply -f api-gateway-deployment.yaml

echo ""
echo "⏳ Waiting for API Gateway (60 seconds)..."
sleep 60

# Show status
echo ""
echo "✅ Deployment Status:"
kubectl get pods -n nexus
echo ""
kubectl get svc -n nexus

echo ""
echo "🎉 Deployment Complete!"
echo ""
echo "🌍 Access your API Gateway at:"
minikube service api-gateway -n nexus --url
