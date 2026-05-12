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
        DEPLOY_USER = "jenkins"
        DEPLOY_HOST = "10.16.149.169"
        DEPLOY_PATH = "/tmp/maven"
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
        sshagent(['jenkins']) {

            sh '''
            set -e

            scp -o StrictHostKeyChecking=no \
            target/hello-jenkins-1.0-SNAPSHOT.jar \
            jenkins@10.16.149.169:/tmp/maven/app.jar

            ssh -o StrictHostKeyChecking=no jenkins@10.16.149.169 << 'EOF'

                mkdir -p /tmp/maven

                pkill -f app.jar || true

                nohup java -jar /tmp/maven/app.jar \
                > /tmp/maven/app.log 2>&1 < /dev/null &

                disown || true

                exit 0

EOF
            '''
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
