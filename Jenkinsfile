pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'Docker_Hub'
        DOCKERHUB_REPO = 'sandipranjit/calculator'
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}"
        BUILD_DATE = "${new Date().format('yyyy-MM-dd')}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/SandipFromPokhara/calculator-fx-db.git'
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                        docker-compose down -v
                        docker-compose up --build -d
                        docker-compose exec -T app mvn clean
                        '''
                    } else {
                        bat '''
                        docker-compose down -v
                        docker-compose up --build -d
                        docker-compose exec -T app mvn clean
                        '''
                    }
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker-compose exec -T app mvn package -DskipTests'
                    } else {
                        bat 'docker-compose exec -T app mvn package -DskipTests'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                        docker build --pull -t ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} .
                        docker images
                        '''
                    } else {
                        bat """
                        docker build --pull -t %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG% .
                        docker images
                        """
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(
                            credentialsId: DOCKERHUB_CREDENTIALS_ID,
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                    )]) {
                        if (isUnix()) {
                            sh '''
                            echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                            docker push ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}
                            docker tag ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} ${DOCKERHUB_REPO}:latest
                            docker push ${DOCKERHUB_REPO}:latest
                            docker image rm ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}
                            docker image prune -f
                            '''
                        } else {
                            bat """
                            echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                            docker push %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG%
                            docker tag %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG% %DOCKERHUB_REPO%:latest
                            docker push %DOCKERHUB_REPO%:latest
                            docker image rm %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG%
                            docker image prune -f
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            cleanWs()
        }
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}