package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.util.ImageTransformationFactory;
import au.com.sensis.mobile.crf.util.ImageTransformationParametersBean;
import au.com.sensis.mobile.crf.util.TransformedImageAttributes;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * {@link ResourceResolver} that resolves abstract image paths to real image paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class TransformedImageResourceResolverBean extends AbstractSingleResourceResolver {

    private static final Logger LOGGER =
        Logger.getLogger(TransformedImageResourceResolverBean.class);

    private static final String PROPERTIES_FILE_EXTENSION = ".properties";

    private static final String WIDTH_PROPERTY_NAME = "width";

    private final String [] fileExtensionWildcards;

    private au.com.sensis.mobile.crf.util.PropertiesLoader propertiesLoader;
    private ImageTransformationFactory imageTransformationFactory;

    /**
     * Constructor.
     *
     * @param commonParams
     *            Holds the common parameters used in constructing all
     *            {@link ResourceResolver}s.
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param fileExtensionWildcards
     *            Array of image file extensions to match. Wildcards supported
     *            are '*' as per standard Unix/Windows command line semantics.
     */
    public TransformedImageResourceResolverBean(
            final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension, final File rootResourcesDir,
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
     * Invoked by {@link #resolve(String, Device)} if
     * {@link #isRecognisedAbstractResourceRequest(String)} resturns true.
     *
     * @param requestedResourcePath
     *            Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @param device
     *            {@link Device} to perform the path mapping for.
     * @return List of {@link Resource}s containing the results. If no resources
     *         can be resolved, an empty list is returned. May not be null.
     */
    @Override
    protected List<Resource> doResolve(final String requestedResourcePath, final Device device) {

        final FoundImageFiles imageFiles = findImageFiles(requestedResourcePath, device);

        if (imageFiles.getFoundFiles().length > 0) {

            final Resource foundResource =
                    createImageResource(requestedResourcePath, device, imageFiles.getGroup(),
                            imageFiles);

            return Arrays.asList(foundResource);
        } else {
            return new ArrayList<Resource>();
        }
    }

    private Properties getImageProperties(final String requestedResourcePath,
            final FoundImageFiles foundImageFiles) {
        final Properties imageProperties = new Properties();
        accumulateImagePropertiesFromGroup(requestedResourcePath, imageProperties,
                foundImageFiles.getGroup());
        while (foundImageFiles.getRemainingGroupsIterator().hasNext()) {
            final Group currGroup = foundImageFiles.getRemainingGroupsIterator().next();
            accumulateImagePropertiesFromGroup(requestedResourcePath, imageProperties,
                    currGroup);
        }
        return imageProperties;
    }

    private FoundImageFiles findImageFiles(final String requestedResourcePath,
            final Device device) {

        File[] matchedFiles = new File[] {};
        final Iterator<Group> matchingGroupIterator =
                getMatchingGroupIterator(device, requestedResourcePath);
        Group currGroup = null;

        while (matchingGroupIterator.hasNext()) {

            currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(requestedResourcePath, currGroup);

            final String newResourcePathMinusExtension =
                    createNewResourcePath(requestedResourcePath, currGroup);

            debugLogCheckingForImagesIn(newResourcePathMinusExtension);

            matchedFiles =
                    FileIoFacadeFactory.getFileIoFacadeSingleton().list(getRootResourcesDir(),
                            newResourcePathMinusExtension, getFileExtensionWildcards());

            if (matchedFiles.length > 0) {
                break;
            }
        }

        return new FoundImageFiles(matchedFiles, currGroup, matchingGroupIterator);

    }

    private void accumulateImagePropertiesFromGroup(final String requestedResourcePath,
            final Properties imageProperties, final Group currGroup) {
        final String newResourcePathMinusExtension =
                createNewResourcePath(requestedResourcePath, currGroup);

        final File propertiesFile =
                new File(getRootResourcesDir(), newResourcePathMinusExtension
                        + PROPERTIES_FILE_EXTENSION);
        Properties foundProperties = new Properties();
        try {
            foundProperties = getPropertiesLoader().loadPropertiesNotNull(propertiesFile);
        } catch (final IOException e) {
            // TODO
            throw new RuntimeException("TODO");
        }
        imageProperties.putAll(foundProperties);
    }

//    /**
//     * Return the first concrete resource path for the requested abstract path
//     * and that has an acceptable image file extension.
//     *
//     * {@inheritDoc}
//     */
//    @Override
//    protected List<Resource> doResolveForGroup(final String requestedResourcePath,
//            final Device device, final Group group) throws ResourceResolutionRuntimeException {
//
//        final String newResourcePathMinusExtension = createNewResourcePath(
//                requestedResourcePath, group);
//
//        debugLogCheckingForImagesIn(newResourcePathMinusExtension);
//
//        final File[] matchedFiles =
//                FileIoFacadeFactory.getFileIoFacadeSingleton().list(getRootResourcesDir(),
//                        newResourcePathMinusExtension, getFileExtensionWildcards());
//
//        warnIfMultipleResourcesWithExtensionsFound(requestedResourcePath, matchedFiles);
//
//        if (matchedFiles.length > 0) {
//            final Resource foundResource =
//                    createFoundResource(requestedResourcePath, device, group, matchedFiles[0]);
//            return Arrays.asList(foundResource);
//        } else {
//            return new ArrayList<Resource>();
//        }
//    }

    private File createBaseTargetImageDir(final File sourceImage) {
        return sourceImage.getParentFile();
    }

    private ImageTransformationParametersBean createImageTransformationParametersBean(
            final Device device, final Properties imageProperties,
            final File foundFile) {

        final ImageTransformationParametersBean result = new ImageTransformationParametersBean();

        setDeviceImagePercentWidthIfRequired(result, imageProperties);
        setAbsolutePixelWidthIfRequired(result, imageProperties);

        // Note that for some devices, the pixelsX value will include the
        // scrollbar, for others
        // it will not. Regardless, the only handle that can be used to control
        // the image size
        // is the width property.
        result.setDevicePixelWidth(device.getPixelsX());

        result.setOutputImageFormat(getOutputImageFormatParameter(foundFile));

        return result;
    }

    private ImageTransformationFactory.ImageFormat getOutputImageFormatParameter(
            final File foundFile) {

        // TODO: image format strategy not decided on yet. Just defaulting to the foundFile
        // for now, which is also backwards compatible.
        final ImageTransformationFactory.ImageFormat format =
            ImageTransformationFactory.ImageFormat.fromString(
                    FilenameUtils.getExtension(foundFile.getPath()));
        if (format != null) {
            return format;
        } else {
            throw new IllegalStateException("TODO: image format not supported for file: "
                    + foundFile);
        }
//        final DeviceLocalExtension deviceLocalExtension = new DeviceLocalExtension(device);
//
//        final ImageTransformationFactory.ImageFormat preferredImageFormat =
//                getPreferredImageFormat(deviceLocalExtension);
//        if (preferredImageFormat != null) {
//            return preferredImageFormat;
//
//        } else if (deviceLocalExtension.isPngImageTypeSupported()) {
//            return ImageTransformationFactory.ImageFormat.PNG;
//
//        } else if (deviceLocalExtension.isJpegImageTypeSupported()) {
//            return ImageTransformationFactory.ImageFormat.JPEG;
//
//        } else {
//            // We assume gif support as the default. This should be fine for all
//            // devices that we care about.
//            return ImageTransformationFactory.ImageFormat.GIF;
//        }
    }

    // TODO
//    private ImageTransformationFactory.ImageFormat getPreferredImageFormat(
//            final DeviceLocalExtension deviceLocalExtension) {
//
//        if (deviceLocalExtension.isPreferredImageTypePng()) {
//            return ImageTransformationFactory.ImageFormat.PNG;
//
//        } else if (deviceLocalExtension.isPreferredImageTypeJpeg()) {
//            return ImageTransformationFactory.ImageFormat.JPEG;
//
//        } else if (deviceLocalExtension.isPreferredImageTypeGif()) {
//            return ImageTransformationFactory.ImageFormat.GIF;
//
//        } else {
//            return null;
//        }
//    }

    private void setDeviceImagePercentWidthIfRequired(
            final ImageTransformationParametersBean parametersBean,
            final Properties imageProperties) {

        try {
            final String rawPropertyValue = imageProperties.getProperty(WIDTH_PROPERTY_NAME);

            if (StringUtils.isNotBlank(rawPropertyValue) && rawPropertyValue.trim().endsWith("%")) {
                final Integer percentWidth =
                        Integer.valueOf(rawPropertyValue.replaceFirst("%\\s*$", StringUtils.EMPTY));
                parametersBean.setDeviceImagePercentWidth(percentWidth);
            }
        } catch (final NumberFormatException e) {
            // TODO: log warning or throw exception?
            throw new ResourceResolutionRuntimeException("TODO", e);
        }
    }

    private void setAbsolutePixelWidthIfRequired(
            final ImageTransformationParametersBean parametersBean,
            final Properties imageProperties) {

        try {
            final String rawPropertyValue = imageProperties.getProperty(WIDTH_PROPERTY_NAME);

            if (StringUtils.isNotBlank(rawPropertyValue)
                    && rawPropertyValue.trim().endsWith("px")) {
                final Integer absolutePixelWidth =
                        Integer.valueOf(rawPropertyValue.replaceFirst("\\s*px\\s*$",
                                StringUtils.EMPTY));
                parametersBean.setAbsolutePixelWidth(absolutePixelWidth);
            }

        } catch (final NumberFormatException e) {
            // TODO: log warning or throw exception?
            throw new ResourceResolutionRuntimeException("TODO", e);
        }
    }

    // private Properties getImageProperties(final Device device, final String
    // requestedResourcePath) {
    // final String requestedPathExtesion =
    // "." + FilenameUtils.getExtension(requestedResourcePath);
    //
    // final String requestedPathMinusExtension =
    // StringUtils.removeEnd(requestedResourcePath, requestedPathExtesion);
    //
    // return getPropertiesLoader().loadPropertiesNotNull(
    // requestedPathMinusExtension + PROPERTIES_FILE_EXTENSION);
    // }

    private Resource createImageResource(final String requestedResourcePath, final Device device,
            final Group group, final FoundImageFiles imageFiles) {

        warnIfMultipleResourcesWithExtensionsFound(requestedResourcePath, imageFiles
                .getFoundFiles());

        final File foundFile = imageFiles.getFoundFiles()[0];

        if (foundFile.getPath().endsWith(Resource.DOT_NULL_EXTENSION)) {
            return createImageResourceFromExactFileFound(requestedResourcePath, group, foundFile);

        } else {
            return createImageResourceByTransformingFoundFile(requestedResourcePath, device, group,
                    imageFiles, foundFile);
        }

    }

    private Resource createImageResourceByTransformingFoundFile(final String requestedResourcePath,
            final Device device, final Group group, final FoundImageFiles imageFiles,
            final File foundFile) {
        final Properties imageProperties =
            getImageProperties(requestedResourcePath, imageFiles);

        final ImageTransformationParametersBean transformationParametersBean =
                createImageTransformationParametersBean(device, imageProperties,
                        foundFile);

        final TransformedImageAttributes transformedImageAttributes =
                getImageTransformationFactory().transformImage(foundFile,
                        createBaseTargetImageDir(foundFile), transformationParametersBean);

        final String relativeOutputImagePath =
                getResourcePathRelativeTo(transformedImageAttributes.getOutputImageAttributes()
                        .getFile(), getRootResourcesDir());

        final ImageResourceBean resource =
                new ImageResourceBean(requestedResourcePath, relativeOutputImagePath,
                        getRootResourcesDir(), group);
        resource.setImageWidth(transformedImageAttributes.getOutputImageAttributes()
                .getPixelWidth());
        resource.setImageHeight(transformedImageAttributes.getOutputImageAttributes()
                .getPixelHeight());
        return resource;
    }

    private Resource createImageResourceFromExactFileFound(final String requestedResourcePath,
            final Group group, final File foundFile) {
        return new ImageResourceBean(requestedResourcePath,
                getResourcePathRelativeTo(foundFile, getRootResourcesDir()),
                getRootResourcesDir(), group);
    }

    private String getResourcePathRelativeTo(final File file, final File rootResourcesDir) {
        String relativePath = StringUtils.removeStart(file.getPath(), rootResourcesDir.getPath());
        relativePath = relativePath.replaceFirst("^\\\\", StringUtils.EMPTY);
        relativePath = relativePath.replaceFirst("^/", StringUtils.EMPTY);
        relativePath = relativePath.replaceAll("\\\\", "/");
        return relativePath;
    }

    private void warnIfMultipleResourcesWithExtensionsFound(
            final String requestedResourcePath,
            final File[] matchedFiles) {
        // TODO: adjust message slightly for scaling.
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

    private void debugLogCheckingForImagesIn(final String newResourcePathMinusExtension) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Checking for images with base path of : '"
                    + newResourcePathMinusExtension + "'");
        }
    }

    /**
     * @return the propertiesLoader
     */
    private au.com.sensis.mobile.crf.util.PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

    /**
     * @return the scaledImageFactory
     */
    private ImageTransformationFactory getImageTransformationFactory() {
        return imageTransformationFactory;
    }

    /**
     * @param propertiesLoader
     *            the propertiesLoader to set
     */
    public void setPropertiesLoader(
            final au.com.sensis.mobile.crf.util.PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
    }

    /**
     * @param imageTransformationFactory
     *            the scaledImageFactory to set
     */
    public void setImageTransformationFactory(
            final ImageTransformationFactory imageTransformationFactory) {
        this.imageTransformationFactory = imageTransformationFactory;
    }

    private final class FoundImageFiles {
        private final File [] foundFiles;
        private final Group group;
        private final Iterator<Group> remainingGroupsIterator;

        private FoundImageFiles(final File[] foundFiles, final Group group,
                final Iterator<Group> remainingGroupsIterator) {
            this.foundFiles = foundFiles;
            this.group = group;
            this.remainingGroupsIterator = remainingGroupsIterator;
        }

        /**
         * @return the foundFiles
         */
        private File[] getFoundFiles() {
            return foundFiles;
        }

        /**
         * @return the group
         */
        private Group getGroup() {
            return group;
        }

        /**
         * @return the remainingGroupsIterator
         */
        private Iterator<Group> getRemainingGroupsIterator() {
            return remainingGroupsIterator;
        }
    }
}
