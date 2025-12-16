pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'Java21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '--- Клонирование репозитория из GitHub ---'
                git branch: 'master', url: 'https://github.com/Xx-Aiser-xX/courier-system-project.git'
            }
        }

        stage('Debug: File System') {
            steps {
                script {
                    echo '--- Проверка содержимого папки после клонирования ---'
                    sh 'ls -la'
                    sh 'test -f pricing-service/pom.xml && echo "Pricing Service POM found" || echo "Pricing Service POM NOT found"'
                }
            }
        }

        stage('Build Contracts (Libs)') {
            steps {
                echo '--- Сборка общих библиотек (install) ---'
                sh 'mvn -B -f events-contract/pom.xml clean install'
                sh 'mvn -B -f couriers-contract/pom.xml clean install'
                sh 'mvn -B -f grpc-contract/pom.xml clean install'
            }
        }

        stage('Build Services (Sequential)') {
            steps {
                echo '--- Сборка JAR файлов микросервисов ---'

                echo 'Building Pricing Service...'
                sh 'mvn -B -f pricing-service/pom.xml clean package -DskipTests'

                echo 'Building Couriers Core...'
                sh 'mvn -B -f couriers/pom.xml clean package -DskipTests'

                echo 'Building Audit Service...'
                sh 'mvn -B -f audit-service/pom.xml clean package -DskipTests'

                echo 'Building Notification Service...'
                sh 'mvn -B -f notification-service/pom.xml clean package -DskipTests'

                echo 'Building Statistics Service...'
                sh 'mvn -B -f statistics-service/pom.xml clean package -DskipTests'
            }
        }

        stage('Build Docker Images') {
             steps {
                 echo '--- Сборка Docker образов ---'
                 script {
                     sh 'docker --version'

                     sh 'docker compose -f docker-compose.yaml build'
                 }
             }
        }
    }
}