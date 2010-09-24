<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>

<%-- 
  - Acceptance criteria covered by the following images (Jira CRF-23).
  -
  - Nokia 7600:
  -     resource found in default group only; gif format:
  -         resources/images/nokia7600/common/unmetered.gif
  -     resource not found in any group:  
  -         no img tag written out for common/app_store.image
  - HD800 device:
  -     resource found in default group only; png format:
  -         resources/images/default/common/unmetered.png
  -     resource not found in any group:  
  -         no img tag written out for common/app_store.image
  - iPhone OS 2.x:
  -     resource found in non-default group; png format:
  -         resources/images/iphone-ipod-os2/common/unmetered.png
  -     image explicitly null:
  -         no img tag written out for common/app_store.image  
  - iPhone OS 3+:
  -     resource found in default group only; png format:
  -         resources/images/default/common/unmetered.png
  -     resource found in intermediate group; png format:
  -         resources/images/applewebkit/common/app_store.png
  --%>

<div><crf:img src="common/unmetered.image" id="unmeteredImg" alt="Unmetered" title="Unmetered" device="${context.device}"/></div>
<div><crf:img src="common/app_store.image" id="appStoreImg" alt="App Store" title="App Store" device="${context.device}"/></div>