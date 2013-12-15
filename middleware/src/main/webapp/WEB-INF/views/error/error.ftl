<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/metalcon.ftl" as mtl>
<@mtl.html>
  <#--
   # Be careful when editing this template. FreeMarker errors that occur while
   # parsing this template will not be displayed in the browser, since this
   # template is responsible for displaying them.
   #-->
  <@mtl.head title="${statusCode} - ${statusMessage}">
    <@mtl.stylesheet href="error.css"/>
  </@mtl.head>
  <@mtl.body>
    <#escape x as x?html>
      <h1>${statusCode} - ${statusMessage}</h1>
    </#escape>
    <#if exception??>
      <p>
        An error occured when trying to acces the request URI:
        <span class="mono">${requestUri}</span>
      </p>
      <p class="mono">
        ${exception?html
          ?replace("\n", "<br/>\n")
          ?replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")}
      </p>
    </#if>
  </@mtl.body>
</@mtl.html>