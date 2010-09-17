package au.com.sensis.mobile.crf.presentation.tag;

import java.util.List;

import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;


/**
 * Singleton factory to return a {@link ResourceSelectorScriptTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorScriptTagWriterFactory {
    private static ResourceSelectorScriptTagWriterFactory
        resourceSelectorScriptTagWriterFactorySingleton;

    static {
        restoreDefaultResourceSelectorScriptTagWriterFactorySingleton();
    }

    /**
     * @return the fileIoFacadeSingleton
     */
    public static ResourceSelectorScriptTagWriterFactory
        getResourceSelectorScriptTagWriterFactorySingleton() {
        return resourceSelectorScriptTagWriterFactorySingleton;
    }

    /**
     * Change the default {@link ResourceSelectorScriptTagWriterFactory}
     * singleton. Only to be called during unit testing.
     *
     * @param resourceSelectorScriptTagWriterFactorySingleton
     *            the {@link ResourceSelectorScriptTagWriterFactory} to use for
     *            unit testing.
     */
    public static void changeDefaultResourceSelectorScriptTagWriterFactorySingleton(
            final ResourceSelectorScriptTagWriterFactory
            resourceSelectorScriptTagWriterFactorySingleton) {
        ResourceSelectorScriptTagWriterFactory.resourceSelectorScriptTagWriterFactorySingleton =
            resourceSelectorScriptTagWriterFactorySingleton;
    }

    /**
     * Restore the default
     * {@link ResourceSelectorScriptTagWriterFactory} singleton.
     * Only to be called during unit testing.
     */
    public static void
        restoreDefaultResourceSelectorScriptTagWriterFactorySingleton() {
        // We actually instantiate a new instance since it contains no state.
        ResourceSelectorScriptTagWriterFactory.resourceSelectorScriptTagWriterFactorySingleton =
                new ResourceSelectorScriptTagWriterFactory();
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
     * @param scriptTagCollaboratorsMemento
     *            Singleton collaborators.
     * @return a new {@link ResourceSelectorScriptTagWriter}.
     */
    public ResourceSelectorTagWriter createResourceSelectorScriptTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes,
            final String href, final ScriptTagCollaboratorsMemento scriptTagCollaboratorsMemento) {
        return new ResourceSelectorScriptTagWriter(device, dynamicAttributes, href,
                scriptTagCollaboratorsMemento);
    }
}
