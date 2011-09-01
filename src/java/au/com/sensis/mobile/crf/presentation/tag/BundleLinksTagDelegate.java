package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;

/**
 * Delegate which implements the writing of output for {@link BundleLinksTag}.
 *
 * @author w12495 (author of original BundleLinksTag)
 * @author Brendan Doyle
 */
public class BundleLinksTagDelegate
        extends BundleTagDelegate {

    public BundleLinksTagDelegate(final JspContext jspContext, final BundleTagData bundleTagData) {

        super(jspContext, bundleTagData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeTag(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes, final String path)
            throws IOException {

        jspWriter.print("<link id=\"");

        jspWriter.print(getId());

        jspWriter.print("\" href=\"");
        jspWriter.print(path);
        jspWriter.print("\" ");

        writeDynamicTagAttributes(jspWriter, dynamicTagAttributes);

        jspWriter.print("/>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeAbsoluteHrefTag(@SuppressWarnings("unused") final JspWriter jspWriter,
            @SuppressWarnings("unused") final List<DynamicTagAttribute> dynamicTagAttributes,
            @SuppressWarnings("unused") final String path) {

        throw new UnsupportedOperationException("Absolute hrefs not supported for links as it is"
                + " not expected absolute css will be required.");
    }

    private void writeDynamicTagAttributes(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes)
            throws IOException {

        boolean relAttributeFound = false;
        boolean typeAttributeFound = false;

        for (final DynamicTagAttribute attribute : dynamicTagAttributes) {

            if ("rel".equals(attribute.getLocalName())) {

                relAttributeFound = true;
            }

            if ("type".equals(attribute.getLocalName())) {

                typeAttributeFound = true;
            }

            jspWriter.print(attribute.getLocalName());
            jspWriter.print("=\"");
            jspWriter.print(attribute.getValue());
            jspWriter.print("\" ");
        }

        writeRelAttributeIfNotFound(jspWriter, relAttributeFound);
        writeTypeAttributeIfNotFound(jspWriter, typeAttributeFound);
    }

    private void writeTypeAttributeIfNotFound(final JspWriter jspWriter,
            final boolean typeAttributeFound)
            throws IOException {

        if (!typeAttributeFound) {

            jspWriter.print("type=\"text/css\" ");
        }
    }


    private void writeRelAttributeIfNotFound(final JspWriter jspWriter,
            final boolean charsetAttributeFound) throws IOException {

        if (!charsetAttributeFound) {

            jspWriter.print("rel=\"stylesheet\" ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBundleFileExtension() {

        return "css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTagDependenciesBeanName() {

        return "crf.bundleLinksTagDependencies";
    }
}

