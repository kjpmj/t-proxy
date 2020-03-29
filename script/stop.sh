#!/bin/sh
TARGET=T-PROXY-SOCKET-1.0.jar
ps -ef |grep -v grep | grep $TARGET | awk '{print $2}' | xargs kill -9
echo "stop T-PROXY-SOCKET..."
./stat.sh

