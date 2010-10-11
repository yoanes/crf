<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

<div class="resultsBlockWrapper">

<div class="resultsHeadingWrapper">

<div class="resultsInfo">
	Computers, Melbourne
</div>


<div class="sortWrapper">
	<span class="sortImgWrapperOn">
		<crf:img device="${context.device}" class="actionIcon" src="furniture/nearest_on.image"/>
	</span>
	<span class="sortImgWrapperOff">
		<a href="#" class="sortLink">
			<crf:img device="${context.device}" class="actionIcon" src="furniture/servicing_off.image"/>
		</a>
	</span>
</div>

</div>


<div class="tabletColumnA">
	<jsp:include page="../../detail/map.crf"/>
</div>

<div class="tabletColumnB">
	<jsp:include page="../../detail/map_results_listings.crf"/>
</div>

<div class="roundedWrapper">
	<div class="roundedUpper">
		<crf:img device="${context.device}" src="common/corners/topleft.image" />
	</div>
	
	<div class="roundedContent">	

	<div class="contentCellNavigationOrphan">
		<a href="results.xdime">Next page</a>
	</div>

	</div>
	
	<div class="roundedLower">
		<crf:img device="${context.device}" src="common/corners/bottomleft.image"/>
	</div>
</div>		



</div>




