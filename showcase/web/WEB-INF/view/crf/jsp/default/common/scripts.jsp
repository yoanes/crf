<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/crf/jsp/default/common/projectTagLibs.jsp"/>

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
<crf:script src="common/main.js" type="text/javascript" device="${context.device}"/> 
<crf:script src="common/columns.js" type="text/javascript" device="${context.device}"/> 
<crf:script src="results/results.js" type="text/javascript"  device="${context.device}"/> 
<crf:script src="common/jazz.js" type="text/javascript" device="${context.device}"/> 
<crf:script src="common/decorations.js" type="text/javascript"  device="${context.device}"/>

<%--
  - Acceptance criteria for CRF-47: This duplicate of the same script above will not result in
  - duplicate links being output to the page.   
  --%>
<crf:script src="common/main.js" type="text/javascript" device="${context.device}"/>

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
<crf:script src="fielddecorators/package" type="text/javascript" device="${context.device}"/> 
<crf:script src="grid/package" type="text/javascript" device="${context.device}"/> 
<crf:script src="component/map/package" type="text/javascript" device="${context.device}"/> 
<crf:script src="layers/package" type="text/javascript" device="${context.device}"/> 
<crf:script src="animation/package" type="text/javascript" device="${context.device}"/> 

<%-- 
  - Acceptance criteria covered by the following links (Jira CRF-60): only
  - one of these should result in an inline script being written to the page. The other
  - is ignored due to having the same name.
  --%>
<crf:script name="myScript" type="text/javascript" device="${context.device}">
    var myScript = "I am here and you should see me only once";
</crf:script> 
<crf:script name="myScript" type="text/javascript" device="${context.device}">
    if (myScript === undefined) { 
        var myScript = "I am here and you should see me only once";
    } else {
        myScript = "I have been included twice";
    }
</crf:script> 

<%-- 
  - Acceptance criteria covered by the following link (Jira CRF-61): absolute url
  - is passed straight through to the output. Usually used to reference a third party
  - JS file. For this showcase, though, we are simply referencing a URL that won't be resolved.
  --%>
<crf:script src="http://localhost:8080/something.js" 
    type="text/javascript" device="${context.device}"/>