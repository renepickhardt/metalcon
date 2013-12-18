<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/metalcon.ftl" as mtl>
<@mtl.html>
  <#--
   # This is the default metalcon page template. Every "page" on metalcon uses
   # this view. Inside it, it includes more specific views.
   #-->

  <#--
   # Lists of all css files to be included in the output. Inside a view a view
   # specific stylesheet can be added like this:
   # <#assign stylesheets = stylesheet + ["mystyle.css"]>
   #-->
  <#assign stylesheets = ["page.css"]>
  
  <#--
   # Include more specific view template. These are expected to set the
   # following variables:
   # view_title - String containing the title to be displayed in <title>-tag.
   # view_content - HTML string to contain body content.
   #-->
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