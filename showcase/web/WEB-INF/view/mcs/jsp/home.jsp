<?xml version="1.0" encoding="UTF-8"?>

<jsp:directive.include file="/WEB-INF/view/mcs/jsp/configInclude.jsp"/>

<html xmlns="http://www.w3.org/2002/06/xhtml2"
    xmlns:mcs="http://www.volantis.com/xmlns/2006/01/xdime/mcs"
    xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:sel="http://www.w3.org/2004/06/diselect"
    xmlns:si="http://www.volantis.com/xmlns/2006/01/xdime2/si"
    xmlns:widget="http://www.volantis.com/xmlns/2006/05/widget"
    xmlns:pipeline="http://www.volantis.com/xmlns/marlin-pipeline">
    <head>
        <title>
            Home
        </title>

        <%--
          - This is supposed to verify to Google that we own this site so we can get more detail in
          - the Google webmaster tool. However, we cannot use this meta tag under XDIME2, so we will
          - have to use one of the other methods provided.
          --%>
        <%--
        <meta name="verify-v1" content="${viewBean.googleWebmasterToolVerificationCode}"/>
        --%>

        <link rel="mcs:layout" href="/ym/ym.mlyt"/>
        <link rel="mcs:theme" href="/ym/ym.mthm"/>
        <link rel="mcs:theme" href="/ym/imageSizeCategory.mthm"/>
        <link rel="mcs:theme" href="/ym/roundedCorner.mthm"/>

        <%-- ****************************************************************** --%>
        <%-- Declare the search model for the form to use --%>
        <%-- ****************************************************************** --%>
<xf:model id="findFormModel">
	<c:url var="findFormUrl" value="/address/resolveAddress.do" />

    <xf:submission id="findFormSubmission" action="${findFormUrl}"/>

    <xf:instance>
        <si:instance>
            <si:item name="keyWords">

            </si:item>
        </si:instance>
    </xf:instance>

    <xf:instance>
        <si:instance>
            <si:item name="suburb">

            </si:item>
        </si:instance>
    </xf:instance>
</xf:model>

    </head>

    <body>

<!-- homeHeader -->
<sel:select>
    <sel:when expr="device:isInstanceOf('Apple-iPad')">
    	<!-- tabletHomeHeader -->
		<div class="headerWrapper" id="HDHomeHeader">
		
		        <div class="carrierHeader">
		            <object src="/ym/images/common/unmetered/unmetered.mimg" id="freeImg">
		                <c:out value="Unmetered"/>
		            </object>
		        </div>
		
		    
		    <div id="touchDevice" class="HDHomeLogo">
		    <div:rc colour="yellow" css="yellow">	
		        
		         	<object id="HDHomeLogo" src="/ym/images/header/HDHomeHeader.mimg">Yellow Pages</object>
		        
		     </div:rc>
			</div>
			
			<div id="nonTouch" class="HDHomeLogo">	
			<div:rc colour="yellow" css="yellow">	
		         <object src="/ym/images/header/HDHomeHeader.mimg">Yellow Pages</object>
		    </div:rc>
			</div>
			    
			<div id="findForm">
				<div:rc css="yellow" colour="yellow">
				
					<div id="findFormWrapper" class="findFormSearch" >
						<div class="findFormLabelCell" id="findFormFindLabel">
							Find
						</div>
						<div class="findFormInputCell" id="findInputCell">
							<xf:input ref="keyWords" id="findSearchInput">
								<xf:label/>
							</xf:input>
			         
			             <div class="hintTextMessage" id="findMessage" >
			                  Near
			             </div>
						</div>
						<div id="suggestions" style="clear:both; z-index: 9999;"></div>
					</div>
			
					<div id="nearWrapper" class="findFormNear">
						<div class="findFormLabelCell" id="findFormNearLabel">
							Near
						</div>
						<div class="findFormInputCell"  id="findInputSuburb">
							<xf:input ref="suburb" id="findLocationInput">
								<xf:label/>
							</xf:input>
						</div>
						<div id="locSuggestions" style="clear:both; z-index: 9999;"></div>
					</div>
			
				    <div class="findFormCellBottom">
				
						<div class="locationTip" id="locationMessage" >
						</div>
						<div class="findFormSubmitCell">
							<xf:submit submission="findFormSubmission" id="findFormSubmit">
								<xf:label/>
							</xf:submit>
						</div>
					</div>
			    
			    <div style="clear: both; height: 1px;">&#160;</div>
					
				</div:rc>
			</div>

		</div>
    </sel:when>
    <sel:otherwise>
        <!-- mobileHeader -->
        <div class="headerWrapper" id="freeHeader">
		
		        <div class="carrierHeader">
		            <object src="/ym/images/common/unmetered/unmetered.mimg" id="freeImg">
		                <c:out value="Unmetered"/>
		            </object>
		        </div>
		
		    <div:rc colour="yellow" css="yellow">
			    <div id="touchDevice" class="yellowLogo">	
			         <a href="http://mobile.yellow.com.au"> 
			         	<object src="/ym/images/header/header.mimg">Yellow Pages</object>
			         </a>
				</div>
				
				<div id="nonTouch" class="yellowLogo">	
			         <object src="/ym/images/header/header.mimg">Yellow Pages</object>
				</div>
		    </div:rc>
		</div>
        
    </sel:otherwise>
