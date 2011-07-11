<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<%--
  - Test scripts requested by name.
  --%>
<crf:script src="selenium/common/columns.js" type="text/javascript" device="${context.device}"/> 
<crf:script src="selenium/common/main.js" type="text/javascript" device="${context.device}"/>
<crf:script src="selenium/results/results.js" type="text/javascript"  device="${context.device}"/> 
<crf:script src="selenium/common/jazz.js" type="text/javascript" device="${context.device}"/> 
<crf:script src="selenium/common/decorations.js" type="text/javascript"  device="${context.device}"/>

<%--
  - Acceptance criteria for CRF-47: This duplicate of the same script above will not result in
  - duplicate links being output to the page.   
  --%>
<crf:script src="selenium/common/main.js" type="text/javascript" device="${context.device}"/>

<%--
  - Test scripts requested by package.
  --%>
<crf:script src="selenium/fielddecorators/package" type="text/javascript" device="${context.device}"/> 
<crf:script src="selenium/grid/package" type="text/javascript" device="${context.device}"/> 
<crf:script src="selenium/reporting/package" type="text/javascript" device="${context.device}"/> 
<crf:script src="selenium/animation/package" type="text/javascript" device="${context.device}"/>
<crf:script src="selenium/layers/package" type="text/javascript" device="${context.device}"/> 

<%--
  - Test scripts from a component.
  --%>
<crf:script src="selenium/component/map/package" type="text/javascript" device="${context.device}"/>

<%-- 
  - Acceptance criteria covered by the following links (Jira CRF-60): only
  - one of these should result in an inline script being written to the page. The other
  - is ignored due to having the same name.
  --%>
<crf:script name="myScript" type="text/javascript" device="${context.device}">
    var myScript = "I am here and you should see me only once";
</crf:script> 
<crf:script name="myScript" type="text/javascript" device="${context.device}">
    if (myScript === undefined) { 
        var myScript = "I am here and you should see me only once";
    } else {
        myScript = "I have been included twice";
    }
</crf:script> 

<%-- 
  - Acceptance criteria covered by the following link (Jira CRF-61): absolute url
  - is passed straight through to the output. Usually used to reference a third party
  - JS file. For this showcase, though, we are simply referencing a URL that won't be resolved.
  --%>
<crf:script src="http://localhost:8080/something.js" 
    type="text/javascript" device="${context.device}"/>
    
<%--
  - Explicit bundling of scripts for page authors.
  --%>
<crf:bundleScripts id="showcaseAppBundle" device="${context.device}">  
    <crf:script src="selenium/showcaseAppBundlePackage1/package" type="text/javascript" device="${context.device}"/>
    
    <crf:script name="showcaseAppBundleInlineScript" type="text/javascript" device="${context.device}">
        var showcaseAppBundleInlineScript = true
    </crf:script>
    
    <crf:script src="selenium/showcaseAppBundlePackage2/package" type="text/javascript" device="${context.device}"/>
    
    <crf:script src="http://localhost:8080/showcaseAppBundleAbsoluteUrl.js" type="text/javascript" device="${context.device}"/>
    
</crf:bundleScripts>