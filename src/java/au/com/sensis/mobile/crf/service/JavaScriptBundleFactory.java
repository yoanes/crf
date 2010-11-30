package au.com.sensis.mobile.crf.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Factory for creating JavaScript bundles, (i.e. concatenating multiple JavaScript files
 * into a single bundle file).
 *
 * @author Tony Filipe
 */
public class JavaScriptBundleFactory extends BundleFactory {

    private static final Logger LOGGER = Logger.getLogger(JavaScriptBundleFactory.class);
    private final String packageFilename;
    private final String javascriptPackageKeyword;

    /**
     * Constructs the JavaScriptBundleFactory with JavaScript specific configuration.
     * @param javascriptPackageKeyword the keyword configured to represent a JavaScript package
     * @param packageFilename the file name to use for a package bundle.
     */
    public JavaScriptBundleFactory(final String javascriptPackageKeyword,
            final String packageFilename) {

        validateJavascriptPackageKeyword(javascriptPackageKeyword);
        validateJavascriptPackageFilename(packageFilename);

        this.javascriptPackageKeyword = javascriptPackageKeyword;
        this.packageFilename = packageFilename;
    }


    /**
     * If the requested resource was a JavaScript package then the file name configured in
     * packageFilename is returned. Otherwise the filename returned is the same as the single
     * JavaScript file requested.
     *
     * @param resource from which to create the new bundle file name
     * @param indexOfFilenameInPath the index of the start of the filename in the {@link Resource}'s
     *  getNewPath() method.
     * @return the name to be used for the bundle file.
     */
    @Override
    protected String createBundleFilename(final Resource resource,
            final int indexOfFilenameInPath) {

        if (isPackageRequested(resource.getOriginalPath())) {

            LOGGER.debug("Using the package file name, " + packageFilename);

            return packageFilename;
        }

        return resource.getNewPath().substring(indexOfFilenameInPath);
    }

    private void validateJavascriptPackageKeyword(final String javascriptPackageKeyword) {

        if (StringUtils.isEmpty(javascriptPackageKeyword)) {
            throw new IllegalArgumentException("javascriptPackageKeyword must not be empty");
        }
    }

    private void validateJavascriptPackageFilename(final String packageFilename) {

        if (StringUtils.isEmpty(packageFilename)) {
            throw new IllegalArgumentException("packageFilename must not be empty");
        }
    }

    private boolean isPackageRequested(final String requestedResourcePath) {
        return requestedResourcePath.endsWith(getJavascriptPackageKeyword());
    }

    /**
     * @return the javascriptPackageKeyword
     */
    private String getJavascriptPackageKeyword() {

        return javascriptPackageKeyword;
    }
}
