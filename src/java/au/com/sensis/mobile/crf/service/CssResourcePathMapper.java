package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * {@link ResourcePathMapper} that maps abstract CSS paths to real CSS paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class CssResourcePathMapper extends AbstractResourcePathMapper {

    private static final Logger LOGGER = Logger.getLogger(CssResourcePathMapper.class);

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Abstract extension for resources.
     * @param rootResourcesDir Root directory where the concrete resources are stored.
     */
    public CssResourcePathMapper(final String abstractResourceExtension,
            final File rootResourcesDir) {
        super(abstractResourceExtension, rootResourcesDir);
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
