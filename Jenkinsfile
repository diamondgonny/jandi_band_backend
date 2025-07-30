pipeline {
    agent any

    environment {
        // 공통 변수
        GHCR_OWNER = 'kyj0503'
        
        // 운영(Production) 환경 변수
        PROD_IMAGE_NAME = 'rhythmeet-be'
        EC2_HOST = 'rhythmeet.yeonjae.kr'
        EC2_USER = 'ubuntu'

        // 개발(Development) 환경 변수
        DEV_IMAGE_NAME = 'rhythmeet-be-dev'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Push to GHCR') {
            steps {
                script {
                    def imageName = (env.BRANCH_NAME == 'master') ? env.PROD_IMAGE_NAME : env.DEV_IMAGE_NAME
                    def fullImageName = "ghcr.io/${env.GHCR_OWNER}/${env.IMAGE_NAME}:${env.BUILD_NUMBER}"

                    echo "Building Docker image: ${fullImageName}"
                    docker.build(fullImageName, '.')

                    docker.withRegistry("https://ghcr.io", 'github-token') {
                        echo "Pushing Docker image to GHCR..."
                        docker.image(fullImageName).push()
                    }
                }
            }
        }

        stage('Deploy') {
            parallel {
                stage('Deploy to Production (EC2)') {
                    when { branch 'master' } // 이 스테이지는 master 브랜치일 때만 실행
                    steps {
                        script {
                            def fullImageName = "ghcr.io/${env.GHCR_OWNER}/${env.PROD_IMAGE_NAME}:${env.BUILD_NUMBER}"
                            withCredentials([sshUserPrivateKey(credentialsId: 'ec2-ssh-key', keyFileVariable: 'EC2_PRIVATE_KEY')]) {
                                echo "Deploying to EC2 host: ${env.EC2_HOST}"
                                sh """
                                    ssh -o StrictHostKeyChecking=no -i \${EC2_PRIVATE_KEY} ${env.EC2_USER}@${env.EC2_HOST} \
                                    "bash /home/ubuntu/spring-app/deploy.sh ${fullImageName}"
                                """
                            }
                        }
                    }
                }

                stage('Deploy to Development (Local)') {
                    when { branch 'dev' } // 이 스테이지는 dev 브랜치일 때만 실행
                    steps {
                        script {
                            def fullImageName = "ghcr.io/${env.GHCR_OWNER}/${env.DEV_IMAGE_NAME}:${env.BUILD_NUMBER}"
                            echo "Deploying to local on-premise server"
                            sh "bash /home/kyj/spring-app-dev/deploy.sh ${fullImageName}"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
