<#ftl encoding="UTF-8" strict_syntax=true>

<#--
 # metalcon.ftl
 #
 # Collections of macros to easy view development.
 #
 # Include in your view like this:
 # <#import "/metalcon.ftl" as mtl>
 #-->
 
<#import "/spring.ftl" as spring>
<#setting locale="de_DE">

<#--
 # Convenience macro to create a <html> tag. Saves us from writing XHTML-
 # boilerplate code everytime.
 # 
 # Usage:
 # <@mtl.html>
 #   Stuff inside <html> tag.
 # </@mtl.html>
 #-->
<#macro html>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/2002/08/xhtml/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.w3.org/1999/xhtml
                          http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd"
      lang="de" xml:lang="de">
  <#nested>
</html>
</#macro>

<#--
 # Convenience macro to create a <head> tag.
 #
 # @param title  A string to be used for the <title> tag.
 #
 # Usage:
 # <@mtl.head title="My Page Title">
 #   Stuff inside <head> tag.
 # </@mtl.head>
 #-->
<#macro head title>
<head>
  <title>${title?html}</title>
  <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8"/>
  <#nested>
</head>
</#macro>

<#--
 # Convenience macro to inclue a CSS-file. Use inside <head> tag.
 #
 # Usage:
 # <mtl.stylesheet href="myStyle.css"/>
 #-->
<#macro stylesheet href>
<link rel="stylesheet" type="text/css" href="<@spring.url "/resources/css/${href}"/>"/>
</#macro>

<#--
 # Convenience macro to create a <body> tag.
 #
 # Usage:
 # <@mtl.body>
 #   Stuff inside <body> tag.
 # </@mtl.body>
 #-->
<#macro body>
<body>
  <#nested>
</body>
</#macro>

<#--
 # To be used in views that are not implemented yet.
 #
 # Usage:
 # <@mtl.not_implemented/>
 #-->
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