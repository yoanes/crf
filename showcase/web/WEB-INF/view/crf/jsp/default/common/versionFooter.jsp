<jsp:directive.include file="/WEB-INF/view/jsp/default/common/configInclude.jsp"/>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="footerLinks">

    <a href="<s:url namespace='/' action='version'/>">
        <fmt:message key="app.version.link.label"/>
    </a>

</div>
