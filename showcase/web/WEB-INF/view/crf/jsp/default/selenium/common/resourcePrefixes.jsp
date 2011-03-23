<%@ page contentType="text/html" %>

<jsp:directive.include file="/WEB-INF/view/common/jsp/commonTagLibs.jsp"/>
<jsp:directive.include file="/WEB-INF/view/common/jsp/projectTagLibs.jsp"/>

<div>
    <div><strong>[default] resourcePrefixes.jsp </strong>: </div>
    <div>
        <p>imgSrcPrefix (direct render): '<crf:imgSrcPrefix />'</p>  
        
        <crf:imgSrcPrefix var="imgSrcPrefix"/>  
        <p>imgSrcPrefix (via var): '${imgSrcPrefix}'</p>
        
        <p>linkHrefPrefix (direct render): '<crf:linkHrefPrefix />'</p>  
        
        <crf:linkHrefPrefix var="linkHrefPrefix"/>  
        <p>linkHrefPrefix (via var): '${linkHrefPrefix}'</p>

        <p>scriptSrcPrefix (direct render): '<crf:scriptSrcPrefix />'</p>  
        
        <crf:scriptSrcPrefix var="scriptSrcPrefix"/>  
        <p>scriptSrcPrefix (via var): '${scriptSrcPrefix}'</p>
        
    </div>
    
</div>