# 🔍 SonarQube Setup Guide

This guide will help you install and configure SonarQube for code quality analysis in your E-Shop microservices project.

---

## 📋 Table of Contents

1. [What is SonarQube?](#what-is-sonarqube)
2. [Installation Options](#installation-options)
3. [Installing SonarQube with Docker](#installing-sonarqube-with-docker)
4. [Installing SonarQube Locally](#installing-sonarqube-locally)
5. [Initial Configuration](#initial-configuration)
6. [Configuring Quality Gates](#configuring-quality-gates)
7. [Integrating with Maven](#integrating-with-maven)
8. [Integrating with Jenkins](#integrating-with-jenkins)
9. [Understanding Reports](#understanding-reports)
10. [Troubleshooting](#troubleshooting)

---

## 🤔 What is SonarQube?

**SonarQube** is a code quality and security analysis platform that:
- Detects bugs and vulnerabilities
- Identifies code smells (bad practices)
- Measures code coverage
- Tracks technical debt
- Enforces quality standards

**Think of SonarQube as:**
- A code reviewer that never gets tired
- A security guard for your codebase
- A quality coach for your team

### What SonarQube Analyzes

```
📊 Code Quality Metrics
├── 🐛 Bugs: Issues that will cause errors
├── 🔒 Vulnerabilities: Security weaknesses
├── 👃 Code Smells: Maintainability issues
├── 📈 Coverage: How much code is tested
├── 🔄 Duplications: Copy-pasted code
└── 📏 Complexity: How hard code is to understand
```

### Quality Gate Concept

A **Quality Gate** is a set of conditions your code must meet:
```
✅ Pass Quality Gate if:
   - No new bugs
   - No new vulnerabilities
   - Test coverage > 80%
   - Duplicated code < 3%
   - Code smells < 5

❌ Fail Quality Gate if any condition fails
   → Build fails
   → Code cannot be deployed
```

---

## 🎯 Installation Options

### Option 1: Docker (Recommended)
- ✅ Easiest setup
- ✅ Isolated environment
- ✅ Easy to remove
- ✅ Works on all platforms

### Option 2: Local Installation
- ✅ Better performance
- ✅ Production-ready
- ❌ More complex setup
- ❌ Platform-specific

### Option 3: Cloud Service
- ✅ No setup required
- ✅ Always up-to-date
- ❌ Requires internet
- ❌ May have costs

---

## 🐳 Installing SonarQube with Docker

### Quick Start (Standalone)

```bash
# Pull SonarQube image
docker pull sonarqube:community

# Run SonarQube container
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  sonarqube:community

# Wait for SonarQube to start (~30 seconds)
docker logs -f sonarqube

# Access SonarQube
# Open browser: http://localhost:9000
```

### Recommended Setup (with Database)

SonarQube needs a database for production use. Create `sonarqube-docker-compose.yml`:

```yaml
version: '3.8'

services:
  sonarqube:
    image: sonarqube:community
    container_name: sonarqube
    restart: unless-stopped
    ports:
      - "9000:9000"
    environment:
      SONAR_JDBC_URL: jdbc:postgresql://postgres:5432/sonarqube
      SONAR_JDBC_USERNAME: sonar
      SONAR_JDBC_PASSWORD: sonar
    volumes:
      - sonarqube-conf:/opt/sonarqube/conf
      - sonarqube-data:/opt/sonarqube/data
      - sonarqube-logs:/opt/sonarqube/logs
      - sonarqube-extensions:/opt/sonarqube/extensions
    networks:
      - sonarqube-network
    depends_on:
      - postgres
    ulimits:
      nofile:
        soft: 65536
        hard: 65536

  postgres:
    image: postgres:14
    container_name: sonarqube-postgres
    restart: unless-stopped
    environment:
      POSTGRES_USER: sonar
      POSTGRES_PASSWORD: sonar
      POSTGRES_DB: sonarqube
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - sonarqube-network

volumes:
  sonarqube-conf:
  sonarqube-data:
  sonarqube-logs:
  sonarqube-extensions:
  postgres-data:

networks:
  sonarqube-network:
    driver: bridge
```

**Start SonarQube:**
```bash
# Start services
docker-compose -f sonarqube-docker-compose.yml up -d

# Check logs
docker-compose -f sonarqube-docker-compose.yml logs -f sonarqube

# Wait for message: "SonarQube is operational"

# Stop services
docker-compose -f sonarqube-docker-compose.yml down

# Stop and remove data
docker-compose -f sonarqube-docker-compose.yml down -v
```

---

## 💻 Installing SonarQube Locally

### Prerequisites

- Java 17 or 21 (SonarQube requires Java 17+)
- 2GB RAM minimum (4GB recommended)
- PostgreSQL or MySQL database (optional but recommended)

### Step 1: Download SonarQube

```bash
# Download SonarQube Community Edition
cd ~/Downloads
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.3.0.82913.zip

# Or use curl
curl -O https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.3.0.82913.zip

# Extract
unzip sonarqube-10.3.0.82913.zip

# Move to /opt (or any preferred location)
sudo mv sonarqube-10.3.0.82913 /opt/sonarqube
```

### Step 2: Configure SonarQube

**For embedded database (development only):**
```bash
cd /opt/sonarqube

# No additional configuration needed
# Uses H2 embedded database
```

**For PostgreSQL (recommended for production):**

1. Install PostgreSQL:
```bash
# macOS
brew install postgresql
brew services start postgresql

# Linux
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

2. Create database:
```bash
# Connect to PostgreSQL
psql postgres

# Create user and database
CREATE USER sonar WITH PASSWORD 'sonar';
CREATE DATABASE sonarqube OWNER sonar;
GRANT ALL PRIVILEGES ON DATABASE sonarqube TO sonar;
\q
```

3. Configure SonarQube to use PostgreSQL:
```bash
# Edit configuration file
vim /opt/sonarqube/conf/sonar.properties

# Add these lines (uncomment if already present):
sonar.jdbc.username=sonar
sonar.jdbc.password=sonar
sonar.jdbc.url=jdbc:postgresql://localhost:5432/sonarqube
```

### Step 3: Start SonarQube

```bash
# Navigate to SonarQube directory
cd /opt/sonarqube

# Start SonarQube (macOS/Linux)
./bin/macosx-universal-64/sonar.sh start

# Or for Linux
./bin/linux-x86-64/sonar.sh start

# Check status
./bin/macosx-universal-64/sonar.sh status

# View logs
tail -f logs/sonar.log

# Stop SonarQube
./bin/macosx-universal-64/sonar.sh stop
```

### Step 4: Run as Service (Optional)

**macOS (using launchd):**
```bash
# Create service file
sudo vim /Library/LaunchDaemons/org.sonarqube.plist
```

Add this content:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>org.sonarqube</string>
    <key>ProgramArguments</key>
    <array>
        <string>/opt/sonarqube/bin/macosx-universal-64/sonar.sh</string>
        <string>start</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
</dict>
</plist>
```

```bash
# Load service
sudo launchctl load /Library/LaunchDaemons/org.sonarqube.plist

# Start service
sudo launchctl start org.sonarqube
```

**Linux (using systemd):**
```bash
# Create service file
sudo vim /etc/systemd/system/sonarqube.service
```

Add this content:
```ini
[Unit]
Description=SonarQube service
After=syslog.target network.target

[Service]
Type=forking
ExecStart=/opt/sonarqube/bin/linux-x86-64/sonar.sh start
ExecStop=/opt/sonarqube/bin/linux-x86-64/sonar.sh stop
User=sonar
Group=sonar
Restart=always

[Install]
WantedBy=multi-user.target
```

```bash
# Create sonar user
sudo useradd -r -s /bin/false sonar
sudo chown -R sonar:sonar /opt/sonarqube

# Enable and start service
sudo systemctl enable sonarqube
sudo systemctl start sonarqube
sudo systemctl status sonarqube
```

---

## ⚙️ Initial Configuration

### Step 1: First Login

1. Open browser: http://localhost:9000
2. Wait for SonarQube to fully start (check for "SonarQube is up" message)
3. Login with default credentials:
   - **Username:** `admin`
   - **Password:** `admin`

### Step 2: Change Admin Password

1. You'll be prompted to change password immediately
2. Enter new password (recommended: strong password)
3. Confirm password

### Step 3: Create a Token for Jenkins

1. Click on profile icon (top right) → **My Account**
2. Click **Security** tab
3. In "Generate Tokens" section:
   - **Name:** `jenkins-token`
   - **Type:** Global Analysis Token
   - **Expires in:** No expiration (or set desired expiration)
4. Click **Generate**
5. **IMPORTANT:** Copy the token immediately (you won't see it again!)
   ```
   Example: squ_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
   ```
6. Store this token securely for Jenkins configuration

### Step 4: Configure Email Notifications (Optional)

1. Go to **Administration → Configuration → General Settings → Email**
2. Configure SMTP:
   ```
   SMTP host: smtp.gmail.com
   SMTP port: 587
   SMTP username: your-email@gmail.com
   SMTP password: [App-specific password]
   From address: sonarqube@yourcompany.com
   ```

---

## 🎯 Configuring Quality Gates

### Understanding Quality Gates

Quality Gates are the conditions your code must meet to pass. SonarQube includes a default quality gate called "Sonar way".

### View Default Quality Gate

1. Go to **Quality Gates** (top menu)
2. Click **Sonar way**
3. Review conditions:
   ```
   On Overall Code:
   - Coverage < 80% → Warning
   - Duplicated Lines (%) > 3% → Error
   
   On New Code:
   - Coverage < 80% → Error
   - Duplicated Lines (%) > 3% → Error
   - Maintainability Rating worse than A → Error
   - Reliability Rating worse than A → Error
   - Security Rating worse than A → Error
   - Security Hotspots Reviewed < 100% → Error
   ```

### Create Custom Quality Gate (For Learning)

1. Click **Quality Gates → Create**
2. Name: `eshop-quality-gate`
3. Add conditions:

**For beginners (lenient):**
```
On New Code:
├── Bugs > 0 → Error
├── Vulnerabilities > 0 → Error
├── Code Smells > 10 → Warning
├── Coverage < 50% → Warning
└── Duplicated Lines (%) > 5% → Warning
```

**For production (strict):**
```
On New Code:
├── Bugs > 0 → Error
├── Vulnerabilities > 0 → Error
├── Security Hotspots Reviewed < 100% → Error
├── Code Smells > 5 → Error
├── Coverage < 80% → Error
├── Duplicated Lines (%) > 3% → Error
├── Maintainability Rating worse than A → Error
├── Reliability Rating worse than A → Error
└── Security Rating worse than A → Error
```

4. Click **Set as Default** to use for all projects

---

## 🔨 Integrating with Maven

### Step 1: Configure pom.xml

Add SonarQube properties and plugins to your service's `pom.xml`:

```xml
<properties>
    <!-- Existing properties -->
    <java.version>21</java.version>
    
    <!-- SonarQube Properties -->
    <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
    <sonar.dynamicAnalysis>reuseReports</sonar.dynamicAnalysis>
    <sonar.coverage.jacoco.xmlReportPaths>
        ${project.build.directory}/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
    <sonar.language>java</sonar.language>
    <sonar.java.source>21</sonar.java.source>
    <jacoco.version>0.8.11</jacoco.version>
</properties>

<build>
    <plugins>
        <!-- JaCoCo for Code Coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${jacoco.version}</version>
            <executions>
                <execution>
                    <id>prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>jacoco-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>PACKAGE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.50</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- SonarQube Scanner -->
        <plugin>
            <groupId>org.sonarsource.scanner.maven</groupId>
            <artifactId>sonar-maven-plugin</artifactId>
            <version>3.10.0.2594</version>
        </plugin>
    </plugins>
</build>
```

### Step 2: Run Analysis Locally

```bash
# Navigate to service directory
cd auth-jwt-service

# Run tests with coverage
./mvnw clean test

# Run SonarQube analysis
./mvnw sonar:sonar \
  -Dsonar.projectKey=auth-jwt-service \
  -Dsonar.projectName=auth-jwt-service \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN_HERE

# Or use environment variable for token
export SONAR_TOKEN=YOUR_TOKEN_HERE
./mvnw sonar:sonar \
  -Dsonar.projectKey=auth-jwt-service \
  -Dsonar.host.url=http://localhost:9000
```

### Step 3: View Results

1. Open http://localhost:9000
2. Click on **Projects**
3. Click on your project (`auth-jwt-service`)
4. Review metrics:
   - **Bugs:** Issues that will cause errors
   - **Vulnerabilities:** Security issues
   - **Code Smells:** Maintainability issues
   - **Coverage:** Test coverage percentage
   - **Duplications:** Duplicated code blocks

---

## 🔗 Integrating with Jenkins

### Step 1: Install SonarQube Scanner Plugin

1. In Jenkins: **Manage Jenkins → Manage Plugins**
2. Search for "SonarQube Scanner"
3. Install and restart Jenkins

### Step 2: Configure SonarQube Server in Jenkins

1. Go to **Manage Jenkins → Configure System**
2. Scroll to "SonarQube servers"
3. Click **Add SonarQube**
4. Configure:
   ```
   Name: SonarQube
   Server URL: http://localhost:9000
   
   (If Jenkins is in Docker and SonarQube is local)
   Server URL: http://host.docker.internal:9000
   
   Server authentication token:
   - Add → Jenkins Credentials
   - Kind: Secret text
   - Secret: [Paste your SonarQube token]
   - ID: sonarqube-token
   - Description: SonarQube Authentication Token
   ```
5. Click **Save**

### Step 3: Configure SonarQube Scanner Tool

1. Go to **Manage Jenkins → Global Tool Configuration**
2. Scroll to "SonarQube Scanner"
3. Click **Add SonarQube Scanner**
4. Configure:
   ```
   Name: SonarQube Scanner
   ☑ Install automatically
   Version: SonarQube Scanner 5.0.1.3006
   ```
5. Click **Save**

### Step 4: Use in Jenkinsfile

Our Jenkinsfile already includes SonarQube integration:

```groovy
// In shared-pipeline.groovy
def sonarAnalysis(String serviceName, String servicePath, String sonarUrl = 'http://localhost:9000') {
    echo "🔍 Running SonarQube analysis for ${serviceName}..."
    
    dir(servicePath) {
        withSonarQubeEnv('SonarQube') {
            sh """
                ./mvnw sonar:sonar \
                    -Dsonar.projectKey=${serviceName} \
                    -Dsonar.projectName=${serviceName} \
                    -Dsonar.host.url=${sonarUrl}
            """
        }
    }
}
```

### Step 5: Enable Quality Gate

Add this to your Jenkinsfile after SonarQube analysis:

```groovy
stage('Quality Gate') {
    steps {
        timeout(time: 5, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}
```

This will:
- Wait for SonarQube to finish analysis
- Check if Quality Gate passed
- Fail the build if Quality Gate failed

---

## 📊 Understanding Reports

### Project Dashboard

When you open a project in SonarQube, you see:

```
┌─────────────────────────────────────────────────────────────┐
│ Project: auth-jwt-service                                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ 🐛 Bugs: 0          🔒 Vulnerabilities: 0                  │
│ 👃 Code Smells: 12   📈 Coverage: 75.3%                    │
│ 🔄 Duplications: 2.1%  📏 Lines of Code: 1,245             │
│                                                             │
│ Quality Gate: ✅ PASSED                                     │
└─────────────────────────────────────────────────────────────┘
```

### Issue Types Explained

**1. Bugs 🐛**
```java
// Example Bug: Null pointer exception risk
public String getUserName() {
    User user = getUser();  // Could return null
    return user.getName();  // ❌ Potential NullPointerException
}

// Fix:
public String getUserName() {
    User user = getUser();
    return user != null ? user.getName() : "Unknown";  // ✅ Safe
}
```

**2. Vulnerabilities 🔒**
```java
// Example Vulnerability: SQL Injection risk
public User getUser(String username) {
    String query = "SELECT * FROM users WHERE username = '" + username + "'";
    // ❌ Vulnerable to SQL injection
}

// Fix: Use PreparedStatement
public User getUser(String username) {
    String query = "SELECT * FROM users WHERE username = ?";
    PreparedStatement stmt = connection.prepareStatement(query);
    stmt.setString(1, username);  // ✅ Safe
}
```

**3. Code Smells 👃**
```java
// Example Code Smell: Method too complex
public void processOrder(Order order) {
    if (order != null) {
        if (order.getItems() != null) {
            for (Item item : order.getItems()) {
                if (item.getQuantity() > 0) {
                    if (item.getPrice() > 0) {
                        // ... more nested conditions
                    }
                }
            }
        }
    }
    // ❌ Too complex, hard to test
}

// Fix: Extract methods, use early returns
public void processOrder(Order order) {
    if (!isValidOrder(order)) return;
    
    for (Item item : order.getItems()) {
        if (isValidItem(item)) {
            processItem(item);
        }
    }
    // ✅ Cleaner, easier to understand
}
```

### Coverage Report

**Coverage Types:**
- **Line Coverage:** % of lines executed by tests
- **Branch Coverage:** % of if/else branches tested
- **Condition Coverage:** % of boolean conditions tested

**Example:**
```java
public int calculateDiscount(int price, boolean isPremium) {
    if (isPremium) {  // Branch 1
        return price * 20 / 100;
    } else {  // Branch 2
        return price * 10 / 100;
    }
}

// To get 100% coverage, tests must cover:
// ✅ Test 1: isPremium = true
// ✅ Test 2: isPremium = false
```

---

## 🔍 Troubleshooting

### Problem: SonarQube won't start

**Symptoms:** Container exits or process dies

**Solutions:**

```bash
# Check system requirements
# macOS/Linux: Increase max_map_count
sudo sysctl -w vm.max_map_count=262144

# Make permanent (Linux)
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf

# Check logs
docker logs sonarqube

# Common error: Insufficient memory
# Solution: Allocate more memory to Docker
# Docker Desktop → Settings → Resources → Memory: 4GB+
```

### Problem: Analysis fails with "Insufficient token permissions"

**Solution:**
```
1. Generate new token with correct scope
2. Go to SonarQube → My Account → Security
3. Generate token with type: "Global Analysis Token"
4. Update token in Jenkins credentials
5. Run analysis again
```

### Problem: Quality Gate always fails

**Symptoms:** Even good code fails quality gate

**Solutions:**
```
1. Check Quality Gate conditions
   - Go to Quality Gates → Your gate
   - Review conditions (may be too strict)

2. For learning, create lenient quality gate:
   - Coverage > 50% (instead of 80%)
   - Allow some code smells initially

3. Check what's failing:
   - Open project in SonarQube
   - Look at "Failed Conditions"
   - Fix those specific issues

4. Generate coverage report:
   ./mvnw test  # Make sure tests run first!
   ./mvnw sonar:sonar
```

### Problem: Coverage shows 0%

**Symptoms:** Tests run but coverage is 0%

**Solutions:**
```bash
# 1. Ensure JaCoCo is configured in pom.xml
# (See "Integrating with Maven" section)

# 2. Run tests before analysis
./mvnw clean test  # Generates coverage data
./mvnw sonar:sonar  # Uploads to SonarQube

# 3. Check JaCoCo report exists
ls target/site/jacoco/jacoco.xml

# 4. Verify JaCoCo execution data
ls target/jacoco.exec
```

### Problem: Jenkins can't connect to SonarQube

**Symptoms:** `Unable to reach SonarQube server`

**Solutions:**

**If Jenkins is in Docker:**
```
# Use host.docker.internal instead of localhost
Server URL: http://host.docker.internal:9000

# Or add SonarQube to same Docker network
docker network create devops-network
docker network connect devops-network jenkins
docker network connect devops-network sonarqube
# Then use: http://sonarqube:9000
```

**If both are local:**
```
# Use localhost
Server URL: http://localhost:9000

# Verify SonarQube is accessible
curl http://localhost:9000/api/system/status
```

### Problem: Old projects appear in SonarQube

**Solution:**
```
1. Go to Administration → Projects → Management
2. Select projects to delete
3. Click "Delete"

Or delete via API:
curl -u admin:admin \
  -X POST \
  "http://localhost:9000/api/projects/delete?project=old-project-key"
```

---

## 📈 Best Practices

### 1. Run Analysis Regularly
```bash
# On every commit (via Jenkins)
# Or at least daily

# Local development
./mvnw clean test sonar:sonar
```

### 2. Fix Issues Immediately
- Don't let technical debt accumulate
- Fix bugs before adding features
- Refactor code smells during development

### 3. Write Tests
- Aim for 80%+ coverage
- Test edge cases
- Test error handling

### 4. Review New Issues
- Focus on "New Code" issues
- Don't ignore warnings
- Understand why issues are flagged

### 5. Customize Quality Gates
```
For learning: Lenient gates, focus on critical issues
For production: Strict gates, enforce all standards
```

---

## ✅ SonarQube Setup Checklist

- [ ] SonarQube installed and running
- [ ] Accessible at http://localhost:9000
- [ ] Admin password changed
- [ ] Token generated for Jenkins
- [ ] Quality Gate configured
- [ ] Maven projects configured with JaCoCo
- [ ] Jenkins integration configured
- [ ] Test analysis run successfully
- [ ] Reports visible in SonarQube dashboard
- [ ] Quality Gate integration working in Jenkins

---

## 🚀 Next Steps

1. ✅ Complete SonarQube setup
2. ➡️ Configure all microservices with SonarQube properties
3. ➡️ Run Jenkins pipeline to see full integration
4. ➡️ Review and fix code quality issues
5. ➡️ Adjust quality gates based on project needs

---

## 📚 Additional Resources

- **SonarQube Documentation:** https://docs.sonarqube.org/latest/
- **SonarQube Rules:** https://rules.sonarsource.com/java/
- **JaCoCo Documentation:** https://www.jacoco.org/jacoco/trunk/doc/
- **Code Quality Best Practices:** https://www.sonarsource.com/learn/

---

**Remember:** Code quality is not about perfection, it's about continuous improvement! 🎯
