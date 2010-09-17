package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Simple nterface for loading a {@link Properties} file.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
//TODO: move to util package.
public interface PropertiesLoader {

    /**
     * Loads the {@link Properties} from the given file. If the file does not
     * exist, an empty Properties file is returned.
     *
     * @param file
     *            File to load the {@link Properties} from.
     * @return the {@link Properties} loaded from the given file. If the file
     *         does not exist, an empty Properties file is returned. May not be
     *         null.
     * @throws IOException
     *             Thrown if there was an error reading the file.
     */
    Properties loadPropertiesNotNull(File file) throws IOException;
}
