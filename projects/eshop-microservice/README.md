# E-Shop Microservice Backend

A comprehensive e-commerce backend application built using Spring Boot microservices architecture with Spring Cloud components.

## 🏗️ Architecture Overview

This application follows a microservices architecture pattern with the following components:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │ Service Registry│    │ Config Server   │
│   (Port: 8080)  │    │   (Port: 8761)  │    │  (Port: 9000)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
    ┌────────────────────────────┼────────────────────────────┐
    │                            │                            │
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Product   │    │  Category   │    │    Order    │    │   Payment   │
│  Service    │    │   Service   │    │   Service   │    │   Service   │
│(Port: 9002) │    │(Port: 9004) │    │(Port: 9001) │    │(Port: 9003) │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## 📋 Services Description

### 🌐 API Gateway
- **Port**: 8080
- **Purpose**: Single entry point for all client requests
- **Technology**: Spring Cloud Gateway
- **Routes**: 
  - `/api/product/**` → Product Service
  - `/api/category/**` → Category Service

### 🔍 Service Registry (Eureka Server)
- **Port**: 8761
- **Purpose**: Service discovery and registration
- **Technology**: Netflix Eureka
- **Dashboard**: http://localhost:8761

### ⚙️ Config Server
- **Port**: 9000
- **Purpose**: Centralized configuration management
- **Technology**: Spring Cloud Config
- **Profile**: Native (file-based configuration)

### 🛍️ Business Services

#### Product Service
- **Port**: 9002
- **Database**: `productdb` (MySQL)
- **Purpose**: Manages product catalog and inventory

#### Category Service
- **Port**: 9004
- **Database**: `categorydb` (MySQL)
- **Purpose**: Manages product categories and hierarchies

#### Order Service
- **Port**: 9001
- **Database**: `orderdb` (MySQL)
- **Purpose**: Handles order processing and management

#### Payment Service
- **Port**: 9003
- **Database**: `paymentdb` (MySQL)
- **Purpose**: Processes payments and transactions

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Java Version**: 25
- **Spring Cloud**: 2025.0.0
- **Database**: MySQL
- **ORM**: Spring Data JPA with Hibernate
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Build Tool**: Maven
- **Additional Libraries**: Lombok, Project Reactor

## 🚀 Getting Started

### Prerequisites

- Java 25 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Database Setup

1. Install and start MySQL server
2. Create the following databases (they will be auto-created if not exist):
   ```sql
   CREATE DATABASE productdb;
   CREATE DATABASE categorydb;
   CREATE DATABASE orderdb;
   CREATE DATABASE paymentdb;
   ```
3. Update database credentials in config files if needed (default: root/root)

### Running the Application

Start the services in the following order:

1. **Service Registry (Eureka Server)**
   ```bash
   cd service-registry
   mvn spring-boot:run
   ```
   Wait for startup, then verify at: http://localhost:8761

2. **Config Server**
   ```bash
   cd config-server
   mvn spring-boot:run
   ```

3. **Business Services** (can be started in parallel)
   ```bash
   # Terminal 1
   cd product-service
   mvn spring-boot:run

   # Terminal 2
   cd category-service
   mvn spring-boot:run

   # Terminal 3
   cd order-service
   mvn spring-boot:run

   # Terminal 4
   cd payment-service
   mvn spring-boot:run
   ```

4. **API Gateway**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

### Verification

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway Health**: http://localhost:8080/actuator/health
- **Product Service**: http://localhost:8080/api/product/
- **Category Service**: http://localhost:8080/api/category/

## 📡 API Endpoints

### Through API Gateway (Recommended)

| Service | Endpoint | Description |
|---------|----------|-------------|
| Product | `GET /api/product/` | Get all products |
| Product | `GET /api/product/{id}` | Get product by ID |
| Product | `POST /api/product/` | Create new product |
| Product | `PUT /api/product/{id}` | Update product |
| Product | `DELETE /api/product/{id}` | Delete product |
| Category | `GET /api/category/` | Get all categories |
| Category | `GET /api/category/{id}` | Get category by ID |
| Category | `POST /api/category/` | Create new category |
| Category | `PUT /api/category/{id}` | Update category |
| Category | `DELETE /api/category/{id}` | Delete category |

### Direct Service Access (Development)

- **Product Service**: http://localhost:9002
- **Category Service**: http://localhost:9004
- **Order Service**: http://localhost:9001
- **Payment Service**: http://localhost:9003

## 🔧 Configuration

### Centralized Configuration

All service configurations are managed through the Config Server located in:
```
config-server/src/main/resources/config/
├── api-gateway.yaml
├── category-service.yaml
├── order-service.yaml
├── payment-service.yaml
└── product-service.yaml
```

### Environment-specific Configurations

Configurations can be environment-specific by using profiles:
- `application-dev.yaml`
- `application-prod.yaml`
- `application-test.yaml`

## 🏗️ Project Structure

```
eshop-microservice/
├── api-gateway/           # API Gateway service
├── service-registry/      # Eureka Server
├── config-server/         # Configuration server
├── product-service/       # Product management
├── category-service/      # Category management
├── order-service/         # Order processing
├── payment-service/       # Payment processing
└── README.md             # This file
```

## 🔄 Development Workflow

### Adding a New Service

1. Create new Spring Boot module
2. Add Eureka client dependency
3. Configure in `application.yaml`:
   ```yaml
   spring:
     application:
       name: YOUR-SERVICE-NAME
     config:
       import: "optional:configserver:http://localhost:9000"
   ```
4. Add service configuration in config-server
5. Update API Gateway routes if needed

### Database Migration

The application uses Hibernate's `ddl-auto: update` for automatic schema updates. For production, consider using Flyway or Liquibase for version-controlled migrations.

## 🚧 Development Status

### ✅ Implemented
- [x] Service Registry (Eureka)
- [x] Config Server
- [x] API Gateway with routing
- [x] Product Service foundation
- [x] Category Service foundation
- [x] Order Service foundation
- [x] Payment Service foundation

### 🔄 In Progress
- [ ] Business logic implementation
- [ ] API endpoint development
- [ ] Data models and relationships
- [ ] Service-to-service communication

### 📝 TODO
- [ ] Authentication and Authorization
- [ ] Circuit breaker pattern
- [ ] Distributed tracing
- [ ] API documentation (Swagger)
- [ ] Unit and integration tests
- [ ] Docker containerization
- [ ] Monitoring and logging
- [ ] Production deployment configs

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team

## 🔗 Related Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

---

**Happy Coding! 🚀**