pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        DOCKER_IMAGE = 'smile2112-hub/dispatch-load-balancer'
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw && ./mvnw -B -q compile'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw -B test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh './mvnw -B -DskipTests package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    env.IMAGE_TAG = "${env.DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                    sh "docker build -t ${env.IMAGE_TAG} ."
                }
            }
        }

        stage('Docker Push') {
            when {
                allOf {
                    branch 'main'
                    expression { return env.DOCKER_CREDENTIALS_ID != '' }
                }
            }
            steps {
                script {
                    docker.withRegistry('', env.DOCKER_CREDENTIALS_ID) {
                        sh "docker push ${env.IMAGE_TAG}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Build, tests, package, and Docker image completed successfully."
        }
        failure {
            echo 'The Jenkins pipeline failed. Review the stage logs and test reports.'
        }
        always {
            cleanWs()
        }
    }
}
