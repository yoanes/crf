package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageMappedResourcePathBean extends MappedResourcePathBean {

    // TODO: once we refactor into ImageGroupResourceResolver, Spring inject this.
    private static final String[] FILE_EXTENSION_WILDCARDS = new String [] {"*"};

    private ResourceResolutionWarnLogger resourceResolutionWarnLogger;

    /**
     * Default constructor.
     *
     * @param originalResourcePath
     *            Original path that was requested.
     * @param newResourcePath
     *            New path that originalResourcePath was mapped to.
     * @param rootResourceDir
     *            Root directory which the newResourcePath is relative to.
     */
    public ImageMappedResourcePathBean(final String originalResourcePath,
            final String newResourcePath, final File rootResourceDir) {
        super(originalResourcePath, newResourcePath, rootResourceDir);
    }

    /**
     * Return the first concrete resource path for the requested abstract path
     * and that has an acceptable image file extension.
     *
     * {@inheritDoc}
     */
    @Override
    public List<MappedResourcePath> resolve() {
        // TODO: possibly cache the result since we are accessing the file
        // system?
        final File[] matchedFiles =
                FileIoFacadeFactory.getFileIoFacadeSingleton().list(
                        getRootResourceDir(), getNewResourcePath(), FILE_EXTENSION_WILDCARDS);

        warnIfMultipleResourcesWithExtensionsFound(getOriginalResourcePath(),
                matchedFiles);

        // TODO: clean this up once we've refactored it into a ResourceResolver.
        if (matchedFiles.length > 0) {
            return Arrays.asList((MappedResourcePath)
                    new ImageMappedResourcePathBean(getOriginalResourcePath(),
                            getNewResourcePathPlusFileExtension(matchedFiles[0]),
                            getRootResourceDir()));
        } else {
            return new ArrayList<MappedResourcePath>();
        }
    }

    private String getNewResourcePathPlusFileExtension(final File matchedFile) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getNewResourcePath());
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
        for (final File mappedResourcePath : matchedFiles) {
            stringBuilder.append(mappedResourcePath);
            if (i < matchedFiles.length - 1) {
                stringBuilder.append(", ");
            }
            i++;
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * @return The {@link ResourceResolutionWarnLogger}.
     */
    public ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

    /**
     * @param resourceResolutionWarnLogger The {@link ResourceResolutionWarnLogger}.
     */
    //TODO: temporary setter method. Use of the resourceResolutionWarnLogger will
    //end up in the ImageGroupResourceResolver, which will be a singleton so this
    //can easily be constructor injected.
    public void setResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
    }
}
