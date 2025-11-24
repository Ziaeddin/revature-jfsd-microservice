#!/bin/bash

# ============================================================================
# Environment Setup Script
# ============================================================================
# This script helps you create a secure .env file for docker-compose
# ============================================================================

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}E-Shop Microservices - Environment Setup${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check if .env already exists
if [ -f ".env" ]; then
    echo -e "${YELLOW}⚠️  Warning: .env file already exists!${NC}"
    read -p "Do you want to overwrite it? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${RED}Aborted. Keeping existing .env file.${NC}"
        exit 1
    fi
fi

# Check if .env.example exists
if [ ! -f ".env.example" ]; then
    echo -e "${RED}❌ Error: .env.example not found!${NC}"
    echo "Make sure you're running this from the devops directory."
    exit 1
fi

echo -e "${YELLOW}📝 Creating .env file from template...${NC}"
cp .env.example .env

echo ""
echo -e "${GREEN}✅ .env file created!${NC}"
echo ""
echo -e "${YELLOW}⚠️  IMPORTANT: You must edit .env and change the default passwords!${NC}"
echo ""
echo "Default passwords like 'changeme' are NOT secure."
echo ""
echo "To edit the file, run:"
echo "  nano .env    # or vim .env, or code .env"
echo ""
echo "Change these values:"
echo "  - MYSQL_ROOT_PASSWORD"
echo "  - DB_PASSWORD"
echo "  - SONAR_JDBC_PASSWORD"
echo "  - POSTGRES_PASSWORD"
echo ""
echo -e "${YELLOW}💡 Tips for strong passwords:${NC}"
echo "  - Use at least 16 characters"
echo "  - Mix uppercase, lowercase, numbers, and symbols"
echo "  - Use a password generator or manager"
echo "  - Never reuse passwords"
echo ""
echo -e "${GREEN}After editing .env, start services with:${NC}"
echo "  cd docker"
echo "  docker-compose up -d"
echo ""
echo -e "${GREEN}========================================${NC}"
