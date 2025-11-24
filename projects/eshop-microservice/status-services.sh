#!/bin/bash

# ============================================================================
# Check Status of All Microservices
# ============================================================================
# This script checks the status of all running microservices
# ============================================================================

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

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║     E-SHOP MICROSERVICES STATUS                       ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}\n"

# Function to check service status
check_service() {
    local service_name=$1
    local port=$2
    local pid_file="$LOG_DIR/${service_name}.pid"

    printf "%-25s" "$service_name"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")

        # Check if process is running
        if ps -p $pid > /dev/null 2>&1; then
            # Check if port is listening
            if lsof -i :$port > /dev/null 2>&1; then
                echo -e "${GREEN}✅ RUNNING${NC} (PID: $pid, Port: $port)"
            else
                echo -e "${YELLOW}⚠️  STARTING${NC} (PID: $pid, Port: $port not ready)"
            fi
        else
            echo -e "${RED}❌ STOPPED${NC} (Stale PID: $pid)"
        fi
    else
        # Check if port is in use (service might be running without our PID file)
        if lsof -i :$port > /dev/null 2>&1; then
            echo -e "${YELLOW}⚠️  UNKNOWN${NC} (Port $port in use, no PID file)"
        else
            echo -e "${RED}❌ STOPPED${NC}"
        fi
    fi
}

# Check if log directory exists
if [ ! -d "$LOG_DIR" ]; then
    echo -e "${YELLOW}⚠️  No services appear to be running (logs directory not found)${NC}\n"
    exit 0
fi

echo -e "${CYAN}Service Status:${NC}\n"

# Check all services
check_service "service-registry" 8761
check_service "config-server" 8888
check_service "auth-jwt-service" 8081
check_service "api-gateway" 8080
check_service "order-service" 8082
check_service "payment-service" 8083
check_service "product-service" 8084
check_service "category-service" 8085

echo -e "\n${CYAN}Quick Links:${NC}"
echo -e "  🔍 Eureka Dashboard: http://localhost:8761"
echo -e "  🌐 API Gateway:      http://localhost:8080"

echo -e "\n${YELLOW}Tip: To see detailed logs, use:${NC}"
echo -e "  tail -f logs/<service-name>.log\n"

