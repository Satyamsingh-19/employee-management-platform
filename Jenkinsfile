pipeline {
agent any


tools {
    maven 'Maven-3.9'
}

environment {
    IMAGE_NAME = 'employee-management'
    CONTAINER_NAME = 'employee-app'
}

stages {

    stage('Checkout') {
        steps {
            git branch: 'main',
                url: 'https://github.com/Satyamsingh-19/employee-management-platform.git'
        }
    }

    stage('Verify Environment') {
        steps {
            sh 'java -version'
            sh 'mvn -version'
            sh 'docker --version'
        }
    }

    stage('Build') {
        steps {
            sh 'mvn clean package -DskipTests'
        }
    }

    stage('Archive Artifact') {
        steps {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }

    stage('Docker Build') {
        steps {
            sh 'docker build -t ${IMAGE_NAME}:latest .'
        }
    }

    stage('Deploy') {
        steps {
            sh '''
                docker stop ${CONTAINER_NAME} || true
                docker rm ${CONTAINER_NAME} || true

                docker run -d \
                  --name ${CONTAINER_NAME} \
                  -p 8080:8080 \
                  -e DB_URL="jdbc:mysql://employee-mysql:3306/employee_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
                  -e DB_USERNAME=root \
                  -e DB_PASSWORD=root123 \
                  ${IMAGE_NAME}:latest
            '''
        }
    }

    stage('Verify Deployment') {
        steps {
            sh 'docker ps'
        }
    }
}

post {
    success {
        echo 'CI/CD Pipeline Executed Successfully!'
    }

    failure {
        echo 'Pipeline Failed!'
    }
}


}
