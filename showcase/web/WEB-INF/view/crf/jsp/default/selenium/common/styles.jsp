<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<crf:link href="selenium/common/main.css" rel="stylesheet"  type="text/css" device="${context.device}"/> 
<crf:link href="selenium/common/columns.css" rel="stylesheet"  type="text/css" device="${context.device}"/> 
<crf:link href="selenium/results/results.css" rel="stylesheet"  type="text/css"  device="${context.device}"/> 
<crf:link href="selenium/common/jazz.css" rel="stylesheet"  type="text/css" device="${context.device}"/> 
<crf:link href="selenium/common/decorations.css" rel="stylesheet"  type="text/css"  device="${context.device}"/> 

<%--
  - Acceptance criteria for CRF-47: This duplicate of the same link above will not result in
  - duplicate links being output to the page.   
  --%>
<crf:link href="selenium/common/main.css" rel="stylesheet"  type="text/css" device="${context.device}"/>