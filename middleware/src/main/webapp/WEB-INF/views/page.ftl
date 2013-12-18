<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/metalcon.ftl" as mtl>
<@mtl.html>
  <#assign stylesheets = ["page.css"]>
  
  <#if view.type == "entity">
    <#include "entity/entity.ftl">
  </#if>

  <@mtl.head title="${view_title}">
    <#list stylesheets as stylesheet>
      <@mtl.stylesheet href=stylesheet/>
    </#list>
  </@mtl.head>
  <@mtl.body>
    ${view_content}
  </@mtl.body>
</@mtl.html>