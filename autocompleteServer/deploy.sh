#!/bin/bash
mvn compile
mvn package war:war
sudo cp target/autocompleteServer-0.0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/autocompleteServer-0.0.1-SNAPSHOT.war
sudo /etc/init.d/tomcat7 restart