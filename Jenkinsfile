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
        }
        stage('Gradle Build & Test') {
			steps {
				sh '''
                    chmod +x ./gradlew
                    ./gradlew clean build test
                '''
            }
        }
        stage('Run SonarQube Analysis') {
			steps {
				withSonarQubeEnv('SonarQube Server') {
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

        stage('Docker Build and Push') {
			steps {
				script {
					withVault([
                         vaultSecrets: [[
                             path: 'jenkins/docker',
                             engineVersion: 2,
                             secretValues: [[envVar: 'DOCKER_USERNAME', vaultKey: 'username'],
                                            [envVar: 'DOCKER_PASSWORD', vaultKey: 'password']]
                         ]],
                         vaultUrl: 'https://vault.leultewolde.com',
                         vaultCredentialId: 'vault-credentials'
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

        stage('Deploy to K3s') {
			steps {
				script {
					withVaultSecrets('jenkins/kubeconfig') {
						sh '''
							echo "GIT_COMMIT = ${env.GIT_COMMIT}"
                            mkdir -p ~/.kube
                            echo "$KUBE_CONFIG" > ~/.kube/config
                            chmod 600 ~/.kube/config
                            kubectl set image deployment/km-ingredients-service \
                                km-ingredients-service=${IMAGE_NAME}:${IMAGE_TAG}
                            kubectl set image deployment/km-ingredients-service \
    							km-ingredients-service=$IMAGE_NAME:$IMAGE_TAG -n hidmo
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
