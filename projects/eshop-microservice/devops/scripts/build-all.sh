#!/bin/bash

# ============================================================================
# Build All Microservices Script
# ============================================================================
# This script builds all microservices locally without Docker
# Useful for quick testing and verification before Docker builds

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║     E-SHOP MICROSERVICES BUILD SCRIPT                 ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# List of all microservices (in dependency order)
SERVICES=(
    "config-server"
    "service-registry"
    "api-gateway"
    "auth-jwt-service"
    "product-service"
    "category-service"
    "order-service"
    "payment-service"
)

# Track successful and failed builds
SUCCESSFUL=()
FAILED=()

# Function to build a service
build_service() {
    local service=$1
    echo -e "\n${YELLOW}🔨 Building ${service}...${NC}"
    
    if [ ! -d "$service" ]; then
        echo -e "${RED}❌ Directory $service not found!${NC}"
        FAILED+=("$service (not found)")
        return 1
    fi
    
    cd "$service"
    
    # Check if pom.xml exists
    if [ ! -f "pom.xml" ]; then
        echo -e "${RED}❌ pom.xml not found in $service!${NC}"
        cd ..
        FAILED+=("$service (no pom.xml)")
        return 1
    fi
    
    # Build with Maven
    if ./mvnw clean package -DskipTests; then
        echo -e "${GREEN}✅ Successfully built ${service}${NC}"
        SUCCESSFUL+=("$service")
    else
        echo -e "${RED}❌ Failed to build ${service}${NC}"
        FAILED+=("$service")
        cd ..
        return 1
    fi
    
    cd ..
}

# Build all services
START_TIME=$(date +%s)

for service in "${SERVICES[@]}"; do
    build_service "$service"
done

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

# Print summary
echo -e "\n${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║                  BUILD SUMMARY                        ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo -e "\n${GREEN}✅ Successful builds (${#SUCCESSFUL[@]}):${NC}"
for service in "${SUCCESSFUL[@]}"; do
    echo -e "   - $service"
done

if [ ${#FAILED[@]} -gt 0 ]; then
    echo -e "\n${RED}❌ Failed builds (${#FAILED[@]}):${NC}"
    for service in "${FAILED[@]}"; do
        echo -e "   - $service"
    done
fi

echo -e "\n${BLUE}⏱  Total time: ${DURATION} seconds${NC}"

# Exit with error if any builds failed
if [ ${#FAILED[@]} -gt 0 ]; then
    exit 1
else
    echo -e "\n${GREEN}🎉 All services built successfully!${NC}"
    exit 0
fi
