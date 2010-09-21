package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import au.com.sensis.mobile.crf.config.DeploymentVersion;
import au.com.sensis.mobile.crf.service.CssBundleFactory;
import au.com.sensis.mobile.crf.service.MappedResourcePath;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceSelector;
import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Simple implementation of {@link ResourceSelectorTagWriter} that outputs a link tag in the
 * default namespace.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorLinkTagWriter implements ResourceSelectorTagWriter {

    private final Device device;
    private final List<DynamicTagAttribute> dynamicAttributes;
    private final String href;

    private final LinkTagCollaboratorsMemento linkTagCollaboratorsMemento;

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
     * @param linkTagCollaboratorsMemento Singleton collaborators.
     */
    public ResourceSelectorLinkTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes, final String href,
            final LinkTagCollaboratorsMemento linkTagCollaboratorsMemento) {
        this.device = device;
        this.dynamicAttributes = dynamicAttributes;
        this.href = href;
        this.linkTagCollaboratorsMemento = linkTagCollaboratorsMemento;
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
    public void writeTag(final JspWriter jspWriter) throws IOException,
            JspException {

        if (getDeploymentVersion().isDevPlatform()) {
            writeLinkTagForEachResource(jspWriter, getAllResourcePaths());
        } else {
            writeLinkTagForBundledResources(jspWriter, getAllResourcePaths());
        }

    }

    private void writeLinkTagForEachResource(final JspWriter jspWriter,
            final List<MappedResourcePath> allMappedResourcePaths) throws IOException {
        if (allMappedResourcePaths.isEmpty()) {
            logNoResourcesFoundWarning();

        } else {
            for (final MappedResourcePath mappedResourcePath : allMappedResourcePaths) {
                writeSingleLinkTag(jspWriter, mappedResourcePath);
            }
        }
    }

    private void writeLinkTagForBundledResources(final JspWriter jspWriter,
            final List<MappedResourcePath> allMappedResourcePaths)
            throws IOException {

        if (allMappedResourcePaths.isEmpty()) {
            logNoResourcesFoundWarning();

        } else {
            final MappedResourcePath bundleResourcePath =
                    getCssBundleFactory().getBundle(allMappedResourcePaths);

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

    private void writeSingleLinkTag(final JspWriter jspWriter,
            final MappedResourcePath mappedResourcePath) throws IOException {
        jspWriter.print("<link ");

        jspWriter.print("href=\""
                + getCollaboratorsMemento().getClientPathPrefix()
                + mappedResourcePath.getNewResourcePath() + "\" ");

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {
            jspWriter.print(attribute.getLocalName() + "=\""
                    + attribute.getValue() + "\" ");
        }

        jspWriter.print("/>\n");
    }

    private ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return getCollaboratorsMemento().getResourceResolutionWarnLogger();
    }

    private List<MappedResourcePath> getAllResourcePaths() throws IOException {
        final List<MappedResourcePath> allResourcePaths =
                getResourceSelector().getAllResourcePaths(getDevice(),
                        getHref());

        assertNotNull(allResourcePaths);

        return allResourcePaths;
    }

    private void assertNotNull(final List<MappedResourcePath> allResourcePaths) {
        if (allResourcePaths == null) {
            throw new IllegalStateException(
                    "getResourceSelector.getAllResourcePaths "
                            + "returned null for '" + getHref() + ". "
                            + "This should never happen !!!");
        }
    }

    /**
     * @return the {@link ResourceSelector}.
     */
    private ResourceSelector
        getResourceSelector() {
        return getCollaboratorsMemento().getResourceSelector();
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
    private CssBundleFactory getCssBundleFactory() {
        return getCollaboratorsMemento().getCssBundleFactory();
    }

    private DeploymentVersion getDeploymentVersion() {
        return getCollaboratorsMemento().getDeploymentVersion();
    }

    /**
     * @return the linkTagCollaboratorsMemento
     */
    private LinkTagCollaboratorsMemento getCollaboratorsMemento() {
        return linkTagCollaboratorsMemento;
    }
}
