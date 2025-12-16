#!/bin/bash
# Setup script for local development environment

set -e

echo "🚀 NexusCart Local Setup"
echo "========================"

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21."
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.9+."
    exit 1
fi

if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker."
    exit 1
fi

echo "✅ All prerequisites met!"

# Start infrastructure
echo ""
echo "🐳 Starting infrastructure services..."
docker-compose up -d postgres kafka zookeeper redis

echo "⏳ Waiting for services to be healthy..."
sleep 30

# Build backend
echo ""
echo "🔨 Building backend services..."
cd backend
mvn clean install -DskipTests

echo ""
echo "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "  1. Start Config Server:    mvn spring-boot:run -pl config-server"
echo "  2. Start Discovery Server: mvn spring-boot:run -pl discovery-server"
echo "  3. Start API Gateway:      mvn spring-boot:run -pl api-gateway"
echo ""
echo "Access points:"
echo "  - Eureka:  http://localhost:8761"
echo "  - Gateway: http://localhost:8080"
echo "  - Kafdrop: http://localhost:9000"
