<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>


<%--
  - Acceptance criteria covered by the following images (Jira CRF-12).
  - 
  - iphone or ipod:
  -     resource found in mapComponent-iphone-ipod group only; png format:
  -         resources/images/mapComponent-iphone-ipod/component/map/in.png
  - All other devices:
  -     no img tag output.
  --%>
<div><crf:img src="component/map/in.image" id="mapZoomIn" alt="Map Zoom In" title="Map Zoom In" device="${context.device}"/></div>

<%--
  - Acceptance criteria covered by the following div (Jira CRF-27):
  -     Background tiles image rendered for iphone/ipod devices. 
  --%>

<div class="map">
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
    <div>Map goes here. Map goes here.</div>
</div>