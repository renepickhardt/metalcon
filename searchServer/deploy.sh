#!/bin/bash
mvn compile
mvn package war:war
sudo cp target/searchServer-0.0.1-SNAPSHOT.war /usr/share/tomcat7/webapps/
sudo systemctl restart tomcat7
