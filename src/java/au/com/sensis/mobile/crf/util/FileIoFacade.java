package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Facade to file query/manipulation methods. Facilitates unit testing.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface FileIoFacade {

    /**
     * @param path File path to test.
     * @return true if the given path exists as a file.
     */
    boolean fileExists(String path);

    /**
     * @param parentDirectory Directory in which to check for path.
     * @param path File path to test.
     * @return true if the given path exists as a file in the given parentDirectory.
     */
    boolean fileExists(File parentDirectory, String path);

    /**
     * @param file File to test.
     * @return true if the given file exists.
     */
    boolean fileExists(File file);

    /**
     * @param dir Directory to test.
     * @return true if the given dir is indeed a directory.
     */
    boolean isDirectory(File dir);

    /**
     * Returns a list of files in the given parentDirectory that match the given
     * path and given set of extensions. As such, the last component of path is
     * assumed to be the stem of a file. eg. if path is
     * "default/common/unmetered", the "unmetered" is assumed to be the stem of
     * a file. Extensions may contain the "*" wildcard character.
     *
     * <b>
     * Note that each extension is prefixed with "." before testing occurs.
     * Therefore, in the above example, default/common/unmetered will never
     * be matched itself even if it exists.
     * </b>
     *
     *
     * @param parentDirectory
     *            Directory in which to check for path.
     * @param path
     *            File path to test.
     * @param extensions
     *            Set of allowed extensions for path to have. Assumed not to start with a
     *            ".".
     * @return a list of files in the given parentDirectory that match the given
     *         path and given set of extensions. Extensions may contain the "*"
     *         wildcard character. May not be null.
     */
    File[] list(File parentDirectory, String path, String[] extensions);

    /**
     * Similar to {@link #list(File, String, String[])} but also supports an
     * array of extension patterns to be excluded.
     *
     * @param parentDirectory
     *            Directory in which to check for path.
     * @param path
     *            File path to test.
     * @param matchedExtensions
     *            Set of allowed extensions for path to have. Assumed not to
     *            start with a ".".
     * @param excludedExtensions
     *            Set of disallowed extensions for path to have. Assumed not to
     *            start with a ".".
     * @return a list of files in the given parentDirectory that match the given
     *         path and given set of matchedExtensions and do not match the
     *         excludedExtensions. Extensions may contain the "*" wildcard
     *         character. May not be null.
     */
    File[] list(File parentDirectory, String path, String[] matchedExtensions,
            String[] excludedExtensions);

    /**
     *
     * Returns a list of files in the given directory that match the given
     * wildcard patterns.
     *
     * @param directory
     *            Directory in which to find the files.
     * @param wildcards
     *            Wildcard patterns to use for matching files. Uses the
     *            characters '?' and '*' to represent a single or multiple
     *            wildcard characters.
     * @return a list of files in the given directory that match the given
     *         wildcard patterns. May not be null.
     */
    File[] list(File directory, String[] wildcards);

    /**
     *
     * Returns a list of files in the given directory that match the given
     * file filter.
     *
     * @param directory
     *            Directory in which to find the files.
     * @param fileFilter
     *            {@link FileFilter} to use for matching files.
     * @return a list of files in the given directory that match the given
     *         {@link FileFilter}. May not be null.
     */
    File[] list(File directory, FileFilter fileFilter);

    /**
     *
     * Returns a list of files in the given directory that match any of the given
     * filenameWildcardPatterns. Sub-directories will optionally be recursed into as well
     * if dirnameWildcardPatterns is not empty.
     *
     * @param directory
     *            Directory in which to find the files.
     * @param filenameWildcardPatterns
     *            Wildcard patterns to use for matching files.
     * @param dirnameWildcardPatterns
     *            Wildcard pattrens to use for recursing into directories.
     * @return a list of files in the given directory that match the given
     *         patterns. May not be null.
     */
    File[] listByFilenameAndDirnameWildcardPatterns(File directory,
            String [] filenameWildcardPatterns, String [] dirnameWildcardPatterns);

    /**
     * Writes the given inputFile to the outputStream, then closes the outputStream.
     *
     * @param inputFile Input file to be written.
     * @param outputStream Stream to write the file to.
     * @throws IOException Thrown if any error occurs.
     */
    void writeFileAndCloseStream(File inputFile, OutputStream outputStream)
        throws IOException;

    /**
     * Invokes {@link File#mkdirs()} on the given argument.
     * @param dir Directory to invoke {@link File#mkdirs()} on.
     * @throws IOException Thrown if {@link File#mkdirs()} returns false.
     */
    void mkdirs(File dir) throws IOException;
}
