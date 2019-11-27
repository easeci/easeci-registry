ipeline {
    agent any
    environment {
        host = '51.254.33.28'
        key_path = '/var/jenkins_home/.ssh/id_rsa'
    }
    stages {
        stage('Create registry directory if not exist') {
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
        stage('Testing application code') {
            steps {
                echo ''
            }
        }
        stage('Application building') {
            steps {
                echo ''
            }
        }
        stage('Application deploy') {
            steps {
                echo ''
            }
        }
        stage('Starting new application build') {
            steps {
                echo ''
            }
        }
    }
}