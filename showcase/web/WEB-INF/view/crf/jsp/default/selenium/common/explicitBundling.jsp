<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<%--
  - Explicit bundling of scripts and links for page authors. Also tests the fact that
  - we can nest the bundleScripts and bundleLinks tags.
  --%>
<crf:bundleScripts id="showcaseAppJavaScriptBundle">  
    <crf:bundleLinks id="showcaseAppCssBundle">  
	    <crf:script src="selenium/showcaseAppBundlePackage1/package" type="text/javascript" device="${context.device}"/>
	    
	    <crf:link type="text/css" rel="stylesheet" href="selenium/showcaseAppBundlePackage1/cssFileForExplicitBundling.css"
	       device="${context.device}" />
	    
	    <crf:script name="showcaseAppBundleInlineScript" type="text/javascript" device="${context.device}">
	        var showcaseAppBundleInlineScript = true
	    </crf:script>
	    
	    <crf:script src="selenium/showcaseAppBundlePackage2/package" type="text/javascript" device="${context.device}"/>
	    
	    <crf:script src="http://localhost:8080/showcaseAppBundleAbsoluteUrl.js" type="text/javascript" device="${context.device}"/>
    </crf:bundleLinks>    
</crf:bundleScripts>