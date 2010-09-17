package au.com.sensis.mobile.crf.config;


/**
 * Factory for getting a {@link UiConfiguration}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ConfigurationFactory {

    /**
     * Returns the (possibly cached) {@link UiConfiguration}.
     *
     * @return {@link UiConfiguration} (possibly cached).
     */
    UiConfiguration getUiConfiguration();
}
