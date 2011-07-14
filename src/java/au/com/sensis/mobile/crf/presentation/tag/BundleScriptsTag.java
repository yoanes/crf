package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

/**
 * Tag that bundles the output of any child {@link ScriptTag}s that register
 * {@link au.com.sensis.mobile.crf.service.Resource}s with
 * this {@link BundleScriptsTag} via the {@link #addResourcesToBundle(java.util.List)} method.
 *
 * @author w12495
 */
public class BundleScriptsTag extends AbstractBundleTag {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeTag(final String src) throws IOException {
        getJspContext().getOut().print("<script id=\"");

        getJspContext().getOut().print(getId());

        getJspContext().getOut().print("\" src=\"");
        getJspContext().getOut().print(src);
        getJspContext().getOut().print("\" ");

        writeDynamicTagAttributes(getJspContext().getOut());

        getJspContext().getOut().print("></script>");
    }


    private void writeDynamicTagAttributes(final JspWriter jspWriter) throws IOException {
        boolean charsetAttributeFound = false;
        boolean typeAttributeFound = false;

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {

            if ("charset".equals(attribute.getLocalName())) {
                charsetAttributeFound = true;
            }

            if ("type".equals(attribute.getLocalName())) {
                typeAttributeFound = true;
            }

            jspWriter.print(attribute.getLocalName());
            jspWriter.print("=\"");
            jspWriter.print(attribute.getValue());
            jspWriter.print("\" ");
        }

        writeCharsetAttributeIfNotFound(jspWriter, charsetAttributeFound);
        writeTypeAttributeIfNotFound(jspWriter, typeAttributeFound);
    }

    private void writeTypeAttributeIfNotFound(final JspWriter jspWriter,
            final boolean typeAttributeFound)
            throws IOException {

        if (!typeAttributeFound) {
            jspWriter.print("type=\"text/javascript\" ");
        }
    }


    private void writeCharsetAttributeIfNotFound(final JspWriter jspWriter,
            final boolean charsetAttributeFound) throws IOException {

        if (!charsetAttributeFound) {
            jspWriter.print("charset=\"utf-8\" ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBundleFileExtension() {
        return "js";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTagDependenciesBeanName() {
        return "crf.bundleScriptsTagDependencies";
    }
}

