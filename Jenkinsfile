pipeline {
    agent any

    tools {
        maven 'Maven-3.9'   // Name configured in Jenkins
        jdk 'JDK-17'        // Optional
    }

    environment {
        GIT_REPO = "https://github.com/your-username/your-repo.git"
        BRANCH = "main"
        ARTIFACT_NAME = ""
        DEPLOY_SERVER = "user@test-server-ip"
        DEPLOY_PATH = "/opt/test-app"
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

        stage('Verify Build Result') {
            steps {
                script {
                    if (currentBuild.currentResult == 'SUCCESS') {
                        echo "Build Passed ✅"
                    } else {
                        error("Build Failed ❌")
                    }
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                script {
                    // Dynamically pick jar from target folder
                    def jarFile = sh(
                        script: "ls target/*.jar | head -n 1",
                        returnStdout: true
                    ).trim()

                    env.ARTIFACT_NAME = jarFile
                    echo "Archiving: ${jarFile}"
                }

                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Test Server') {
            steps {
                script {
                    echo "Deploying ${env.ARTIFACT_NAME} to ${DEPLOY_SERVER}"
                }

                sshagent (credentials: ['ssh-cred-id']) {
                    sh """
                        scp ${env.ARTIFACT_NAME} ${DEPLOY_SERVER}:${DEPLOY_PATH}/
                        ssh ${DEPLOY_SERVER} '
                            pkill -f java || true
                            nohup java -jar ${DEPLOY_PATH}/$(basename ${env.ARTIFACT_NAME}) > app.log 2>&1 &
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully 🎉"
        }
        failure {
            echo "Pipeline failed 🚨"
        }
    }
}
