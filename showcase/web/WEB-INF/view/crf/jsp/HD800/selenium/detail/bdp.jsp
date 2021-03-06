<?xml version="1.0" encoding="UTF-8"?>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<crf:html device="${context.device}">

  <head>
    <%--
      - Acceptance criteria: test inclusion of relative JSPs via CRF (Jira CRF-16).
      --%>
    <jsp:include page="../../../selenium/common/header.crf"/>

    <jsp:include page="../../../selenium/component/map/mapSetup.crf"/>
  </head>
  <body>
    <%--
      - Acceptance criteria: test inclusion of servlet context relative JSPs via CRF (Jira CRF-16).
      --%>
    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/logo.crf"/>

    <div><strong>[HD800] bdp.jsp </strong> </div>

    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/clickToCall.crf"/>

    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/deviceProperties.crf"/>

    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/mainProperties.crf"/>

    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/resourcePrefixes.crf"/>

    <%--
      - Acceptance criteria: test inclusion of relative JSPs via CRF (Jira CRF-16).
      --%>
    <jsp:include page="../../../selenium/common/images.crf"/>

    <jsp:include page="../../../selenium/component/map/map.crf"/>

    <jsp:include page="../../../selenium/detail/mapAddons.crf"/>

    <crf:renderBundledScripts var="bundleScriptsDelayedRenderingVar"/>

  </body>
</crf:html>
