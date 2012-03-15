<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>

<div>
    <div><strong>[default] deviceProperties.jsp </strong>: </div>   
    
    <div>
        <%--
          - Acceptance criteria covered by the following (Jira CRF-59):
          - custom.imageCategory property of the device successfully retrieved and written
          - directly to the page.
          --%>
        'custom.imageCategory': <crf:deviceProperty property="custom.imageCategory" device="${context.device}"/> 
    </div>
    
</div>