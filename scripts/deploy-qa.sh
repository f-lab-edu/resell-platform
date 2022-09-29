#!/bin/bash

REPOSITORY=/home/ec2-user/
PROJECT_NAME=resell-platform

cd $REPOSITORY/$PROJECT_NAME/

echo "> 프로젝트 디렉토리로 이동"

cd $REPOSITORY/$PROJECT_NAME

echo "stop and remove containers"
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker rmi -f $(docker images -a)
sleep 5

echo "> 애플리케이션 배포"
docker load -i resell-platform-app.tar
docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d
sleep 5