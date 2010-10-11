<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

<div class="roundedWrapper">
	<div class="roundedUpper">
		<crf:img src="common/corners/topleft.image" device="${context.device}"/>
		<%--<crap:image device="${context.device}" path="resources/images/common/corners/topleft.gif" />--%>
	</div>
	
	<div class="roundedContent">


	<div class="contentCellDetailActionFirst">
		
		 <a href="#"><%--<crap:image device="${context.device}" class="actionIcon" path="resources/images/common/icons/add.gif"/>--%>
		 <crf:img src="common/icons/add.image" class="actionIcon" device="${context.device}"/>
		 Add to contacts</a>
		
	</div>

	<div class="contentCellDetailAction">
		<a href="#"><%--<crap:image device="${context.device}" class="actionIcon" path="resources/images/common/icons/send.gif"/>--%>
		<crf:img src="common/icons/send.image" class="actionIcon" device="${context.device}"/>
		 Send to a friend</a>
	</div>
	
	

	<div class="contentCellDetailActionLast">
		<a href="#"><%--<crap:image device="${context.device}" class="actionIcon" path="resources/images/common/icons/directions.gif"/>--%>
		<crf:img src="common/icons/directions.image" class="actionIcon" device="${context.device}"/>
		 Get directions</a>
	</div>	

	</div>
	
	<div class="roundedLower">
		<%--<crap:image device="${context.device}" path="resources/images/common/corners/bottomleft.gif"/>--%>
		<crf:img src="common/corners/bottomleft.image" device="${context.device}"/>
	</div>
</div>
