<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/spring.ftl" as spring>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/2002/08/xhtml/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.w3.org/1999/xhtml
                          http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd"
      lang="de" xml:lang="de">
  <#escape x as x?html>
    <head>
      <title>News ${userId} - Metalcon Middleware</title>
      <meta http-equiv="content-type"
            content="application/xhtml+xml; charset=UTF-8" />
      <link rel="stylesheet" type="text/css"
            href="<@spring.url "/resources/css/news.css"/>"/>
    </head>
    <body>
      <h1>Metalcon Newsstream</h1>
      <ul class="info">
        <li>User: <em>${userId}</em></li>
        <li>Poster: <em>${posterId}</em></li>
        <li><em>${ownUpdates?string("Showing", "Not showing")}</em> own updates</li>
      </ul>
      <h2>Post</h2>
      <form id="post" action="<@spring.url "/news/${userId}/${posterId}/${ownUpdates?c}/post"/>" method="POST">
        <label for="formMessage">Message:</label>
        <textarea id="formMessage" name="formMessage" rows="5"></textarea>
        <label for="formSubmit">Submit:</label>
        <input id="formSubmit" name="formSubmit" type="submit" value="Post"/>
      </form>
      <h2>News</h2>
      <ul id="news">
        <#list news as item>
          <li class="item">
            <p class="head">
              <span class="verb">${item.verb.value}</span>
              <span class="actor">${item.actor.displayName}</span>
              <span class="actor_info">(${item.actor.id} - ${item.actor.objectType} )</span>
            </p>
            <p class="body">
              <span class="body_info">(${item.object.id} - ${item.object.objectType},${item.object.type} )</span>
              ${item.object.message}
            </p>
          </li>
        </#list>
      </ul>
    </body>
  </#escape>
</html>