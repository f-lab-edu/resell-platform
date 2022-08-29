#!/bin/bash

REPOSITORY=/home/ec2-user/app/step2
PROJECT_NAME=resell-platform

cd $REPOSITORY/$PROJECT_NAME/

echo "> Change gradlew to executable"

chmod +x gradlew

echo "> 프로젝트 Build 시작"

./gradlew build

echo "> step1/zip 디렉토리로 이동"

cd $REPOSITORY/zip

CURRENT_DOCKER_ID=$(docker ps -af "ancestor=resell-platform_app" -q)
echo ">현재 애플리케이션 docker id: $CURRENT_DOCKER_ID"

echo "stop and remove containers"
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
sleep 5

echo "> 새 애플리케이션 배포"
docker-compose -f $PROJECT_NAME/docker-compose.yml -f $PROJECT_NAME/docker-compose.prod.yml up -d

NEW_DOCKER_ID=$(docker ps -af "ancestor=resell-platform_app" -q)
echo ">새 애플리케이션 docker id: $NEW_DOCKER_ID"