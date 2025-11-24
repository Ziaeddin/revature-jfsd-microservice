#!/bin/bash

# ============================================================================
# Start All Microservices Script
# ============================================================================
# This script starts all 8 microservices in the correct order using Maven
#
# Order of startup:
# 1. service-registry (Eureka Server)
# 2. config-server
# 3. auth-jwt-service
# 4. api-gateway
# 5. order-service, payment-service, product-service, category-service
# ============================================================================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Log file directory
LOG_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOG_DIR"

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║     E-SHOP MICROSERVICES STARTUP                      ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Function to check if MySQL is running
check_mysql() {
    echo -e "${CYAN}🔍 Checking MySQL connection...${NC}"

    # Check if MySQL is running on default port 3306
    if ! lsof -i :3306 > /dev/null 2>&1; then
        echo -e "${RED}❌ MySQL is not running on port 3306${NC}"
        echo -e "${YELLOW}Please start MySQL before running the services:${NC}"
        echo -e "  • On macOS: brew services start mysql"
        echo -e "  • Or: mysql.server start"
        echo -e "  • Or start via System Preferences / Settings"
        return 1
    fi

    # Try to connect to MySQL (attempts common configurations)
    if command -v mysql &> /dev/null; then
        # Try to ping MySQL server
        if mysql -u root -e "SELECT 1;" &> /dev/null || \
           mysql -u root -proot -e "SELECT 1;" &> /dev/null || \
           mysql -e "SELECT 1;" &> /dev/null; then
            echo -e "${GREEN}✅ MySQL is running and accessible${NC}"
            return 0
        else
            echo -e "${YELLOW}⚠️  MySQL is running but connection test failed${NC}"
            echo -e "${YELLOW}   Proceeding anyway, but services may fail if they can't connect${NC}"
            return 0
        fi
    else
        echo -e "${YELLOW}⚠️  MySQL client not found, skipping connection test${NC}"
        echo -e "${GREEN}✅ MySQL appears to be running on port 3306${NC}"
        return 0
    fi
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local wait_time=$3
    local port=$4

    echo -e "${CYAN}🚀 Starting ${service_name}...${NC}"

    if [ ! -d "$service_dir" ]; then
        echo -e "${RED}❌ Directory not found: ${service_dir}${NC}"
        echo -e "${RED}❌ STARTUP FAILED - Cannot continue${NC}"
        exit 1
    fi

    # Check if mvnw is executable
    if [ ! -x "$service_dir/mvnw" ]; then
        echo -e "${RED}❌ Maven wrapper not executable: ${service_dir}/mvnw${NC}"
        echo -e "${YELLOW}Attempting to fix permissions...${NC}"
        chmod +x "$service_dir/mvnw"
    fi

    cd "$service_dir"

    # Start the service in background and redirect output to log file
    nohup ./mvnw spring-boot:run > "$LOG_DIR/${service_name}.log" 2>&1 &

    local pid=$!
    echo $pid > "$LOG_DIR/${service_name}.pid"

    echo -e "${GREEN}✅ ${service_name} started (PID: ${pid})${NC}"
    echo -e "${YELLOW}⏳ Waiting ${wait_time} seconds for ${service_name} to initialize...${NC}"

    # Wait and check if process is still running
    local count=0
    while [ $count -lt $wait_time ]; do
        if ! ps -p $pid > /dev/null 2>&1; then
            echo -e "${RED}❌ ${service_name} process died unexpectedly!${NC}"
            echo -e "${RED}Last 20 lines of log:${NC}"
            tail -20 "$LOG_DIR/${service_name}.log"
            echo -e "${RED}❌ STARTUP FAILED - Cannot continue${NC}"
            exit 1
        fi
        sleep 1
        count=$((count + 1))
    done

    # Verify service is responding on port (if port specified)
    if [ ! -z "$port" ]; then
        echo -e "${CYAN}🔍 Verifying ${service_name} on port ${port}...${NC}"
        if lsof -i :$port > /dev/null 2>&1; then
            echo -e "${GREEN}✅ ${service_name} is listening on port ${port}${NC}"
        else
            echo -e "${YELLOW}⚠️  ${service_name} not yet listening on port ${port} (may still be starting)${NC}"
        fi
    fi

    cd "$SCRIPT_DIR"
}

# Check if services are already running
if [ -f "$LOG_DIR/service-registry.pid" ]; then
    echo -e "${YELLOW}⚠️  Services may already be running. Run ./stop-all-services.sh first if you want to restart.${NC}"
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Clear old logs
rm -f "$LOG_DIR"/*.log

# Check MySQL availability before starting services
echo -e "\n${BLUE}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}Pre-Flight Check: MySQL Database${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}\n"

if ! check_mysql; then
    echo -e "\n${RED}❌ STARTUP ABORTED - MySQL is not available${NC}"
    exit 1
fi

echo -e "\n${BLUE}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}Phase 1: Starting Service Registry (Eureka)${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}\n"

start_service "service-registry" "$SCRIPT_DIR/service-registry" 30 8761

echo -e "\n${BLUE}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}Phase 2: Starting Config Server${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}\n"

start_service "config-server" "$SCRIPT_DIR/config-server" 20 8888

echo -e "\n${BLUE}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}Phase 3: Starting Auth Service${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}\n"

start_service "auth-jwt-service" "$SCRIPT_DIR/auth-jwt-service" 20 8081

echo -e "\n${BLUE}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}Phase 4: Starting API Gateway${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}\n"

start_service "api-gateway" "$SCRIPT_DIR/api-gateway" 20 8080

echo -e "\n${BLUE}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}Phase 5: Starting Business Services${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}\n"

start_service "order-service" "$SCRIPT_DIR/order-service" 15 8082
start_service "payment-service" "$SCRIPT_DIR/payment-service" 15 8083
start_service "product-service" "$SCRIPT_DIR/product-service" 15 8084
start_service "category-service" "$SCRIPT_DIR/category-service" 15 8085

echo -e "\n${GREEN}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║           ALL SERVICES STARTED!                       ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo -e "${BLUE}Service URLs:${NC}"
echo -e "  🔍 Service Registry:    http://localhost:8761"
echo -e "  🔧 Config Server:       http://localhost:8888"
echo -e "  🔐 Auth Service:        http://localhost:8081"
echo -e "  🌐 API Gateway:         http://localhost:8080"
echo -e "  🛒 Order Service:       http://localhost:8082"
echo -e "  💳 Payment Service:     http://localhost:8083"
echo -e "  📦 Product Service:     http://localhost:8084"
echo -e "  🏷️  Category Service:    http://localhost:8085"

echo -e "\n${YELLOW}📝 Useful Information:${NC}"
echo -e "  • Logs are stored in: $LOG_DIR"
echo -e "  • To view logs: tail -f $LOG_DIR/<service-name>.log"
echo -e "  • To stop all services: ./stop-all-services.sh"
echo -e "  • Check Eureka Dashboard at http://localhost:8761 to verify all services are registered"

echo -e "\n${YELLOW}⏳ Note: Services may take 1-2 minutes to be fully ready and registered.${NC}\n"

