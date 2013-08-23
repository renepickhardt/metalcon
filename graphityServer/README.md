Graphity-Server
===============

Welcome to the Graphity News Stream Server!

We would like to introduce you with a guide showing you how to pull the Graphity Server and start it.
To do so please follow the steps below.


## Dependencies

Make sure you have installed and configured:
+ Maven (3.0.5)
+ Java JDK 1.7
+ Tomcat 7

If you are not sure how to install or configure one of the components or if you want to contribute to the server feel free to follow our [detailed guide](DETAILED-SETUP-GUIDE.md).

## 1. Pull Graphity Server

## 2. Prepare for Tomcat deployment

Your copy must now get installed in order to deploy the WAR file to your Tomcat server.
Simply run

`sudo ./install.sh`

This will compile and export the WAR file and create the directory
>/etc/graphity/

in which the server will live for now.  
Therefore make sure to have enough space available there for your database.
Otherwise you can change the database directory in the configuration file.
[Step 5.1](#51-configuration) describes how to make changes to the server configuration.

## 3. Deploy to Tomcat

Now you can deploy the WAR file to Tomcat.
You should find it in the directory 'target'.
The Graphity server should start up succesfully after the deployment.

## 4. Have fun

Congratulations!

You now own a working news stream server.  

To test this manually feel free to use the following test files:
* [create a user](src/test/java/de/metalcon/server/tomcat/createUser.html)
* [create a follow edge](src/test/java/de/metalcon/server/tomcat/createFriendship.html)
* [create a status update](src/test/java/de/metalcon/server/tomcat/createStatusUpdate.html)


## 5. Customization (optional, of course)

The Graphity server is a highly configurable application.  
You can edit the server configuration file if you do not want to use the default paths or if you want to change the server's behavior.
In addition Graphity supports templates for status updates so you can make the server match your needs.

### 5.1 Configuration

The server configuration file is stored at
>/etc/graphity/graphity.conf

The configuration has 7 fields that have to be set at the moment.
You set a field using the following syntax:
>field_name = value

**database_path**  
Sets the path to the directory containing the Neo4j database.  
If the directory does not contain a database it will get created at startup.  
Default value is
>/etc/graphity/database/

**templates_path**  
Sets the path to the directory containing the status update template files.  
If the directory does not contain a template file a default template "Plain" will get created.  
Default value is
>/etc/graphity/templates/

**picture_path**  
Demonstrates the usage of custom file paths in the configuration file.  
It sets the path to the directory containing picture files used for status updates.  
By default there is no status update template that would allow image files to be sent, but you want to create a matching tempalte on your own.
If you do so the server needs to know where to put these files. You can specify as many additional paths in the configuration file as you want to.  
At the moment the server does use this path to store all files sent. To change this behavior you have to make changes in the code (de.metalcon.server.tomcat.Create:getFileDir) directly, as long as the path is not detected automatically using MIME-types, which is a future task for the development team.  
Default value is
>/etc/graphity/pictures/

**read_only**  
Sets the database state.  
Use "false" to allow the server to make changes to the database, use "true" to let the server read only.  
Default value is
>false

**algorithm**  
Sets the Graphity algorithm used.  
Use "read-optimized" to use the algorithm optimized for read requests, use "write-optimized" to use the one optimized for write requests.  
Default value is
>read-optimized

**use_memory_mapped_buffers**  
Database configuration field. Look in the Neo4j documentation for more information.  
Default value is
>false

**cache_type**  
Database configuration field. Look in the Neo4j documentation for more information.  
Default value is
>strong

### 5.2 Status update templates

Graphity tries not to limit you in your control over the server and the social network you own.  
While every news stream server provider has own needs and targets different user groups it is not possible to determine a set of status update types that will satisfy the needs of each participant.  
Therefore the server allows you to define the status update types that are allowed in your news stream.  
This gets done using status update templates which are XML files following a concrete syntax.

By default the Graphity server knows only one status update template called "Plain".  
You can find this template after a first startup and you have to put all your custom templates in the template path set in the server configuration, so its path is
>/etc/graphity/templates/Plain.xml

by default. If you open the file it will contain
```
<class name="Plain" version="1.0">
  <param name="message" type="String" />
</class>
```

Templates start with a "class"-tag having to attributes called "name" and "version".  
The name of a template is used to identify the template and gets passed by users that want to create a status update of this particular type. Therefore the name has to be unique, otherwise other templates will get overwritten.
The version is not really used yet, but if you have two templates with the same name the previous version gets stored in the database so you could restore it.  
At the moment you can not restore old template versions any other way than making changes to the server code.  

Back to the important part:
The "class"-tag can contains as many "param"- and "file"-tags as you want.
These are the parameters a user has to send in addition to the regular status update create parameters in order to create a status update of this type.

"param"-tags have two attributes called "name" and "type".
The name is used as the name in the create form and has to be unique in the template.
The type sets the Java class corresponding to the value you want to have.
At the moment only "String", "Integer" and "Boolean" are allowed.

"file"-tags do also have the "name"-attribute and another one called "contentType".
This String sets the MIME-type the file sent by the user has to match to.

Please keep in mind that the templates get loaded **at startup** and therefore the server needs to restart to know new templates.
