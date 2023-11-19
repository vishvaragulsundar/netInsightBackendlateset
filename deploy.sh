#!/bin/bash
# This script is used to deploy the application to the server
sudo docker stop netInsight
sudo docker rm netInsight
sudo chmod 777 gradlew
./gradlew clean build
sudo docker build . -t netinsight:withjwt
sudo docker run -itd --name netInsight --network host --restart=always -v "$(pwd)"/requiredFiles:/usr/app:Z netinsight:withjwt