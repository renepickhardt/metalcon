<#ftl encoding="UTF-8" strict_syntax=true strip_whitespace=true>
<#import "/metalcon.ftl" as mtl>
<#import "/spring.ftl" as spring>
<@mtl.html>
  <@mtl.head title="News ${userId} - Metalcon Middleware">
    <link rel="stylesheet" type="text/css" href="<@spring.url "/resources/css/test/news.css"/>"/>
  </@mtl.head>
  <@mtl.body>
    <h1>Metalcon Newsstream</h1>
    <ul class="info">
      <li>User: <em>${userId}</em></li>
       <li>Poster: <em>${posterId}</em></li>
       <li><em>${ownUpdates?string("Showing", "Not showing")}</em> own updates</li>
    </ul>
    <h2>Post</h2>
    <form id="post" action="<@spring.url "/test/news/${userId}/${posterId}/${ownUpdates?c}/post"/>" method="POST">
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
            <span class="verb">${item.verb}</span>
            <span class="actor">${item.actor.displayName}</span>
            <span class="actor_info">(${item.actor.id} - ${item.actor.objectType})</span>
            <span class="published">${item.published}</span>
          </p>
          <p class="body">
            <span class="body_info">(${item.object.id} - ${item.object.objectType},${item.object.type})</span>
            ${item.object.message}
          </p>
        </li>
      </#list>
    </ul>
  </@mtl.body>
</@mtl.html>