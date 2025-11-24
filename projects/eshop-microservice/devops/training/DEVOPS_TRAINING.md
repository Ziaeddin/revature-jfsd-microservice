# 🎓 DevOps Pipeline Training Guide

Welcome! This guide will help you understand and implement a complete DevOps pipeline for the E-Shop microservices project.

---

## 📚 Table of Contents

1. [What is DevOps?](#what-is-devops)
2. [Key Concepts Explained](#key-concepts-explained)
3. [Project Architecture](#project-architecture)
4. [Step-by-Step Implementation](#step-by-step-implementation)
5. [Common Commands Reference](#common-commands-reference)
6. [Troubleshooting](#troubleshooting)

---

## 🤔 What is DevOps?

**DevOps** is a combination of **Development** and **Operations** practices that aims to:
- Automate software delivery
- Improve collaboration between developers and operations teams
- Deploy code faster and more reliably
- Catch bugs early through automated testing

### The DevOps Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│                    DEVOPS LIFECYCLE                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  PLAN → CODE → BUILD → TEST → RELEASE → DEPLOY → MONITOR  │
│    │      │      │       │        │        │         │     │
│    └──────┴──────┴───────┴────────┴────────┴─────────┘     │
│                    (Continuous Loop)                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔑 Key Concepts Explained

### 1. Continuous Integration (CI)

**What it is:** Automatically building and testing code every time developers make changes.

**Why it matters:**
- Catches bugs early
- Ensures code always compiles
- Reduces integration problems

**In our pipeline:**
- Jenkins automatically builds services when code changes
- Tests run on every build
- SonarQube checks code quality

### 2. Continuous Deployment (CD)

**What it is:** Automatically deploying tested code to production.

**Why it matters:**
- Faster delivery to users
- Less manual work
- Consistent deployment process

**In our pipeline:**
- Docker images are automatically created
- Services can be deployed with one command
- docker-compose orchestrates all services

### 3. Microservices Architecture

**What it is:** Breaking an application into small, independent services.

**Our Services:**
```
┌─────────────────────────────────────────────────────────┐
│                     API GATEWAY                         │
│                      (Port 8080)                        │
└──────────────┬──────────────────────────────────────────┘
               │
       ┌───────┴────────┬─────────────┬──────────────┐
       │                │             │              │
┌──────▼──────┐  ┌─────▼─────┐ ┌────▼─────┐  ┌────▼──────┐
│   Auth      │  │  Product  │ │ Order    │  │  Payment  │
│ Service     │  │  Service  │ │ Service  │  │  Service  │
│ (8081)      │  │  (8084)   │ │ (8082)   │  │  (8083)   │
└─────────────┘  └───────────┘ └──────────┘  └───────────┘
       │                │             │              │
       └────────────────┴─────────────┴──────────────┘
                        │
                 ┌──────▼──────┐
                 │   MySQL     │
                 │  Database   │
                 │   (3306)    │
                 └─────────────┘
```

**Benefits:**
- Each service can be developed independently
- Scale individual services as needed
- Deploy changes without affecting other services
- Use different technologies for different services

### 4. Docker Containers

**What it is:** A lightweight, portable package containing your application and all its dependencies.

**Think of it like this:**
- **Without Docker:** "It works on my machine!" 🤷‍♂️
- **With Docker:** "It works everywhere!" ✅

**Key Components:**
- **Dockerfile:** Recipe for building a container image
- **Image:** Template for creating containers (like a class in OOP)
- **Container:** Running instance of an image (like an object in OOP)

**Our Dockerfile Strategy:**
```dockerfile
# Multi-stage build (reduces image size)

# Stage 1: Build the JAR
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline    # Download dependencies
COPY src ./src
RUN mvn clean package -DskipTests  # Build JAR

# Stage 2: Create runtime image
FROM eclipse-temurin:21-jre-alpine  # Smaller base image
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar  # Only copy JAR
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Why multi-stage?**
- Stage 1 includes Maven (large) → used only for building
- Stage 2 includes only JRE (small) → used for running
- Final image is much smaller (saves disk space and network bandwidth)

### 5. Service Discovery (Eureka)

**What it is:** Automatic registration and discovery of microservices.

**The Problem:**
- Microservices need to communicate with each other
- IP addresses and ports might change
- Hard-coding addresses is brittle

**The Solution:**
```
1. Service starts up
2. Registers itself with Eureka: "I'm Order Service at 172.18.0.5:8082"
3. Other services ask Eureka: "Where is Order Service?"
4. Eureka responds with current location
5. Services communicate directly
```

**Benefits:**
- Dynamic service location
- Load balancing
- Health monitoring
- Automatic failover

### 6. API Gateway Pattern

**What it is:** Single entry point for all client requests.

**Why we need it:**
- **Without Gateway:** Clients call multiple services directly
  - Complex client code
  - CORS issues
  - Security challenges
  - Hard to change service structure

- **With Gateway:** All requests go through one point
  - Simple client code
  - Centralized security (JWT validation)
  - Request routing
  - Rate limiting, logging, monitoring

**Example Flow:**
```
Mobile App → API Gateway → Route to appropriate service
              (8080)        ↓
                           Auth Service (if /api/auth/*)
                           Product Service (if /api/products/*)
                           Order Service (if /api/orders/*)
```

### 7. Jenkins Pipeline

**What it is:** Automated workflow for building, testing, and deploying code.

**Our Pipeline Stages:**

```
┌──────────────────────────────────────────────────────────┐
│ STAGE 1: INITIALIZE                                      │
│ - Set up workspace                                       │
│ - Display build information                              │
└────────────────────┬─────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────┐
│ STAGE 2: DETECT CHANGES                                  │
│ - Compare with main branch                               │
│ - Identify which services changed                        │
│ - Only rebuild what's necessary (Smart Build!)           │
└────────────────────┬─────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────┐
│ STAGE 3: BUILD & TEST (Parallel!)                        │
│                                                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ Service1 │  │ Service2 │  │ Service3 │  ...         │
│  │  Build   │  │  Build   │  │  Build   │              │
│  │  Test    │  │  Test    │  │  Test    │              │
│  │  Docker  │  │  Docker  │  │  Docker  │              │
│  └──────────┘  └──────────┘  └──────────┘              │
└────────────────────┬─────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────┐
│ STAGE 4: QUALITY GATE                                    │
│ - Check SonarQube results                                │
│ - Fail build if quality standards not met                │
└────────────────────┬─────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────┐
│ STAGE 5: DEPLOY                                          │
│ - Start services with docker-compose                     │
│ - Or push images to registry                             │
└──────────────────────────────────────────────────────────┘
```

**Key Features:**
- **Parallel Execution:** Multiple services build at the same time
- **Smart Building:** Only rebuild changed services
- **Automatic Testing:** Tests run on every build
- **Quality Checks:** SonarQube ensures code quality

### 8. SonarQube Code Quality

**What it is:** Tool that analyzes code for bugs, vulnerabilities, and code smells.

**What it checks:**
- **Bugs:** Code that will cause errors
- **Vulnerabilities:** Security issues
- **Code Smells:** Bad practices that make code hard to maintain
- **Coverage:** How much code is tested
- **Duplications:** Copy-pasted code

**Quality Gates:**
- Set minimum standards (e.g., 80% test coverage)
- Build fails if standards not met
- Enforces code quality automatically

---

## 🏗️ Project Architecture

### Directory Structure

```
eshop-microservice/
├── Jenkinsfile                     # Main pipeline definition
├── README.md
│
├── devops/                         # All DevOps configurations
│   ├── docker/
│   │   ├── docker-compose.yml      # Orchestrates all services
│   │   └── .env                    # Environment variables
│   │
│   ├── jenkins/
│   │   └── shared-pipeline.groovy  # Reusable pipeline functions
│   │
│   ├── scripts/
│   │   ├── build-all.sh            # Build all services locally
│   │   ├── start-services.sh       # Start all services
│   │   └── cleanup.sh              # Clean up Docker resources
│   │
│   ├── training/                   # Learning materials
│   │   └── DEVOPS_TRAINING.md      # This file!
│   │
│   └── docs/                       # Documentation
│       ├── JENKINS_SETUP.md
│       ├── SONARQUBE_SETUP.md
│       └── DOCKER_GUIDE.md
│
├── auth-jwt-service/               # Each service has:
│   ├── Dockerfile                  # - Dockerfile for containerization
│   ├── pom.xml                     # - Maven build file
│   └── src/                        # - Source code
│
├── order-service/
├── payment-service/
├── product-service/
├── category-service/
├── api-gateway/
└── service-registry/
```

### Network Communication

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                       │
│                   (eshop-network)                       │
│                                                         │
│  All services communicate using service names:         │
│  - http://service-registry:8761                        │
│  - http://auth-jwt-service:8081                        │
│  - http://mysql:3306                                   │
│                                                         │
│  Benefits:                                             │
│  - No hardcoded IPs                                    │
│  - Automatic DNS resolution                            │
│  - Isolated from host network                          │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 Step-by-Step Implementation

### Prerequisites

Before starting, ensure you have:
- ✅ Java 21 installed
- ✅ Maven 3.9+ installed
- ✅ Docker Desktop installed and running
- ✅ Jenkins installed (see JENKINS_SETUP.md)
- ✅ SonarQube running (see SONARQUBE_SETUP.md)
- ✅ Git installed

### Step 1: Understanding Your Services

1. **Identify all microservices:**
   ```bash
   ls -d */ | grep -E "(service|gateway|registry)"
   ```

2. **Check each service has:**
   - `pom.xml` (Maven configuration)
   - `Dockerfile` (Container configuration)
   - `src/` directory (Source code)

3. **Understand dependencies:**
   - Service Registry must start first
   - Database must be ready before business services
   - API Gateway needs service registry

### Step 2: Building Services Locally

**Build a single service:**
```bash
cd auth-jwt-service
./mvnw clean package
# This creates: target/auth-jwt-service-0.0.1-SNAPSHOT.jar
```

**Build all services:**
```bash
# Option 1: Manual
for dir in *-service api-gateway service-registry; do
    echo "Building $dir..."
    cd $dir && ./mvnw clean package -DskipTests && cd ..
done

# Option 2: Use helper script
./devops/scripts/build-all.sh
```

### Step 3: Creating Docker Images

**Build image for one service:**
```bash
cd auth-jwt-service
docker build -t auth-jwt-service:latest .
```

**Build all images:**
```bash
# Using docker-compose (builds all at once)
cd devops/docker
docker-compose build
```

**Verify images:**
```bash
docker images | grep -E "(auth|order|payment|product|category|gateway|registry)"
```

### Step 4: Starting Services with Docker Compose

**Start all services:**
```bash
cd devops/docker
docker-compose up -d
# -d means "detached" (runs in background)
```

**Watch startup logs:**
```bash
docker-compose logs -f
# Press Ctrl+C to stop watching (services keep running)
```

**Check service health:**
```bash
docker-compose ps
# Shows status of all services
```

**Service startup order:**
```
1. MySQL (database)             ~10 seconds
2. SonarQube (code quality)     ~30 seconds
3. Service Registry (Eureka)    ~40 seconds
4. API Gateway                  ~60 seconds
5. Business Services            ~80 seconds
```

### Step 5: Verifying Everything Works

**Check Eureka Dashboard:**
- Open: http://localhost:8761
- All services should be registered

**Check API Gateway:**
- Open: http://localhost:8080/actuator/health
- Should return: `{"status":"UP"}`

**Check SonarQube:**
- Open: http://localhost:9000
- Login: admin/admin (first time)

**Test a service:**
```bash
# Test auth service through gateway
curl http://localhost:8080/api/auth/health

# Or directly
curl http://localhost:8081/actuator/health
```

### Step 6: Setting Up Jenkins Pipeline

1. **Create Jenkins job:**
   - Open Jenkins: http://localhost:8080 (if different from gateway port)
   - New Item → Pipeline
   - Name: "eshop-microservices-pipeline"

2. **Configure Source:**
   - Pipeline from SCM
   - Git repository URL
   - Branch: main
   - Script Path: Jenkinsfile

3. **Run the pipeline:**
   - Click "Build Now"
   - Watch console output
   - Pipeline will detect changes and build services

### Step 7: Understanding the Build Process

**What happens during a build:**

```
1. INITIALIZE
   - Jenkins checks out code
   - Sets up workspace

2. DETECT CHANGES
   - Compares with main branch
   - git diff --name-only origin/main...HEAD
   - Lists changed services

3. BUILD SERVICES (in parallel!)
   Service A          Service B          Service C
   ├─ Maven build     ├─ Maven build     ├─ Maven build
   ├─ Run tests       ├─ Run tests       ├─ Run tests
   ├─ SonarQube       ├─ SonarQube       ├─ SonarQube
   └─ Docker build    └─ Docker build    └─ Docker build

4. QUALITY GATE
   - Wait for SonarQube analysis
   - Check if standards met
   - Fail build if quality issues

5. DEPLOY
   - docker-compose up -d
   - Or push to registry
```

### Step 8: Making Changes and Rebuilding

**Typical development workflow:**

```bash
# 1. Create feature branch
git checkout -b feature/new-endpoint

# 2. Make changes to a service
cd auth-jwt-service
# Edit code...

# 3. Test locally
./mvnw test

# 4. Build locally
./mvnw clean package

# 5. Test with Docker
docker build -t auth-jwt-service:latest .
docker run -p 8081:8081 auth-jwt-service:latest

# 6. Commit and push
git add .
git commit -m "Add new authentication endpoint"
git push origin feature/new-endpoint

# 7. Jenkins automatically:
   - Detects changes in auth-jwt-service only
   - Builds only auth-jwt-service
   - Runs tests
   - Checks code quality
   - Creates new Docker image
```

---

## 📖 Common Commands Reference

### Docker Commands

```bash
# Build images
docker-compose build                    # Build all services
docker-compose build auth-jwt-service   # Build specific service

# Start/Stop services
docker-compose up -d                    # Start all in background
docker-compose down                     # Stop all services
docker-compose restart auth-jwt-service # Restart one service

# View logs
docker-compose logs -f                  # All services (follow)
docker-compose logs auth-jwt-service    # Specific service
docker-compose logs --tail=100 -f       # Last 100 lines, follow

# Check status
docker-compose ps                       # List all services
docker-compose top                      # Show running processes

# Execute commands in containers
docker-compose exec auth-jwt-service sh # Open shell in container
docker-compose exec mysql mysql -u root -p # Connect to MySQL

# Cleanup
docker-compose down -v                  # Stop and remove volumes
docker system prune -a                  # Remove unused Docker data
```

### Maven Commands

```bash
# Build
./mvnw clean package                   # Build JAR
./mvnw clean package -DskipTests       # Build without tests

# Test
./mvnw test                            # Run all tests
./mvnw test -Dtest=UserServiceTest     # Run specific test

# Code quality
./mvnw sonar:sonar                     # Run SonarQube analysis

# Dependencies
./mvnw dependency:tree                 # Show dependency tree
./mvnw dependency:resolve              # Download all dependencies
```

### Git Commands (for Jenkins)

```bash
# Check what changed
git diff --name-only origin/main...HEAD

# Check specific service
git diff --name-only origin/main...HEAD | grep auth-jwt-service

# View commit history
git log --oneline --graph --all
```

### Jenkins Pipeline Commands

```bash
# Inside Jenkinsfile, you can use:
sh './mvnw clean package'              # Run Maven
sh 'docker build -t service:latest .'  # Build Docker image
sh 'docker-compose up -d'              # Start services
```

---

## 🔧 Troubleshooting

### Problem: Service won't start

**Symptoms:** Container exits immediately

**Solutions:**
```bash
# 1. Check logs
docker-compose logs service-name

# 2. Check if port is already in use
lsof -i :8081  # Replace with your port
# Kill the process if needed
kill -9 PID

# 3. Rebuild the image
docker-compose build --no-cache service-name

# 4. Check environment variables
docker-compose config  # Shows resolved configuration
```

### Problem: Services can't connect to database

**Symptoms:** `Connection refused` or `Unknown host`

**Solutions:**
```bash
# 1. Check MySQL is running and healthy
docker-compose ps mysql
docker-compose logs mysql | grep "ready for connections"

# 2. Verify network
docker network ls
docker network inspect eshop-network

# 3. Check connection string
# Should be: jdbc:mysql://mysql:3306/eshop_db
# NOT: jdbc:mysql://localhost:3306/eshop_db
```

### Problem: Jenkins build fails

**Symptoms:** Pipeline shows red

**Solutions:**
```bash
# 1. Check Jenkins console output
# Click on build number → Console Output

# 2. Common issues:
# - Maven not configured: Configure in Jenkins → Global Tool Configuration
# - Docker not available: Ensure Jenkins user can run Docker
# - Git authentication: Configure credentials in Jenkins

# 3. Test locally first
./mvnw clean package  # Should work before Jenkins
```

### Problem: Changes not detected

**Symptoms:** Pipeline says "No changes"

**Solutions:**
```bash
# 1. Check git diff manually
git diff --name-only origin/main...HEAD

# 2. Make sure you're on a feature branch
git branch

# 3. Ensure changes are committed
git status

# 4. Push changes to remote
git push origin your-branch
```

### Problem: Docker build fails

**Symptoms:** `ERROR [builder 4/5]` in logs

**Solutions:**
```bash
# 1. Build locally to see full error
cd service-directory
docker build -t test:latest .

# 2. Common issues:
# - Missing files: Ensure pom.xml and src/ exist
# - Maven dependencies fail: Check internet connection
# - Out of disk space: docker system prune -a

# 3. Try multi-stage build without cache
docker build --no-cache -t service:latest .
```

### Problem: Port conflicts

**Symptoms:** `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solutions:**
```bash
# 1. Find what's using the port
lsof -i :8080

# 2. Stop the conflicting service
docker-compose down
# Or kill the process
kill -9 PID

# 3. Change port in docker-compose.yml
ports:
  - "8081:8080"  # Maps host 8081 to container 8080
```

### Problem: SonarQube quality gate fails

**Symptoms:** Pipeline fails at Quality Gate stage

**Solutions:**
```bash
# 1. Check SonarQube dashboard
# http://localhost:9000

# 2. Review issues:
# - Code coverage too low → Write more tests
# - Code smells → Refactor code
# - Duplications → Extract common code

# 3. Adjust quality gate (for learning):
# SonarQube → Quality Gates → Create/Edit
# Lower thresholds temporarily

# 4. Skip quality gate in Jenkinsfile (not recommended for production):
# Set skipSonar = true in processMicroservice()
```

---

## 🎯 Learning Exercises

### Exercise 1: Add a new microservice

1. Create new Spring Boot service
2. Add Dockerfile
3. Update docker-compose.yml
4. Update Jenkinsfile microservices list
5. Build and deploy

### Exercise 2: Modify the pipeline

1. Add a stage for integration tests
2. Add email notifications on failure
3. Add deployment to different environments (dev, staging, prod)

### Exercise 3: Optimize builds

1. Add Maven dependency caching in Dockerfile
2. Implement selective testing (only test changed services)
3. Add build time metrics

---

## 📚 Additional Resources

- **Spring Boot:** https://spring.io/guides
- **Docker:** https://docs.docker.com/get-started/
- **Jenkins Pipeline:** https://www.jenkins.io/doc/book/pipeline/
- **SonarQube:** https://docs.sonarqube.org/latest/
- **Microservices Patterns:** https://microservices.io/patterns/

---

## ✅ Quick Start Checklist

- [ ] All prerequisites installed
- [ ] Jenkins configured with Maven and JDK
- [ ] SonarQube running on port 9000
- [ ] Docker Desktop running
- [ ] All services have Dockerfiles
- [ ] docker-compose.yml configured
- [ ] Jenkinsfile in repository root
- [ ] Services build locally with Maven
- [ ] Docker images build successfully
- [ ] Services start with docker-compose
- [ ] All services registered in Eureka
- [ ] Jenkins pipeline runs successfully

---

**Remember:** DevOps is a journey, not a destination. Start simple, automate gradually, and always keep learning! 🚀
