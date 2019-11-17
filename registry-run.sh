#!/bin/bash

function create_user() {
  echo "Creating user ${1}"
  sudo adduser $1 --disabled-password
}

function create_directory() {
    DIR="/var/ease/registry"
    echo "Directories creation"
    mkdir /var/ease/ && mkdir ${DIR}
    chown ${1}:${1} ${DIR}
}

if [[ $UID -ne 0 ]]; then
  echo "Run this shell script with sudo privilages"
  exit 1
fi

USERNAME='easeci'
IS_EXISTS=$(id -u ${USERNAME} &> /dev/null)

if [[ ${?} -eq 0 ]]; then
  echo "User ${USERNAME} exists so easeci-registry starts to running"
else
  echo "User ${USERNAME} not exists in system, so trying to create one"
  create_user $USERNAME
fi

create_directory $USERNAME

./gradlew build -x test

nohup java -jar -Dspring.profiles.active=local build/libs/easeci-registry-0.0.1-SNAPSHOT.jar &> /dev/null