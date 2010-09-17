<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<?xml version="1.0" encoding="UTF-8"?>

<%-- 
  - Acceptance criteria covered by the links on this page (Jira CRF-20).
  -
  - Nokia 7600:
  -     only the default group output:
  -         resources/js/default/common/main.js
  -         resources/js/default/results/results.js
  -     nothing output for columns.js, jazz.js, decorations.js
  - iphone:
  -     inheritence from multiple groups up to default group:
  -         resources/js/default/common/main.js
  -         resources/js/webkit/common/main.js
  -         resources/js/applewebkit/common/main.js
  -         resources/js/iphone-ipod/common/main.js
  -
  -     nothing inherited:
  -         resources/js/iphone-ipod/common/columns.js
  -
  -     only the default group output:
  -         resources/js/default/results/results.js
  -
  -     only intermediate group output:
  -         resources/js/webkit/common/jazz.js
  -
  -     only intermediate group + inherited groups but not all the way up to the default group.
  -         resources/js/webkit/common/decorations.js  
  -         resources/js/applewebkit/common/decorations.js
  --%>
<crf:script href="common/main.js" type="text/javascript" device="${context.device}"/> 
<crf:script href="common/columns.js" type="text/javascript" device="${context.device}"/> 
<crf:script href="results/results.js" type="text/javascript"  device="${context.device}"/> 
<crf:script href="common/jazz.js" type="text/javascript" device="${context.device}"/> 
<crf:script href="common/decorations.js" type="text/javascript"  device="${context.device}"/>

<%-- 
  - Acceptance criteria covered by the links on this page (Jira CRF-31).
  -
  - Nokia 7600:
  -     only the default group output; bundles.properties with partially defined order 
  -         resources/js/default/fielddecorators/decorator2.js
  -         resources/js/default/fielddecorators/decorator1.js
  -         resources/js/default/fielddecorators/decorator3.js
  -         resources/js/default/component/map/map1.js
  -         resources/js/default/component/map/map2.js
  -     no scripts output for grid/bundle, layers/bundle and animation/bundle 
  - iphone:
  -     only the default group output; bundles.properties with partially defined order 
  -         resources/js/default/fielddecorators/decorator2.js
  -         resources/js/default/fielddecorators/decorator1.js
  -         resources/js/default/fielddecorators/decorator3.js
  -  
  -     nothing inherited; bundles.properties with partial ordering plus explicit *.js wildcard 
  -         resources/js/iphone-ipod/grid/grid2.js
  -         resources/js/iphone-ipod/grid/grid1.js
  -         resources/js/iphone-ipod/grid/grid3.js
  -  
  -     inheritence from multiple groups up to default group; bundles.properties with complete order defined:
  -         resources/js/default/component/map/map1.js
  -         resources/js/default/component/map/map2.js
  -         resources/js/webkit/component/map/map1.js
  -         resources/js/webkit/component/map/map2.js
  -         resources/js/applewebkit/component/map/map1.js
  -         resources/js/applewebkit/component/map/map2.js
  -         resources/js/mapComponent-iphone-ipod/component/map/map2.js
  -         resources/js/mapComponent-iphone-ipod/component/map/map1.js
  -
  -     only intermediate group + inherited groups but not all the way up to the default group 
  -         resources/js/webkit/layers/layers1.js  
  -         resources/js/webkit/layers/layers2.js  
  -         resources/js/applewebkit/layers/layers1.js
  -         resources/js/applewebkit/layers/layers2.js
  -
  -     only intermediate group output:
  -         resources/js/webkit/animation/animation1.js
  -         resources/js/webkit/animation/animation2.js
  --%>
<crf:script href="fielddecorators/bundle" type="text/javascript" device="${context.device}"/> 
<crf:script href="grid/bundle" type="text/javascript" device="${context.device}"/> 
<crf:script href="component/map/bundle" type="text/javascript" device="${context.device}"/> 
<crf:script href="layers/bundle" type="text/javascript" device="${context.device}"/> 
<crf:script href="animation/bundle" type="text/javascript" device="${context.device}"/> 