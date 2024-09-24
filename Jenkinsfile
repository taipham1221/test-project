pipeline {
  agent {
    kubernetes {
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
               name: docker-sock
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
        SONARQUBE_CREDENTIALSID = 'sonar-credential'
        GIT_DEPLOYMENT_REPO_URL='https://github.com/taipham1221/test-project-deployment.git'
    }
  stages {
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
    stage('Build sonarqube') {
            steps {
                container('maven') {
                    script {
                        withSonarQubeEnv(credentialsId: "${SONARQUBE_CREDENTIALSID}") {
                            sh "mvn clean verify sonar:sonar -Dsonar.projectKey=test-project -Dsonar.projectName='test-project'"
                        }
                    }                   
                }
            }
        }
    stage('Build and Push Docker Image'){
        steps{
            container('docker'){
                script{
                    withCredentials([usernamePassword(credentialsId: 'docker-credential', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh 'docker build -t taipham1221/test-project:latest .'
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                        sh 'docker push taipham1221/test-project:${DATE_TAG}'
                    }
                }
            }
        }
    }
    stage('Clean file') {
      steps {
          container('maven') {
              script {
                  sh 'rm -rf *'
              }                   
          }
      }
    }
    stage('Push deployment'){
        steps{
            container('jnlp'){
                script {
                    git branch: "${GIT_BRANCH}",
                        credentialsId: "${GIT_CREDENTIALSID}",
                        url: "${GIT_DEPLOYMENT_REPO_URL}"
                    withCredentials([gitUsernamePassword(credentialsId: "${GIT_CREDENTIALSID}", gitToolName: 'Default')]) {
                            sh '''
                                git config --global user.name "Jenkins"
                                git config --global user.email "jenkins@localhost.local"
                                sed -i "s/test-project:[0-9]*/test-project:${DATE_TAG}/" business-services/test-project/test-project.yaml
                                git add hello-world/deploy.yaml
                                git commit -m "'Updates image with $PROJECT_NAME - ${DATE_TAG}'"
                                git push -f origin main
                            '''
                        }
                }
            }
        }
    }
  }
}