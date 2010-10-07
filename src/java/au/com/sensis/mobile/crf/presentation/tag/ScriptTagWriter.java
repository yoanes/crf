package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.mobile.crf.service.ScriptBundleFactory;
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
            final String name, final ScriptTagDependencies scriptTagDependencies) {
        this.device = device;
        this.dynamicAttributes = dynamicAttributes;
        this.href = href;
        this.name = name;
        this.scriptTagDependencies = scriptTagDependencies;
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
        if (getDeploymentMetadata().isDevPlatform()) {
            writeLinkTagForEachResource(jspWriter, getAllResourcePaths());
        } else {
            writeLinkTagForBundledResources(jspWriter, getAllResourcePaths());
        }
    }

    private void writeLinkTagForEachResource(final JspWriter jspWriter,
            final List<Resource> allResources) throws IOException {

        if (allResources.isEmpty()) {
            logNoResourcesFoundWarning();

        } else {
            for (final Resource resource : allResources) {
                writeSingleLinkTag(jspWriter, resource);
            }
        }
    }

    private void writeLinkTagForBundledResources(final JspWriter jspWriter,
            final List<Resource> allResources)
            throws IOException {

        if (allResources.isEmpty()) {
            logNoResourcesFoundWarning();
        } else {
            final Resource bundleResourcePath =
                    getScriptBundleFactory().getBundle(allResources);

            writeSingleLinkTag(jspWriter, bundleResourcePath);
        }
    }

    private void logNoResourcesFoundWarning() {
        if (getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "No resource was found for requested resource '"
                    + getHref()
                    + "' and device " + getDevice());
        }
    }

    private void writeSingleLinkTag(final JspWriter jspWriter, final Resource resource)
            throws IOException {
        writeSingleLinkTag(jspWriter, getTagDependencies().getClientPathPrefix()
                + resource.getNewPath());
    }

    private void writeSingleLinkTag(final JspWriter jspWriter,
            final String src) throws IOException {
        jspWriter.print("<script ");

        jspWriter.print("src=\""
                + src + "\" ");

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {
            jspWriter.print(attribute.getLocalName() + "=\""
                    + attribute.getValue() + "\" ");
        }

        jspWriter.print("></script>\n");
    }

    private void writeLinkTagWithBodyContent(final JspWriter jspWriter,
            final JspFragment jspBody) throws IOException, JspException {
        jspWriter.print("<script ");

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {
            jspWriter.print(attribute.getLocalName() + "=\""
                    + attribute.getValue() + "\" ");
        }

        jspWriter.print(">");
        jspBody.invoke(jspWriter);
        jspWriter.print("</script>\n");
    }

    private ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return getTagDependencies().getResourceResolutionWarnLogger();
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
     * @return the cssBundleFactory
     */
    private ScriptBundleFactory getScriptBundleFactory() {
        return getTagDependencies().getScriptBundleFactory();
    }

    private DeploymentMetadata getDeploymentMetadata() {
        return getTagDependencies().getDeploymentMetadata();
    }

    /**
     * @return the {@link ScriptTagDependencies}
     */
    private ScriptTagDependencies getTagDependencies() {
        return scriptTagDependencies;
    }

}
