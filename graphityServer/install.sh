#!/bin/bash

# create directories
mkdir /etc/graphity
mkdir /etc/graphity/database
mkdir /etc/graphity/templates
mkdir /etc/graphity/pictures

# create configuration file
echo "#social graph settings
database_path = /etc/graphity/database/
templates_path = /etc/graphity/templates/
picture_path = /etc/graphity/pictures/
read_only = false
algorithm = graphity

#neo4j settings
use_memory_mapped_buffers = false
cache_type = strong" > /etc/graphity/graphity.conf

# set directory permissions
chown -R tomcat.tomcat /etc/graphity/
