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
//         stage('Docker Build and Push') {
//             steps {
//                 script {
//                     withVault([
//                         vaultSecrets: [[
//                             path: '/v1/secret/data/jenkins/docker',
//                             engineVersion: 2,
//                             secretValues: [[envVar: 'DOCKER_USERNAME', vaultKey: 'username'],
//                                            [envVar: 'DOCKER_PASSWORD', vaultKey: 'password']]
//                         ]],
//                         vaultUrl: 'https://vault.leultewolde.com',
//                         vaultCredentialId: 'vault-root-token'
//                     ]) {
//                         sh '''
//                             echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin ${REGISTRY}
//                             docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
//                             docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:${IMAGE_TAG_TIMESTAMP}
//                             docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
//                             docker push ${IMAGE_NAME}:${IMAGE_TAG}
//                             docker push ${IMAGE_NAME}:${IMAGE_TAG_TIMESTAMP}
//                             docker push ${IMAGE_NAME}:latest
//                         '''
//                     }
//                 }
//             }
//         }
        stage('Docker Build and Push') {
          steps {
            script {
              docker.image('docker:24.0.5-cli').inside('--privileged -v /var/run/docker.sock:/var/run/docker.sock') {
                withVault([
                  vaultSecrets: [[
                    path: '/v1/secret/data/jenkins/docker',
                    engineVersion: 2,
                    secretValues: [
                      [envVar: 'DOCKER_USERNAME', vaultKey: 'username'],
                      [envVar: 'DOCKER_PASSWORD', vaultKey: 'password']
                    ]
                  ]],
                  vaultUrl: 'https://vault.leultewolde.com',
                  vaultCredentialId: 'vault-root-token'
                ]) {
                  sh '''
                    echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin ${REGISTRY}
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                    docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:${IMAGE_TAG_TIMESTAMP}
                    docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${IMAGE_NAME}:${IMAGE_TAG_TIMESTAMP}
                    docker push ${IMAGE_NAME}:latest
                  '''
                }
              }
            }
          }
        }

        stage('Promote') {
            when {
                expression { params.ENVIRONMENT != null }
            }
            steps {
                script {
                    withVault([
                        vaultSecrets: [[
                            path: '/v1/secret/data/jenkins/kubeconfig',
                            engineVersion: 2,
                            secretValues: [[envVar: 'KUBE_CONFIG', vaultKey: 'config']]
                        ]],
                        vaultUrl: 'https://vault.leultewolde.com',
                        vaultCredentialId: 'vault-root-token'
                    ]) {
                        sh '''
                            mkdir -p ~/.kube
                            echo "$KUBE_CONFIG" > ~/.kube/config
                            chmod 600 ~/.kube/config
                            kubectl set image deployment/km-ingredients-service km-ingredients-service=$IMAGE_NAME:$IMAGE_TAG -n $ENVIRONMENT
                        '''
                    }
                }
            }
        }
        stage('Deploy to K3s') {
            steps {
                script {
                    withVault([
                        vaultSecrets: [[
                            path: '/v1/secret/data/jenkins/kubeconfig',
                            engineVersion: 2,
                            secretValues: [[envVar: 'KUBE_CONFIG', vaultKey: 'config']]
                        ]],
                        vaultUrl: 'https://vault.leultewolde.com',
                        vaultCredentialId: 'vault-root-token'
                    ]) {
                        sh '''
                            mkdir -p ~/.kube
                            echo "$KUBE_CONFIG" > ~/.kube/config
                            chmod 600 ~/.kube/config
                            kubectl set image deployment/km-ingredients-service km-ingredients-service=$IMAGE_NAME:$IMAGE_TAG
                        '''
                    }
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
