pipeline {
    agent any
    options {
        timestamps()
    }
    environment {
        host = '51.254.33.28'
        key_path = '/var/jenkins_home/.ssh/id_rsa'
        remote_dir = '/opt/app/registry'
        jenkins_test_dir = '/tmp/ease/registry'
        deploy_user = 'root'
    }
    stages {
        stage('Clean old test data in Jenkins host') {
            steps {
                sh '''
                  if [[ -d ${jenkins_test_dir} ]]
                  then
                    rm -r ${jenkins_test_dir}
                    echo 'Test directory for saving Performers deleted'
                  else
                    echo 'Test directory is clean'
                  fi
                '''
            }
        }
        stage('Create registry directories if not exist') {
            environment {
                directory = '/var/ease/registry/'
            }
            steps {
                sh '''
                  ssh -i ${key_path} root@${host} << EOF
                    if [[ -d ${directory} ]]
                    then
                      echo "Directory ${directory} is exist"
                    else
                      mkdir -p ${directory}
                      if [[ "$?" -eq 0 ]]
                      then
                        echo "Directory created with success"
                      else
                        echo "Something went wrong and directory was not created" >&2
                      fi
                    fi

                    if [[ -d ${remote_dir} ]]
                    then
                      echo "Directory ${remote_dir} is exist"
                    else
                      mkdir -p ${remote_dir} &>/dev/null
                      echo "Directory ${remote_dir} created"
                    fi
                    exit
                '''
            }
        }
        stage('Environment variables checking') {
            environment {
                POSTGRES_USERNAME='POSTGRES_USERNAME'
                POSTGRES_PASSWORD='POSTGRES_PASSWORD'
            }
            steps {
                sh '''
                  ssh -i ${key_path} root@${host} << EOF
                    if [[ -z ${POSTGRES_USERNAME} ]] && [[ -z ${POSTGRES_PASSWORD} ]];
                    then
                      echo "One or more variables not detected!" >&2
                      exit 1
                    else
                      echo "Variables defined correctly"
                    fi
                '''
            }
        }
        stage('Checkout source code') {
            steps {
                cleanWs()
                checkout(
                    [$class: 'GitSCM',
                    branches: [[name: '*/master']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[$class: 'WipeWorkspace']],
                    submoduleCfg: [],
                    userRemoteConfigs: [[url: 'https://github.com/easeci/easeci-registry.git']]])
            }
        }
        stage('Testing application code') {
            steps {
                echo '==> Running gradle test task'
                sh '''
                  mkdir -p ${jenkins_test_dir}
                  ./gradlew test
                  rm -r ${jenkins_test_dir}
                '''
            }
        }
        stage('Application building') {
            steps {
                echo '==> Application building'
                sh 'mkdir -p ${jenkins_test_dir}'
                sh './gradlew clean build --info'
            }
        }
        stage('Publish artifact over SSH') {
            steps {
                echo '==> Sending artifact via SSH'
                sh "scp -i ${key_path} ${WORKSPACE}/build/libs/*.jar ${deploy_user}@${host}:${remote_dir}"
            }
        }
        stage('Starting new application build') {
            steps {
                echo '==> Starting application with systemd'
                sh '''
                  ssh -i ${key_path} root@${host} << EOF
                    if systemctl list-unit-files | grep -Fq 'easeci-registry'
                    then
                      echo 'systemd unit found for easeci-registry, so restarted this'
                      systemctl restart easeci-registry
                    else
                      echo 'Cannot found systemd unit for easeci-registry!' >&2
                      exit 1
                    fi
                '''
            }
        }
    }
    post {
        always {
            deleteDir()
            sh 'rm -r ${jenkins_test_dir}'
        }
    }
}