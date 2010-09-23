package au.com.sensis.mobile.crf.presentation.tag;

import java.util.List;

import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;


/**
 * Singleton factory to return a {@link LinkTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkTagWriterFactory {
    private static LinkTagWriterFactory singletonInstance;

    static {
        restoreDefaultLinkTagWriterFactorySingleton();
    }

    /**
     * @return the {@link LinkTagWriterFactory} singleton instance.
     */
    public static LinkTagWriterFactory getSingletonInstance() {
        return singletonInstance;
    }

    /**
     * Change the default {@link LinkTagWriterFactory} singleton. Only to be
     * called during unit testing.
     *
     * @param singletonInstance
     *            the {@link LinkTagWriterFactory} to use for unit testing.
     */
    public static void changeDefaultLinkTagWriterFactorySingleton(
            final LinkTagWriterFactory singletonInstance) {
        LinkTagWriterFactory.singletonInstance = singletonInstance;
    }

    /**
     * Restore the default {@link LinkTagWriterFactory} singleton. Only to be
     * called during unit testing.
     */
    public static void restoreDefaultLinkTagWriterFactorySingleton() {
        // We actually instantiate a new instance since it contains no state.
        LinkTagWriterFactory.singletonInstance = new LinkTagWriterFactory();
    }

    /**
     * Factory method for {@link LinkTagWriter}. This level of
     * indirection is only used to facilitate unit testing.
     *
     * @param device
     *            {@link Device} of the current request.
     * @param dynamicAttributes
     *            List of {@link DynamicTagAttribute}s containing dynamic JSP
     *            tag attributes to be written out.
     * @param href
     *            Href attribute of the tag to be written.
     * @param linkTagDependencies
     *            Singleton collaborators.
     * @return a new {@link LinkTagWriter}.
     */
    public LinkTagWriter createLinkTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes,
            final String href, final LinkTagDependencies linkTagDependencies) {
        return new LinkTagWriter(device, dynamicAttributes, href,
                linkTagDependencies);
    }
}
