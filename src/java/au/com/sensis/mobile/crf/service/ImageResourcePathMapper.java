package au.com.sensis.mobile.crf.service;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * {@link ResourcePathMapper} that maps abstract image paths to real image
 * paths. Note that the generated path will not have an extension given that
 * there is a multitude of possible image formats. The
 * {@link MappedResourcePath} returned by
 * {@link AbstractResourcePathMapper#mapResourcePath(String, au.com.sensis.mobile.crf.config.Group)}
 * can be consulted to resolve the new path to actual files.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageResourcePathMapper extends AbstractResourcePathMapper {

    private static final Logger LOGGER =
            Logger.getLogger(ImageResourcePathMapper.class);

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Abstract extension for resources.
     * @param rootResourcesDir
     *            Root directory where the concrete resources are stored.
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger}.
     */
    public ImageResourcePathMapper(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(abstractResourceExtension, rootResourcesDir,
                resourceResolutionWarnLogger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MappedResourcePath createMappedResourcePath(
            final String requestedResourcePath, final String newResourcePath) {
        final ImageMappedResourcePathBean imageMappedResourcePathBean =
                new ImageMappedResourcePathBean(requestedResourcePath,
                        newResourcePath, getRootResourcesDir());
        imageMappedResourcePathBean
                .setResourceResolutionWarnLogger(getResourceResolutionWarnLogger());
        return imageMappedResourcePathBean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDebugResourceTypeName() {
        return "image";
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
    protected String getRealResourcePathExtension() {
        return StringUtils.EMPTY;
    }

}
