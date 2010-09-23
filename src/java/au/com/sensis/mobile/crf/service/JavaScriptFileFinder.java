package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Callback interface to be passed to {@link #findJavaScriptFiles(Resource)}.
 */
public interface JavaScriptFileFinder {

    /**
     * Find all JavaScript files in the given directory, sorted according to the dependencies of
     * the files.
     *
     * @param dir Directory to find the files in.
     * @return all JavaScript files in the given directory, sorted according to the dependencies of
     * the files.
     * @throws IOException Thrown if any IO error occurs.
     */
    List<File> findJavaScriptFiles(File dir) throws IOException;
}
