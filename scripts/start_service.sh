#!/bin/bash

JAVA_HOME=/usr/java/jdk-11.0.6
java -jar  /opt/codedeploy-agent/deployment-root/$DEPLOYMENT_GROUP_ID/$DEPLOYMENT_ID/deployment-archive/target/help-giver-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &
echo $! > /var/run/help-giver.pid