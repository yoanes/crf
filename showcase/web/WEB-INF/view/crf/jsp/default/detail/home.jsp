<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <crf:link device="${context.device}" rel="stylesheet" type="text/css" href="main.css" />
    <crf:link device="${context.device}" rel="stylesheet" type="text/css" href="corners.css" />
	<meta name="viewport" content="width = device-width, initial-scale = 1, user-scalable = no" />
	<crf:script device="${context.device}" type="text/javascript" src="main.js"/> 
  </head>
  <body>

	<jsp:include page="../../detail/logo.crf"/>
	<jsp:include page="../../detail/find.crf"/>
    <jsp:include page="../../detail/home_body.crf"/>
	<jsp:include page="../../detail/footer.crf"/>
  </body>
</html>
