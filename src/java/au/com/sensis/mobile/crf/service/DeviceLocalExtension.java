package au.com.sensis.mobile.crf.service;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Local extension of {@link Device} via delegation.
 * @author Adrian.Koh2@sensis.com.au
 *
 */
// TODO: move into Device proper (and hence into sdpcommon). Leaving here for now until code
// is more stable.
public class DeviceLocalExtension {

    private static final String GIF_IN_PAGE = "gifinpage";
    private static final String PNG_IN_PAGE = "pnginpage";
    private static final String JPEG_IN_PAGE = "jpeginpage";
    private static final String PREFERRED_IMAGE_TYPE = "preferredimagetype";
    private final Device device;

    /**
     * @param device Device to extend.
     */
    public DeviceLocalExtension(final Device device) {
        this.device = device;
    }

    /**
     * @return true if the device's preferred image type is gif.
     */
    public boolean isPreferredImageTypeGif() {
        return "image/gif".equalsIgnoreCase(getDevice().getPropertyAsString(PREFERRED_IMAGE_TYPE));
    }

    /**
     * @return true if the device's preferred image type is png.
     */
    public boolean isPreferredImageTypePng() {
        return "png".equalsIgnoreCase(getDevice().getPropertyAsString(PREFERRED_IMAGE_TYPE));
    }

    /**
     * @return true if the device's preferred image type is jpeg.
     */
    public boolean isPreferredImageTypeJpeg() {
        return "jpeg".equalsIgnoreCase(getDevice().getPropertyAsString(PREFERRED_IMAGE_TYPE));
    }

    /**
     * @return true if the device supports the gif image type.
     */
    public boolean isGifImageTypeSupported() {
        return "gif".equalsIgnoreCase(getDevice().getPropertyAsString(GIF_IN_PAGE));
    }

    /**
     * @return true if the device supports the png image type.
     */
    public boolean isPngImageTypeSupported() {
        return "image/png".equalsIgnoreCase(getDevice().getPropertyAsString(PNG_IN_PAGE));
    }

    /**
     * @return true if the device supports the jpeg image type.
     */
    public boolean isJpegImageTypeSupported() {
        return "image/jpeg".equalsIgnoreCase(getDevice().getPropertyAsString(JPEG_IN_PAGE));
    }

    /**
     * @return the device
     */
    private Device getDevice() {
        return device;
    }

}
