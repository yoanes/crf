package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourcePathMapper} that maps abstract JSP paths to real JSP paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JspResourcePathMapper extends AbstractResourcePathMapper {

    private static final Logger LOGGER = Logger.getLogger(JspResourcePathMapper.class);

    private final String jspResourcesRootServletPath;

    /**
     * Constructor.
     *
     * @param jspResourcesRootServletPath
     *            Root of JSP resources, relative to the servlet context root.
     * @param abstractResourceExtension
     *            Abstract extension for resources.
     * @param rootResourcesDir
     *            Root directory where the concrete resources are stored.
     */
    public JspResourcePathMapper(final String jspResourcesRootServletPath,
            final String abstractResourceExtension, final File rootResourcesDir) {
        super(abstractResourceExtension, rootResourcesDir);

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
    protected String insertGroupNameIntoPath(final String requestedResourcePath,
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

}