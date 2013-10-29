#!/bin/bash

# compile, test and export Graphity in order to deploy it
mvn compile
mvn test
mvn war:war

# create directories
mkdir /etc/graphity
mkdir /etc/graphity/database
mkdir /etc/graphity/templates
mkdir /etc/graphity/pictures

# create configuration file
echo "#Graphity settings

# Sets the path to the directory containing the Neo4j database.
# If the directory does not contain a database it will get created at startup.
database_path = /etc/graphity/database/

# Sets the path to the directory containing the status update template files.
# If the directory does not contain a template file a default template \"Plain\" will get created.
templates_path = /etc/graphity/templates/

# Demonstrates the usage of custom file paths in the configuration file.
# Visit the Graphity page on Github for more information.
picture_path = /etc/graphity/pictures/

# Sets the database state.
# Use \"false\" to allow the server to make changes to the database, use \"true\" to let the server read only.
read_only = false

# Sets the Graphity algorithm used.
# Use \"read-optimized\" to use the algorithm optimized for read requests, use \"write-optimized\" to use the one optimized for write requests.
algorithm = read-optimized

#neo4j settings

# Database configuration field. Look in the Neo4j documentation for more information.
use_memory_mapped_buffers = false

# Database configuration field. Look in the Neo4j documentation for more information.
cache_type = strong

#Tomcat settings

# Sets the HTTP header \"Access-Control-Allow-Origin\".
headerAccessControl = *" > /etc/graphity/graphity.conf

echo "<class name=\"Plain\" version=\"1.0\">
  <param name=\"message\" type=\"String\" />
</class>" > /etc/graphity/templates/Plain.xml


# set directory permissions
chown -R tomcat.tomcat /etc/graphity/
