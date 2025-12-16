# Changelog

All notable changes to NexusCart will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure with multi-module Maven setup
- Parent POM with Spring Boot 3.2.1 and Spring Cloud 2023.0.0
- Docker Compose configuration for local development
- GitHub Actions CI/CD pipelines
- Common libraries (common-dto, common-security, common-kafka)
- Infrastructure services (config-server, discovery-server, api-gateway)
- Business services POMs (auth, user, product, inventory, order, payment, notification)
- Prometheus and Grafana monitoring setup
- Project documentation (README, CONTRIBUTING guidelines)

### Infrastructure
- PostgreSQL 16 with multi-database initialization
- Apache Kafka with Zookeeper
- Redis for caching
- Zipkin for distributed tracing

## [1.0.0] - TBD

### Planned
- Complete microservices implementation
- Angular frontend
- Kubernetes deployment manifests
- GCP Terraform infrastructure
