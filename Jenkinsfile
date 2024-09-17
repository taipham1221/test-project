pipeline {
    agent { label 'maven' }
    environment {
        DATE_TAG = sh(script: 'date +"%Y%m%d%H%M"', returnStdout: true).trim()
        GIT_REPO_URL = 'https://git.hdbank.com.vn/digital-core-dev-team/business-services/card-business-services/card-transaction-service.git' 
		GIT_REPO_CONFIG = 'https://git.hdbank.com.vn/digital-core-dev-team/vault-config.git' 
        GIT_BRANCH = 'main'
        GIT_CREDENTIALSID = 'app_cicd_gitlab'
        DOCKER_CREDENTIALSID = 'docker-registry-uat'
        SONARQUBE_CREDENTIALSID = 'sonarqube-test'
        DOCKER_REPO_URL = 'docker-registry-uat.hdbank.com.vn'
        PROJECT_NAME = 'card-transaction-service' 
    }
    stages {
        stage('Pull source repo gitlab') {
            steps {
				container('jnlp') {
                    script {
                        git branch: "${GIT_BRANCH}",
                            credentialsId: "${GIT_CREDENTIALSID}",
                            url: "${GIT_REPO_URL}"
                        env.GIT_COMMIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    }
				}
            }
        }
        stage('Build maven') {
            steps {
                container('maven') {
                    script {
                        //sh 'mvn clean package'
                        sh 'mvn clean package '
                    }                   
                }
            }
        }
        
	
}