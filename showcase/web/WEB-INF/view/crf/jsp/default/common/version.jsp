<jsp:directive.include file="/WEB-INF/view/jsp/default/common/configInclude.jsp"/>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div style="margin-top: 10px; margin-bottom: 10px;">
    <fmt:setBundle var="globalProperties" basename="global-version"/> 
    
    <fmt:message key="app.help.build.label" />
    <fmt:message key="version" bundle="${globalProperties}" />.<fmt:message key="build" bundle="${globalProperties}"/>
    <span style="display: block; margin-top: 10px;" />
 
    <fmt:message key="app.help.platform.label" /> <fmt:message key="platform" bundle="${globalProperties}" />
    <span style="display: block; margin-top: 10px;" />
 
    <fmt:message key="app.help.builddate.label"/> <fmt:message key="time" bundle="${globalProperties}" />

</div>
