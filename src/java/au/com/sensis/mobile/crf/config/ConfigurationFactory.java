package au.com.sensis.mobile.crf.config;


/**
 * Factory for getting a {@link UiConfiguration}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ConfigurationFactory {

    /**
     * Returns the {@link UiConfiguration} that applies to the requested path.
     *
     * @param requestedResourcePath
     *            Path of the resource requested.
     *
     * @return {@link UiConfiguration} that applies to the requested path.
     */
    UiConfiguration getUiConfiguration(String requestedResourcePath);
}
