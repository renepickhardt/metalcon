<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/metalcon.ftl" as mtl>
<@mtl.html>
  <@mtl.head title="Band"/>
  <@mtl.body>
    <p>${band.name}</p>
    <p>${band.name}</p>
    <p>${band.foundation?datetime}</p>
    <p>${.locale}</p>
  </@mtl.body>
</@mtl.html>