<%--
  - TODO: do we need different content types for different devices or do we assume XHTML-MP for all?
  --%>
<%@ page contentType="application/vnd.wap.xhtml+xml" %>

<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Configuring this in web.xml using a jsp-property-group didn't seem to work (not sure why). 
  - Should also work with Tomcat 6.  
  --%>
<%@ page isELIgnored ="false" %>

<jsp:directive.include file="/WEB-INF/view/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/jsp/default/common/projectTagLibs.jsp"/>
