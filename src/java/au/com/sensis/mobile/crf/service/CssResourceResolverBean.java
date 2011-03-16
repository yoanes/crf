package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * {@link ResourceResolver} that resolves abstract CSS paths to real CSS paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CssResourceResolverBean extends AbstractMultipleResourceResolver {

    private static final Logger LOGGER = Logger.getLogger(CssResourceResolverBean.class);

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
     * @param resourceAccumulatorFactory
     *            Provides a {@link ResourceAccumulator} for this
     *            {@link ResourceResolver}.
     *
     */
    public CssResourceResolverBean(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceAccumulatorFactory resourceAccumulatorFactory) {

        super(commonParams, abstractResourceExtension, rootResourcesDir,
                resourceAccumulatorFactory);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResourceAccumulator createResourceAccumulator() {

        return getResourceAccumulatorFactory().getCSSResourceAccumulator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getResourceSubDirName() {
        return "css";
    }

}
