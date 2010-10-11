<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

<div class="mapWrapper">
	<div class="map">
		<crf:img device="${context.device}" src="sandpit/map.image"/>
	</div>
	
	<div class="mapControlsWrapper">
		<div class="zoomControls">
			<a href="#">
				<crf:img device="${context.device}" src="common/icons/in.image" class="mapControl"/>
					
				
			</a>
			<a href="#">
				<crf:img device="${context.device}" src="common/icons/out.image" class="mapControl"/>
					
			
			</a>
		</div>
		
		<div class="directionControls">
			<a href="#">
				<crf:img device="${context.device}" src="common/icons/north.image" class="mapControl"/>
					
				
			</a>
			<a href="#">
				<crf:img device="${context.device}" src="common/icons/south.image" class="mapControl"/>
					
				
			</a>
			<a href="#">
				<crf:img device="${context.device}" src="common/icons/west.image" class="mapControl"/>
					
				
			</a>
			<a href="#">
				<crf:img device="${context.device}" src="common/icons/east.image" class="mapControl"/>
					
				
			</a>
			
		</div>
	
	</div>


</div>
