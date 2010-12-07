package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Simple implementation of {@link TagWriter} that outputs a link tag in the
 * default namespace.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkTagWriter implements TagWriter {

    private final Device device;
    private final List<DynamicTagAttribute> dynamicAttributes;
    private final String href;

    private final LinkTagDependencies linkTagDependencies;

    /**
     * Default constructor.
     *
     * @param device
     *            {@link Device} of the current request.
     * @param dynamicAttributes
     *            List of {@link DynamicTagAttribute}s containing dynamic JSP
     *            tag attributes to be written out.
     * @param href
     *            Href attribute of the tag to be written.
     * @param linkTagDependencies Singleton collaborators.
     */
    public LinkTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes, final String href,
            final LinkTagDependencies linkTagDependencies) {
        this.device = device;
        this.dynamicAttributes = dynamicAttributes;
        this.href = href;
        this.linkTagDependencies = linkTagDependencies;
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
     * {@inheritDoc}
     */
    public String getId() {
        return getHref();
    }

    /**
     * {@inheritDoc}
     */
    public void writeTag(final JspWriter jspWriter, final JspFragment jspBody) throws IOException,
    JspException {

        writeLinkTagForEachResource(jspWriter, getAllResourcePaths());
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

    private void logNoResourcesFoundWarning() {
        if (getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "No resource was found for requested resource '"
                    + getHref()
                    + "' and device " + getDevice());
        }
    }

    private void writeSingleLinkTag(final JspWriter jspWriter,
            final Resource resource) throws IOException {
        jspWriter.print("<link ");

        jspWriter.print("href=\""
                + getTagDependencies().getClientPathPrefix()
                + resource.getNewPath() + "\" ");

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {
            jspWriter.print(attribute.getLocalName() + "=\""
                    + attribute.getValue() + "\" ");
        }

        jspWriter.print("/>");
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
                    + "returned null for '" + getHref() + ". "
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

    private DeploymentMetadata getDeploymentMetadata() {
        return getTagDependencies().getDeploymentMetadata();
    }

    /**
     * @return the linkTagDependencies
     */
    private LinkTagDependencies getTagDependencies() {
        return linkTagDependencies;
    }
}
