pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
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
                sh 'docker build -t employee-management-platform:latest .'
            }
        }

        stage('Verify Docker Image') {
            steps {
                sh 'docker images'
            }
        }
    }

    post {
        success {
            echo 'Build & Docker Image Creation Successful!'
        }

        failure {
            echo 'Build Failed!'
        }
    }
}