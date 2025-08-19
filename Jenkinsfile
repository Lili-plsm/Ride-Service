pipeline {
    agent any

    environment {
        PATH = "/usr/local/bin:${env.PATH}"
        PROJECT_DIR = "/Users/linara/Desktop/DO/Ride-Service"
    }

    stages {
        stage('Build') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    sh './gradlew build -x test'
                }
            }
        }

        stage('Test') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    sh '/usr/local/bin/docker compose -f docker-compose.test.yml up --build --abort-on-container-exit --exit-code-from app || true'
                    sh '/usr/local/bin/docker compose -f docker-compose.test.yml down -v'
                }
            }
        }

        stage('Run') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    sh '/usr/local/bin/docker compose -f docker-compose.prod.yml up --build -d'
                }
            }
        }
    }

    post {
        always {
            dir("${env.PROJECT_DIR}") {
                sh '/usr/local/bin/docker compose down -v || true'
            }
        }
    }
}
