# Service Management Scripts

Simple scripts to start and stop all 8 microservices in the correct order.

## Scripts

### start-all-services.sh
Starts all microservices in the correct startup order:
1. **Service Registry** (Eureka Server) - Port 8761
2. **Config Server** - Port 8888
3. **Auth JWT Service** - Port 8081
4. **API Gateway** - Port 8080
5. **Business Services** (started in parallel):
   - Order Service - Port 8082
   - Payment Service - Port 8083
   - Product Service - Port 8084
   - Category Service - Port 8085

### stop-all-services.sh
Stops all running microservices gracefully.

## Usage

### Start All Services
```bash
./start-all-services.sh
```

This will:
- Start all 8 services in the correct order
- Wait for each service to initialize before starting the next
- Create PID files in the `logs/` directory
- Redirect service logs to `logs/<service-name>.log`

**Note:** The complete startup process takes approximately 2-3 minutes.

### Stop All Services
```bash
./stop-all-services.sh
```

This will:
- Stop all running services gracefully
- Clean up PID files
- Preserve log files for review

### View Service Logs
```bash
# View logs in real-time
tail -f logs/service-registry.log
tail -f logs/api-gateway.log
tail -f logs/order-service.log

# View all logs from a service
cat logs/product-service.log
```

## Service URLs

Once started, you can access the services at:

| Service | URL | Purpose |
|---------|-----|---------|
| Service Registry | http://localhost:8761 | Eureka Dashboard - See all registered services |
| Config Server | http://localhost:8888 | Configuration management |
| Auth Service | http://localhost:8081 | Authentication & JWT tokens |
| API Gateway | http://localhost:8080 | Main entry point for all API calls |
| Order Service | http://localhost:8082 | Order management |
| Payment Service | http://localhost:8083 | Payment processing |
| Product Service | http://localhost:8084 | Product catalog |
| Category Service | http://localhost:8085 | Product categories |

## Startup Order Explanation

The startup order is critical for proper service registration and configuration:

1. **Service Registry** must start first so other services can register themselves
2. **Config Server** provides configuration to all other services
3. **Auth Service** handles authentication for the gateway and other services
4. **API Gateway** routes requests to business services
5. **Business Services** can start in parallel once the infrastructure is ready

## Troubleshooting

### Services not starting
- Check if ports are already in use: `lsof -i :8761` (replace with specific port)
- View the logs: `tail -f logs/<service-name>.log`
- Ensure Maven is installed: `mvn --version`

### Services not registering with Eureka
- Wait 1-2 minutes for registration to complete
- Check Eureka dashboard: http://localhost:8761
- Review service logs for connection errors

### Clean restart
```bash
./stop-all-services.sh
rm -rf logs/
./start-all-services.sh
```

## Requirements

- Java 11 or higher
- Maven (or use the Maven wrapper `./mvnw` in each service)
- All services must have their dependencies installed (`mvn clean install` in each service directory)

## Directory Structure

After running the scripts, you'll have:
```
eshop-microservice/
├── start-all-services.sh
├── stop-all-services.sh
├── SERVICES_README.md
└── logs/
    ├── service-registry.log
    ├── service-registry.pid
    ├── config-server.log
    ├── config-server.pid
    ├── auth-jwt-service.log
    ├── auth-jwt-service.pid
    ├── api-gateway.log
    ├── api-gateway.pid
    ├── order-service.log
    ├── order-service.pid
    ├── payment-service.log
    ├── payment-service.pid
    ├── product-service.log
    ├── product-service.pid
    ├── category-service.log
    └── category-service.pid
```

## Notes

- Logs are preserved when you stop services for debugging
- PID files are used to track running processes
- Each service gets its own log file
- Business services start in parallel to save time
- The scripts use the Maven wrapper (`./mvnw`) in each service directory

