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
        DEPLOY_HOST = "10.16.123.237"
        BASE_DEPLOY_PATH = "/tmp/maven"
	BUILD_VERSION= "${BUILD_NUMBER}"
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

        stage('Debug Environment'){
	    steps{
	      sh'''
		echo"===== DEBUG INFO ====="
		whoami
		pwd
		ls -la
		java -version
		mvn -version
		ls -la target/
	      """
	    }
	}
	
	stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Test Server') {
            steps {
                sshagent(['ssh-key']) {  // Jenkins credential ID

                    sh '''
		    echo "===== CREATING REMOTE BUILD DIRECTORY ====="
                    
                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST}"
			mkdir -p ${BASE_DEPLOY_PATH}/${BUILD_VERSION}
			chmod 755 ${BASE_DEPLOY_PATH}/${BUILD_VERSION}
                    "
                    
                    echo "===== COPYING ARTIFACT ====="

		    scp -o StrictHostKeyChecking=no target/*.jar \
                    ${DEPLOY_USER}@${DEPLOY_HOST}:${BASE_DEPLOY_PATH}/${BUILD_VERSION}/${APP_NAME}.jar

                    echo "===== VERIFYING FILE ON REMOTE SERVER ====="

                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "
                        ls -lh ${BASE_DEPLOY_PATH}/${BUILD_VERSION}
                    "

                    echo "===== STOPPING OLD APPLICATION ====="

                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "
                        pkill -f ${APP_NAME}.jar || true
                    "

                    echo "===== STARTING NEW APPLICATION ====="

                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} "
                        ln -sfn ${BASE_DEPLOY_PATH}/${BUILD_VERSION}/${APP_NAME}.jar \
                        ${BASE_DEPLOY_PATH}/current.jar

                        nohup java -jar ${BASE_DEPLOY_PATH}/current.jar \
                        > ${BASE_DEPLOY_PATH}/app.log 2>&1 &
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
