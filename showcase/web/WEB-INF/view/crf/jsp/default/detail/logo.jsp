<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

	<div class="carrierHeader" id="freeHeader">
		<crf:img src="common/unmetered/unmetered.image" id="freeImg" alt="Unmetered" title="Unmetered" device="${context.device}"/>
         <%--<crap:image device="${context.device}" path="resources/images/common/unmetered/unmetered.gif" id="freeImg" alt="Unmetered" title="Unmetered"/>--%>
	</div>
	

		
<div class="roundedWrapper yellow">
	<div class="roundedUpper">
		<crf:img src="common/corners/corner_TL_y.image" device="${context.device}"/>
		<%--<crap:image device="${context.device}" path="resources/images/common/corners/corner_TL_y.gif" />--%>
	</div>
	
	<div class="roundedContent">
		<crf:img src="header/ym.image" alt="Yellow Pages" title="Yellow Pages" device="${context.device}"/>
		<%--<crap:image device="${context.device}" path="resources/images/header/ym.gif" alt="Yellow Pages" title="Yellow Pages"/>--%>
	</div>
	
	<div class="roundedLower">
		<crf:img src="common/corners/corner_BL_y.image" device="${context.device}"/>
		<%--<crap:image device="${context.device}" path="resources/images/common/corners/corner_BL_y.gif"/>--%>
	</div>
</div>		
