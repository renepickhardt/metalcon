## how to get the server running after cloning the git repository
* mvn compile
* mvn package war:war
* cd tomcat
* sudo cp autocompleteServer-0.0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/
* sudo /etc/init.d/tomcat7 restart
* http://localhost:8080/autocompleteServer-0.0.1-SNAPSHOT/suggest?Search_Term=Nightw