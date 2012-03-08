<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<%--
  - Explicit bundling of scripts and links for page authors. Also tests the fact that
  - we can nest the bundleScripts and bundleLinks tags.
  --%>
<crf:bundleScripts id="showcaseAppJavaScriptBundle">
    <crf:bundleLinks id="showcaseAppCssBundle">
        <%--
          - Include the scripts and links as a JSP include to make sure that the bundle tags
          - will still be able to work.
          --%>
        <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/explicitBundlingDelegate.crf" />
    </crf:bundleLinks>
</crf:bundleScripts>


<%--
  - Similar to the above, but using the var attribute of bundleScripts to delay rendering.
  --%>
<crf:bundleScripts id="showcaseAppJavaScriptBundleWithDelayedRendering" var="bundleScriptsDelayedRenderingVar">
    <crf:bundleLinks id="showcaseAppCssBundleWithinBundleScriptsWithDelayedRendering">
        <%--
          - Include the scripts and links as a JSP include to make sure that the bundle tags
          - will still be able to work.
          --%>
        <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/explicitBundlingWithDelayedRenderingDelegate.crf" />
    </crf:bundleLinks>
</crf:bundleScripts>
