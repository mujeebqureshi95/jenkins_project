pipeline {
    agent any

    tools {
        maven 'M1'
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git branch to build')
    }

    environment {
        APP_NAME = "hello-jenkins"
        DEPLOY_USER = "ec2-user"
        DEPLOY_HOST = "43.204.28.223"
        DEPLOY_PATH = "/home/ec2-user/deployments"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: "${params.BRANCH_NAME}",
                    url: 'https://github.com/mujeebqureshi95/jenkins_project.git'
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Verify Output') {
            steps {
                sh 'mvn test'
            }
            post {
                success {
                    echo "✅ Tests Passed"
                }
                failure {
                    echo "❌ Tests Failed"
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
                sshagent(credentials: ['ec2-ssh-key']) {

                    sh '''
                    echo "Creating deployment directory on test server..."

                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                        mkdir -p ${DEPLOY_PATH}
                    '

                    echo "Copying JAR to test server..."

                    scp -o StrictHostKeyChecking=no target/*.jar \
                    ${DEPLOY_USER}@${DEPLOY_HOST}:${DEPLOY_PATH}/app.jar

                    echo "Stopping old application if running..."

                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                        pkill -f app.jar || true
                    '

                    echo "Starting new application..."

                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                        nohup java -jar ${DEPLOY_PATH}/app.jar > ${DEPLOY_PATH}/app.log 2>&1 &
                    '

                    echo "Deployment Completed Successfully"

                    """
                }
            }
        }
    }

    post {

        always {
            echo "Pipeline execution completed."
        }

        success {
            echo "🎉 Build and Deployment Successful"
        }

        failure {
            echo "🔥 Pipeline Failed"
        }
    }
}
