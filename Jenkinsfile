pipeline {
    agent any

    environment {
        DOCKER_API_VERSION = "1.43"
    }

    tools {
        maven 'Maven3'
        jdk 'Java21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '--- Скачивание кода ---'
                git branch: 'master', url: 'https://github.com/Xx-Aiser-xX/courier-system-project.git'
            }
        }

        stage('Build Contracts') {
            steps {
                echo '--- Сборка библиотек ---'
                sh 'mvn -B -f events-contract/pom.xml clean install'
                sh 'mvn -B -f couriers-contract/pom.xml clean install'
                sh 'mvn -B -f grpc-contract/pom.xml clean install'
            }
        }

        stage('Build Services') {
            steps {
                echo '--- Сборка JAR файлов ---'
                script {
                    sh 'mvn -B -f pricing-service/pom.xml clean package -DskipTests'
                    sh 'mvn -B -f couriers/pom.xml clean package -DskipTests'
                    sh 'mvn -B -f audit-service/pom.xml clean package -DskipTests'
                    sh 'mvn -B -f notification-service/pom.xml clean package -DskipTests'
                    sh 'mvn -B -f statistics-service/pom.xml clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
             steps {
                 echo '--- Создание Docker образов ---'
                 sh 'docker compose build'
             }
        }

        stage('Deploy (CD)') {
             steps {
                 echo '--- Деплой (Запуск контейнеров) ---'
                 script {
                     echo '1. Удаляем старые конфликтующие контейнеры...'
                     sh 'docker rm -f zipkin rabbitmq prometheus grafana postgres pricing-service couriers audit-service notification-service statistics-service || true'

                     echo '2. Запускаем сервисы (без jenkins)...'
                     sh '''
                        docker compose up -d \
                        rabbitmq \
                        zipkin \
                        prometheus \
                        grafana \
                        postgres \
                        pricing-service \
                        couriers \
                        audit-service \
                        notification-service \
                        statistics-service
                     '''

                     sh 'docker image prune -f'
                 }
             }
        }
    }
}