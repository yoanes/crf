package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;

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
    private final BundleTag parentBundleLinksTag;

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
     * @param parentBundleLinksTag {@link BundleTag} that is the parent of the link tag.
     *            Null if there is no parent.
     */
    public LinkTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes, final String href,
            final LinkTagDependencies linkTagDependencies,
            final BundleTag parentBundleLinksTag) {
        this.device = device;
        this.dynamicAttributes = dynamicAttributes;
        this.href = href;
        this.linkTagDependencies = linkTagDependencies;
        this.parentBundleLinksTag = parentBundleLinksTag;
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

        final List<Resource> allResourcePaths = getAllResourcePaths();
        if (bundlingEnabled()) {
            postponeWriteForBundleLinksTag(allResourcePaths);
        } else {
            writeLinkTagForEachResource(jspWriter, allResourcePaths);
        }
    }

    /**
     * Should bundling occur?
     * - We must be inside a parent bundle scripts tag.
     * - The configuration must have bundling set to true.
     *
     * @return boolean - true when bundling should occur.
     */
    protected boolean bundlingEnabled() {

        return (getParentBundleLinksTag() != null)
                && (getParentBundleLinksTag().hasBundlingEnabled());
    }

    private void postponeWriteForBundleLinksTag(final List<Resource> allResourcePaths) {
        getParentBundleLinksTag().addResourcesToBundle(allResourcePaths);
    }

    private void writeLinkTagForEachResource(final JspWriter jspWriter,
            final List<Resource> allResources) throws IOException {
        if (!allResources.isEmpty()) {
            for (final Resource resource : allResources) {
                writeSingleLinkTag(jspWriter, resource);
            }
        }
    }

    private void writeSingleLinkTag(final JspWriter jspWriter,
            final Resource resource) throws IOException {
        jspWriter.print("<link ");

        jspWriter.print("href=\""
                + getTagDependencies().getClientPathPrefix()
                + resource.getNewPath() + getUniqueRequestParam() + "\" ");

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {
            jspWriter.print(attribute.getLocalName() + "=\""
                    + attribute.getValue() + "\" ");
        }

        jspWriter.print("/>");
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

    /**
     * @return the linkTagDependencies
     */
    private LinkTagDependencies getTagDependencies() {
        return linkTagDependencies;
    }

    /**
     * @return the parentBundleLinksTag
     */
    private BundleTag getParentBundleLinksTag() {
        return parentBundleLinksTag;
    }
}
