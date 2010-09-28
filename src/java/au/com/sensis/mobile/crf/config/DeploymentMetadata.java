package au.com.sensis.mobile.crf.config;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Holder of deployment metadata for the currently deployed app.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DeploymentMetadata {

    /**
     * Flags indicating the deployment platform.
     */
    public static enum Platform {
        /**
         * Development platform.
         */
        DEVELEOPMENT,

        /**
         * Production platform.
         */
        PRODUCTION
    }

    private final Platform platform;
    private final String version;

    /**
     * Constructor.
     *
     * @param platform Platform that the deployment is on.
     * @param version Version of the deployment.
     */
    public DeploymentMetadata(final Platform platform, final String version) {
        this.platform = platform;
        this.version = version;
    }

    /**
     * @return true if the current platform is development.
     */
    public boolean isDevPlatform() {
        return Platform.DEVELEOPMENT.equals(getPlatform());
    }

    /**
     * @return true if the current platform is production.
     */
    public boolean isProdPlatform() {
        return Platform.PRODUCTION.equals(getPlatform());
    }

    /**
     * @return Platform that the deployment is on.
     */
    private Platform getPlatform() {
        return platform;
    }


    /**
     * @return Version of the deployment.
     */
    public String getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("platform", getPlatform());
        toStringBuilder.append("version", getVersion());
        return toStringBuilder.toString();
    }
}
