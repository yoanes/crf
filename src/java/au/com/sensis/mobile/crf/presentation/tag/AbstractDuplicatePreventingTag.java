package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * {@link AbstractTag} which also prevents duplicates from being
 * written to the current request. The unique id of each tag is governed by the
 * value of the href.
 *
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractDuplicatePreventingTag extends
        AbstractTag {

    /**
     * If the current tag has not been written to the response for the current
     * request yet, then delegate to a new {@link TagWriter}
     * returned by {@link #createTagWriter()}.
     *
     * {@inheritDoc}
     */
    @Override
    public final void doTag() throws JspException, IOException {
        validateAttributes();

        initTagWriterMapInJspContextIfRequired();

        final TagWriter scriptTagWriter =
                createTagWriter();
        if (!getTagWriterMapFromJspContext().containsKey(
                scriptTagWriter.getId())) {
            scriptTagWriter.writeTag(getJspContext().getOut(), getJspBody());
            getTagWriterMapFromJspContext().put(
                    scriptTagWriter.getId(), scriptTagWriter);
        }

    }

    /**
     * Validate attributes. By default, calls {@link #validatePathAttribute(String)}.
     */
    protected void validateAttributes() {
        validatePathAttribute(getPathAttribute());
    }

    /**
     * @return Attribute name that
     *         {@link #initTagWriterMapInJspContextIfRequired()}
     *         and {@link #getTagWriterMapFromJspContext()} uses
     *         for setting and getting the {@link Map} of ids to
     *         {@link TagWriter}s from the
     *         {@link javax.servlet.jsp.JspContext}.
     */
    protected abstract String getTagWriterMapAttributeName();

    /**
     * @return {@link TagWriter} for writing the actual tag to
     *         the JSP.
     */
    protected abstract TagWriter createTagWriter();

    /**
     * @return the value of the path attribute (either href or src, depending on the tag).-
     */
    protected abstract String getPathAttribute();

    /**
     * If ({@link #getTagWriterMapFromJspContext()} returns
     * null, creates a new Map and sets it into the request.
     */
    protected void initTagWriterMapInJspContextIfRequired() {
        if (getTagWriterMapFromJspContext() == null) {
            getJspContext().setAttribute(
                    getTagWriterMapAttributeName(),
                    new HashMap<String, TagWriter>(),
                    PageContext.REQUEST_SCOPE);
        }
    }

    /**
     * @return Map of ids to {@link TagWriter}s obtained from
     *         the JspContext.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, TagWriter>
        getTagWriterMapFromJspContext() {
        return (Map<String, TagWriter>) getJspContext()
                .getAttribute(getTagWriterMapAttributeName(),
                        PageContext.REQUEST_SCOPE);
    }

}
