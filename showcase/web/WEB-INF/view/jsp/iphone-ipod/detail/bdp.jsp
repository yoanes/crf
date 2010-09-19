<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <%--
      - Acceptance criteria: test inclusion of relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="../../common/header.crf"/>
    
    <jsp:include page="../../component/map/mapSetup.crf"/>
  </head>
  <body>
    <%--
      - Acceptance criteria: test inclusion of servlet context relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="/WEB-INF/view/jsp/common/logo.crf"/>
    
    <div><strong>[iphone-ipod] bdp.jsp </strong> </div>
    
    <%--
      - Acceptance criteria: test inclusion of relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="../../common/images.crf"/>
    

    <jsp:include page="../../component/map/map.crf"/>    
  </body>
</html>