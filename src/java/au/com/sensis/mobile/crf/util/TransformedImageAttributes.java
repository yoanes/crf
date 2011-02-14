package au.com.sensis.mobile.crf.util;


/**
 * Encapsulates the attributes of a transformed image.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface TransformedImageAttributes {

    /**
     * @return {@link ImageAttributes} of the source image.
     */
    ImageAttributes getSourceImageAttributes();

    /**
     * @return {@link ImageAttributes} of the output/transformed image.
     */
    ImageAttributes getOutputImageAttributes();

}
