pipeline {
  agent any

  environment {
    DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
    DOCKER_IMAGE = 'ivtheforth/km-ingredients-service'
    IMAGE_TAG = "latest"
  }

  stages {
    stage('Build & Test') {
      steps {
        sh './gradlew clean test'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh """
          docker build -t $DOCKER_IMAGE:$IMAGE_TAG .
          echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin
          docker push $DOCKER_IMAGE:$IMAGE_TAG
        """
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        sh 'kubectl rollout restart deployment km-ingredients-service -n lwt-api'
      }
    }
  }
}
