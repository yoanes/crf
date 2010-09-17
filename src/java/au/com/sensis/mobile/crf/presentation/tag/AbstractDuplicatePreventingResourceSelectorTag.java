package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * {@link AbstractResourceSelectorTag} which also prevents duplicates from being
 * written to the current request. The unique id of each tag is governed by the
 * value of the href.
 *
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractDuplicatePreventingResourceSelectorTag extends
        AbstractResourceSelectorTag {

    /**
     * If the current tag has not been written to the response for the current
     * request yet, then delegate to a new {@link ResourceSelectorTagWriter}
     * returned by {@link #createResourceSelectorTagWriter()}.
     *
     * {@inheritDoc}
     */
    @Override
    public final void doTag() throws JspException, IOException {
        validateHrefAttribute();

        initResourceSelectorTagWriterMapInJspContextIfRequired();

        final ResourceSelectorTagWriter scriptTagWriter =
                createResourceSelectorTagWriter();
        if (!getResourceSelectorTagWriterMapFromJspContext().containsKey(
                scriptTagWriter.getId())) {
            scriptTagWriter.writeTag(getJspContext().getOut());
            getResourceSelectorTagWriterMapFromJspContext().put(
                    scriptTagWriter.getId(), scriptTagWriter);
        }

    }

    /**
     * @return Attribute name that
     *         {@link #initResourceSelectorTagWriterMapInJspContextIfRequired()}
     *         and {@link #getResourceSelectorTagWriterMapFromJspContext()} uses
     *         for setting and getting the {@link Map} of ids to
     *         {@link ResourceSelectorTagWriter}s from the
     *         {@link javax.servlet.jsp.JspContext}.
     */
    protected abstract String getResourceSelectorTagWriterMapAttributeName();

    /**
     * @return {@link ResourceSelectorTagWriter} for writing the actual tag to
     *         the JSP.
     */
    protected abstract ResourceSelectorTagWriter createResourceSelectorTagWriter();

    /**
     * If ({@link #getResourceSelectorTagWriterMapFromJspContext()} returns
     * null, creates a new Map and sets it into the request.
     */
    protected void initResourceSelectorTagWriterMapInJspContextIfRequired() {
        if (getResourceSelectorTagWriterMapFromJspContext() == null) {
            getJspContext().setAttribute(
                    getResourceSelectorTagWriterMapAttributeName(),
                    new HashMap<String, ResourceSelectorTagWriter>(),
                    PageContext.REQUEST_SCOPE);
        }
    }

    /**
     * @return Map of ids to {@link ResourceSelectorTagWriter}s obtained from
     *         the JspContext.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, ResourceSelectorTagWriter>
        getResourceSelectorTagWriterMapFromJspContext() {
        return (Map<String, ResourceSelectorTagWriter>) getJspContext()
                .getAttribute(getResourceSelectorTagWriterMapAttributeName(),
                        PageContext.REQUEST_SCOPE);
    }

}
