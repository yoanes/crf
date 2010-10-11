<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>

<%-- 
  - Acceptance criteria covered by the links on this page (Jira CRF-20).
  -
  - Nokia 7600:
  -     only the default group output:
  -         resources/css/default/common/main.css
  -         resources/css/default/results/results.css
  -     nothing output for columns.css, jazz.css, decorations.css
  - iphone:
  -     inheritence from multiple groups up to default group:
  -         resources/css/default/common/main.css
  -         resources/css/webkit/common/main.css
  -         resources/css/applewebkit/common/main.css
  -         resources/css/iphone-ipod/common/main.css
  -
  -     nothing inherited:
  -         resources/css/iphone-ipod/common/columns.css
  -
  -     only the default group output:
  -         resources/css/default/results/results.css
  -
  -     only intermediate group output:
  -         resources/css/webkit/common/jazz.css
  -
  -     only intermediate group + inherited groups but not all the way up to the default group.
  -         resources/css/webkit/common/decorations.css  
  -         resources/css/applewebkit/common/decorations.css
  --%>

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