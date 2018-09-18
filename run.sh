#!/bin/bash

APP=/usr/share/mongodb-server-load

CLASSPATH=$CLASSPATH:${APP}/lib/mongo-java-driver-3.5.0.jar
CLASSPATH=$CLASSPATH:${APP}/lib/mongodb-server-tunning-1.0.jar

CLASSPATH=$CLASSPATH:${APP}/conf

JAVA_OPTS="-Djava.util.logging.config.file=conf/logging.properties"

#Heap size and others 
JAVA_OPTS="$JAVA_OPTS -Xms64m"
JAVA_OPTS="$JAVA_OPTS -Xmx64m"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=heapdumpfile.hprof"


JAVA_OPTS="$JAVA_OPTS -verbose:gc"
JAVA_OPTS="$JAVA_OPTS -Xloggc:./gclogs.log"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGC"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
JAVA_OPTS="$JAVA_OPTS -XX:MaxGCPauseMillis=20000"
#JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=127.0.0.1"
#JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.codebase=file:///D:/whereever/lib/mongodb-server-tunning-1.0.jar"

export CLASSPATH
export JAVA_OPTS

java $JAVA_OPTS com.saven.mongodb.loadtest.ConcurrentLoadingnQuery \
                        --file ${APP}/conf/config.properties \
                        > ${APP}/console.log 2> ${APP}/err.log

#                        --host 192.168.195.19 \
#                        --db test \
#                        --connections 20 \
#                        --max-ll-connections 20 \

