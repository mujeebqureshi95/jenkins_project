pipeline {
    agent any

    tools {
        maven 'M1'
    }

    parameters {
        string(
            name: 'BRANCH_NAME',
            defaultValue: 'main',
            description: 'GitHub branch to build'
        )
    }

    environment {
        APP_NAME = "hello-jenkins"
        REMOTE_USER = "root"
        REMOTE_HOST = "13.232.72.145"
        REMOTE_DIR  = "/home/ec2-user/deployments"
        JAR_NAME = "hello-jenkins-1.0-SNAPSHOT.jar"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: "${params.BRANCH_NAME}",
                url: 'https://github.com/mujeebqureshi95/jenkins_project.git'
            }
        }

        stage('Verify Java & Maven') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Verify Output') {
            steps {
                sh '''
                    if [ -f target/${JAR_NAME} ]; then
                        echo "JAR file generated successfully"
                    else
                        echo "JAR generation failed"
                        exit 1
                    fi
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }

            post {
                success {
                    echo 'Tests Passed'
                }

                failure {
                    echo 'Tests Failed'
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true

                sh '''
                    mkdir -p /var/lib/jenkins/build-artifacts
                    cp target/*.jar /var/lib/jenkins/build-artifacts/
                '''
            }
        }

        stage('Deploy To Test Server') {
            steps {

                sshagent(credentials: ['ec2-ssh-key']) {

                    sh '''
                        ssh ${REMOTE_USER}@${REMOTE_HOST} "
                            mkdir -p ${REMOTE_DIR}
                        "

                        scp -o StrictHostKeyChecking=no \
                        target/${JAR_NAME} \
                        ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/app.jar

                        ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "

                            pkill -f app.jar || true

                            nohup java -jar ${REMOTE_DIR}/app.jar \
                            > ${REMOTE_DIR}/app.log 2>&1 &
                        "
                    '''
                }
            }
        }
    }

    post {

        success {
            echo 'Pipeline executed successfully'
        }

        failure {
            echo 'Pipeline failed'
        }

        always {
            cleanWs()
        }
    }
}
