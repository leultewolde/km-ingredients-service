pipeline {
  agent {
    docker {
      image 'bitnami/kubectl:latest'
      args  '-v /root/.kube:/root/.kube:ro'  // Adjust for kubeconfig location
    }
  }

  environment {
    DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
    DOCKER_IMAGE = 'ivtheforth/km-ingredients-service'
    IMAGE_TAG = "latest"
  }

  stages {
    stage('Build & Test') {
      agent {
        docker {
          image 'gradle:8.5.0-jdk17'
          args '-v $HOME/.gradle:/home/gradle/.gradle'
        }
      }
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
        sh """
          kubectl rollout restart deployment km-ingredients-service -n lwt-api
        """
      }
    }
  }
}
