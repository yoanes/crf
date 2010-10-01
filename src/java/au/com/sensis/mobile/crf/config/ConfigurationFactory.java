package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.exception.ConfigurationRuntimeException;


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
     * @throws ConfigurationRuntimeException Thrown if any error occurs.
     */
    UiConfiguration getUiConfiguration(String requestedResourcePath)
        throws ConfigurationRuntimeException;
}
