pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'Java21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Забираем код из GitHub...'
            }
        }

        stage('Debug: File System') {
            steps {
                script {
                    echo '--- Список папок в корне проекта ---'
                    sh 'ls -la'

                    echo '--- Проверка наличия папки notification-service ---'
                    sh 'ls -la notification-service || echo "!!! ВНИМАНИЕ: Папка notification-service НЕ НАЙДЕНА !!!"'
                }
            }
        }

        stage('Build Contracts (Libs)') {
            steps {
                echo '--- Сборка общих библиотек ---'
                sh 'mvn -B -f events-contract/pom.xml clean install'
                sh 'mvn -B -f couriers-contract/pom.xml clean install'
                sh 'mvn -B -f grpc-contract/pom.xml clean install'
            }
        }

        stage('Build Services (Sequential)') {
            steps {
                echo '--- Сборка микросервисов ---'

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
                 echo 'Сборка Docker образов...'
                 sh 'docker --version'
                 sh 'docker compose build'
             }
        }
    }
}