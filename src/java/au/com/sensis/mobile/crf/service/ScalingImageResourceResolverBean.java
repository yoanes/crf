package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.util.Image;
import au.com.sensis.mobile.crf.util.ImageScalingParametersBean;
import au.com.sensis.mobile.crf.util.ScaledImageFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * {@link ResourceResolver} that resolves abstract image paths to real image paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScalingImageResourceResolverBean extends AbstractSingleResourceResolver {

    private static final Logger LOGGER =
        Logger.getLogger(ScalingImageResourceResolverBean.class);

    private static final String PROPERTIES_FILE_EXTENSION = ".properties";

    private static final String WIDTH_PROPERTY_NAME = "width";

    private final String [] fileExtensionWildcards;

    private PropertiesLoader propertiesLoader;
    private ScaledImageFactory scaledImageFactory;

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
    public ScalingImageResourceResolverBean(final ResourceResolverCommonParamHolder commonParams,
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
    protected List<Resource> doResolveForGroup(final String requestedResourcePath,
            final Device device, final Group group) throws ResourceResolutionRuntimeException {

        final String newResourcePathMinusExtension = createNewResourcePath(
                requestedResourcePath, group);

        debugLogCheckingForImagesIn(newResourcePathMinusExtension);

        final File[] matchedFiles =
                FileIoFacadeFactory.getFileIoFacadeSingleton().list(getRootResourcesDir(),
                        newResourcePathMinusExtension, getFileExtensionWildcards());

        warnIfMultipleResourcesWithExtensionsFound(requestedResourcePath, matchedFiles);

        if (matchedFiles.length > 0) {
            final Resource foundResource =
                    createFoundResource(requestedResourcePath, device, group, matchedFiles[0]);
            return Arrays.asList(foundResource);
        } else {
            return new ArrayList<Resource>();
        }
    }

    private File createBaseTargetImageDir(final File sourceImage) {
        return sourceImage.getParentFile();
    }

    private ImageScalingParametersBean createImageScalingParametersBean(
            final String requestedResourcePath, final Device device) {

        final ImageScalingParametersBean result = new ImageScalingParametersBean();

        final Properties imageProperties = getImageProperties(device, requestedResourcePath);
        result.setDeviceImagePercentWidth(getImagePercentWidthImageProperty(imageProperties));

        // Note that for some devices, the pixelsX value will include the
        // scrollbar, for others
        // it will not. Regardless, the only handle that can be used to control
        // the image size
        // is the width property.
        result.setDevicePixelWidth(device.getPixelsX());

        result.setOutputImageFormat(getOutputImageFormatParameter(device));

        return result;
    }

    private ScaledImageFactory.ImageFormat getOutputImageFormatParameter(final Device device) {

        final DeviceLocalExtension deviceLocalExtension = new DeviceLocalExtension(device);

        final ScaledImageFactory.ImageFormat preferredImageFormat
            = getPreferredImageFormat(deviceLocalExtension);
        if (preferredImageFormat != null) {
            return preferredImageFormat;

        } else if (deviceLocalExtension.isPngImageTypeSupported()) {
            return ScaledImageFactory.ImageFormat.PNG;

        } else if (deviceLocalExtension.isJpegImageTypeSupported()) {
            return ScaledImageFactory.ImageFormat.JPEG;

        } else {
            // We assume gif support as the default. This should be fine for all
            // devices that we care about.
            return ScaledImageFactory.ImageFormat.GIF;
        }
    }

    private ScaledImageFactory.ImageFormat getPreferredImageFormat(
            final DeviceLocalExtension deviceLocalExtension) {

        if (deviceLocalExtension.isPreferredImageTypePng()) {
            return ScaledImageFactory.ImageFormat.PNG;

        } else if (deviceLocalExtension.isPreferredImageTypeJpeg()) {
            return ScaledImageFactory.ImageFormat.JPEG;

        } else if (deviceLocalExtension.isPreferredImageTypeGif()) {
            return ScaledImageFactory.ImageFormat.GIF;

        } else {
            return null;
        }
    }

    private int getImagePercentWidthImageProperty(final Properties imageProperties) {
        // TODO: validation and handle both percent width and pixel width?
        final String rawPropertyValue = imageProperties
                .getProperty(WIDTH_PROPERTY_NAME);

        return Integer.parseInt(rawPropertyValue.replaceFirst("%\\s*$", StringUtils.EMPTY));
    }

    private Properties getImageProperties(final Device device, final String requestedResourcePath) {
        final String requestedPathExtesion =
                "." + FilenameUtils.getExtension(requestedResourcePath);

        final String requestedPathMinusExtension =
                StringUtils.removeEnd(requestedResourcePath, requestedPathExtesion);

        return getPropertiesLoader().loadProperties(device,
                requestedPathMinusExtension + PROPERTIES_FILE_EXTENSION);
    }

    private Resource createFoundResource(final String requestedResourcePath,
            final Device device, final Group group,
            final File foundFile) {

        final ImageScalingParametersBean scalingParametersBean
            = createImageScalingParametersBean(requestedResourcePath, device);

        final Image image =
                getScaledImageFactory().scaleImage(foundFile, createBaseTargetImageDir(foundFile),
                        scalingParametersBean);

        final ImageResourceBean resource =
                new ImageResourceBean(requestedResourcePath, getResourcePathRelativeTo(
                        image.getFile(), getRootResourcesDir()), getRootResourcesDir(), group);
        resource.setImageWidth(image.getPixelWidth());
        resource.setImageHeight(image.getPixelHeight());

        return resource;
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

    private void debugLogCheckingForImagesIn(final String newResourcesBasePath) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Checking for images in: '" + newResourcesBasePath + "'");
        }
    }

    /**
     * @return the propertiesLoader
     */
    private PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

    /**
     * @return the scaledImageFactory
     */
    private ScaledImageFactory getScaledImageFactory() {
        return scaledImageFactory;
    }

    /**
     * @param propertiesLoader the propertiesLoader to set
     */
    public void setPropertiesLoader(final PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
    }

    /**
     * @param scaledImageFactory the scaledImageFactory to set
     */
    public void setScaledImageFactory(final ScaledImageFactory scaledImageFactory) {
        this.scaledImageFactory = scaledImageFactory;
    }
}
