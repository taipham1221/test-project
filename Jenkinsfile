pipeline{
    agent{
        kubernetes{
            yaml '''
                apiVersion: v1
                kind: Pod
                spec:
                    containers:
                    - name: jnlp
                      image: jenkins/inbound-agent:latest
                      args: ['$(JENKINS_SECRET)', '$(JENKINS_NAME)']
                    containers:
                    - name: maven
                      image: maven:3.9.9-eclipse-temurin-21-alpine
                      tty: true
            '''
        }
    }
    environment {
        GIT_REPO_URL = 'https://github.com/taipham1221/test-project'
        GIT_BRANCH = 'main'
        GIT_CREDENTIALSID = 'github_ssh'
    }
    stages{
        stage('Pull code'){
            steps{
                container('jnlp'){
                    script {
                        git branch: "${GIT_BRANCH}",
                            credentialsId: "${GIT_CREDENTIALSID}",
                            url: "${GIT_REPO_URL}"
                        env.GIT_COMMIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                        sh 'ls -la'
                    }
                }
            }
        }
    }
}