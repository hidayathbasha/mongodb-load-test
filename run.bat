@echo off
 
set CLASSPATH=%CLASSPATH%;%MAVEN_REPO%/org/mongodb/mongo-java-driver/3.5.0/mongo-java-driver-3.5.0.jar
set CLASSPATH=%CLASSPATH%;lib/mongodb-server-tunning-1.0.jar

set CLASSPATH=%CLASSPATH%;conf/

set JAVA_OPTS="-Djava.util.logging.config.file=conf/logging.properties"

rem Heap size and others 
set "JAVA_OPTS=%JAVA_OPTS% -Xms64m"
set "JAVA_OPTS=%JAVA_OPTS% -Xmx64m"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError"
set "JAVA_OPTS=%JAVA_OPTS% -XX:HeapDumpPath=heapdumpfile.hprof"


set "JAVA_OPTS=%JAVA_OPTS% -verbose:gc"
set "JAVA_OPTS=%JAVA_OPTS% -Xloggc:./gclogs.log"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGC"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDetails"
rem set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCTimeStamps"
set "JAVA_OPTS=%JAVA_OPTS% -XX:MaxGCPauseMillis=20000"
rem set "JAVA_OPTS=%JAVA_OPTS% -Djava.rmi.server.hostname=127.0.0.1"
rem set "JAVA_OPTS=%JAVA_OPTS% -Djava.rmi.server.codebase=file:///D:/whereever/lib/mongodb-server-tunning-1.0.jar"

java %JAVA_OPTS% com.saven.mongodb.loadtest.ConcurrentLoadingnQuery ^
						--file ./conf/config.properties ^
						> console.log 2> err.log
rem						--host 127.0.0.1 ^
rem						--db test ^
rem						--collection restaurant ^
rem 					--connections 20 ^
rem 					--max-ll-connections 20 ^

pause
