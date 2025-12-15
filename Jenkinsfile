pipeline {
    agent any

    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out...'
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                docker run --rm -v "$PWD":/usr/src/mymaven -v "$PWD/.m2":/root/.m2 -w /usr/src/mymaven maven:3.9-eclipse-temurin-21 mvn clean package -DskipTests
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose build'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }
}