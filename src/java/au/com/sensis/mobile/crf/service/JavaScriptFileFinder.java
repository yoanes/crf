package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Simple interface for finding JavaScript files in a directory whilst preserving the dependency order
 * of those files.
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
    List<File> findFiles(File dir) throws IOException;
}
