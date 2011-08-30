package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;

/**
 * Delegate which implements the writing of output for {@link BundleScriptsTag}.
 *
 * @author w12495 (author of original BundleLinksTag)
 * @author Brendan Doyle
 */
public class BundleScriptsTagDelegate
        extends BundleTagDelegate {

    public BundleScriptsTagDelegate(final JspContext jspContext,
            final BundleTagData bundleTagData) {

        super(jspContext, bundleTagData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeTag(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes, final String src)
            throws IOException {

        jspWriter.print("<script id=\"");

        jspWriter.print(getId());

        jspWriter.print("\" src=\"");
        jspWriter.print(src);
        jspWriter.print("\" ");

        writeDynamicTagAttributes(jspWriter, dynamicTagAttributes);

        jspWriter.print("></script>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeAbsoluteHrefTag(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes, final String path)
            throws IOException {

        // same as writeTag above, but we don't want the id.

        jspWriter.print("<script");

        jspWriter.print(" src=\"");
        jspWriter.print(path);
        jspWriter.print("\" ");

        writeDynamicTagAttributes(jspWriter, dynamicTagAttributes);

        jspWriter.print("></script>");
    }

    private void writeDynamicTagAttributes(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes)
            throws IOException {

        boolean charsetAttributeFound = false;
        boolean typeAttributeFound = false;

        for (final DynamicTagAttribute attribute : dynamicTagAttributes) {

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
            final boolean charsetAttributeFound)
            throws IOException {

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
