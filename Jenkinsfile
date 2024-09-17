pipeline {
    agent { label 'maven' }
    environment {
        DATE_TAG = sh(script: 'date +"%Y%m%d%H%M"', returnStdout: true).trim()
        GIT_REPO_URL = 'https://github.com/taipham1221/test-project' 
        GIT_BRANCH = 'main'
        GIT_CREDENTIALSID = 'github-credentials'
        PROJECT_NAME = 'test-project' 
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
                        sh 'mvn clean package '
                    }                   
                }
            }
        }
    }
	
}