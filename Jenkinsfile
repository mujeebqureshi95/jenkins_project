pipeline {
    agent any

    tools {
        maven 'Maven'   // Configure in Jenkins Global Tool Config
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Git branch to build')
    }

    environment {
        APP_NAME = "hello-jenkins"
        DEPLOY_USER = "ec2-user"
        DEPLOY_HOST = "13.204.74.34"
        DEPLOY_PATH = "/home/ec2-user/app"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: "${params.BRANCH_NAME}",
                    url: 'https://github.com/mujeebqureshi95/jenkins_project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Verify Output (Tests)') {
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
                sshagent(['ec2-ssh-key']) {
                    sh '''
                    set -e
                    
                    scp -o StrictHostKeyChecking=no target/*.jar ${DEPLOY_USER}@${DEPLOY_HOST}:${DEPLOY_PATH}/app.jar
                    
                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "
                        set +e
                        pkill -f app.jar
                        set -e
                        nohup java -jar ${DEPLOY_PATH}/app.jar > ${DEPLOY_PATH}/app.log 2>&1 &
                    "
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline execution completed."
        }
        success {
            echo "🎉 Build + Deploy Successful"
        }
        failure {
            echo "🔥 Pipeline Failed"
        }
    }
}
