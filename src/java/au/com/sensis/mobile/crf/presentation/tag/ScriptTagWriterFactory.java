package au.com.sensis.mobile.crf.presentation.tag;

import java.util.List;

import au.com.sensis.devicerepository.Device;


/**
 * Singleton factory to return a {@link ScriptTagWriter}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptTagWriterFactory {
    private static ScriptTagWriterFactory singletonInstance;

    static {
        restoreDefaultScriptTagWriterFactorySingleton();
    }

    /**
     * @return the fileIoFacadeSingleton
     */
    public static ScriptTagWriterFactory getSingletonInstance() {
        return singletonInstance;
    }

    /**
     * Change the default {@link ScriptTagWriterFactory} singleton. Only to be
     * called during unit testing.
     *
     * @param singletonInstance
     *            the {@link ScriptTagWriterFactory} to use for unit testing.
     */
    public static void changeDefaultScriptTagWriterFactorySingleton(
            final ScriptTagWriterFactory singletonInstance) {
        ScriptTagWriterFactory.singletonInstance = singletonInstance;
    }

    /**
     * Restore the default {@link ScriptTagWriterFactory} singleton. Only to be
     * called during unit testing.
     */
    public static void restoreDefaultScriptTagWriterFactorySingleton() {
        // We actually instantiate a new instance since it contains no state.
        ScriptTagWriterFactory.singletonInstance =
                new ScriptTagWriterFactory();
    }

    /**
     * Factory method for {@link ScriptTagWriter}. This level of
     * indirection is only used to facilitate unit testing.
     *
     * @param device
     *            {@link Device} of the current request.
     * @param dynamicAttributes
     *            List of {@link DynamicTagAttribute}s containing dynamic JSP
     *            tag attributes to be written out.
     * @param href
     *            href attribute of the tag to be written.
     * @param name
     *            name attribute of the tag.
     * @param scriptTagDependencies
     *            Singleton collaborators.
     * @param parentBundleScriptsTag Parent {@link BundleTag} of the calling tag.
     * @return a new {@link ScriptTagWriter}.
     */
    public TagWriter createScriptTagWriter(
            final Device device,
            final List<DynamicTagAttribute> dynamicAttributes,
            final String href, final String name,
            final ScriptTagDependencies scriptTagDependencies,
            final BundleTag parentBundleScriptsTag) {
        return new ScriptTagWriter(device, dynamicAttributes, href, name,
                scriptTagDependencies, parentBundleScriptsTag);
    }
}
