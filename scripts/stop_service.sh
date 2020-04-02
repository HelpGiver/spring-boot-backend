#!/bin/bash

PID=$(cat /var/run/help-giver.pid)
if [[ -z $PID ]]
then
     echo "Application is not running"
     exit 1
else
    echo "Application pid $PID stopping"
    kill -9 $PID
fi