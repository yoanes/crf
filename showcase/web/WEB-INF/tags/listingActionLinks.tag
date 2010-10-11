<%@ attribute name="actionLinkDisplay" required="true" type="au.com.sensis.yellowMobile.data.search.common.BusinessListingActionModuleViewBean" %>
<%@ attribute name="emailWebId" %>

<%@ taglib uri="/tags/c" prefix="c"%>
<%@ taglib prefix="div" uri="/tags/roundedCorner"%>
<%@ taglib prefix="fmt" uri="/tags/fmt"%>
<%@ taglib prefix="fn" uri="/tags/fn"%>

<div id="listingActionLinks">
	
	<c:if test="${not empty actionLinkDisplay.emailAddress or not empty actionLinkDisplay.confirmLink}">
	
		<div:rc>

	     	<c:if test="${not empty actionLinkDisplay.emailAddress}">
	     	    <c:set var="divToUse" value="contentCellDetailActionLast"/>
	     	
				<c:if test="${empty actionLinkDisplay.confirmLink}">
			     	<c:set var="divToUse" value="contentCellDetailActionLast"/>
				</c:if>
				<c:if test="${not empty actionLinkDisplay.confirmLink}">
				    <c:set var="divToUse" value="contentCellDetailActionFirst"/>
				</c:if>

				<div class="${divToUse} listingActionLinkOverflow">
                    <a href="mailto:${actionLinkDisplay.emailAddress}" id="listingActionLinkEmail${emailWebId}">
                        <c:out value="${actionLinkDisplay.emailAddress}"/>
                    </a>
		    	</div>
			</c:if>

			<c:if test="${not empty actionLinkDisplay.confirmLink}">
				<c:url var="confirmLinkUrl" value="${actionLinkDisplay.confirmLink}"/>
	        	
	        	      	
	        	
	        	<div class="contentCellDetailActionLast listingActionLinkOverflow">
	        		<a href="<c:out value="${confirmLinkUrl}" escapeXml="true"/>" 
					   id="listingActionLinkWeb${emailWebId}">
	        			<%-- The label must be escaped to cater for unescaped ampersands. --%>
                        <c:out value="${fn:substring(actionLinkDisplay.webSite, 0, 60)}" escapeXml="true"/>
                    </a>
        		</div>
			</c:if>
		
		</div:rc>
 			
 	</c:if>
	
	<%-- Displays action links --%>	
	<jsp:directive.include file="/WEB-INF/businessdetail/jsp/actionLinksInclude.jsp" />
	
</div>
