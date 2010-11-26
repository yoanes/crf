package au.com.sensis.mobile.crf.config;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Default {@link GroupsCacheKey} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsCacheKeyBean implements GroupsCacheKey {

    private static final long serialVersionUID = 1L;

    private final String userAgent;
    private final String uiConfigurationConfigPath;

    /**
     * Constructor.
     *
     * @param userAgent User agent.
     * @param uiConfigurationConfigPath Value of {@link UiConfiguration#getConfigPath()}.
     */
    public GroupsCacheKeyBean(final String userAgent, final String uiConfigurationConfigPath) {
        super();
        this.userAgent = userAgent;
        this.uiConfigurationConfigPath = uiConfigurationConfigPath;
    }


    /**
     * @return the userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @return the uiConfigurationConfigPath
     */
    public String getUiConfigurationConfigPath() {
        return uiConfigurationConfigPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        final GroupsCacheKeyBean rhs = (GroupsCacheKeyBean) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getUserAgent(), rhs.getUserAgent());
        equalsBuilder.append(getUiConfigurationConfigPath(), rhs
                .getUiConfigurationConfigPath());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getUserAgent());
        hashCodeBuilder.append(getUiConfigurationConfigPath());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("userAgent", getUserAgent());
        toStringBuilder.append("uiConfigurationConfigPath", getUiConfigurationConfigPath());
        return toStringBuilder.toString();
    }
}
