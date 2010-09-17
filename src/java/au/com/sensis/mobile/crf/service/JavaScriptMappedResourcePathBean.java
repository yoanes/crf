package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptMappedResourcePathBean extends MappedResourcePathBean {

    /**
     * Callback interface to be passed to {@link #expandPath(MappedResourcePath)}.
     */
    public static interface PathExpander {

        /**
         * Expand the given {@link MappedResourcePath} into a list of
         * {@link File}s.
         *
         * @param mappedResourcePath
         *            Callback {@link MappedResourcePath}.
         * @return List of {@link File}s dervived from the given
         *         {@link MappedResourcePath}.
         * @throws IOException Thrown if any IO error occurs during the expansion.
         */
        List<File> expandPath(MappedResourcePath mappedResourcePath) throws IOException;
    }

    private PathExpander pathExpander;

    // TODO: temp hack for testing. PathExpander will be refactored into oblivion, anyway.
    /**
     * @param pathExpander the PathExpander.
     */
    public void setPathExpander(final PathExpander pathExpander) {
        this.pathExpander = pathExpander;
    }

    /**
     * Default constructor.
     *
     * @param originalResourcePath
     *            Original path that was requested.
     * @param newResourcePath
     *            New path that originalResourcePath was mapped to.
     * @param rootResourceDir
     *            Root directory which the newResourcePath is relative to.
     */
    public JavaScriptMappedResourcePathBean(final String originalResourcePath,
            final String newResourcePath, final File rootResourceDir) {
        super(originalResourcePath, newResourcePath, rootResourceDir);

        pathExpander =
                new JavaScriptBundlePathExpander(new PropertiesLoaderBean(),
                        "bundles.properties", "order");
    }

    private PathExpander getPathExpander() {
        return pathExpander;
    }

    /**
     * Returns a list of {@link MappedResourcePath}s that exist in
     * {@link #getRootResourceDir()} based on this {@link MappedResourcePath}
     * and the given {@link PathExpander}.
     *
     * @return list of {@link MappedResourcePath}s that exist in
     *         {@link #getRootResourceDir()} based on this
     *         {@link MappedResourcePath} and the given {@link PathExpander}.
     *         May not be null. Empty indicates no matches exist.
     * @throws IOException
     *             Thrown if any IO error occurs during the expansion.
     */
    @Override
    public List<MappedResourcePath> existByExpansion() throws IOException {
        // TODO: hack implementation. Really testing the difference between
        // requesting a named JS file and a *.js type of grouping.
        if (isBundlePath() && getNewResourcePath().endsWith("bundle-all.js")) {
            // TODO: possibly cache the result since we are accessing the file
            // system?
            final List<MappedResourcePath> result =
                    new ArrayList<MappedResourcePath>();

            final List<File> foundFiles = getPathExpander().expandPath(this);
            if (foundFiles != null) {
                for (final File file : foundFiles) {
                    final MappedResourcePath mappedResourcePath =
                            new MappedResourcePathBean(
                                    getOriginalResourcePath(),
                                    getRootResourceDirRelativePath(file),
                                    getRootResourceDir());
                    result.add(mappedResourcePath);
                }
            }

            return result;
        } else {
            return Arrays.asList((MappedResourcePath) this);
        }

    }

    private String getRootResourceDirRelativePath(final File file) {
        final String rootResourceDirRelativePath = StringUtils.substringAfter(file.getPath(),
                getRootResourceDir().getPath()).replace(File.separator, SEPARATOR);
        return StringUtils.substringAfter(rootResourceDirRelativePath, SEPARATOR);
    }
}