</sel:select>

        <div class="contentWrapper">

            <%-- Search form --%>
			<sel:select>
			    <sel:when expr="device:isInstanceOf('Apple-iPad')">
				</sel:when>
				<sel:otherwise>
			        <div id="findForm" class="findFormWrapper">
			            <div:rc css="yellow" colour="yellow">
			            
			                <div id="findFormWrapper" class="findFormSearch" >
			                    <div class="findFormLabelCell" id="findFormFindLabel">
			                        Find
			                    </div>
			                    <div class="findFormInputCell" id="findInputCell">
			                        <xf:input ref="keyWords" id="findSearchInput">
			                            <xf:label/>
			                        </xf:input>
			                 
			                     <div class="hintTextMessage" id="findMessage" >
			                          Business type or name
			                     </div>
			                    </div>
			                    <div id="suggestions" style="clear:both;"></div>
			                </div>
			        
			                <div id="nearWrapper" class="findFormNear">
			                    <div class="findFormLabelCell" id="findFormNearLabel">
			                        Near
			                    </div>
			                    <div class="findFormInputCell"  id="findInputSuburb">
			                        <xf:input ref="suburb" id="findLocationInput">
			                            <xf:label/>
			                        </xf:input>
			                    </div>
			                    <div id="locSuggestions" style="clear:both;"></div>
			                </div>
			        
			                <div class="findFormCellBottom">
			            
			                    <div class="locationTip" id="locationMessage" >
			                    </div>
			                    <div class="findFormSubmitCell">
			                        <xf:submit submission="findFormSubmission" id="findFormSubmit">
			                            <xf:label/>
			                        </xf:submit>
			                    </div>
			                </div>
			            
			            <div style="clear: both; height: 1px;">&#160;</div>
			                
			            </div:rc>
			        </div>
				</sel:otherwise>
			</sel:select>

			<sel:select>
			    <sel:when expr="device:isInstanceOf('Apple-iPhone')">

	                <div id="apple" class="contentCellWrapper">
	                    <c:url var="downloadUrl"
	                            value="http://itunes.apple.com/WebObjects/MZStore.woa/wa/viewSoftware?id=325629947&amp;mt=8"/>
	                    <a href="${downloadUrl}" >
	                        <object src="/ym/images/furniture/apple_icon.mimg" id="appleIcon">Apple Download Link</object>
	                    </a>
	                </div>
	            </sel:when>
	        </sel:select>        


            <div id="popular"  class="contentCellWrapper">
                <div:rc>
	                <div class="contentCellBrowseHeader">
	                    Popular Searches
	                </div>
				
					<div class="contentCellBrowseLink">
						<a href="#">Services</a>
					</div>
				
							
					<div class="contentCellBrowseLink">
						<a href="#">Health &#38; Beauty</a>
					</div>
					
					<div class="contentCellBrowseLink">
						<a href="#">Home &#38; Garden</a>
					</div>
				
				
					<div class="contentCellBrowseLink">
						<a href="#">Automotive</a>
					</div>
				
					<div class="contentCellBrowseLink">
						<a href="#">Entertainment</a>
					</div>
					
					<div class="contentCellBrowseLink">
						<a href="#">Shopping and Retail</a>
					</div>
                    <div class="clearBoth"></div>
                </div:rc>
            </div>

            <div id="networkWrapper" class="networkWrapper">

                <div class="networkBreakWrapper">
                    <div id="whitePages" class="networkInnerWrapper">
                        <a href="http://mobile.whitepages.com.au">
                            <object src="/ym/images/common/footer/wpm.mimg"> White Pages </object>
                        </a>
                    </div>

                    <div id="whereisMobile" class="networkInnerWrapper">
                        <a href="http://mobile.whereis.com.au">
                            <object src="/ym/images/common/footer/wm.mimg"> Whereis Mobile </object>
                        </a>
                    </div>
                </div>
                <div class="networkBreakWrapper">
                    <div id="sensisSearch" class="networkInnerWrapper">
                        <a href="http://mobile.sensis.com.au">
                            <object src="/ym/images/common/footer/ss.mimg"> Sensis Search </object>
                        </a>
                    </div>

                <div id="tradingPost" class="networkInnerWrapper">
                    <a href="http://mobile.tradingpost.com.au">
                        <object    src="/ym/images/common/footer/tp.mimg"> Trading Post </object>
                    </a>
                </div>

                </div>
            </div>

            <div class="footerWrapper">


                    <div class="footerLegalHelp" >
                        <sel:select>
                            <sel:when expr="device:getPolicyValue('custom.business.phone')='true'">
                                <a href="${telstraUrl}">
                                    Telstra Home
                                </a>
                            </sel:when>
                            <sel:otherwise>
                            <a href="${bigpondUrl}">
                                    BigPond Home
                            </a>
                            </sel:otherwise>
                        </sel:select>
                    </div>

				<sel:if expr="device:isInstanceOf('Apple-iPad')">

					<div class="footerLegalHelp">
	
						View : Mobile | <a href="http://www.yellowpages.com.au/?ref=mobile" id="footerDesktopLink">Desktop</a>
						
					</div>
					
				</sel:if>

                <div class="footerLegalHelp">

                        <a href="http://m.bigpond.com/pricing?mob=bom_footer_pricing">Pricing - Green Dot</a> |

                    <a href="/legal/initialiseLegal.do">Legal</a> |
                    <a href="/help/initialiseHelp.do">Help</a>
                </div>

				<div class="footerLegalHelp" id="seoStateList">
					<a class="footerLegalHelpA" href="#">NSW</a> 
					<a class="footerLegalHelpA" href="#">VIC</a> 
					<a class="footerLegalHelpA" href="#">QLD</a> 
					<a class="footerLegalHelpA" href="#">SA</a> 
					<a class="footerLegalHelpA" href="#">WA</a> 
					<a class="footerLegalHelpA" href="#">TAS</a> 
					<a class="footerLegalHelpA" href="#">NT</a> 
					<a class="footerLegalHelpA" href="#">ACT</a>
				</div>

                <div class="footerLegalHelp" id="footerCopyright">
                    Yellow Pages &#169;
                </div>

            </div>
        </div>
    </body>
</html>

