pipeline {
    agent any

    environment {
        DEPLOY_SERVER = ''
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${env.BRANCH_NAME}", url: 'git@github.com/kaique132004/auth-service.git'
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
                    // Escolhendo o servidor conforme o branch
                    if (env.BRANCH_NAME == 'master') {
                        DEPLOY_SERVER = '172.16.90.9'  // Prod
                    } else if (env.BRANCH_NAME == 'develop') {
                        DEPLOY_SERVER = '172.16.90.9'  // Dev
                    } else {
                        DEPLOY_SERVER = '172.16.90.9'  // Outros ambientes
                    }

                    // Realizando o deploy via SSH + SCP
                    sshagent (credentials: ['nexventory']) {
                        // Copia o JAR para o diretório correto
                        sh "scp target/auth_nexventory.jar ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_BASE_DIR}/"

                        // Copia os arquivos de configuração para a pasta config
                        sh "scp src/main/resources/application.properties ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_BASE_DIR}/config/"
                        sh "scp src/main/resources/logback-spring.xml ${DEPLOY_USER}@${DEPLOY_SERVER}:${DEPLOY_BASE_DIR}/config/"

                        // Reinicia o serviço remoto
                        sh "ssh ${DEPLOY_USER}@${DEPLOY_SERVER} 'sudo systemctl restart auth.service'"
                    }
                }
            }
        }
    }
}
