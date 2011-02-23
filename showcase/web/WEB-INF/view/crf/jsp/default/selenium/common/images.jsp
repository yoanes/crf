<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<div><crf:img src="selenium/common/unmetered.image" id="unmeteredImg" alt="Unmetered" 
    title="Unmetered" device="${context.device}"/></div>
    
<div><crf:img src="selenium/common/app_store.image" id="appStoreImg" alt="App Store" 
    title="App Store" device="${context.device}"/></div>
    
<div><crf:img src="selenium/common/wm.image" id="wherisMobileImg" alt="Whereis Mobile" 
    title="Whereis Mobile" device="${context.device}">WM</crf:img></div>
<div>resolved wm.image: '<crf:imgPath src="selenium/common/wm.image" 
    device="${context.device}"/>'</div>
    
<div><crf:img src="selenium/common/search.image" id="searchImg" alt="Search" 
    title="Search" device="${context.device}">Search</crf:img></div>
    