package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourceResolver} that maps abstract image paths to real image
 * paths. Note that the generated path will not have an extension given that
 * there is a multitude of possible image formats. The
 * {@link Resource} returned by
 * {@link AbstractResourceResolver#resolve(String, au.com.sensis.mobile.crf.config.Group)}
 * can be consulted to resolve the new path to actual files.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
// TODo: clean up code. Current mess is due to refactoring ImageResourceBean into oblivion
// and pushing the code into here.
public class ImageResourceResolverBean extends AbstractResourceResolver {

    private static final Logger LOGGER =
            Logger.getLogger(ImageResourceResolverBean.class);

    // TODO: once we refactor into ImageGroupResourceResolver, Spring inject this.
    private static final String[] FILE_EXTENSION_WILDCARDS = new String [] {"*"};

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
    public ImageResourceResolverBean(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(abstractResourceExtension, rootResourcesDir,
                resourceResolutionWarnLogger);
    }

    /**
     * Template method for mapping requested resource paths to real resource
     * paths.
     *
     * {@inheritDoc}
     *
     * @throws IOException
     *             Thrown if an IO error occurs.
     */
    @Override
    public List<Resource> resolve(final String requestedResourcePath,
            final Group group) throws IOException {
        if (isRecognisedAbstractResourceRequest(requestedResourcePath)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "Mapping '"
                                + requestedResourcePath
                                + "' to '"
                                + doMapResourcePath(requestedResourcePath,
                                        group) + "'");
            }
            final Resource resource =
                    createResource(requestedResourcePath,
                            doMapResourcePath(requestedResourcePath, group));

            return doResolve(resource);
        } else {

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "Requested resource '" + requestedResourcePath
                                + "' is not for a "
                                + getDebugResourceTypeName()
                                + " file. Returning an empty list.");
            }

            return new ArrayList<Resource>();
        }
    }

    /**
     * Return the first concrete resource path for the requested abstract path
     * and that has an acceptable image file extension.
     *
     * {@inheritDoc}
     *
     * @param resource
     */
    private List<Resource> doResolve(
            final Resource resource) {
        // TODO: possibly cache the result since we are accessing the file
        // system?
        final File[] matchedFiles =
                FileIoFacadeFactory.getFileIoFacadeSingleton().list(
                        resource.getRootResourceDir(),
                        resource.getNewPath(),
                        FILE_EXTENSION_WILDCARDS);

        warnIfMultipleResourcesWithExtensionsFound(resource
                .getOriginalPath(), matchedFiles);

        // TODO: clean this up once we've refactored it into a ResourceResolver.
        if (matchedFiles.length > 0) {
            return Arrays
                    .asList((Resource) new ResourceBean(
                            resource.getOriginalPath(),
                            getNewPathPlusFileExtension(
                                    matchedFiles[0], resource),
                            resource.getRootResourceDir()));
        } else {
            return new ArrayList<Resource>();
        }
    }

    private String getNewPathPlusFileExtension(final File matchedFile,
            final Resource resource) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(resource.getNewPath());
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
                    + ArrayUtils.toString(FILE_EXTENSION_WILDCARDS)
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

}
