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
  </head>
  <body>
    <%--
      - Acceptance criteria: test inclusion of servlet context relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="/WEB-INF/view/jsp/common/logo.crf"/>
  
    <div><strong>[default] bdp.jsp </strong> </div>

    <%--
      - Acceptance criteria: test inclusion of relative JSPs via CRF (Jira CRF-16). 
      --%>
    <jsp:include page="../../common/images.crf"/>
  </body>
</html>
