/**
 * Shared Jenkins Pipeline Library for E-Shop Microservices
 * 
 * This file contains reusable functions for building, testing, and deploying microservices.
 * Each function is well-documented to help you understand what it does.
 */

/**
 * CONCEPT: Git Diff Detection
 * This function checks which files have changed in the current commit/PR.
 * If a service's files changed, we need to rebuild it.
 * 
 * @param servicePath - The relative path to the service (e.g., "auth-jwt-service")
 * @return true if the service has changes, false otherwise
 */
def hasChanges(String servicePath) {
    // Get the list of changed files compared to the main branch
    def changes = sh(
        script: "git diff --name-only origin/main...HEAD",
        returnStdout: true
    ).trim()
    
    // Check if any changed file is in the service directory
    def hasServiceChanges = changes.contains(servicePath)
    
    echo "📝 Checking changes for ${servicePath}: ${hasServiceChanges ? 'YES ✅' : 'NO ❌'}"
    return hasServiceChanges
}

/**
 * CONCEPT: Maven Build
 * Compiles Java code and packages it into a JAR file.
 * Maven handles dependencies, testing, and packaging.
 * 
 * @param serviceName - Name of the service to build
 * @param servicePath - Path to the service directory
 */
def buildService(String serviceName, String servicePath) {
    echo "🔨 Building ${serviceName}..."
    
    dir(servicePath) {
        // Clean previous builds and package new JAR (skip tests for faster builds)
        sh './mvnw clean package -DskipTests'
    }
    
    echo "✅ ${serviceName} built successfully!"
}

/**
 * CONCEPT: Unit Testing
 * Runs automated tests to verify code correctness.
 * Tests ensure your changes don't break existing functionality.
 * 
 * @param serviceName - Name of the service to test
 * @param servicePath - Path to the service directory
 */
def runTests(String serviceName, String servicePath) {
    echo "🧪 Running tests for ${serviceName}..."
    
    dir(servicePath) {
        try {
            // Run Maven tests
            sh './mvnw test'
            echo "✅ All tests passed for ${serviceName}!"
        } catch (Exception e) {
            echo "❌ Tests failed for ${serviceName}!"
            throw e
        }
    }
}

/**
 * CONCEPT: Code Quality Analysis with SonarQube
 * SonarQube analyzes code for bugs, vulnerabilities, and code smells.
 * It provides a quality gate to ensure code meets standards.
 * 
 * @param serviceName - Name of the service to analyze
 * @param servicePath - Path to the service directory
 * @param sonarUrl - URL of your SonarQube server
 */
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
    
    echo "✅ SonarQube analysis completed for ${serviceName}!"
}

/**
 * CONCEPT: Docker Image Build
 * Creates a Docker image containing your application and all its dependencies.
 * Images are portable and can run anywhere Docker is installed.
 * 
 * @param serviceName - Name of the service
 * @param servicePath - Path to the service directory
 * @param imageTag - Tag for the Docker image (e.g., "latest" or "v1.0.0")
 */
def buildDockerImage(String serviceName, String servicePath, String imageTag = 'latest') {
    echo "🐳 Building Docker image for ${serviceName}..."
    
    dir(servicePath) {
        // Build image with service name and tag
        sh "docker build -t ${serviceName}:${imageTag} ."
    }
    
    echo "✅ Docker image ${serviceName}:${imageTag} built successfully!"
}

/**
 * CONCEPT: Docker Registry Push
 * Uploads your Docker image to a registry (like Docker Hub or a private registry).
 * This makes the image available for deployment.
 * 
 * @param serviceName - Name of the service
 * @param imageTag - Tag of the Docker image
 * @param registry - Docker registry URL (optional, defaults to Docker Hub)
 */
def pushDockerImage(String serviceName, String imageTag = 'latest', String registry = '') {
    echo "📤 Pushing Docker image ${serviceName}:${imageTag}..."
    
    def fullImageName = registry ? "${registry}/${serviceName}:${imageTag}" : "${serviceName}:${imageTag}"
    
    // Tag with registry if provided
    if (registry) {
        sh "docker tag ${serviceName}:${imageTag} ${fullImageName}"
    }
    
    // Push to registry
    sh "docker push ${fullImageName}"
    
    echo "✅ Image pushed successfully!"
}

/**
 * CONCEPT: Quality Gate Check
 * Waits for SonarQube to finish analysis and checks if code meets quality standards.
 * Pipeline fails if quality gate fails.
 */
def checkQualityGate() {
    echo "⏳ Waiting for SonarQube Quality Gate..."
    
    timeout(time: 5, unit: 'MINUTES') {
        def qg = waitForQualityGate()
        if (qg.status != 'OK') {
            error "❌ Quality Gate failed: ${qg.status}"
        }
    }
    
    echo "✅ Quality Gate passed!"
}

/**
 * CONCEPT: Complete Service Pipeline
 * Orchestrates all steps for a single service: build, test, analyze, dockerize.
 * This is the main function you'll call for each service.
 * 
 * @param serviceName - Name of the service
 * @param servicePath - Path to the service directory
 * @param skipTests - Whether to skip tests (default: false)
 * @param skipSonar - Whether to skip SonarQube analysis (default: false)
 */
def processMicroservice(String serviceName, String servicePath, boolean skipTests = false, boolean skipSonar = false) {
    stage("🔨 Build ${serviceName}") {
        buildService(serviceName, servicePath)
    }
    
    if (!skipTests) {
        stage("🧪 Test ${serviceName}") {
            runTests(serviceName, servicePath)
        }
    }
    
    if (!skipSonar) {
        stage("🔍 SonarQube ${serviceName}") {
            sonarAnalysis(serviceName, servicePath)
        }
    }
    
    stage("🐳 Docker Build ${serviceName}") {
        buildDockerImage(serviceName, servicePath, env.BUILD_NUMBER)
        buildDockerImage(serviceName, servicePath, 'latest')
    }
}

/**
 * CONCEPT: Notification
 * Sends notifications about build status to Slack, email, etc.
 * 
 * @param status - Build status (SUCCESS, FAILURE, etc.)
 * @param message - Custom message
 */
def notify(String status, String message = '') {
    def color = status == 'SUCCESS' ? 'good' : 'danger'
    def emoji = status == 'SUCCESS' ? '✅' : '❌'
    
    echo "${emoji} Build ${status}: ${message}"
    
    // Uncomment and configure for Slack notifications
    // slackSend(
    //     color: color,
    //     message: "${emoji} Build ${status}\n${message}"
    // )
}

// Return this to make functions available
return this
