package au.com.sensis.mobile.crf.util;

import au.com.sensis.mobile.crf.exception.MinificationException;


/**
 * Minifies (compresses and obfuscates) the content of a given JavaScript or CSS file.
 *
 * @author Tony Filipe
 */
public interface Minifier {

    /**
     * Minifies the contents of the given filename.
     * @param inputFilename the full path to the file to be minified.
     * @throws MinificationException if unable to perform the minification on the file specified.
     */
    void minify(final String inputFilename) throws MinificationException;
}
