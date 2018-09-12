@echo off
 
set CLASSPATH=%CLASSPATH%;D:/E/apache-maven-repo/.m2/repository/org/mongodb/mongo-java-driver/3.5.0/mongo-java-driver-3.5.0.jar
set CLASSPATH=%CLASSPATH%;lib/mongodb-server-tunning-1.0.jar

set CLASSPATH=%CLASSPATH%;conf/

set JAVA_OPTS="-Djava.util.logging.config.file=conf/logging.properties"

java %JAVA_OPTS% com.saventech.javadriversample.Sample ^
						--file ./conf/config.properties ^
						> console.log 2> err.log
rem						--host 192.168.195.19 ^
rem						--db test ^
rem 					--connections 20 ^

pause
