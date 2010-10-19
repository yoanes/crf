<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <%--
      - Acceptance criteria: test inclusion of relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="../../../selenium/common/header.crf"/>
  </head>
  <body>
    <%--
      - Acceptance criteria: test inclusion of servlet context relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/logo.crf"/>
    
    <div><strong>[HD800] bdp.jsp </strong> </div>
    
    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/deviceProperties.crf"/>
    
    <jsp:include page="/WEB-INF/view/crf/jsp/selenium/common/mainProperties.crf"/>    
        
    <%--
      - Acceptance criteria: test inclusion of relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="../../../selenium/common/images.crf"/>
  </body>
</html>
