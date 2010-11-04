<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>


<div class="roundedWrapper yellow">
	<div class="roundedUpper">
		<crf:img device="${context.device}" src="common/corners/corner_TL_y.image" />
	</div>
	
	<div class="roundedContent">

	<div class="findFormCellTop">
		&#160;
	</div>


	<div class="findFormSearch">
		<div class="findFormLabelCell" id="findFormFindLabel">
				Find
			</div>
			<div class="findFormInputCell" id="findInputCell">
				<input type="text" id="findSearchInput"/>
			</div>
	
	</div>

		
	<div class="findFormNear">
			<div class="findFormLabelCell" id="findFormNearLabel">
				Near
			</div>
			<div class="findFormInputCell">
				<input type="text" id="findLocationInput"/>
			</div>

	</div>
	

	<div class="findFormCellBottom">

			<div class="locationTip">
					Leave blank to use your location
			</div>

	
			<div class="findFormSubmitCell">
				<c:set var="contextpath" value=""/>
				<c:if test="${pageContext.request.contextPath == ''}">
					<c:set var="contextpath" value="${pageContext.request.contextPath}/"/>
				</c:if>
				
				<input type="image" src="${contextpath}uiresources/images/furniture/search.image" id="findFormSubmit"/>
					
			</div>
	</div>
	
	<div class="dirtyThubHack">&#160;</div>
	
	</div>
	
	<div class="roundedLower">
		<crf:img device="${context.device}" src="common/corners/corner_BL_y.image"/>
	</div>
</div>	