<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<title>CRF BPP [default]</title>
  	<crf:link href="main.css" rel="stylesheet"  type="text/css" device="${context.device}"/> 
  	<crf:link href="corners.css" rel="stylesheet"  type="text/css" device="${context.device}"/> 

	<meta name="viewport" content="width = device-width, initial-scale = 1, user-scalable = no" />
	
	<jsp:include page="../../common/scripts.crf"/>
	
  </head>
  <body>

	<jsp:include page="../../detail/logo.crf"/>
    <jsp:include page="../../detail/bdp_details.crf"/>
	<jsp:include page="../../detail/bdp_actions.crf"/>
	<jsp:include page="../../detail/map.crf"/>
	<jsp:include page="../../detail/bdp_pagination.crf"/>	
	<jsp:include page="../../detail/footer.crf"/>
	
  </body>
</html>
