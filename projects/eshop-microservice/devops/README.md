# 🚀 E-Shop Microservices DevOps Setup

Complete CI/CD pipeline for E-Shop microservices with Jenkins, SonarQube, and Docker.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [Detailed Setup](#detailed-setup)
- [Project Structure](#project-structure)
- [CI/CD Pipeline](#cicd-pipeline)
- [Common Tasks](#common-tasks)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This DevOps setup provides:

- ✅ **Automated builds** - Jenkins detects changes and builds only affected services
- ✅ **Code quality checks** - SonarQube analyzes code for bugs, vulnerabilities, and code smells
- ✅ **Parallel execution** - Multiple services build simultaneously for faster pipelines
- ✅ **Docker containerization** - All services packaged as portable containers
- ✅ **Orchestrated deployment** - docker-compose manages all services
- ✅ **Training materials** - Comprehensive guides for learning DevOps concepts

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     CI/CD PIPELINE                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Developer → Git Push → Jenkins → Build & Test             │
│                            ↓                                │
│                        SonarQube (Quality Check)            │
│                            ↓                                │
│                        Docker Build                         │
│                            ↓                                │
│                     docker-compose up                       │
│                            ↓                                │
│                  🚀 Services Running                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Microservices

```
                    ┌─────────────────┐
                    │  API Gateway    │
                    │    (8080)       │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼───────┐    ┌──────▼──────┐    ┌───────▼────────┐
│ Auth Service  │    │  Product    │    │ Order Service  │
│    (8081)     │    │  Service    │    │    (8082)      │
└───────────────┘    │   (8084)    │    └────────────────┘
                     └─────────────┘
        │                    │                    │
        └────────────────────┴────────────────────┘
                             │
                     ┌───────▼────────┐
                     │     MySQL      │
                     │    (3306)      │
                     └────────────────┘
```

---

## ⚡ Quick Start

### Prerequisites

Ensure you have installed:
- ☑️ Java 21
- ☑️ Maven 3.9+
- ☑️ Docker Desktop
- ☑️ Git

### 1. Start Infrastructure Services

```bash
# Start SonarQube and MySQL
cd devops/docker
docker-compose up -d mysql sonarqube

# Wait for services to be ready (~30 seconds)
docker-compose logs -f sonarqube mysql
```

### 2. Build All Services

```bash
# From project root
./devops/scripts/build-all.sh
```

### 3. Create Docker Images

```bash
# Build all Docker images
cd devops/docker
docker-compose build
```

### 4. Start All Services

```bash
# Start everything
./devops/scripts/start-services.sh

# Or manually
cd devops/docker
docker-compose up -d
```

### 5. Verify Services

- **Service Registry:** http://localhost:8761
- **API Gateway:** http://localhost:8080
- **SonarQube:** http://localhost:9000 (admin/admin)

---

## 📖 Detailed Setup

### Step 1: Infrastructure Setup

#### Install Jenkins

Follow the comprehensive guide: [devops/docs/JENKINS_SETUP.md](./docs/JENKINS_SETUP.md)

**Quick start with Docker:**
```bash
docker run -d \
  --name jenkins \
  -p 8090:8080 \
  -p 50000:50000 \
  -v jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts

# Get initial password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

#### Install SonarQube

Follow the comprehensive guide: [devops/docs/SONARQUBE_SETUP.md](./docs/SONARQUBE_SETUP.md)

**Quick start with Docker:**
```bash
cd devops/docker
docker-compose up -d sonarqube postgres

# Access at http://localhost:9000
# Login: admin/admin
```

### Step 2: Configure Services

#### Configure Jenkins

1. Install required plugins:
   - Pipeline
   - Git
   - Docker Pipeline
   - SonarQube Scanner
   - Maven Integration

2. Configure tools:
   - JDK 21
   - Maven 3.9
   - Docker
   - SonarQube Scanner

3. Create pipeline job:
   - New Item → Pipeline
   - Name: `eshop-microservices-pipeline`
   - Pipeline from SCM → Git
   - Repository URL: `[your-repo-url]`
   - Script Path: `Jenkinsfile`

#### Configure SonarQube

1. Login to SonarQube (admin/admin)
2. Change admin password
3. Generate token: My Account → Security → Generate Token
4. Configure Jenkins:
   - Manage Jenkins → Configure System → SonarQube servers
   - Name: `SonarQube`
   - Server URL: `http://localhost:9000`
   - Add token as credential

### Step 3: Run First Build

#### Option A: Jenkins Pipeline

```
1. Open Jenkins: http://localhost:8090
2. Click on "eshop-microservices-pipeline"
3. Click "Build Now"
4. Watch the pipeline execute
```

#### Option B: Manual Local Build

```bash
# Build and test a single service
cd auth-jwt-service
./mvnw clean package
./mvnw test
./mvnw sonar:sonar \
  -Dsonar.projectKey=auth-jwt-service \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN

# Build Docker image
docker build -t auth-jwt-service:latest .
```

---

## 📁 Project Structure

```
eshop-microservice/
│
├── Jenkinsfile                      # Main CI/CD pipeline
│
├── devops/                          # All DevOps configurations
│   │
│   ├── docker/                      # Docker configurations
│   │   ├── docker-compose.yml       # Orchestrates all services
│   │   └── .env                     # Environment variables
│   │
│   ├── jenkins/                     # Jenkins configurations
│   │   └── shared-pipeline.groovy   # Reusable pipeline functions
│   │
│   ├── scripts/                     # Helper scripts
│   │   ├── build-all.sh            # Build all services
│   │   ├── start-services.sh       # Start all services
│   │   └── cleanup.sh              # Clean Docker resources
│   │
│   ├── docs/                        # Documentation
│   │   ├── JENKINS_SETUP.md        # Jenkins installation guide
│   │   ├── SONARQUBE_SETUP.md      # SonarQube installation guide
│   │   └── DOCKER_GUIDE.md         # Docker reference
│   │
│   └── training/                    # Learning materials
│       └── DEVOPS_TRAINING.md      # Comprehensive DevOps guide
│
├── auth-jwt-service/                # Microservices
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── order-service/
├── payment-service/
├── product-service/
├── category-service/
├── api-gateway/
└── service-registry/
```

---

## 🔄 CI/CD Pipeline

### Pipeline Stages

```
┌──────────────────────────────────────────────────────────────┐
│ 1. INITIALIZE                                                │
│    - Checkout code from Git                                  │
│    - Set up workspace                                        │
└────────────────────┬─────────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────────┐
│ 2. DETECT CHANGES                                            │
│    - Compare with main branch                                │
│    - Identify changed services                               │
│    - Only rebuild what's necessary                           │
└────────────────────┬─────────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────────┐
│ 3. BUILD & TEST (Parallel)                                   │
│                                                               │
│    ┌──────────┐  ┌──────────┐  ┌──────────┐                │
│    │ Service1 │  │ Service2 │  │ Service3 │                │
│    ├──────────┤  ├──────────┤  ├──────────┤                │
│    │ • Build  │  │ • Build  │  │ • Build  │                │
│    │ • Test   │  │ • Test   │  │ • Test   │                │
│    │ • Sonar  │  │ • Sonar  │  │ • Sonar  │                │
│    │ • Docker │  │ • Docker │  │ • Docker │                │
│    └──────────┘  └──────────┘  └──────────┘                │
└────────────────────┬─────────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────────┐
│ 4. QUALITY GATE                                              │
│    - Wait for SonarQube analysis                             │
│    - Check quality standards                                 │
│    - Fail if standards not met                               │
└────────────────────┬─────────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────────┐
│ 5. DEPLOY                                                    │
│    - Start with docker-compose                               │
│    - Or push to registry                                     │
└──────────────────────────────────────────────────────────────┘
```

### Smart Change Detection

The pipeline only rebuilds services that have changes:

```bash
# Example: Only auth-service changed
git diff --name-only origin/main...HEAD
# Output: auth-jwt-service/src/main/java/...

# Result:
✅ auth-jwt-service → Build
❌ order-service → Skip
❌ payment-service → Skip
❌ product-service → Skip
```

**Benefits:**
- Faster builds (only 2-3 minutes instead of 15+ minutes)
- Less resource usage
- Quicker feedback to developers

---

## 🛠️ Common Tasks

### Building Services

```bash
# Build all services
./devops/scripts/build-all.sh

# Build single service
cd auth-jwt-service
./mvnw clean package

# Build with tests
./mvnw clean install

# Build without tests (faster)
./mvnw clean package -DskipTests
```

### Working with Docker

```bash
# Build all images
cd devops/docker
docker-compose build

# Build specific service
docker-compose build auth-jwt-service

# Start all services
docker-compose up -d

# Start specific services
docker-compose up -d mysql sonarqube service-registry

# View logs
docker-compose logs -f                    # All services
docker-compose logs -f auth-jwt-service   # Specific service

# Check status
docker-compose ps

# Restart a service
docker-compose restart auth-jwt-service

# Stop all services
docker-compose down

# Stop and remove volumes (deletes data!)
docker-compose down -v
```

### Code Quality Analysis

```bash
# Run SonarQube analysis for a service
cd auth-jwt-service
./mvnw clean test sonar:sonar \
  -Dsonar.projectKey=auth-jwt-service \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN

# View results
# Open: http://localhost:9000
```

### Running Tests

```bash
# Run tests for a service
cd auth-jwt-service
./mvnw test

# Run specific test
./mvnw test -Dtest=UserServiceTest

# Run tests with coverage
./mvnw clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Jenkins Pipeline

```bash
# Trigger build via CLI
curl -X POST http://localhost:8090/job/eshop-microservices-pipeline/build \
  --user admin:YOUR_TOKEN

# Check build status
curl http://localhost:8090/job/eshop-microservices-pipeline/lastBuild/api/json \
  --user admin:YOUR_TOKEN
```

---

## 🔍 Troubleshooting

### Services Won't Start

**Problem:** Container exits immediately

```bash
# Check logs
docker-compose logs SERVICE_NAME

# Common issues:
# 1. Port already in use
lsof -i :8080
kill -9 PID

# 2. Database not ready
docker-compose logs mysql | grep "ready for connections"

# 3. Service can't connect to registry
# Wait for service-registry to be healthy first
docker-compose ps service-registry
```

### Build Fails

**Problem:** Maven build errors

```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
./mvnw clean install -U

# Check Java version
java -version  # Should be Java 21

# Check Maven version
./mvnw --version
```

### SonarQube Issues

**Problem:** Quality gate fails

```bash
# Check what failed
# Open: http://localhost:9000/dashboard?id=SERVICE_NAME

# Common issues:
# 1. Low test coverage → Write more tests
# 2. Code smells → Refactor code
# 3. Duplications → Extract common code

# Temporarily lower quality gate (for learning)
# SonarQube → Quality Gates → Create lenient gate
```

### Jenkins Connection Issues

**Problem:** Jenkins can't connect to SonarQube/Docker

```bash
# If Jenkins is in Docker:
# Use host.docker.internal instead of localhost
# Example:
# http://host.docker.internal:9000

# Or add to same network:
docker network create devops-network
docker network connect devops-network jenkins
docker network connect devops-network sonarqube
# Then use: http://sonarqube:9000
```

---

## 📚 Learning Resources

### Getting Started

1. **[DevOps Training Guide](./training/DEVOPS_TRAINING.md)**
   - Complete beginner-friendly guide
   - Explains all concepts with examples
   - Step-by-step tutorials

2. **[Jenkins Setup Guide](./docs/JENKINS_SETUP.md)**
   - Installation on macOS/Linux/Docker
   - Configuration walkthroughs
   - Troubleshooting tips

3. **[SonarQube Setup Guide](./docs/SONARQUBE_SETUP.md)**
   - Installation and configuration
   - Understanding quality metrics
   - Integrating with Jenkins

### Key Concepts to Learn

- **CI/CD:** Continuous Integration & Deployment
- **Microservices:** Distributed system architecture
- **Docker:** Containerization platform
- **Jenkins:** Automation server
- **SonarQube:** Code quality platform
- **Service Discovery:** Dynamic service location
- **API Gateway:** Single entry point pattern

---

## ✅ Checklist

### Initial Setup

- [ ] Java 21 installed
- [ ] Maven 3.9+ installed
- [ ] Docker Desktop running
- [ ] Jenkins installed and configured
- [ ] SonarQube installed and configured
- [ ] All services have Dockerfiles
- [ ] docker-compose.yml configured
- [ ] Jenkinsfile in repository root

### Verification

- [ ] All services build locally
- [ ] Docker images build successfully
- [ ] Services start with docker-compose
- [ ] All services register in Eureka
- [ ] SonarQube analysis runs
- [ ] Jenkins pipeline executes successfully
- [ ] Quality gates pass

---

## 🎯 Next Steps

1. ✅ Complete infrastructure setup (Jenkins + SonarQube)
2. ✅ Build all services locally
3. ✅ Create Docker images
4. ✅ Start services with docker-compose
5. ➡️ Configure Jenkins pipeline
6. ➡️ Run first automated build
7. ➡️ Fix code quality issues
8. ➡️ Set up CI/CD for your team

---

## 🤝 Contributing

When adding a new microservice:

1. Create service directory with standard structure
2. Add Dockerfile
3. Update `docker-compose.yml`
4. Update `Jenkinsfile` microservices list
5. Add service to helper scripts
6. Test locally before pushing

---

## 📞 Support

- **DevOps Training:** See [devops/training/DEVOPS_TRAINING.md](./training/DEVOPS_TRAINING.md)
- **Jenkins Issues:** See [devops/docs/JENKINS_SETUP.md](./docs/JENKINS_SETUP.md)
- **SonarQube Issues:** See [devops/docs/SONARQUBE_SETUP.md](./docs/SONARQUBE_SETUP.md)
- **Docker Issues:** `docker-compose logs -f SERVICE_NAME`

---

## 📝 License

This DevOps setup is part of the E-Shop Microservices project.

---

**Happy Building! 🚀**

Remember: DevOps is a journey, not a destination. Start simple, automate gradually, and keep learning!
