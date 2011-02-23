<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<div><strong>[default] logo.jsp </strong> </div>
<div><crf:img src="selenium/common/yellow-pages.image" id="yellowPagesLogoImg" alt="Yellow Pages" 
    title="Yellow Pages" device="${context.device}">Yellow Pages</crf:img></div>
<div>resolved yellow-pages.image: '<crf:imgPath src="selenium/common/yellow-pages.image" 
    device="${context.device}"/>'</div>

