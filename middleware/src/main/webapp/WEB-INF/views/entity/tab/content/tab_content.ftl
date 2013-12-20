<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/metalcon.ftl" as mtl>

<div id="tabContent">
  <#--
   # Will include the corrrect tabPreview subtemplate.
   # For example, if tabPreviewName is "ABOUT_TAB" this will include "impl/about_tab.ftl"
   #-->
  <#include "impl/" + tab.entityTabType?lower_case + ".ftl">
</div>