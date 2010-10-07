package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
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
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger} to use to log warnings.
     * @param deploymentMetadata {@link DeploymentMetadata} of the deployed app.
     * @param fileExtensionWildcards
     *            Array of image file extensions to match. Wildcards supported
     *            are '*' as per standard Unix/Windows command line
     *            semantics.
     */
    public ImageResourceResolverBean(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final DeploymentMetadata deploymentMetadata,
            final String[] fileExtensionWildcards) {
        super(abstractResourceExtension, rootResourcesDir,
                resourceResolutionWarnLogger, deploymentMetadata);

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
    protected List<Resource> doResolve(
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
        return new ResourceBean(requestedResourcePath,
                getNewResourcePathPlusFileExtension(foundFile, newResourcePath),
                getRootResourcesDir());
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

    private String[] getFileExtensionWildcards() {
        return fileExtensionWildcards;
    }

    private void debugLogCheckingForImagesIn(final String newResourcesBasePath) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Checking for images in: '" + newResourcesBasePath + "'");
        }
    }
}