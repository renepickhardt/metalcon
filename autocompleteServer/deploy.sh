#!/bin/bash
mvn compile
mvn package war:war
#sudo /etc/init.d/tomcat7 stop
#sudo cp target/autocompleteServer-0.0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/a#utocompleteServer-0.0.1-SNAPSHOT.war
#sudo /etc/init.d/tomcat7 start
