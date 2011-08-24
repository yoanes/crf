<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<crf:script src="selenium/showcaseAppBundleWithDelayedRenderingPackage1/package" type="text/javascript" device="${context.device}"/>

<crf:link type="text/css" rel="stylesheet" href="selenium/showcaseAppBundleWithDelayedRenderingPackage1/cssFileForExplicitBundling.css"
   device="${context.device}" />

<crf:script name="showcaseAppBundleInlineScriptWithDelayedRendering" type="text/javascript" device="${context.device}">
    var showcaseAppBundleInlineScriptWithDelayedRendering = true
</crf:script>

<crf:script src="selenium/showcaseAppBundleWithDelayedRenderingPackage2/package" type="text/javascript" device="${context.device}"/>

<crf:script src="http://localhost:8080/showcaseAppBundleDelayedRenderingAbsoluteUrl.js" type="text/javascript" device="${context.device}"/>
