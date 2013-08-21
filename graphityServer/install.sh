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

# you can set the algorithm acting at the database
# use \"read-optimized\" to use the algorithm optimized for reading processes or
# \"write-optimized\" for the one optimized for writing operation
algorithm = read-optimized

#neo4j settings
use_memory_mapped_buffers = false
cache_type = strong" > /etc/graphity/graphity.conf

# set directory permissions
chown -R tomcat.tomcat /etc/graphity/
