pipeline {
    agent any

    tools {
        maven 'M1'
    }

    environment {
        GIT_REPO = "https://github.com/mujeebqureshi95/jenkins_project.git"
        BRANCH = "main"
        DEPLOY_SERVER = "ec2-user@13.207.2.148"
        DEPLOY_PATH = "/home/ec2-user/deployments"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Locate Artifact') {
            steps {
                script {
                    // Automatically find the JAR file
                    def jarFile = sh(
                        script: "find target -type f -name '*.jar' ! -name '*original*.jar' | head -n 1",
                        returnStdout: true
                    ).trim()

                    if (!jarFile) {
                        error("No JAR file found in target directory ❌")
                    }

                    env.JAR_FILE = jarFile
                    echo "Detected artifact: ${env.JAR_FILE}"
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Test Server') {
            steps {
                sshagent (credentials: ['ec2-ssh-key']) {
                    sh """
                        echo "Copying ${env.JAR_FILE} to ${DEPLOY_SERVER}"
                        
                        scp ${env.JAR_FILE} ${DEPLOY_SERVER}:${DEPLOY_PATH}/

                        ssh ${DEPLOY_SERVER} '
                            echo "Stopping existing app..."
                            pkill -f java || true

                            echo "Starting new app..."
                            nohup java -jar ${DEPLOY_PATH}/$(basename ${env.JAR_FILE}) > app.log 2>&1 &
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully ✅"
        }
        failure {
            echo "Pipeline failed ❌"
        }
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}
