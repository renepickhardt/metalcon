<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/metalcon.ftl" as mtl>
<@mtl.html>
  <@mtl.head title="404 - Not found">
    <@mtl.stylesheet href="error.css"/>
  </@mtl.head>
  <@mtl.body>
    <#escape x as x?html>
      <h1>404 - Not Found</h1>
    </#escape>
  </@mtl.body>
</@mtl.html>