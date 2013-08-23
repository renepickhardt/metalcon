The protocol is called "News Stream Server Protocol" or short "NSSP" and defines how the REST-API of the News Stream Server can be accessed and also how the API will responst do requests. One main feature is that the retrieved streams in JSON format are compatible with the [Activitystrea.ms protocol](http://activitystrea.ms/specs/json/1.0/) thus most clients should be able to plugin right away.

The typical **CRUD** operations are supported.

**Status messages**  
For every operation there are human readable status messages naming the problem. In addition a second line says how to solve the problem and may give a more detailed description of the problem.
These two lines together will compose a JSON object with the field "statusMessage" and the optional field "solution" if the status message is an error.
In case multiple errors occurred only the first problem will be reported and the server will stop handling the request.  
For example the response for an incomplete request with a missing number X will be
```
{
    "statusMessage":"request incomplete: parameter X is missing",
    "solution":"Please provide the number X that must be greater than zero."
}
```

### Create

Two ways to create data entries. One is for single entries (or rather small numbers sequentially).  
The other one is for importing whole databases (so called "bulk import"). This allows streaming whole CSV-files instead of every single entry of it separately.

There are three different types of create requests, therefore each create request has to define the type of the request. You can create users, add follow edges to users and create status updates.

As status updates may contain files all create request have to be **multipart requests**.  
Otherwise you will get a status message:
```
{
    "statusMessage":"create requests have to be multipart requests",
    "solution":"Please use a multipart form to execute your request."
}
```

#### Create a user

To create a user pass **"user"** as the type of the create request.
In addition you need to specify an identifier for the new user, the display name and the path to the user profile picture.  

**Parameters**
* type: "user" (String)
* user_id: specifying the identifier of the new user (String)
* user_display_name: setting the name to be displayed in status updates of this user (String)
* user_profile_picture_path: specifying the path to the user's profile picture (String)

**Status messages**
* "ok" if everything succeeded
* "type invalid" if the create type is invalid
* "user identifier invalid" if the user identifier is already in use

**Example**
```
HTTP/1.0 POST somedomain/stream/create
Content-type: multipart/form-data, boundary=AaB03x

--AaB03x
content-disposition: form-data; name="type"

user
--AaB03x
content-disposition: form-data; name="user_id"

12345
--AaB03x
content-disposition: form-data; name="user_display_name"

Testy
--AaB03x
content-disposition: form-data; name="user_profile_picture_path"

http://global3.memecdn.com/believe-it-or-not-there-is-a-panda-in-the-picture_o_1314677.jpg
--AaB03x--
```

#### Create a follow edge

To create a follow edge to a user pass **"follow"** as the type of the create request.
In addition you need to specify which user you want to create the edge to.
The edge will add this user to the social network of the user requesting.

**Parameters**
* type: "follow" (String)
* user_id: specifying the requesting user's identifier (String) 
* followed_id: specifying the identifier of the user the new follow edge shall lead to (String)

**Status messages**
* "ok" if everything succeeded
* "user identifier invalid" if there is no user with such identifier
* "type invalid" if the create type is invalid
* "followed identifier invalid" if there is no user with such identifier
* "follow edge existing" if there is already a follow edge to the user specified

**Example**
```
HTTP/1.0 POST somedomain/stream/create
Content-type: multipart/form-data, boundary=AaB03x

--AaB03x
content-disposition: form-data; name="user_id"

12345
--AaB03x
content-disposition: form-data; name="type"

follow
--AaB03x
content-disposition: form-data; name="followed_id"

1337
--AaB03x--
```

#### Create a status update

To create a status update pass **"status_update"** as the type of the create request.
In addition you need to specify the identifier and the type of the status update, as there may exist various status update templates registered in the server.
This template may lead to several other parameters necessary to create the status update.  
For example a simple template for text-only status updates could require a parameter named "message" holding the message that should be displayed.

**Parameters**
* type: "status_update" (String)
* user_id: specifying the requesting user's identifier (String) 
* status_update_id: specifying the status update identifier (String)
* status_update_type: specifying the status update template used (String)
* { ... } various parameters determined by the status update template used


**Status messages**
* "ok" if everything succeeded
* "user identifier invalid" if there is no user with such identifier
* "type invalid" if the create type is invalid
* "status update identifier invalid" if the status update identifier is already in use
* "status update type invalid" if the status update template is unknown
* "status update instantiation failed" if the parameters passed did not match the status update template specified

**Example**
```
HTTP/1.0 POST somedomain/stream/create
Content-type: multipart/form-data, boundary=AaB03x

--AaB03x
content-disposition: form-data; name="user_id"

12345
--AaB03x
content-disposition: form-data; name="type"

status_update
--AaB03x
content-disposition: form-data; name="status_update_type"

text
--AaB03x
content-disposition: form-data; name="message"

Hello World
--AaB03x--
```

## Read

Be aware that the server response currently sets the http header `Access-Control-Allow-Origin: *` which allows http://en.wikipedia.org/wiki/Cross-Origin_Resource_Sharing and therby allowing any domain and any website to request news streams

To read status updates you need to pass two parameters.  
Then pass the identifier of the user whose status update you want to retrieve.
You also need to define how many news feed items should be retrieved.
A flag to switch between the retrieval of status updates **for** the user, from the user's social network, and **from** the user, meaning the update items this user created.

**Parameters**
* user_id: specifying the requesting user's identifier (String) 
* poster_id: specifying the identifier of the user whose status update you want to retrieve (String)
* num_items: defining how many news feed items should be retrieved (int)
* own_updates: **0** to retrieve status updates **for** the user (**notice**: this will also be recognized if the requesting user accesses his own stream), **1** to retrieve status updates **from** the user (int)

**Status messages**
* JSON matching Activitystrea.ms representing the news stream accessed, if everything succeeded
* "user identifier invalid" if there is no user with such identifier
* "poster identifier invalid" if there is no user with such identifier
* "number of items invalid" if the number of items passed is invalid
* "retrieval flag invalid" if the flag for own updates is invalid

**Example**
```
HTTP/1.0 GET somedomain/stream/read?user_id=12345&num_items=15&own_updates=0
```

## Update

The news stream server does not provide updates yet.

## Delete

There are three different types of delete requests, therefore each delete request has to define the type of the request. You can delete users, delete follow edges to users and delete status updates.

#### Delete a user

To delete a user pass **"user"** as the type of the delete request.
In addition you need to pass the identifier of the user you want to delete.  
Please notice: The delete user request uses the basic parameter **user_id** in another way.

**Parameters**
* type: "user" (String)
* user_id: specifying the identifier of the user that shall be deleted (String)

**Status message**
* "ok" if everything succeeded
* "type invalid" if the delete type is invalid
* "user identifier invalid" if there is no user with such identifier

**Example**
```
HTTP/1.0 POST somedomain/stream/delete
Content-type: application/x-www-form-urlencoded

type=user&user_id=12345
```

#### Delete a follow edge

To delete a follow edge to a user pass **"follow"** as the type of the delete request.  
In addition you need to specify which user you want to delete the edge from.
The deletion will remove this user from the social network of the user requesting.

**Parameters**
* user_id: specifying the requesting user's identifier (String) 
* followed_id: specifying the identifier of the user the existing follow edge leads to (String)

**Status messages**
* "ok" if everything succeeded
* "user identifier invalid" if there is no user with such identifier
* "type invalid" if the delete type is invalid
* "followed identifier invalid" if there is no user with such identifier
* "follow edge not existing" if there is no follow edge to the user specified

**Example**
```
HTTP/1.0 POST somedomain/stream/delete
Content-type: application/x-www-form-urlencoded

user_id=12345&type=follow&followed_id=815
```

#### Delete a status update

To delete a status update pass **"status_update"** as the type of the delete request.
In addition you need to specify the identifier of the status update you want to delete.

**Parameters**
* user_id: specifying the requesting user's identifier (String) 
* status_update_id: specifying the identifier of the status update that shall be deleted (String)

**Status messages**
* "ok" if everything succeeded
* "user identifier invalid" if there is no user with such identifier
* "type invalid" if the delete type is invalid
* "status update identifier invalid" if there is no status update with such identifier
* "status update not owned" if the status update is not owned by the user requesting

**Example**
```
HTTP/1.0 POST somedomain/stream/delete
Content-type: application/x-www-form-urlencoded

user_id=12345&type=status_update&status_update_id=12345678
```

## Features

### Status Update Templates

**Example**
```
<class name="Plain" version="1.0">
  <param name="message" type="String" />
</class>
```

