pipeline {
    agent any

    environment {
        DEPLOY_USER = 'nexventory'
        DEPLOY_BASE_DIR = '/etc/nexventory/auth'
        DEPLOY_SERVER = ''
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout padrão no Multibranch
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Escolhe o servidor com base no branch
                    if (env.BRANCH_NAME == 'master') {
                        DEPLOY_SERVER = '172.16.90.9'
                    } else if (env.BRANCH_NAME == 'develop') {
                        DEPLOY_SERVER = '172.16.90.9'
                    } else {
                        DEPLOY_SERVER = '172.16.90.9'
                    }

                    sshagent (credentials: ['nexventory']) {
                        // Copia o JAR
                        sh "scp target/auth_nexventory.jar ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_BASE_DIR}/"

                        // Copia configs
                        sh "scp src/main/resources/application.properties ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_BASE_DIR}/config/"
                        sh "scp src/main/resources/logback-spring.xml ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_BASE_DIR}/config/"

                        // Reinicia o serviço
                        sh "ssh ${DEPLOY_USER}@${DEPLOY_SERVER} 'sudo systemctl restart auth.service'"
                    }
                }
            }
        }
    }
}
