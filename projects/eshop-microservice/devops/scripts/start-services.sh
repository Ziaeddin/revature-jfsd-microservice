#!/bin/bash

# ============================================================================
# Start All Services Script
# ============================================================================
# This script starts all microservices using docker-compose

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║     E-SHOP MICROSERVICES STARTUP                      ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Navigate to docker directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/../docker"

if [ ! -d "$DOCKER_DIR" ]; then
    echo -e "${RED}❌ Docker directory not found!${NC}"
    exit 1
fi

cd "$DOCKER_DIR"

# Check if docker-compose.yml exists
if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED}❌ docker-compose.yml not found!${NC}"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running!${NC}"
    echo -e "${YELLOW}Please start Docker Desktop and try again.${NC}"
    exit 1
fi

echo -e "${YELLOW}🐳 Starting all services with Docker Compose...${NC}\n"

# Start services
docker-compose up -d

echo -e "\n${GREEN}✅ All services started!${NC}"

# Wait a moment for containers to initialize
echo -e "\n${YELLOW}⏳ Waiting for services to initialize...${NC}"
sleep 5

# Show status
echo -e "\n${BLUE}📊 Service Status:${NC}"
docker-compose ps

echo -e "\n${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║              SERVICE URLS                             ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo -e "🌐 API Gateway:         http://localhost:8080"
echo -e "🔧 Config Server:       http://localhost:8888"
echo -e "🔍 Service Registry:    http://localhost:8761"
echo -e "🔐 Auth Service:        http://localhost:8081"
echo -e "📦 Product Service:     http://localhost:8084"
echo -e "🏷️  Category Service:    http://localhost:8085"
echo -e "🛒 Order Service:       http://localhost:8082"
echo -e "💳 Payment Service:     http://localhost:8083"
echo -e "🔍 SonarQube:           http://localhost:9000"
echo -e "🗄️  MySQL:               localhost:3306"

echo -e "\n${YELLOW}📝 Useful Commands:${NC}"
echo -e "   View logs:           docker-compose logs -f [service-name]"
echo -e "   Stop all services:   docker-compose down"
echo -e "   Restart a service:   docker-compose restart [service-name]"
echo -e "   Check status:        docker-compose ps"

echo -e "\n${GREEN}🎉 All services are starting up!${NC}"
echo -e "${YELLOW}⏳ Note: Services may take 1-2 minutes to be fully ready.${NC}"
echo -e "${YELLOW}   Check http://localhost:8761 to see when all services are registered.${NC}\n"
