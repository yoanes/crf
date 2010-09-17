package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourcePathMapper} that maps abstract Script paths to real Script paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourcePathMapper extends AbstractResourcePathMapper {

    private static final Logger LOGGER = Logger.getLogger(JavaScriptResourcePathMapper.class);

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Abstract extension for resources.
     * @param rootResourcesDir Root directory where the concrete resources are stored.
     */
    public JavaScriptResourcePathMapper(final String abstractResourceExtension,
            final File rootResourcesDir) {
        super(abstractResourceExtension, rootResourcesDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doMapResourcePath(final String requestedResourcePath, final Group group) {
        // TODO: "bundle" name creation will probably change. We want "package" or something
        // instead? Hard code for now.
        if (isBundleRequested(requestedResourcePath)) {
            return insertGroupNameIntoPath(requestedResourcePath, group) + "/bundle-all.js";
        } else {
            return super.doMapResourcePath(requestedResourcePath, group);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MappedResourcePath createMappedResourcePath(
            final String requestedResourcePath, final String newResourcePath) {
        return new JavaScriptMappedResourcePathBean(requestedResourcePath,
                newResourcePath, getRootResourcesDir());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isRecognisedAbstractResourceRequest(
            final String requestedResourcePath) {
        return super.isRecognisedAbstractResourceRequest(requestedResourcePath)
            || isBundleRequested(requestedResourcePath);
    }



    private boolean isBundleRequested(final String requestedResourcePath) {
        // TODO: "bundle" checking will probably change. We want "package" or something instead?
        // Hard code for now.
        return requestedResourcePath.endsWith("bundle");
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRealResourcePathExtension() {
        return ".js";
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
        return "JavaScript";
    }
}
