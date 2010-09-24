package au.com.sensis.mobile.crf.presentation.tag;


import javax.servlet.jsp.PageContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Facade to a script tag that uses the Content Rendering Framework to resolve the
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
public class ScriptTag extends AbstractDuplicatePreventingTag {

    private String src;
    
    /**
     * Attribute name used to store a map of ({@link TagWriter#getId()},
     * {@link TagWriter}) pairs.
     */
    public static final String SCRIPT_WRITER_MAP_ATTRIBUTE_NAME =
            ScriptTag.class.getName() + ".scriptTagWriterMap";

    /**
     * {@inheritDoc}
     */
    @Override
    protected TagWriter createTagWriter() {
        return ScriptTagWriterFactory
            .getSingletonInstance()
                .createScriptTagWriter(getDevice(),
                        getDynamicAttributes(), getSrc(),
                        getTagDependencies());
    }

    private ScriptTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc
                        .getServletContext());
        return (ScriptTagDependencies) webApplicationContext
                .getBean(ScriptTagDependencies.BEAN_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTagWriterMapAttributeName() {
        return SCRIPT_WRITER_MAP_ATTRIBUTE_NAME;
    }
    
    /**
     * @return the src
     */
    public final String getPathAttribute() {
        return getSrc();
    }
    
    /**
     * @return the src
     */
    public final String getSrc() {
        return src;
    }

    /**
     * @param href the src to set
     */
    public final void setSrc(final String src) {
        this.src = src;
    }    
}

