<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="crap" uri="/au/com/sensis/mobile/crap/crap.tld"%>

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="crap-environment" />    

<c:set var="webappContextRoot">
    <fmt:message key="crap.env.webapp.context.root" />
</c:set>

<crap:link device="${context.device}" rel="stylesheet" href="${webappContextRoot}/css/home.css" />

