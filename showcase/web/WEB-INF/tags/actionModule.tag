<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Configuring this in web.xml using a jsp-property-group didn't seem to work (not sure why). 
  - Should also work with Tomcat 6.  
  --%>

<%@ attribute name="listingCount" required="true" %> 
<%@ attribute name="listingSummary" required="true" type="au.com.sensis.yellowMobile.data.search.common.BusinessListingSummaryViewBean" %>
<%@ attribute name="listingUrl" required="true" %> 

<%@ taglib uri="/tags/c" prefix="c" %>
<%@ taglib prefix="div" uri="/tags/roundedCorner" %>
<%@ taglib prefix="listing" uri="/tags/listing" %>


<c:choose>
	<c:when test="${listingSummary.displayBusinessLogo == 'true'}">

		<div class="actionModuleOpenButtonBelowImageWrapper">
		
	</c:when>
	<c:otherwise>
	
		<div class="actionModuleOpenButtonWrapper">
	
	</c:otherwise>
</c:choose>

			<a href="${listingUrl}" class="actionModuleOpenButtonAnchor">
				<object src="/ym/images/common/icons/actionModuleOpen.mimg" id="Num${listingCount}"/>
			</a>
		</div>

<div class="actionModuleHiddenContent" id="actionModuleHiddenNum${listingCount}">
		
	<div class="actionModuleTitle">
		<c:out value="${listingSummary.businessName}"/>
	</div>
	
	<div class="actionModuleContent">
	
		<listing:actionLinks actionLinkDisplay="${listingSummary.actionModule}" emailWebId="Num${listingCount}" />
	
	</div>
	
	<div class="contentCellWrapper actionModuleMoreLink">
		<div:rc>
			<a href="${listingUrl}">More</a>
		</div:rc>
	</div>
			
	<div style="clear:right; height: 1px;">&#160;</div>	
	
</div>
		
