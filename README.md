# NexusCart 🛒

A production-grade **Real-Time Order Processing & Inventory Management System** built with microservices architecture, demonstrating Java development, Kafka event streaming, and DevOps best practices.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway (8080)                        │
│                    JWT Validation + Rate Limiting                │
└───────────────────────────────┬─────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────────┐
│ Auth Service │      │User Service  │      │ Product Service  │
│    (8081)    │      │   (8082)     │      │     (8083)       │
└──────────────┘      └──────────────┘      └──────────────────┘
        │                                           │
        │           ┌──────────────┐                │
        │           │  Inventory   │                │
        └──────────▶│   Service    │◀───────────────┘
                    │   (8084)     │
                    └──────┬───────┘
                           │
┌──────────────┐           ▼           ┌──────────────────┐
│   Payment    │   ┌──────────────┐   │   Notification   │
│   Service    │◀──│Order Service │──▶│     Service      │
│   (8086)     │   │   (8085)     │   │     (8087)       │
└──────────────┘   └──────────────┘   └──────────────────┘
                           │
                    ┌──────▼───────┐
                    │ Apache Kafka │
                    └──────────────┘
```

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.2.x |
| Frontend | Angular 17+ |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Messaging | Apache Kafka |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Containerization | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Monitoring | Prometheus + Grafana |

## 📁 Project Structure

```
nexuscart/
├── backend/                    # All Java Microservices
│   ├── common/                 # Shared libraries
│   │   ├── common-dto/
│   │   ├── common-security/
│   │   └── common-kafka/
│   ├── config-server/
│   ├── discovery-server/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── user-service/
│   ├── product-service/
│   ├── inventory-service/
│   ├── order-service/
│   ├── payment-service/
│   └── notification-service/
├── frontend/                   # Angular Application
│   └── nexuscart-web/
├── infrastructure/             # DevOps
│   ├── docker/
│   ├── k8s/
│   └── terraform/
└── docs/                       # Documentation
```

## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven 3.9+
- Docker & Docker Compose
- Node.js 20+ (for frontend)

### Local Development

1. **Start Infrastructure**
   ```bash
   docker-compose up -d postgres kafka redis
   ```

2. **Build Backend**
   ```bash
   cd backend
   mvn clean install -DskipTests
   ```

3. **Start Services** (in order)
   ```bash
   # 1. Config Server
   mvn spring-boot:run -pl config-server
   
   # 2. Discovery Server
   mvn spring-boot:run -pl discovery-server
   
   # 3. API Gateway + Other Services
   mvn spring-boot:run -pl api-gateway
   ```

4. **Start Frontend**
   ```bash
   cd frontend/nexuscart-web
   npm install
   ng serve
   ```

### Access Points

| Service | URL |
|---------|-----|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Angular App | http://localhost:4200 |
| Kafdrop (Kafka UI) | http://localhost:9000 |
| Grafana | http://localhost:3000 |
| pgAdmin | http://localhost:5050 |

## 🔐 Authentication

The system uses **Custom JWT** authentication:

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 900
}
```

## 📊 Kafka Topics

| Topic | Producer | Consumers |
|-------|----------|-----------|
| `order.created` | Order Service | Inventory, Notification |
| `inventory.reserved` | Inventory Service | Order Service |
| `payment.completed` | Payment Service | Order, Notification |
| `order.completed` | Order Service | Notification |

## 🌿 Git Workflow

This project uses **GitFlow**:

- `main` - Production-ready code
- `develop` - Integration branch
- `feature/*` - New features
- `release/*` - Release preparation
- `hotfix/*` - Emergency fixes

## 📜 License

MIT License - see [LICENSE](LICENSE) for details.

---

Built with ❤️ for learning and portfolio demonstration.
