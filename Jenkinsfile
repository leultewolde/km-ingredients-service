pipeline {
    agent any
    environment {
        IMAGE_NAME = 'ivtheforth/km-ingredients-service'
        IMAGE_TAG = "${env.GIT_COMMIT}"
        IMAGE_TAG_TIMESTAMP = "${env.BUILD_ID}-${env.BUILD_NUMBER}-${env.GIT_COMMIT}"
        REGISTRY = 'docker.io'
    }
    options {
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    try {
                        deleteDir()
                        checkout scm
                    } catch (err) {
                        echo "Git checkout failed, retrying with forced cleanup..."
                        deleteDir()
                        checkout scm
                    }
                }
                echo "Building ${env.JOB_NAME}..."
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
        stage('Run SonarQube Analysis') {
            steps {
                withSonarQubeEnv('My SonarQube Server') {
                    sh './gradlew sonar'
                }
            }
        }
        stage('Wait for SonarQube Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Build Docker Image') {
          steps {
            script {
              dockerImage = docker.build("${env.IMAGE_NAME}:${env.BUILD_NUMBER}")
            }
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
    post {
            // Clean after build
            always {
                cleanWs(cleanWhenNotBuilt: false,
                        deleteDirs: true,
                        disableDeferredWipeout: true,
                        notFailBuild: true,
                        patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                                   [pattern: '.propsfile', type: 'EXCLUDE']])
            }
    }
}
