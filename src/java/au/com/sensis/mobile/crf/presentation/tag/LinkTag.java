package au.com.sensis.mobile.crf.presentation.tag;


import javax.servlet.jsp.PageContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.web.component.core.tag.ScriptTag;

/**
 * Facade to a link tag that uses the Content Rendering Framework to resolve the
 * resource path set into {@link #setHref(String)}.
 *
 * <p>
 * This facade also prevents duplicate tags from being written in the current
 * HTTP request. The unique id of each tag is governed by the value of the href
 * attribute.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkTag extends AbstractDuplicatePreventingTag {

    private String href;
    
    /**
     * Attribute name used to store a map of ({@link TagWriter#getId()},
     * {@link TagWriter}) pairs.
     */
    public static final String LINK_WRITER_MAP_ATTRIBUTE_NAME =
            ScriptTag.class.getName() + ".linkTagWriterMap";

    /**
     * {@inheritDoc}
     */
    @Override
    protected TagWriter createTagWriter() {
        return LinkTagWriterFactory
                .getSingletonInstance()
                .createLinkTagWriter(
                        getDevice(), getDynamicAttributes(), getHref(),
                        getTagDependencies());
    }

    private LinkTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc
                        .getServletContext());
        return (LinkTagDependencies) webApplicationContext
                .getBean(LinkTagDependencies.BEAN_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTagWriterMapAttributeName() {
        return LINK_WRITER_MAP_ATTRIBUTE_NAME;
    }
    
    /**
     * @return the href
     */
    public final String getPathAttribute() {
        return getHref();
    }
    
    /**
     * @return the href
     */
    public final String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public final void setHref(final String href) {
        this.href = href;
    }
}
