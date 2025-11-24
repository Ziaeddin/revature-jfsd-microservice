#!/bin/bash

# ============================================================================
# Cleanup Docker Resources Script
# ============================================================================
# This script stops all services and optionally cleans up Docker resources

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║     E-SHOP MICROSERVICES CLEANUP                      ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Navigate to docker directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/../docker"

if [ -d "$DOCKER_DIR" ]; then
    cd "$DOCKER_DIR"
    
    if [ -f "docker-compose.yml" ]; then
        echo -e "${YELLOW}🛑 Stopping all services...${NC}"
        docker-compose down
        echo -e "${GREEN}✅ All services stopped${NC}\n"
    fi
fi

# Ask if user wants to clean up more
echo -e "${YELLOW}Do you want to perform additional cleanup?${NC}"
echo -e "1) Remove only stopped containers"
echo -e "2) Remove containers and volumes (⚠️  deletes database data)"
echo -e "3) Remove containers, volumes, and images (⚠️  requires rebuild)"
echo -e "4) Clean all unused Docker resources (⚠️  system-wide cleanup)"
echo -e "5) Skip additional cleanup"
echo -e ""
read -p "Select option (1-5): " choice

case $choice in
    1)
        echo -e "\n${YELLOW}🧹 Removing stopped containers...${NC}"
        docker container prune -f
        echo -e "${GREEN}✅ Stopped containers removed${NC}"
        ;;
    2)
        echo -e "\n${YELLOW}🧹 Removing containers and volumes...${NC}"
        cd "$DOCKER_DIR"
        docker-compose down -v
        echo -e "${GREEN}✅ Containers and volumes removed${NC}"
        echo -e "${RED}⚠️  Database data has been deleted${NC}"
        ;;
    3)
        echo -e "\n${YELLOW}🧹 Removing containers, volumes, and images...${NC}"
        cd "$DOCKER_DIR"
        docker-compose down -v --rmi all
        echo -e "${GREEN}✅ Containers, volumes, and images removed${NC}"
        echo -e "${RED}⚠️  You will need to rebuild images before starting services${NC}"
        ;;
    4)
        echo -e "\n${YELLOW}🧹 Cleaning all unused Docker resources...${NC}"
        echo -e "${RED}⚠️  This will remove:${NC}"
        echo -e "   - All stopped containers"
        echo -e "   - All networks not used by at least one container"
        echo -e "   - All dangling images"
        echo -e "   - All build cache"
        echo -e ""
        read -p "Are you sure? (yes/no): " confirm
        if [ "$confirm" == "yes" ]; then
            docker system prune -a --volumes -f
            echo -e "${GREEN}✅ All unused Docker resources removed${NC}"
            echo -e "${RED}⚠️  You will need to rebuild images before starting services${NC}"
        else
            echo -e "${YELLOW}Cleanup cancelled${NC}"
        fi
        ;;
    5)
        echo -e "\n${GREEN}✅ Cleanup complete (no additional actions)${NC}"
        ;;
    *)
        echo -e "\n${RED}❌ Invalid option${NC}"
        exit 1
        ;;
esac

# Show Docker disk usage
echo -e "\n${BLUE}📊 Current Docker disk usage:${NC}"
docker system df

echo -e "\n${GREEN}🎉 Cleanup completed!${NC}\n"
