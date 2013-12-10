<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/metalcon.ftl" as mtl>
<@mtl.html>
  <@mtl.head title="Home - Metalcon Middleware"/>
  <@mtl.body>
    <ul>
      <#list bands as band>
        <li>${band}</li>
      </#list>
    </ul>
  </@mtl.body>
</@mtl.html>