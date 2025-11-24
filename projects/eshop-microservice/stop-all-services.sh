#!/bin/bash

# ============================================================================
# Stop All Microservices Script
# ============================================================================
# This script stops all running microservices
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

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║     E-SHOP MICROSERVICES SHUTDOWN                     ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="$LOG_DIR/${service_name}.pid"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")

        # Check if process is still running
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${CYAN}🛑 Stopping ${service_name} (PID: ${pid})...${NC}"
            kill $pid

            # Wait for process to stop (max 10 seconds)
            local count=0
            while ps -p $pid > /dev/null 2>&1 && [ $count -lt 10 ]; do
                sleep 1
                count=$((count + 1))
            done

            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${YELLOW}⚠️  Force stopping ${service_name}...${NC}"
                kill -9 $pid
            fi

            echo -e "${GREEN}✅ ${service_name} stopped${NC}"
        else
            echo -e "${YELLOW}⚠️  ${service_name} is not running (PID: ${pid})${NC}"
        fi

        rm -f "$pid_file"
    else
        echo -e "${YELLOW}⚠️  No PID file found for ${service_name}${NC}"
    fi
}

# Check if log directory exists
if [ ! -d "$LOG_DIR" ]; then
    echo -e "${YELLOW}⚠️  No services appear to be running (logs directory not found)${NC}"
    exit 0
fi

# List of all services in reverse order
services=(
    "category-service"
    "product-service"
    "payment-service"
    "order-service"
    "api-gateway"
    "auth-jwt-service"
    "config-server"
    "service-registry"
)

echo -e "${CYAN}Stopping all services...${NC}\n"

# Stop all services
for service in "${services[@]}"; do
    stop_service "$service"
done

# Additional cleanup: kill any remaining Spring Boot processes
echo -e "\n${CYAN}🧹 Cleaning up any remaining processes...${NC}"

# Find and kill any Spring Boot processes related to our services
pids=$(ps aux | grep "spring-boot:run" | grep -v grep | awk '{print $2}')
if [ ! -z "$pids" ]; then
    echo -e "${YELLOW}Found additional Spring Boot processes: $pids${NC}"
    for pid in $pids; do
        kill $pid 2>/dev/null || true
    done
    echo -e "${GREEN}✅ Cleaned up additional processes${NC}"
else
    echo -e "${GREEN}✅ No additional processes found${NC}"
fi

echo -e "\n${GREEN}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║           ALL SERVICES STOPPED!                       ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo -e "${YELLOW}📝 Log files are preserved in: $LOG_DIR${NC}"
echo -e "${YELLOW}   To clean up logs: rm -rf $LOG_DIR${NC}\n"

