Connect to MongoDB with many clients 
and insert
and then query
and then close the connection

This program is to load test the mongodb ... Need to tune no. of opened connections and no. of opened files (lsof)

Requirement:
============
JDK 8
Apache Maven 3.3.9
mongo-java-driver 3.5.0
mongodb server 3.4.x
Windows 7 (x86_64) or Linux

BUILD:
======
$ git clone https://github.com/hidayathbasha/mongodb-load-test.git .
$ mvn clean install 

Copy the dependencies
$ cp target/mongodb-server-tunning-1.0.jar lib/
$ cp <maven-repo>/org/mongodb/mongo-java-driver/3.5.0/mongo-java-driver-3.5.0.jar lib/

where maven-repo is as pointed by <localRepository> in ${maven.home}/conf/settings.xml

Make sure that mongodb is running

Make necessary changes in conf/config.properties and run
$ sh run.sh

References:
===========
https://docs.mongodb.com/manual/reference/ulimit/#ulimit
https://docs.mongodb.com/manual/reference/configuration-options/#net.maxIncomingConnections

Getting Started with MongoDB (Java Edition)
https://docs.mongodb.com/getting-started/java/
