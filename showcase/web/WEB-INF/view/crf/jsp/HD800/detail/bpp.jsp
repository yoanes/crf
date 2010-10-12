<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<title>CRF BPP [HD800]</title>
   	<crf:link device="${context.device}" rel="stylesheet" type="text/css" href="main.css" /> 
   	<crf:link device="${context.device}" rel="stylesheet" type="text/css" href="column.css" />   
    <crf:link device="${context.device}" rel="stylesheet" type="text/css" href="corners.css" />
	<meta name="viewport" content="width = device-width, initial-scale = 1, user-scalable = no" />
	
	<jsp:include page="../../common/scripts.crf"/>
	
  </head>
  <body>

	<jsp:include page="../../detail/logo.crf"/>
	
	
	<div class="tabletColumnA">
	<jsp:include page="../../detail/bdp_actions.crf"/>
	<jsp:include page="../../detail/map.crf"/>
	<jsp:include page="../../detail/bdp_adpoints.crf"/>
	
	</div>
	
	<div class="tabletColumnB">
	    
    <jsp:include page="../../detail/bdp_details.crf"/>
	
	
	</div>
	
	<div style="clear:both;"></div>	
	<jsp:include page="../../detail/bdp_pagination.crf"/>	
	<jsp:include page="../../detail/footer.crf"/>
  </body>
</html>
