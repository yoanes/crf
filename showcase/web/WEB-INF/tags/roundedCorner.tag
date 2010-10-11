<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Configuring this in web.xml using a jsp-property-group didn't seem to work (not sure why). 
  - Should also work with Tomcat 6.  
  --%>
<%@ tag isELIgnored="false" %>

<%@ attribute name="css" %>
<%@ attribute name="colour" %>
<%@ taglib uri="/tags/c" prefix="c"%>


<c:set var="addedClass" value="" />

<c:if test="${not empty css}">
	<c:set var="addedClass" value=" ${css}" />
</c:if> 

<c:set var="topImagePath" value="upperRoundedCorner.mimg" />
<c:set var="bottomImagePath" value="lowerRoundedCorner.mimg" />

<c:if test="${colour eq 'yellow'}">
	<c:set var="topImagePath" value="yellowUpperRoundedCorner.mimg" />
	<c:set var="bottomImagePath" value="yellowLowerRoundedCorner.mimg" />
</c:if>

<c:if test="${colour eq 'lightYellow'}">
	<c:set var="topImagePath" value="lightYellowUpperRoundedCorner.mimg" />
	<c:set var="bottomImagePath" value="lightYellowLowerRoundedCorner.mimg" />
</c:if>

<div class="roundedWrapper${addedClass}">
	<div class="roundedUpper">
		<object src="/ym/images/furniture/${topImagePath}" />
	</div>
	
	<div class="roundedContent">
		<jsp:doBody/>
	</div>
	
	<div class="roundedLower">
		<object src="/ym/images/furniture/${bottomImagePath}"/>
	</div>
</div>
