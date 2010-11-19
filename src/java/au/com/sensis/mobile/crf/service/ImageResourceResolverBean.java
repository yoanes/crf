package au.com.sensis.mobile.crf.service;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;

/**
 * {@link ResourceResolver} that resolves abstract image paths to real image paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageResourceResolverBean extends AbstractResourceResolver {

    private static final Logger LOGGER =
        Logger.getLogger(ImageResourceResolverBean.class);

    private final String [] fileExtensionWildcards;

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
     * @param fileExtensionWildcards
     *            Array of image file extensions to match. Wildcards supported
     *            are '*' as per standard Unix/Windows command line
     *            semantics.
     */
    public ImageResourceResolverBean(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension,
            final File rootResourcesDir,
            final String[] fileExtensionWildcards) {

        super(commonParams, abstractResourceExtension, rootResourcesDir);

        validateFileExtensionWildcards(fileExtensionWildcards);

        this.fileExtensionWildcards = fileExtensionWildcards;
    }

    private void validateFileExtensionWildcards(
            final String[] fileExtensionWildcards) {
        if ((fileExtensionWildcards == null)
                || (fileExtensionWildcards.length == 0)
                || containsBlanks(fileExtensionWildcards)) {
            throw new IllegalArgumentException(
                    "fileExtensionWildcards must be an array of non-blank Strings but was: '"
                    + ArrayUtils.toString(fileExtensionWildcards) + "'");
        }
    }

    private boolean containsBlanks(final String[] fileExtensionWildcards) {
        for (final String wildcard : fileExtensionWildcards) {
            if (StringUtils.isBlank(wildcard)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the first concrete resource path for the requested abstract path
     * and that has an acceptable image file extension.
     *
     * {@inheritDoc}
     */
    @Override
    protected List<Resource> doResolveForGroup(
            final String requestedResourcePath, final Group group)
            throws ResourceResolutionRuntimeException {

        final String newResourcesBasePath = createNewResourcePath(requestedResourcePath, group);

        debugLogCheckingForImagesIn(newResourcesBasePath);

        // TODO: possibly cache the result since we are accessing the file
        // system?
        final File[] matchedFiles =
            FileIoFacadeFactory.getFileIoFacadeSingleton().list(
                    getRootResourcesDir(),
                    newResourcesBasePath,
                    getFileExtensionWildcards());

        warnIfMultipleResourcesWithExtensionsFound(requestedResourcePath, matchedFiles);

        if (matchedFiles.length > 0) {
            return Arrays
            .asList(createFoundResource(requestedResourcePath, newResourcesBasePath,
                    matchedFiles[0]));
        } else {
            return new ArrayList<Resource>();
        }
    }

    private Resource createFoundResource(final String requestedResourcePath,
            final String newResourcePath, final File foundFile) {

        final ImageResourceBean resource =  new ImageResourceBean(requestedResourcePath,
                getNewResourcePathPlusFileExtension(foundFile, newResourcePath),
                getRootResourcesDir());

        final Dimension dimensions = getImageDimensions(foundFile);

        if (dimensions != null) {
            resource.setImageHeight((int) dimensions.getHeight());
            resource.setImageWidth((int) dimensions.getWidth());
        }
        return resource;
    }

    private Dimension getImageDimensions(final File imageFile) {

        Dimension dimensions = null;

        final Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(
                FilenameUtils.getExtension(imageFile.getName()));

        if (readers.hasNext()) {
            final ImageReader reader = readers.next();

            try {
                final ImageInputStream stream = new FileImageInputStream(imageFile);
                reader.setInput(stream);

                dimensions = new Dimension(reader.getWidth(reader.getMinIndex()),
                        reader.getHeight(reader.getMinIndex()));

            } catch (final IOException e) {
                // It will work OK without width and height, but this error shouldn't happen.
                LOGGER.warn("Unable to retrieve image dimensions for " + imageFile.getName()
                        + "\n " + e);
            } finally {
                reader.dispose();
            }
        }

        return dimensions;
    }

    private String getNewResourcePathPlusFileExtension(final File matchedFile,
            final String newResourcePath) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(newResourcePath);
        stringBuilder.append(".");
        stringBuilder.append(FilenameUtils.getExtension(matchedFile.getName()));
        return stringBuilder.toString();
    }

    private void warnIfMultipleResourcesWithExtensionsFound(
            final String requestedResourcePath,
            final File[] matchedFiles) {
        if ((matchedFiles.length > 1)
                && getResourceResolutionWarnLogger().isWarnEnabled()) {
            getResourceResolutionWarnLogger().warn(
                    "Requested resource '"
                    + requestedResourcePath
                    + "' resolved to multiple real resources with extensions matching "
                    + ArrayUtils.toString(getFileExtensionWildcards())
                    + ". Will only return the first resource. Total found: "
                    + nonEmptyArrayToString(matchedFiles)
                    + ".");
        }
    }

    private String nonEmptyArrayToString(
            final File[] matchedFiles) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        int i = 0;
        for (final File currFile : matchedFiles) {
            stringBuilder.append(currFile);
            if (i < matchedFiles.length - 1) {
                stringBuilder.append(", ");
            }
            i++;
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResourceAccumulator createResourceAccumulator() {

        return getResourceAccumulatorFactory().getImageResourceAccumulator();
    }

    private String[] getFileExtensionWildcards() {
        return fileExtensionWildcards;
    }

    private void debugLogCheckingForImagesIn(final String newResourcesBasePath) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Checking for images in: '" + newResourcesBasePath + "'");
        }
    }
}
