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
                        docker-compose exec -T app mvn clean test
                        '''
                    } else {
                        bat '''
                        docker-compose down -v
                        docker-compose up --build -d
                        docker-compose exec -T app mvn clean test
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

        stage('Code Coverage') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn jacoco:report'
                    } else {
                        bat 'mvn jacoco:report'
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
                        REM --- Build Docker image with build number tag ---
                        docker build --pull -t %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG% .
        
                        REM --- Verify image exists ---
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
                                REM --- Login to Docker Hub ---
                                echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                                
                                REM --- Push image with build number tag ---
                                echo Pushing Docker image %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG%...
                                docker push %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG%
                               
                                REM --- Tag as latest and push ---
                                docker tag %DOCKERHUB_REPO%:%DOCKER_IMAGE_TAG% %DOCKERHUB_REPO%:latest
                                docker push %DOCKERHUB_REPO%:latest
                                
                                REM --- Cleanup local images to save disk space ---
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
            junit '**/target/surefire-reports/*.xml'
            jacoco()
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