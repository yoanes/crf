package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * {@link ResourceResolver} that resolves abstract CSS paths to real CSS paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CssResourceResolverBean extends AbstractResourceResolver {

    private static final Logger LOGGER = Logger.getLogger(CssResourceResolverBean.class);

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger} to use to log warnings.
     */
    public CssResourceResolverBean(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(abstractResourceExtension, rootResourcesDir,
                resourceResolutionWarnLogger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRealResourcePathExtension() {
        return ".css";
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
        return "CSS";
    }
}
