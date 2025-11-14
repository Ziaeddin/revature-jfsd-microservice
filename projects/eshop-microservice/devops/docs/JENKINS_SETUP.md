# 🔧 Jenkins Setup Guide

This guide will help you install and configure Jenkins for your E-Shop microservices CI/CD pipeline.

---

## 📋 Table of Contents

1. [What is Jenkins?](#what-is-jenkins)
2. [Installation Options](#installation-options)
3. [Installing Jenkins on macOS](#installing-jenkins-on-macos)
4. [Installing Jenkins with Docker](#installing-jenkins-with-docker)
5. [Initial Configuration](#initial-configuration)
6. [Installing Required Plugins](#installing-required-plugins)
7. [Configuring Tools](#configuring-tools)
8. [Creating Your First Pipeline](#creating-your-first-pipeline)
9. [Troubleshooting](#troubleshooting)

---

## 🤔 What is Jenkins?

**Jenkins** is an open-source automation server that helps you:
- Build your code automatically
- Run tests on every commit
- Deploy applications
- Schedule repetitive tasks

**Think of Jenkins as:**
- A robot that builds your code for you
- A guard that checks code quality before deployment
- A scheduler that runs tasks at specific times

**Key Features:**
- **Pipeline as Code:** Define your build process in code (Jenkinsfile)
- **Plugins:** Extend functionality (1800+ plugins available)
- **Distributed Builds:** Run builds on multiple machines
- **Easy Configuration:** Web-based interface

---

## 🎯 Installation Options

You have three main options:

### Option 1: Docker (Recommended for Learning)
- ✅ Easy to install and remove
- ✅ Isolated from your system
- ✅ Consistent across all platforms
- ❌ Requires Docker knowledge

### Option 2: Homebrew (macOS)
- ✅ Native installation
- ✅ Easy to manage
- ✅ Good for local development
- ❌ macOS only

### Option 3: Manual Installation
- ✅ Full control
- ✅ Production-ready
- ❌ More complex
- ❌ Platform-specific

---

## 🍎 Installing Jenkins on macOS

### Method 1: Using Homebrew (Recommended)

#### Step 1: Install Homebrew (if not installed)
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

#### Step 2: Install Jenkins
```bash
# Install Jenkins LTS (Long Term Support)
brew install jenkins-lts

# Or latest version
brew install jenkins
```

#### Step 3: Start Jenkins
```bash
# Start Jenkins as a service (runs in background)
brew services start jenkins-lts

# Or run Jenkins in foreground (see logs)
jenkins-lts
```

#### Step 4: Verify Installation
```bash
# Check if Jenkins is running
brew services list | grep jenkins

# Jenkins runs on port 8080 by default
# Open in browser: http://localhost:8080
```

#### Step 5: Get Initial Password
```bash
# Jenkins creates an initial admin password
cat ~/.jenkins/secrets/initialAdminPassword

# Copy this password for first-time setup
```

### Method 2: Download WAR File

#### Step 1: Download Jenkins
```bash
# Download latest Jenkins WAR file
curl -O https://get.jenkins.io/war-stable/latest/jenkins.war

# Create Jenkins directory
mkdir ~/jenkins
cd ~/jenkins
```

#### Step 2: Run Jenkins
```bash
# Run Jenkins (requires Java 11 or 17)
java -jar jenkins.war --httpPort=8080

# Or specify different port
java -jar jenkins.war --httpPort=9090
```

#### Step 3: Access Jenkins
- Open browser: http://localhost:8080
- Find initial password in console output

---

## 🐳 Installing Jenkins with Docker

### Step 1: Create Jenkins Docker Setup

Create a `docker-compose.yml` file:

```yaml
version: '3.8'

services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins-server
    restart: unless-stopped
    user: root
    ports:
      - "8090:8080"      # Jenkins web UI (changed to 8090 to avoid conflict)
      - "50000:50000"    # Jenkins agent port
    volumes:
      - jenkins-data:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock  # Allow Jenkins to use Docker
    environment:
      - JAVA_OPTS=-Djenkins.install.runSetupWizard=false  # Skip setup wizard (optional)
    networks:
      - jenkins-network

  # Optional: Jenkins agent for distributed builds
  jenkins-agent:
    image: jenkins/inbound-agent
    container_name: jenkins-agent
    restart: unless-stopped
    depends_on:
      - jenkins
    environment:
      - JENKINS_URL=http://jenkins:8080
      - JENKINS_AGENT_NAME=agent1
    networks:
      - jenkins-network

volumes:
  jenkins-data:
    # Persists Jenkins configuration and jobs

networks:
  jenkins-network:
    driver: bridge
```

### Step 2: Start Jenkins
```bash
# Start Jenkins container
docker-compose up -d

# View logs
docker-compose logs -f jenkins

# Get initial admin password
docker exec jenkins-server cat /var/jenkins_home/secrets/initialAdminPassword
```

### Step 3: Install Docker inside Jenkins Container (Required)

Since Jenkins needs to build Docker images:

```bash
# Enter Jenkins container
docker exec -it -u root jenkins-server bash

# Install Docker CLI
apt-get update
apt-get install -y docker.io

# Verify Docker works
docker --version

# Exit container
exit
```

### Step 4: Access Jenkins
- Open browser: http://localhost:8090
- Use initial password from step 2

---

## ⚙️ Initial Configuration

### Step 1: Unlock Jenkins

1. Open Jenkins in browser: http://localhost:8080
2. Enter the initial admin password you retrieved earlier
3. Click "Continue"

### Step 2: Install Plugins

**Option A: Install Suggested Plugins (Recommended for beginners)**
- Click "Install suggested plugins"
- Wait for installation to complete (~5 minutes)

**Option B: Select Plugins to Install (Advanced)**
- Click "Select plugins to install"
- Choose plugins you need (see next section)

### Step 3: Create Admin User

1. Fill in the form:
   - Username: `admin`
   - Password: `your-secure-password`
   - Full name: `Your Name`
   - Email: `your-email@example.com`

2. Click "Save and Continue"

### Step 4: Configure Jenkins URL

1. Jenkins URL: `http://localhost:8080` (or your server URL)
2. Click "Save and Finish"
3. Click "Start using Jenkins"

---

## 🔌 Installing Required Plugins

### Essential Plugins for Our Pipeline

Navigate to: **Manage Jenkins → Manage Plugins → Available**

1. **Pipeline Plugins** (Usually pre-installed)
   - Pipeline
   - Pipeline: Stage View
   - Pipeline: GitHub Groovy Libraries

2. **Source Control**
   - Git
   - GitHub
   - GitHub Branch Source

3. **Build Tools**
   - Maven Integration
   - Maven Invoker

4. **Code Quality**
   - SonarQube Scanner
   - Warnings Next Generation

5. **Docker**
   - Docker
   - Docker Pipeline
   - Docker Commons

6. **Notifications**
   - Email Extension
   - Slack Notification (optional)

7. **Utilities**
   - Workspace Cleanup
   - Timestamper
   - Build Timeout

### Installing Plugins

**Method 1: Web Interface**
```
1. Go to Manage Jenkins → Manage Plugins
2. Click "Available" tab
3. Search for plugin name
4. Check the checkbox
5. Click "Install without restart" or "Download now and install after restart"
```

**Method 2: Jenkins CLI**
```bash
# Download Jenkins CLI
wget http://localhost:8080/jnlpJars/jenkins-cli.jar

# Install plugin
java -jar jenkins-cli.jar -s http://localhost:8080/ install-plugin docker-plugin

# Restart Jenkins
java -jar jenkins-cli.jar -s http://localhost:8080/ safe-restart
```

---

## 🛠️ Configuring Tools

### Step 1: Configure JDK

1. Go to: **Manage Jenkins → Global Tool Configuration**
2. Scroll to "JDK" section
3. Click "Add JDK"
4. Configure:
   ```
   Name: JDK-21
   JAVA_HOME: /usr/local/opt/openjdk@21  (macOS Homebrew)
   
   Or use auto-installer:
   ☑ Install automatically
   Version: jdk-21.0.1
   ```

**Find your Java path:**
```bash
# macOS
/usr/libexec/java_home -v 21

# Linux
update-alternatives --config java
```

### Step 2: Configure Maven

1. In **Global Tool Configuration**
2. Scroll to "Maven" section
3. Click "Add Maven"
4. Configure:
   ```
   Name: Maven-3.9
   
   Option A: Use existing Maven
   MAVEN_HOME: /usr/local/opt/maven  (macOS Homebrew)
   
   Option B: Auto-install (recommended)
   ☑ Install automatically
   Version: 3.9.5
   ```

**Find your Maven path:**
```bash
which mvn
# Shows: /usr/local/bin/mvn
# Maven home is usually: /usr/local/opt/maven
```

### Step 3: Configure Git

1. In **Global Tool Configuration**
2. Scroll to "Git" section
3. Usually auto-detected
4. Verify:
   ```bash
   which git
   # Jenkins finds this automatically
   ```

### Step 4: Configure Docker

1. Docker should be available on the host
2. Verify Jenkins can access Docker:
   ```bash
   # If using Docker Jenkins
   docker exec -it jenkins-server docker --version
   
   # If using native Jenkins
   docker --version
   ```

### Step 5: Configure SonarQube

1. Go to: **Manage Jenkins → Configure System**
2. Scroll to "SonarQube servers"
3. Click "Add SonarQube"
4. Configure:
   ```
   Name: SonarQube
   Server URL: http://localhost:9000
   
   Server authentication token:
   - Add → Jenkins
   - Kind: Secret text
   - Secret: [Your SonarQube token]
   - ID: sonarqube-token
   ```

**Get SonarQube Token:**
```
1. Login to SonarQube: http://localhost:9000
2. Go to: My Account → Security → Generate Tokens
3. Name: jenkins-token
4. Generate and copy the token
```

---

## 🚀 Creating Your First Pipeline

### Step 1: Create a New Pipeline Job

1. Click "New Item" on Jenkins dashboard
2. Enter name: `eshop-microservices-pipeline`
3. Select "Pipeline"
4. Click "OK"

### Step 2: Configure Pipeline

**Option A: Pipeline from SCM (Recommended)**

In the Pipeline section:
```
Definition: Pipeline script from SCM

SCM: Git

Repository URL: https://github.com/Ziaeddin/revature-jfsd-microservice.git

Credentials: (Add if private repository)
  - Add → Jenkins
  - Kind: Username with password
  - Username: [Your GitHub username]
  - Password: [Your GitHub personal access token]

Branches to build: */main

Script Path: Jenkinsfile
```

**Option B: Pipeline Script (For testing)**

In the Pipeline section:
```groovy
pipeline {
    agent any
    
    stages {
        stage('Hello') {
            steps {
                echo 'Hello, Jenkins!'
            }
        }
        
        stage('Check Tools') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
                sh 'docker --version'
            }
        }
    }
}
```

### Step 3: Run the Pipeline

1. Click "Save"
2. Click "Build Now"
3. Click on build number (e.g., #1)
4. Click "Console Output" to see logs

---

## 🔧 Advanced Configuration

### Setting Up GitHub Webhooks

**Automatic builds on git push:**

1. In Jenkins job → Configure
2. Under "Build Triggers"
3. Check "GitHub hook trigger for GITScm polling"
4. Save

5. In GitHub repository → Settings → Webhooks
6. Add webhook:
   ```
   Payload URL: http://your-jenkins-url:8080/github-webhook/
   Content type: application/json
   Events: Just the push event
   ```

### Configuring Email Notifications

1. Go to: **Manage Jenkins → Configure System**
2. Scroll to "Extended E-mail Notification"
3. Configure SMTP:
   ```
   SMTP server: smtp.gmail.com
   SMTP port: 587
   Username: your-email@gmail.com
   Password: [App-specific password]
   Use SSL: Yes
   Default Recipients: your-email@gmail.com
   ```

### Setting Up Credentials

**For Docker Registry:**
```
Manage Jenkins → Manage Credentials
→ (global) → Add Credentials

Kind: Username with password
Username: [Docker Hub username]
Password: [Docker Hub password]
ID: dockerhub-credentials
Description: Docker Hub Credentials
```

**For SonarQube:**
```
Kind: Secret text
Secret: [SonarQube token]
ID: sonarqube-token
Description: SonarQube Authentication Token
```

---

## 🔍 Troubleshooting

### Problem: Jenkins won't start

**Symptoms:** Port already in use

**Solutions:**
```bash
# Check what's using port 8080
lsof -i :8080

# Stop Jenkins
brew services stop jenkins-lts

# Start on different port
jenkins-lts --httpPort=9090

# Or configure default port
# Edit: /usr/local/opt/jenkins-lts/homebrew.mxcl.jenkins-lts.plist
# Change: --httpPort=8080 to --httpPort=9090
```

### Problem: Can't find initial password

**Solutions:**
```bash
# Homebrew installation
cat ~/.jenkins/secrets/initialAdminPassword

# Docker installation
docker exec jenkins-server cat /var/jenkins_home/secrets/initialAdminPassword

# WAR file installation
cat [jenkins-home]/secrets/initialAdminPassword
# Where jenkins-home is shown in console output
```

### Problem: Maven not found in pipeline

**Symptoms:** `mvn: command not found`

**Solutions:**
```groovy
// In Jenkinsfile, specify Maven tool
pipeline {
    agent any
    tools {
        maven 'Maven-3.9'  // Must match name in Global Tool Configuration
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn --version'
            }
        }
    }
}
```

### Problem: Docker permission denied

**Symptoms:** `permission denied while trying to connect to Docker daemon`

**Solutions:**
```bash
# Add Jenkins user to docker group (native installation)
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins

# For Docker Jenkins, ensure volume is mounted
# In docker-compose.yml:
volumes:
  - /var/run/docker.sock:/var/run/docker.sock
```

### Problem: Git credentials not working

**Solutions:**
```
1. Generate GitHub Personal Access Token:
   - GitHub → Settings → Developer settings → Personal access tokens
   - Generate new token (classic)
   - Select scopes: repo, admin:repo_hook
   - Copy token

2. Add to Jenkins:
   - Manage Jenkins → Manage Credentials
   - Add credentials
   - Username: GitHub username
   - Password: Personal access token (not your GitHub password!)
```

### Problem: SonarQube connection failed

**Symptoms:** `Unable to connect to SonarQube`

**Solutions:**
```bash
# 1. Check SonarQube is running
curl http://localhost:9000/api/system/status

# 2. If using Docker Jenkins and local SonarQube, use host.docker.internal
# In Jenkins System Configuration:
Server URL: http://host.docker.internal:9000

# 3. Verify token is valid
# Login to SonarQube → My Account → Security → Check token exists
```

---

## 📊 Monitoring Jenkins

### View System Information
```
Manage Jenkins → System Information
- Shows Jenkins version
- Java version
- Environment variables
- System properties
```

### Check System Logs
```
Manage Jenkins → System Log
- Shows Jenkins internal logs
- Useful for debugging
```

### Monitor Disk Space
```
Manage Jenkins → Disk Usage
- Shows disk usage per job
- Clean up old builds: Job → Configure → Discard old builds
```

---

## 🎯 Best Practices

### 1. Security
- ✅ Use strong passwords
- ✅ Enable CSRF protection
- ✅ Use credentials plugin for secrets
- ✅ Restrict access with role-based security
- ❌ Never commit credentials in Jenkinsfile

### 2. Performance
- ✅ Clean up old builds regularly
- ✅ Use build agents for heavy tasks
- ✅ Cache Maven dependencies
- ✅ Use parallel stages when possible

### 3. Pipeline Design
- ✅ Keep Jenkinsfile in source control
- ✅ Use shared libraries for common code
- ✅ Fail fast (run tests before building images)
- ✅ Send notifications on failures

### 4. Backup
- ✅ Backup Jenkins home directory regularly
- ✅ Export job configurations
- ✅ Version control your Jenkinsfiles

```bash
# Backup Jenkins (Homebrew)
tar -czf jenkins-backup-$(date +%Y%m%d).tar.gz ~/.jenkins

# Backup Jenkins (Docker)
docker run --rm \
  --volumes-from jenkins-server \
  -v $(pwd):/backup \
  busybox tar czf /backup/jenkins-backup-$(date +%Y%m%d).tar.gz /var/jenkins_home
```

---

## 📚 Useful Jenkins URLs

After starting Jenkins:

- **Dashboard:** http://localhost:8080
- **Blue Ocean UI:** http://localhost:8080/blue
- **Configure System:** http://localhost:8080/configure
- **Manage Plugins:** http://localhost:8080/pluginManager
- **System Log:** http://localhost:8080/log
- **Script Console:** http://localhost:8080/script (for debugging)

---

## ✅ Jenkins Setup Checklist

- [ ] Jenkins installed and running
- [ ] Initial admin password retrieved
- [ ] Admin user created
- [ ] Required plugins installed
- [ ] JDK configured (Java 21)
- [ ] Maven configured (Maven 3.9)
- [ ] Git configured
- [ ] Docker accessible
- [ ] SonarQube connection configured
- [ ] Test pipeline created and runs successfully
- [ ] GitHub webhook configured (optional)
- [ ] Backup strategy in place

---

## 🚀 Next Steps

1. ✅ Complete this Jenkins setup
2. ➡️ Follow [SONARQUBE_SETUP.md](./SONARQUBE_SETUP.md) to set up code quality analysis
3. ➡️ Read [DEVOPS_TRAINING.md](../training/DEVOPS_TRAINING.md) to understand the full pipeline
4. ➡️ Run your first build!

---

**Need Help?**
- Jenkins Documentation: https://www.jenkins.io/doc/
- Jenkins Plugins: https://plugins.jenkins.io/
- Jenkins Community: https://community.jenkins.io/

Happy Building! 🎉
