<jsp:directive.include file="/WEB-INF/view/jsp/default/common/configInclude.jsp" />

<s:if test="hasActionOutcomeMessages">

    <div class="actionOutcomeBar" id="actionOutcomeBar">

        <s:set name="actionOutcome" value="displayActionOutcome" />

        <s:iterator value="#actionOutcome.messages">

            <s:if test="messageType.name() == 'MESSAGE'">
                <s:set name="errorStyle" value="%{'message'}" />
            </s:if>
            <s:else>
                <s:set name="errorStyle" value="%{'tip'}" />
            </s:else>

            <div class="<s:property value='#errorStyle'/>">

                <s:iterator id="component" value="components">

                    <s:if test="%{#component instanceof
                            au.com.sensis.mobile.whitepages.presentation.common.IconMessageComponent}">

                        <s:if test="%{#component.name() == 'NEUTRAL'}">
                            <object src="/wpm/images/furniture/statusNeutral.mimg" />
                        </s:if>
                        <s:elseif test="%{#component.name() == 'POSITIVE'}">
                            <object src="/wpm/images/furniture/statusPositive.mimg" />
                        </s:elseif>
                        <s:elseif test="%{#component.name() == 'NEGATIVE'}">
                            <object src="/wpm/images/furniture/statusNeutral.mimg" />
                        </s:elseif>
                    </s:if>

                    <s:elseif test="%{#component instanceof
                            au.com.sensis.mobile.whitepages.presentation.common.ActionUrlMessage}">

                        <a href="<s:url namespace='/%{namespace}' action='%{actionName}'/>">
                            <s:property value="text" />
                        </a>
                    </s:elseif>

                    <s:elseif test="%{#component instanceof
                            au.com.sensis.mobile.whitepages.presentation.common.I18nMultiActionUrlMessage}">

                        <%--
                          - Process the component.i18nMessageKey message by filling in its
                          - placeholders with actionURLMessages.
                          --%>
                        <s:text name="%{#component.i18nMessageKey}">

                            <%--
                              - We apparently must use JSTL iteration here to avoid confusing
                              - Struts' context. I think if you use Struts iteration, it thinks the
                              - s:param is being passed to the Struts iterator tag and not to the
                              - s:text tag.
                              --%>
                            <c:forEach var="embeddedActionUrlMessage"
                                    items="${component.embeddedActionUrlMessages}">
                                <c:choose>
                                    <c:when test="${embeddedActionUrlMessage.enableUrl}">
                                        <s:param>
                                            <a href="<s:url namespace='/%{#attr.embeddedActionUrlMessage.namespace}'
                                                    action='%{#attr.embeddedActionUrlMessage.actionName}'/>">
                                                <c:out value="${embeddedActionUrlMessage.text}"/>
                                            </a>
                                        </s:param>
                                    </c:when>
                                    <c:otherwise>
                                        <s:param>
                                            <c:out value="${embeddedActionUrlMessage.text}"/>
                                        </s:param>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                        </s:text>

                    </s:elseif>

                    <s:elseif test="%{#component instanceof
                            au.com.sensis.mobile.whitepages.presentation.common.ExternalLinkMessageComponent}">

                        <a href="<s:property value='url' />">
                            <s:property value="text" />
                        </a>

                    </s:elseif>
                    <s:else>

                        <s:property value="text"/>

                    </s:else>

                </s:iterator>

            </div>

        </s:iterator>

    </div>

</s:if>
