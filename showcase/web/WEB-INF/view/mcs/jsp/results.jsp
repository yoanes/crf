<?xml version="1.0" encoding="UTF-8"?>
<jsp:directive.include file="/WEB-INF/view/mcs/jsp/configInclude.jsp"/>

<fmt:setBundle basename="global-version"/>
<c:set var="crfversion"><fmt:message key="version"/>.<fmt:message key="build"/></c:set>

<html xmlns="http://www.w3.org/2002/06/xhtml2"
    xmlns:mcs="http://www.volantis.com/xmlns/2006/01/xdime/mcs"
    xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:si="http://www.volantis.com/xmlns/2006/01/xdime2/si"
	xmlns:sel="http://www.w3.org/2004/06/diselect"
	xmlns:widget="http://www.volantis.com/xmlns/2006/05/widget"
	xmlns:pipeline="http://www.volantis.com/xmlns/marlin-pipeline">
  <head>
        <title>
            Results
        </title>
        <link rel="mcs:layout" href="/ym/ym.mlyt"/>
        <link rel="mcs:theme" href="/ym/ym.mthm"/>
        <link rel="mcs:theme" href="/ym/imageSizeCategory.mthm"/>
        <link rel="mcs:theme" href="/ym/roundedCorner.mthm"/>

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

	<!-- header -->
	<sel:select>
	    <sel:when expr="device:isInstanceOf('Apple-iPad')">
	    <!-- tabletSubHeader -->
		    <c:url var="ymHome" value="/home/welcome.do" />
		
			<div class="headerWrapper" id="HDSubHeader">
			        <div class="carrierHeader">
			            <object src="/ym/images/common/unmetered/unmetered.mimg" id="freeImg">
			                <c:out value="Unmetered"/>
			            </object>
			        </div>
			    
			    <div id="touchDevice" class="HDSubLogo">
			    <div:rc colour="yellow" css="yellow">	
			         <a href="${ymHome}"> 
			         	<object id="HDSubLogo" src="/ym/images/header/subPageHeader.mimg">Yellow Pages</object>
			         </a>
			     </div:rc>
				</div>
				
				<div id="nonTouch" class="HDSubLogo">	
				<div:rc colour="yellow" css="yellow">	
			         <object src="/ym/images/header/subPageHeader.mimg">Yellow Pages</object>
			    </div:rc>
				</div>
				    
				<div id="findForm" class="findFormSubPage">
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
		
		<div class="resultsBlockWrapper">
			
			<div class="resultsInfo">
				Computers, Melbourne
			</div>
		</div>

		<div class="sortWrapper">
			<span class="sortImgWrapperOn">
				<object src="/ym/images/furniture/nearestOn.mimg" class="sortImgOn"/>
			</span>
			<span class="sortImgWrapperOff">
				<a href="#" class="sortLink">
					<object src="/ym/images/furniture/servicingOff.mimg" class="sortImgOff"/>
				</a>
			</span>
		</div>

        <div class="hdColumnWrapper" id="resultsMapColumn">

        	<div id="resultsMapColumnInner">

					<div class="mapWrapper">
						<div class="map">
							<object src="http://dsb.sensis.com.au/uidev/crfshowcase/resources/images/${crfversion}/HD800/sandpit/map.gif" srctype="image/gif">
                                <param name="mcs-transcode" value="false"/>
                            </object>							
						</div>
						
						<div class="mapControlsWrapper">
							<div class="zoomControls">
								<a href="#">
									<object class="mapControl" src="/ym/images/common/icons/in.mimg" />	
								</a>
								<a href="#">
									<object class="mapControl" src="/ym/images/common/icons/out.mimg" />							
								</a>
							</div>
							
							<div class="directionControls">
								<a href="#">
									<object class="mapControl" src="/ym/images/common/icons/north.mimg" />	
								</a>
								<a href="#">
									<object class="mapControl" src="/ym/images/common/icons/south.mimg" />		
								</a>
								<a href="#">
									<object class="mapControl" src="/ym/images/common/icons/west.mimg" />	
								</a>
								<a href="#">
									<object class="mapControl" src="/ym/images/common/icons/east.mimg" />		
								</a>								
							</div>				
						</div>					
					</div>
					
				</div>						
		</div>



       <div class="hdColumnWrapper" id="resultsColumn">

	       	<div id="resultsColumnInner">
	        	<div class="contentCellWrapper">
	            	<div:rc>	
	
						<div class="contentCellResultsCount">
							128 mapped results
						</div>
		
	                    <sel:if expr="device:isInstanceOf('Apple')">
							<div id="iphone104_donotremove" style="visibility:hidden;height:0px;width:1px;overflow:hidden;">Map these results</div>
						</sel:if>
	
		
						<div class="contentCellResultWrapper">
							<div class="contentCellResultHeader">
								<a href="#">1. Computers Universe</a>
							</div>						
						
							<div class="contentCellResultAddress">
								<div>342 Belmore Rd, Balwyn East</div>
								<div>VIC 3103  427m</div>
							</div>
							
							<div class="contentCellResultExtraInfo">
								Open 7am-7pm Monday-Saturday
							</div>
						
							<div class="contentCellResultAction">
								<a href="tel:0353321999">
									<object class="actionIcon" src="/ym/images/common/icons/call.mimg">Call</object>  (03) 5332 1999
								</a>
							</div>					
						</div>
			
						<div class="premiumCellOrganicResultWrapper">				
							<div class="premiumCellHeader">
								<a href="#">2. Computers Universe</a>
							</div>							
							
							<div class="premiumCellAddress">
								<div>342 Belmore Rd, Balwyn East</div>
								<div>VIC 3103  5.4km</div>
							</div>
							
							<div class="premiumCellAction">
								<a href="tel:0353321999">								
									<object class="actionIcon" src="/ym/images/common/icons/call.mimg">Call</object> (03) 5332 1999
								</a>
							</div>
							
							<div class="premiumCellAction">
								<a href="addToContacts.xdime">
									<object class="actionIcon" src="/ym/images/common/icons/add.mimg"></object> Add to contacts
								</a>	
							</div>
						</div>
			
			
					<div class="contentCellResultWrapper">
						<div class="contentCellResultHeaderWrapper">
							<div class="contentCellResultHeader">
								<a href="#">3. Computers Universe</a>
							</div>					
						</div>
						
						<div class="contentCellResultAddress">
							<div>342 Belmore Rd, Balwyn East</div>
							<div>VIC 3103  1.2km</div>
						</div>
					
						<div class="contentCellResultAction">
							<a href="tel:0353321999">
								<object class="actionIcon" src="/ym/images/common/icons/call.mimg">Call</object>  (03) 5332 1999
							</a>
						</div>					
					</div>
					
					<div class="contentCellResultWrapper">
						<div class="contentCellResultHeader">
							<a href="#">4. Computers Universe</a>
						</div>					
					
						<div class="contentCellResultAddress">
							<div>342 Belmore Rd, Balwyn East</div>
							<div>VIC 3103  1.9km</div>
						</div>
					
						<div class="contentCellResultAction">
							<a href="tel:0353321999">
								<object class="actionIcon" src="/ym/images/common/icons/call.mimg">Call</object> (03) 5332 1999
							</a>
						</div>					
					</div>					
					
					<div class="premiumCellResultWrapperLast">
						<div class="premiumCellIndentWrapper">
							<div class="premiumCellHeader">
								<a href="#">Computers Universe</a>
							</div>							
						
							<div class="premiumCellAddress">
								<div>342 Belmore Rd, Balwyn East</div>
								<div>VIC 3103  5.4km</div>
							</div>
							
							<div class="premiumCellAction">
								<a href="tel:0353321999">
									<object class="actionIcon" src="/ym/images/common/icons/call.mimg">Call</object>  (03) 5332 1999
								</a>
							</div>
							
							<div class="premiumCellAction">
								<a href="addToContacts.xdime">
									<object class="actionIcon" src="/ym/images/common/icons/add.mimg"></object> Add to contacts
								</a>	
							</div>
						</div>
					</div>
		
				</div:rc>
			</div>	
		</div>
	
		<div class="contentCellWrapper">
			<div:rc>		
				<div class="contentCellNavigationOrphan">
					<a href="results.xdime">Next page</a>
				</div>			
			</div:rc>
		</div>			
	
	</div>
		<div style="clear:both; z-index: 9999;"></div>
		<div:rc css="yellow" colour="yellow">
	
			<div class="findFormCellTop">
				&#160;
			</div>
	
			<div class="findFormSearch">
				<div class="findFormLabelCell" id="findFormFindLabel">
					Find
				</div>
				<div class="findFormInputCell" id="findInputCell">
					<xf:input ref="keyWords" id="findSearchInput">
						<xf:label/>
					</xf:input>
				</div>	
			</div>
			
			<div class="findFormNear">
				<div class="findFormLabelCell" id="findFormNearLabel">
					Near
				</div>
				<div class="findFormInputCell">
					<xf:input ref="suburb" id="findLocationInput">
						<xf:label/>
					</xf:input>
				</div>	
			</div>	
	
			<div class="findFormCellBottom">	
				<div class="locationTip">
					Leave blank to use your location
				</div>	
		
				<div class="findFormSubmitCell">					
					<xf:submit submission="findFormSubmission" id="findFormSubmit">
						<xf:label/>
					</xf:submit>					
				</div>
			</div>
			
			<div class="dirtyThubHack">&#160;</div>
			<div style="clear:both; z-index: 9999;"></div>
		</div:rc>

	</div>		

	<div class="footerWrapper">
	    <div class="contentCellWrapper" id="homeLinkWrapper">
	        <div:rc>
	            <div class="contentCellDetailActionOrphan">
	                <a href="#">
	                    <object src="/ym/images/footer/yellowFooterIcon.mimg" class="yellowHomeImage"></object> Yellow Pages Home</a>
	            </div>
	        </div:rc>
	    </div>


		<div class="footerTelstraWrapper">
			<a href="http://wap.telstra.com/wap">Bigpond Home</a>
		</div>
	
		<div class="footerLegalHelp">
			<a class="footerLegalHelpA" href="#">
				
			Pricing - the Green Dot</a> | <a class="footerLegalHelpA" href="#">
				
			Legal</a> | <a class="footerLegalHelpA" href="#">
			Help</a>
		</div>
		
		<div class="footerLegalHelp" id="footerCopyright">
			Copyright &#169; Yellow Pages
		</div>
	
	</div>
  </body>
</html>
