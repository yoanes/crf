<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Configuring this in web.xml using a jsp-property-group didn't seem to work (not sure why). 
  - Should also work with Tomcat 6.  
  --%>
<%@ attribute name="device" required="true"
    type="au.com.sensis.wireless.common.volantis.devicerepository.api.Device"  
    description="Device of the current user." %>

<%@ taglib uri="/tags/c" prefix="c" %>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>

<core:deviceConfig var="deviceConfig" device="${device}" 
    registryBeanName="yellowadhoc.comp.deviceConfigRegistry"/>

<c:if test="${deviceConfig.supportsActionModule}" >	

	<div id="actionModuleFirewall"> </div>
		           
	<div id="actionModuleWindowContainer">
	
		<div id="actionModuleWindowBorder">
		
			<div id="actionModuleWindowArrow">
				 <object src="/ym/images/furniture/actionWindowTail.mimg"/>
			</div>
		
			<div id="actionModuleWindowContent">
			
				content
			
			</div>
			
		</div>
		
	</div>	

</c:if>
		
