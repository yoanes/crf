package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourceResolver} that maps abstract JSP paths to real JSP paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspResourceResolverBean extends AbstractSingleResourceResolver {

    private static final Logger LOGGER = Logger.getLogger(JspResourceResolverBean.class);

    private final String jspResourcesRootServletPath;

    /**
     * Constructor.
     *
     * @param commonParams
     *            Holds the common parameters used in constructing all {@link ResourceResolver}s.
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param jspResourcesRootServletPath
     *            Root of JSP resources, relative to the servlet context root.
     * @param resourceCache {@link ResourceCache} for caching {@link Resource}s.
     */
    public JspResourceResolverBean(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension,
            final File rootResourcesDir,
            final String jspResourcesRootServletPath, final ResourceCache resourceCache) {

        super(commonParams, abstractResourceExtension, rootResourcesDir, resourceCache);

        validateJspResourcesRootServletPath(jspResourcesRootServletPath);

        this.jspResourcesRootServletPath = jspResourcesRootServletPath;
    }

    private void validateJspResourcesRootServletPath(
            final String jspResourcesRootServletPath) {
        if (StringUtils.isBlank(jspResourcesRootServletPath)) {
            throw new IllegalArgumentException(
                    "jspResourcesRootServletPath must not be blank: '"
                    + jspResourcesRootServletPath + "'");
        }
    }

    /**
     * Overrides the standard implementation to additionally check that the requested resource path
     * starts with the standard, servlet context relative path for JSPs.
     *
     * {@inheritDoc}
     */
    @Override
    protected boolean isRecognisedAbstractResourceRequest(final String requestedResourcePath) {
        return requestedResourcePath.startsWith(getJspResourcesRootServletPath())
        && super.isRecognisedAbstractResourceRequest(requestedResourcePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRealResourcePathExtension() {
        return ".jsp";
    }

    /**
     * Overrides the standard implementation. Correctly handles the expected
     * servlet context relative path prefix for JSPs.
     *
     * {@inheritDoc}
     */
    @Override
    protected String insertGroupNameAndDeploymentVersionIntoPath(final String requestedResourcePath,
            final Group group) {
        return getJspResourcesRootServletPath()
        + group.getName()
        + RESOURCE_SEPARATOR
        + StringUtils.substringAfter(requestedResourcePath,
                getJspResourcesRootServletPath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDebugResourceTypeName() {
        return "JSP";
    }

    /**
     * @return the jspResourcesRootServletPath
     */
    private String getJspResourcesRootServletPath() {
        return jspResourcesRootServletPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addResourcesToResourceResolutionTreeIfEnabled(final List<Resource> resources) {
        // Do nothing. JSP resource resolution debugging occurs in ResourceResolverServlet
        // since that is the only point which can tell when a JSP is including other resources.
    }

}
