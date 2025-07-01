pipeline {
    agent any
    environment {
        IMAGE_NAME = 'ivtheforth/km-ingredients-service'
        IMAGE_TAG = "${env.GIT_COMMIT}"
        IMAGE_TAG_TIMESTAMP = "${env.BUILD_ID}-${env.BUILD_NUMBER}-${env.GIT_COMMIT}"
        REGISTRY = 'docker.io'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Set up JDK') {
            tools {
                jdk 'temurin-24'
            }
            steps {
                echo 'JDK configured'
            }
        }
        stage('Grant execute permission to Gradle') {
            steps {
                sh 'chmod +x ./gradlew'
            }
        }
        stage('Run tests') {
            steps {
                sh './gradlew test'
            }
        }
        stage('Run SonarQube analysis') {
            environment {
                SONAR_TOKEN = credentials('sonarqube-token')
            }
            steps {
                sh './gradlew sonar'
            }
        }
        stage('Wait for SonarQube Quality Gate') {
            environment {
                SONAR_TOKEN = credentials('sonarqube-token')
                SONAR_HOST = credentials('sonar_host_url')
            }
            steps {
                sh '''
                TASK_ID=$(cat build/sonar/report-task.txt | grep ceTaskId | cut -d= -f2)
                echo "Waiting for SonarQube quality gate result for task $TASK_ID"
                STATUS="PENDING"
                for i in {1..30}; do
                    sleep 5
                    STATUS=$(curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/ce/task?id=$TASK_ID" | jq -r '.task.status')
                    if [ "$STATUS" = "SUCCESS" ]; then
                        break
                    elif [ "$STATUS" = "FAILED" ]; then
                        echo "SonarQube analysis failed."
                        exit 1
                    fi
                done
                ANALYSIS_ID=$(curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/ce/task?id=$TASK_ID" | jq -r '.task.analysisId')
                QG_STATUS=$(curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/qualitygates/project_status?analysisId=$ANALYSIS_ID" | jq -r '.projectStatus.status')
                echo "Quality Gate status: $QG_STATUS"
                if [ "$QG_STATUS" != "OK" ]; then
                    echo "Quality Gate failed. Failing pipeline."
                    exit 1
                fi
                '''
            }
        }
        stage('Create GitHub Issues from SonarQube') {
            environment {
                GH_TOKEN = credentials('github_token')
                SONAR_TOKEN = credentials('sonarqube-token')
                SONAR_HOST = credentials('sonar_host_url')
            }
            steps {
                sh '''
                sudo apt-get update
                sudo apt-get install -y gh jq
                PROJECT_KEY="hidmo-km-ingredients-service"
                echo "Fetching existing SonarQube-related GitHub issues..."
                EXISTING_KEYS=$(gh issue list --label "sonarqube" --limit 100 --json body --jq '.[].body' | grep -o 'sonar-key:[^ ]*' | cut -d':' -f2)
                echo "Querying unresolved issues from SonarQube..."
                curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/issues/search?componentKeys=$PROJECT_KEY&resolved=false" | jq -c '.issues[]' | while read -r issue; do
                    KEY=$(echo "$issue" | jq -r '.key')
                    if echo "$EXISTING_KEYS" | grep -q "$KEY"; then
                        echo "Issue for SonarQube key $KEY already exists. Skipping."
                        continue
                    fi
                    TITLE=$(echo "$issue" | jq -r '.message')
                    RULE=$(echo "$issue" | jq -r '.rule')
                    FILE=$(echo "$issue" | jq -r '.component')
                    LINE=$(echo "$issue" | jq -r '.line')
                    ISSUE_BODY="**Rule**: $RULE\n**File**: $FILE\n**Line**: $LINE\n**Key**: $KEY\n<!-- sonar-key:$KEY -->"
                    echo "Creating GitHub issue for $KEY"
                    gh issue create --title "$TITLE" --body "$ISSUE_BODY" --label "sonarqube"
                done
                '''
            }
        }
        stage('Docker Build and Push') {
            steps {
                script {
                    docker.withRegistry("https://${env.REGISTRY}", 'docker-hub-credentials') {
                        sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
                        sh "docker tag ${env.IMAGE_NAME}:${env.IMAGE_TAG} ${env.IMAGE_NAME}:${env.IMAGE_TAG_TIMESTAMP}"
                        sh "docker tag ${env.IMAGE_NAME}:${env.IMAGE_TAG} ${env.IMAGE_NAME}:latest"
                        sh "docker push ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                        sh "docker push ${env.IMAGE_NAME}:${env.IMAGE_TAG_TIMESTAMP}"
                        sh "docker push ${env.IMAGE_NAME}:latest"
                    }
                }
            }
        }
        stage('Promote') {
            when {
                expression { params.ENVIRONMENT != null }
            }
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KCFG')]) {
                    sh '''
                    mkdir -p ~/.kube
                    cp "$KCFG" ~/.kube/config
                    chmod 600 ~/.kube/config
                    kubectl set image deployment/km-ingredients-service km-ingredients-service=${env.IMAGE_NAME}:${env.IMAGE_TAG} -n ${params.ENVIRONMENT}
                    '''
                }
            }
        }
        stage('Deploy to K3s') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KCFG')]) {
                    sh '''
                    mkdir -p ~/.kube
                    cp "$KCFG" ~/.kube/config
                    chmod 600 ~/.kube/config
                    kubectl set image deployment/km-ingredients-service km-ingredients-service=${env.IMAGE_NAME}:${env.IMAGE_TAG}
                    '''
                }
            }
        }
    }
}
