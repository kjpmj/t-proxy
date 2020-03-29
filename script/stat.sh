#!/bin/bash


TARGET=T-PROXY-SOCKET-1.0.jar

isRun=`ps -ef | grep -v grep | grep -c $TARGET`

if [ $isRun -eq 0 ]
then
	echo "T-PROXY-SOCKET is not running..."
else
	ps -ef | grep -v grep | grep $TARGET
fi

