<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<%--
  - Acceptance criteria covered by the following (Jira CRF-67):
  - property file successfully looked up via CRF by the underlying Struts action, then a property 
  - from it is exposed to this JSP.
  --%>
<div>
    <div><strong>[default] mainProperties.jsp </strong>: </div>
    <div>
        'app.property1': <c:out value="${appProperty1}" />  
    </div>
    
</div>