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
                    - name: dind
                      image: docker:24.0.2-dind
                      securityContext:
                        privileged: true  # Required for Docker daemon to run
                      volumeMounts:
                      - name: docker-graph-storage
                        mountPath: /var/lib/docker
                      command:
                      - cat
                      tty: true
            '''
            volumes:
            - name: docker-graph-storage
              emptyDir: {}
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
                container('dind'){
                    script{
                        withDockerRegistry(credentialsId: "${DOCKER_CREDENTIALSID}", url: "https://${DOCKER_REPO_URL}") {
                            sh 'docker build -t ${PROJECT_NAME}:${DATE_TAG} .'
                            sh 'docker tag ${PROJECT_NAME}:${DATE_TAG} ${DOCKER_REPO_URL}/${PROJECT_NAME}:${DATE_TAG}'
                            sh 'docker push ${DOCKER_REPO_URL}/${PROJECT_NAME}:${DATE_TAG}'
                        }
                    }
                }
            }
        }
    }
}
