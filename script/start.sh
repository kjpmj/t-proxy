#!/bin/bash

RETVAL=0
CONF=proxyconf.xml

TARGET=T-PROXY-SOCKET-1.0.jar
cd $HOME/DEV/server/T-PROXY-SOCKET/bin

# check if program is run...
function runcheck()
{
	isRun=`ps -ef | grep java | grep -v grep | grep $TARGET | wc -l`
	return ${isRun}
}

runcheck
isRun=$?

echo "start..........."
if [ $isRun -eq 0 ]
then
	# in case of executing shell with argument...
	if [ -z $CONF ]
	then
		echo "Fail to run $TARGET"
        	echo "usage : ./start.sh proxyconf.xml"
		exit 1
	fi
        nohup java -jar ./$TARGET $CONF > /dev/null 2>&1 &
	sleep 2
	runcheck
	isRun=$?
	if [ $isRun -eq 0 ]
	then
		echo "Fail to run $TARGET"
		RETVAL=1
	else
		echo "Success to run $TARGET"
		./stat.sh
	fi
else
	RETVAL=1
        echo "$TARGET is already running!!"
fi

exit $RETVAL
