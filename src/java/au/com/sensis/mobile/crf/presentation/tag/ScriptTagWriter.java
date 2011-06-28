package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Simple implementation of {@link TagWriter} that outputs a script tag in the
 * default namespace.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptTagWriter implements TagWriter {

    private static final String ABSOLUTE_URL_PREFIX = "http://";

    private final Device device;
    private final List<DynamicTagAttribute> dynamicAttributes;
    private final String href;
    private final String name;
    private final BundleScriptsTag parentBundleScriptsTag;

    private final ScriptTagDependencies scriptTagDependencies;

    /**
     * Default constructor.
     *
     * @param device
     *            {@link Device} of the current request.
     * @param dynamicAttributes
     *            List of {@link DynamicTagAttribute}s containing dynamic JSP
     *            tag attributes to be written out.
     * @param href
     *            href attribute of the tag to be written.
     * @param name
     *            name attribute of the tag.
     * @param scriptTagDependencies Singleton collaborators.
     */
    public ScriptTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes, final String href,
            final String name, final ScriptTagDependencies scriptTagDependencies,
            final BundleScriptsTag parentBundleScriptsTag) {
        this.device = device;
        this.dynamicAttributes = dynamicAttributes;
        this.href = href;
        this.name = name;
        this.scriptTagDependencies = scriptTagDependencies;
        this.parentBundleScriptsTag = parentBundleScriptsTag;
    }


    /**
     * {@inheritDoc}
     */
    public List<DynamicTagAttribute> getDynamicAttributes() {
        return dynamicAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public String getHref() {
        return href;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * {@inheritDoc}
     */
    public String getId() {
        if (StringUtils.isNotBlank(getHref())) {
            return getHref();
        } else {
            return getName();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTag(final JspWriter jspWriter, final JspFragment jspBody) throws IOException,
    JspException {
        if (StringUtils.isNotBlank(getHref())) {
            writeTagWithHref(jspWriter);
        } else {
            writeLinkTagWithBodyContent(jspWriter, jspBody);
        }

    }


    private void writeTagWithHref(final JspWriter jspWriter) throws IOException {
        if (isAbsoluteUrl(getHref())) {
            writeSingleLinkTag(jspWriter, getHref());
        } else {
            resolveResourceAndWriteTag(jspWriter);
        }
    }


    private boolean isAbsoluteUrl(final String href) {
        return href.startsWith(ABSOLUTE_URL_PREFIX);
    }


    private void resolveResourceAndWriteTag(final JspWriter jspWriter) throws IOException {

        final List<Resource> allResourcePaths = getAllResourcePaths();
        if (getParentBundleScriptsTag() != null) {
            getParentBundleScriptsTag().addResourcesToBundle(allResourcePaths);
        }
        writeLinkTagForEachResource(jspWriter, allResourcePaths);

    }

    private void writeLinkTagForEachResource(final JspWriter jspWriter,
            final List<Resource> allResources) throws IOException {

        if (!allResources.isEmpty()) {
            for (final Resource resource : allResources) {
                writeSingleLinkTag(jspWriter, resource);
            }
        }
    }

    private void writeSingleLinkTag(final JspWriter jspWriter, final Resource resource)
    throws IOException {
        writeSingleLinkTag(jspWriter, getTagDependencies().getClientPathPrefix()
                + resource.getNewPath() + getUniqueRequestParam());
    }

    private void writeSingleLinkTag(final JspWriter jspWriter,
            final String src) throws IOException {
        jspWriter.print("<script ");

        jspWriter.print("src=\""
                + src + "\" ");

        writeDynamicTagAttributes(jspWriter);

        jspWriter.print("></script>");
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

            jspWriter.print(attribute.getLocalName() + "=\"" + attribute.getValue() + "\" ");
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


    private void writeLinkTagWithBodyContent(final JspWriter jspWriter,
            final JspFragment jspBody) throws IOException, JspException {
        jspWriter.print("<script ");

        writeDynamicTagAttributes(jspWriter);

        jspWriter.print(">");
        jspBody.invoke(jspWriter);
        jspWriter.print("</script>");
    }

    private String getUniqueRequestParam() {

        String uniqueRequestParam = StringUtils.EMPTY;

        if (!getTagDependencies().getDeploymentMetadata().isDownstreamCachingEnabled()) {
            uniqueRequestParam = "?" + new Date().getTime();
        }

        return uniqueRequestParam;
    }

    private List<Resource> getAllResourcePaths() throws IOException {
        final List<Resource> allResourcePaths =
            getResourceResolverEngine().getAllResources(getDevice(),
                    getHref());

        assertNotNull(allResourcePaths);

        return allResourcePaths;
    }

    private void assertNotNull(final List<Resource> allResourcePaths) {
        if (allResourcePaths == null) {
            throw new IllegalStateException(
                    "getResourceResolverEngine.getAllResourcePaths "
                    + "returned no results for '" + getHref() + ". "
                    + "This should never happen !!!");
        }
    }

    /**
     * @return the {@link ResourceResolverEngine}.
     */
    private ResourceResolverEngine getResourceResolverEngine() {
        return getTagDependencies().getResourceResolverEngine();
    }

    /**
     * @return the device
     */
    private Device getDevice() {
        return device;
    }

    /**
     * @return the {@link ScriptTagDependencies}
     */
    private ScriptTagDependencies getTagDependencies() {
        return scriptTagDependencies;
    }


    private BundleScriptsTag getParentBundleScriptsTag() {
        return parentBundleScriptsTag;
    }

}
