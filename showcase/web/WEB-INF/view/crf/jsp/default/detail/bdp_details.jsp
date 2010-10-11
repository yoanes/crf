<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

<div class="roundedWrapper">
	<div class="roundedUpper">
		<crf:img src="common/corners/topleft.image" device="${context.device}"/>
		<%--<crap:image device="${context.device}" path="resources/images/common/corners/topleft.gif" />--%>
	</div>
	
	<div class="roundedContent">


	<div class="contentCellResultWrapperLast">
		<div class="contentCellResultHeader">
			Computers Universe
		</div>
	
	
		<div class="contentCellResultAddress">
			<div>342 Belmore Rd, Balwyn</div>
			<div>East VIC 3103</div>
		</div>
	
		<div class="contentCellResultAction">
			<a href="tel:0353321999">
				<crf:img src="common/icons/call.image" class="actionIcon" device="${context.device}"/>
				<%--<crap:image device="${context.device}" class="actionIcon" path="resources/images/common/icons/call.gif"/>--%>
				Call (03) 5332 1999
			</a>
		</div>
	
	</div>
	

</div>
	
	<div class="roundedLower">
		<crap:image device="${context.device}" path="resources/images/common/corners/bottomleft.gif"/>
	</div>
</div>		



<div class="roundedWrapper">
	<div class="roundedUpper">
		<crap:image device="${context.device}" path="resources/images/common/corners/topleft.gif" />
	</div>
	
	<div class="roundedContent">
	<div class="contentCellDetailActionFirst">
		<a href="#">info@computersballarat.com.au</a>
	</div>
	
	<div class="contentCellDetailActionLast">
		<a href="#">computersballarat.com.au</a>
	</div>	
	

	</div>
	
	<div class="roundedLower">
		<crap:image device="${context.device}" path="resources/images/common/corners/bottomleft.gif"/>
	</div>
</div>	
