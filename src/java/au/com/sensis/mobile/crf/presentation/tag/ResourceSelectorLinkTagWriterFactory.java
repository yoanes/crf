package au.com.sensis.mobile.crf.presentation.tag;

import java.util.List;

import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;


/**
 * Singleton factory to return a {@link ResourceSelectorLinkTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorLinkTagWriterFactory {
    private static ResourceSelectorLinkTagWriterFactory
        resourceSelectorLinkTagWriterFactorySingleton;

    static {
        restoreDefaultResourceSelectorLinkTagWriterFactorySingleton();
    }

    /**
     * @return the fileIoFacadeSingleton
     */
    public static ResourceSelectorLinkTagWriterFactory
        getResourceSelectorLinkTagWriterFactorySingleton() {
        return resourceSelectorLinkTagWriterFactorySingleton;
    }

    /**
     * Change the default {@link ResourceSelectorLinkTagWriterFactory}
     * singleton. Only to be called during unit testing.
     *
     * @param resourceSelectorLinkTagWriterFactorySingleton
     *            the {@link ResourceSelectorLinkTagWriterFactory} to use for
     *            unit testing.
     */
    public static void changeDefaultResourceSelectorLinkTagWriterFactorySingleton(
            final ResourceSelectorLinkTagWriterFactory
                resourceSelectorLinkTagWriterFactorySingleton) {
        ResourceSelectorLinkTagWriterFactory.resourceSelectorLinkTagWriterFactorySingleton =
                resourceSelectorLinkTagWriterFactorySingleton;
    }

    /**
     * Restore the default
     * {@link ResourceSelectorLinkTagWriterFactory} singleton.
     * Only to be called during unit testing.
     */
    public static void
        restoreDefaultResourceSelectorLinkTagWriterFactorySingleton() {
        // We actually instantiate a new instance since it contains no state.
        ResourceSelectorLinkTagWriterFactory.resourceSelectorLinkTagWriterFactorySingleton =
                new ResourceSelectorLinkTagWriterFactory();
    }

    /**
     * Factory method for {@link ResourceSelectorLinkTagWriter}. This level of
     * indirection is only used to facilitate unit testing.
     *
     * @param device
     *            {@link Device} of the current request.
     * @param dynamicAttributes
     *            List of {@link DynamicTagAttribute}s containing dynamic JSP
     *            tag attributes to be written out.
     * @param href
     *            Href attribute of the tag to be written.
     * @param linkTagCollaboratorsMemento
     *            Singleton collaborators.
     * @return a new {@link ResourceSelectorLinkTagWriter}.
     */
    public ResourceSelectorLinkTagWriter createResourceSelectorLinkTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes,
            final String href, final LinkTagCollaboratorsMemento linkTagCollaboratorsMemento) {
        return new ResourceSelectorLinkTagWriter(device, dynamicAttributes, href,
                linkTagCollaboratorsMemento);
    }
}
