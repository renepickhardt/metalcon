<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/spring.ftl" as spring>

<#macro xhtml>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/2002/08/xhtml/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.w3.org/1999/xhtml
                          http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd"
      lang="de" xml:lang="de">
  <#escape x as x?html>
    <#nested>
  </#escape>
</html>
</#macro>

<#macro head title>
<head>
  <title>${title}</title>
  <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8"/>
  <#nested>
</head>
</#macro>

<#macro body>
<body>
  <#nested>
</body>
</#macro>

<#macro not_implemented>
<@xhtml>
  <@head title="Not implemented"/>
  <@body>
    <h1>Not implemented</h1>
    <p>This view is not implemented yet.</p>
    <#nested>
  </@body>
</@xhtml>
</#macro>