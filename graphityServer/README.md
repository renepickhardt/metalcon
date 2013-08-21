Graphity-Server
===============

Welcome to the Graphity Server component!

We would like to introduce you with a guide showing you how to pull the Graphity News Stream Server and start it.
To do so please follow the steps below.


## Dependencies

Make sure you have installed and configured:
+ Maven (3.0.5)
+ Java JDK 1.7
+ Eclipse (Classic 4.2.2 'Juno')
+ m2e
+ EGit
+ Tomcat 7

If you are not sure how to install or configure one of the components feel free to follow our [detailed guide](DETAILED-SETUP-GUIDE.md).

## 1. Pull Graphity Server

## 2. Test Graphity Server

To check that you have a working copy of the server switch to your git repository directory containing the server and run

```
mvn compile
mvn test
mvn war:war
```

All test must run successfully and the WAR file should get built.

## 3. Prepare for Tomcat deployment

Your working copy must now get installed in order to deploy the WAR file created before.
Simply run

`sudo ./install.sh`

This will create the directory

>/etc/graphity/

in which the server will live for now.  
Therefore make sure to have enough space available there for your database.
Otherwise you can change the database directory in the configuration file.

## 4. Deploy to Tomcat

Now you can deploy the WAR fil to Tomcat.
You should find it in the directory 'target'.
The Graphity server should start up succesfully after the deployment.

## 5. Have fun

Congratulations!

You now own a working news stream server.  

To test this manually feel free to use the following test files:
* [create a user](src/test/java/de/metalcon/server/tomcat/createUser.html)
* [create a follow edge](src/test/java/de/metalcon/server/tomcat/createFriendship.html)
* [create a status update](src/test/java/de/metalcon/server/tomcat/createStatusUpdate.html)
