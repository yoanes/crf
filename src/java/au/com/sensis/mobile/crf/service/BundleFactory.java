package au.com.sensis.mobile.crf.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.exception.MinificationException;
import au.com.sensis.mobile.crf.util.Minifier;
import au.com.sensis.mobile.crf.util.YUIMinifier;


/**
 * Factory for creating bundles, (i.e. concatenating multiple resource files into a single file).
 *
 * @author Tony Filipe
 */
public class BundleFactory {

    private static final Logger LOGGER = Logger.getLogger(BundleFactory.class);
    private boolean willDoMinification = true;
    private final String bundleDir = "bundle/";
    private final Minifier minifier = new YUIMinifier();

    /**
     * Gets a {@link Resource} formed from bundling the contents of all of the given files.
     *
     * @param resourcePathsToInclude
     *            {@link Resource} of each file to include in the bundle.
     * @return {@link Resource} for the bundle.
     * @throws IOException if unable to read resource files or write out the bundle
     */
    public Resource getBundle(final List<Resource> resourcePathsToInclude) throws IOException {

        if (resourcePathsToInclude.isEmpty()) {
            return null;
        }

        final Resource lastResource = getLastResource(resourcePathsToInclude);

        final String bundleFilename = determineBundleFilename(lastResource);

        writeToFile(bundleFilename, readSourceFiles(resourcePathsToInclude));

        doMinification(bundleFilename);

        // make Resource for bundle
        return createBundleResourceFrom(lastResource);
    }

    private String readSourceFiles(final List<Resource> resourcePathsToInclude) throws IOException {

        final StringBuilder combinedContent = new StringBuilder();

        for (final Resource resource : resourcePathsToInclude) {

            final File file = resource.getNewFile();

            readFileInto(file, combinedContent);
        }

        return combinedContent.toString();
    }

    /**
     * Reads the text of the given {@link File} into the given {@link StringBuilder}.
     * @param file containing the text to read in
     * @param content the {@link StringBuilder} to which the content will be appended
     * @throws IOException if unable to read from file
     */
    private void readFileInto(final File file, final StringBuilder content) throws IOException {

        final BufferedReader in = new BufferedReader(new FileReader(file));

        String line;
        try {
            while ((line = in.readLine()) != null) {
                content.append(line);
                // preserve newlines in case there are single-line comments in the file
                // Note: if minification is enabled newlines and comments will be removed later
                content.append("\n");
            }
        } finally {
            in.close();
        }
    }

    /**
     * Writes the given content input string to the file at filename.
     * @param filename the full path to the filename to be written to
     * @param content to be written to file
     * @throws IOException if unable to write to file
     */
    private void writeToFile(final String filename, final String content) throws IOException {

        if (!StringUtils.isEmpty(filename)) {

            BufferedWriter bufferedWriter = null;

            try {

                final int lastSeparator = getIndexOfLastPathSeparator(filename);
                // Create the bundle directory (if it doesn't exist)
                new File(filename.substring(0, lastSeparator)).mkdirs();
                // Create the empty bundle filename (if it doesn't exist)
                new File(filename).createNewFile();

                bufferedWriter = new BufferedWriter(new FileWriter(filename));
                bufferedWriter.write(content);
                LOGGER.debug("The bundle file has been written out to: " + filename);

            } finally {

                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.flush();
                        bufferedWriter.close();
                    }
                } catch (final IOException e) {
                    LOGGER.error("Unable to close the BufferedWriter. ", e);
                }
            }
        }
    }


    /**
     * Determines the filename to use for the bundle containing the given Resource.
     * @param lastResource the Resource to base the bundle filename on
     * @return a full file path for the bundle
     */
    private String determineBundleFilename(final Resource lastResource) {

        final StringBuilder bundleFilePath = new StringBuilder();
        bundleFilePath.append(lastResource.getRootResourceDir());
        bundleFilePath.append("/");
        bundleFilePath.append(createBundleRelativeFilepath(lastResource));

        return bundleFilePath.toString();
    }

    private Resource getLastResource(final List<Resource> resourcePathsToInclude) {

        if (resourcePathsToInclude.isEmpty()) {
            return null;
        }

        return resourcePathsToInclude.get(resourcePathsToInclude.size() - 1);
    }

    /**
     * Creates a path to the bundle relative to the rootResourceDir (i.e. that relative path
     * doesn't include the root resource directory path).
     * @param resource from which to base the file path on
     * @return a file path for the bundle, relative to the root resource directory
     */
    private String createBundleRelativeFilepath(final Resource resource) {

        final String resourcePath = resource.getNewPath();

        final int lastSeparator = getIndexOfLastPathSeparator(resourcePath);

        final StringBuilder bundleFilePath = new StringBuilder();
        // append the original file path (without the file name)
        bundleFilePath.append(resourcePath.substring(0, lastSeparator + 1));
        // insert bundle path
        bundleFilePath.append(bundleDir);
        // append the new bundle file name
        bundleFilePath.append(createBundleFilename(resource, lastSeparator + 1));

        return bundleFilePath.toString();
    }

    /**
     * Creates an appropriate bundle filename from the given {@link Resource}.
     *
     * @param resource from which to derive the bundle name from
     * @param indexOfFilenameInPath the index of the start of the filename in the {@link Resource}'s
     *  getNewPath() method.
     * @return the name to be used for the bundle file.
     */
    protected String createBundleFilename(final Resource resource,
            final int indexOfFilenameInPath) {

        return resource.getNewPath().substring(indexOfFilenameInPath);
    }

    private Resource createBundleResourceFrom(final Resource lastResource) {

        return new ResourceBean(lastResource.getOriginalPath(),
                createBundleRelativeFilepath(lastResource),
                lastResource.getRootResourceDir(), lastResource.getGroup());
    }

    private int getIndexOfLastPathSeparator(final String path) {

        int lastSeparator = path.lastIndexOf("/");
        if (lastSeparator < 0) {
            lastSeparator = path.lastIndexOf("\\");
        }

        return lastSeparator;
    }

    private void doMinification(final String bundleFilename) {

        if (shouldDoMinification()) {
            try {
                minifier.minify(bundleFilename);
            } catch (final MinificationException e) {
                // The exception is logged and swallowed because we can still proceed with
                // an un-minified bundle.
                LOGGER.error("Failed to perform minification for bundle '"
                        + bundleFilename + "'" + e);
            }
        }
    }

    /**
     * @return the willDoMinification
     */
    boolean shouldDoMinification() {

        return willDoMinification;
    }

    /**
     * @param willDoMinification  the willDoMinification to set
     */
    void setDoMinification(final boolean willDoMinification) {

        this.willDoMinification = willDoMinification;
    }
}
