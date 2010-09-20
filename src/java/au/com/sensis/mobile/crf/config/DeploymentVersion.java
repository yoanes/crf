package au.com.sensis.mobile.crf.config;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Holder of deployment version info.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DeploymentVersion {

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

    private Platform platform;

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
     * @return the platform
     */
    private Platform getPlatform() {
        return platform;
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(final Platform platform) {
        this.platform = platform;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("platform", getPlatform());
        return toStringBuilder.toString();
    }
}
