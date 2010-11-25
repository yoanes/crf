package au.com.sensis.mobile.crf.service;



/**
 * Constructs new {@link ResourceAccumulator}s for each of the different {@link ResourceResolver}s.
 *
 * @author Tony Filipe
 */
public class ResourceAccumulatorFactory {

    private final boolean bundlingEnabled;


    /**
     * Constructs an initialised ResourceAccumulatorFactory.
     * @param bundlingEnabled determines whether or not the returned {@link ResourceAccumulator}
     * should perform bundling.
     */
    public ResourceAccumulatorFactory(final boolean bundlingEnabled) {

        this.bundlingEnabled = bundlingEnabled;
    }

    /**
     * @return a {@link ResourceAccumulator} implementation appropriate for use by the
     * {@link PropertiesResourceResolverBean}.
     */
    public ResourceAccumulator getPropertiesResourceAccumulator() {

        return makeNewResourceAccumulator();
    }

    /**
     * Returns a {@link ResourceAccumulator} implementation suitable for use by the
     * {@link JavaScriptResourceResolverBean}.
     *
     * @param packageKeyword the special keyword used for denoting a Javascript package request.
     * @return a {@link ResourceAccumulator} implementation suitable for use by the
     *      {@link JavaScriptResourceResolverBean}
     */
    public ResourceAccumulator getJavaScriptResourceAccumulator(final String packageKeyword) {

        return makeNewJavascriptResourceAccumulator(packageKeyword);
    }

    /**
     * Returns a {@link ResourceAccumulator} implementation suitable for use by the
     * {@link JavaScriptResourceResolverBean}, according to whether or not bundling is enabled.
     *
     * @return a {@link ResourceAccumulator} implementation suitable for use by the
     *      {@link JavaScriptResourceResolverBean}
     */
    public ResourceAccumulator getCSSResourceAccumulator() {

        if (isBundlingEnabled()) {

            return makeNewBundleResourceAccumulator();
        }

        return makeNewResourceAccumulator();
    }

    /**
     * Constructs a new {@link JavaScriptResourceAccumulatorBean}.
     *
     * @param packageKeyword the special keyword used for denoting a Javascript package request.
     * @return a new {@link JavaScriptResourceAccumulatorBean}.
     */
    protected JavaScriptResourceAccumulatorBean makeNewJavascriptResourceAccumulator(
            final String packageKeyword) {

        return new JavaScriptResourceAccumulatorBean(packageKeyword, isBundlingEnabled());
    }

    /**
     * @return a new {@link BundleResourceAccumulatorBean}.
     */
    protected BundleResourceAccumulatorBean makeNewBundleResourceAccumulator() {
        return new BundleResourceAccumulatorBean();
    }

    /**
     * @return a new {@link ResourceAccumulatorBean}.
     */
    protected ResourceAccumulatorBean makeNewResourceAccumulator() {
        return new ResourceAccumulatorBean();
    }

    /**
     * @return true if the {@link ResourceAccumulator}s returned by the factory
     *         should support bundling. False otherwise.
     */
    public boolean isBundlingEnabled() {
        return bundlingEnabled;
    }
}
