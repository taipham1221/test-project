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
                    - name: maven
                      image: maven:3.9.9-eclipse-temurin-21-alpine
                      command:
                      - cat
                      tty: true
                    - name: docker
                      image: docker:latest
                      command:
                      - cat
                      tty: true
                      volumeMounts:
                      - mountPath: /var/run/docker.sock
                        name: docker-sock  # Correct volume mount name
                volumes:
                - name: docker-sock
                  hostPath:
                    path: /var/run/docker.sock 
            '''
        }
    }
    environment {
        GIT_REPO_URL = 'https://github.com/taipham1221/test-project'
        GIT_BRANCH = 'main'
        GIT_CREDENTIALSID = 'github-credential'
        DATE_TAG = sh(script: 'date +"%Y%m%d%H%M"', returnStdout: true).trim()
        PROJECT_NAME = 'test-project' 
        DOCKER_CREDENTIALSID = 'docker-credential' 
        DOCKER_REPO_URL = 'docker.io/taipham1221'
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
        stage('Build Maven'){
            steps{
                container('maven'){
                    script{
                        sh 'mvn clean package'
                        sh 'ls -la ./target'
                    }
                }
            }
        }
        stage('Build and Push Docker Image'){
            steps{
                container('docker'){
                    script{
                        sh 'docker build -t taipham1221/test-project:latest .'
                        sh "docker login -u taipham1221 -p ${DOCKER_CREDENTIALSID}"
                        sh 'docker push taipham1221/test-project:latest'
                    }
                }
            }
        }
    }
    post{
        always{
            container('docker'){
                sh 'docker logout'
            }
        }
    }
}
