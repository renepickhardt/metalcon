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
      <meta http-equiv="content-type"
            content="application/xhtml+xml; charset=UTF-8"/>
      <title>Model</title>
      <link rel="stylesheet" type="text/css"
            href="<@spring.url "/resources/css/model.css"/>"/>
    </head>
    <body>
      <#assign blacklist = ["springMacroRequestContext", "Request", "Session",
                            "RequestParameters", "Application", "JspTaglibs"]>
    
      <#macro print name var rec>
        <div class="name">${name}</div>
        <#if rec?seq_contains(name)>
          <span class="recursive">recursive</span>
        <#else>
          <#local rec=rec+[name]>
          <ul>
            <#if var?is_string>
              <@print_var type="String">"${var}"</@print_var>
            </#if>
            <#if var?is_number>
              <@print_var type="Number">${var?c}</@print_var>
            </#if>
            <#if var?is_boolean>
              <@print_var type="Boolean">${var?c}</@print_var>
            </#if>
            <#if var?is_hash>
              <#if var?is_hash_ex>
                <@print_var type="Extended-Hash">
                  <@print_hash hash=var rec=rec/>
                </@print_var>
              <#else>
                <@print_var type="Hash">?</@print_var>
              </#if>
            </#if>
            <#if var?is_enumerable>
              <@print_var type="Enumerable">
                <@print_enum enum=var rec=rec/>
              </@print_var>
            </#if>
            <#if var?is_node>
              <@print_var type="Node">?</@print_var>
            </#if>
          </ul>
        </#if>
      </#macro>
      
      <#macro print_var type>
        <li class="var-${type?lower_case}">
          <span class="type">${type}</span>:
          <#nested>
        </li>
      </#macro>
      
      <#macro print_enum enum rec>
        <#if enum?size == 0>
          <span class="empty">Empty Enum</span>
        <#else>
          <ul class="list">
            <#local index=0>
            <#list enum as val>
              <li>
                <@print name="["+index+"]" var=val rec=rec/>
              </li>
              <#local index=index+1>
            </#list>
          </ul>
        </#if>
      </#macro>
      
      <#macro print_hash hash rec>
        <#if hash?keys?size == 0>
          <span class="empty">Empty Hash</span>
        <#else>
          <ul class="hash">
            <#list hash?keys as key>
              <#if !(blacklist?seq_contains(key))>
                <li>
                  <@print name=key var=hash[key]!"null" rec=rec/>
                </li>
              </#if>
            </#list>
          </ul>
        </#if>
      </#macro>

      <@print_hash hash=.data_model rec=[]/>
      
    </body>
  </#escape>
</html>