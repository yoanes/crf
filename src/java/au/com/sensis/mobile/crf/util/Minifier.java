package au.com.sensis.mobile.crf.util;

import java.io.Reader;
import java.io.Writer;

import au.com.sensis.mobile.crf.exception.MinificationException;


/**
 * Minifies (compresses and obfuscates) the content of a given JavaScript or CSS file.
 *
 * @author Tony Filipe
 * @author Adrian.Koh2@sensis.com.au
 */
public interface Minifier {

    /**
     * Minifies the CSS contents from the reader and writes them to the writer.
     *
     * @param inputReader
     *            Reader to minify contents from.
     * @param outputWriter
     *            Writer to write minified contents to.
     * @throws MinificationException
     *             Thrown if there is any error during minification.
     */
    void minifyCss(final Reader inputReader, Writer outputWriter) throws MinificationException;

    /**
     * Minifies the JavaScript contents from the reader and writes them to the
     * writer.
     *
     * @param inputReader
     *            Reader to minify contents from.
     * @param outputWriter
     *            Writer to write minified contents to.
     * @throws MinificationException
     *             Thrown if there is any error during minification.
     */
    void minifyJavaScript(final Reader inputReader, Writer outputWriter)
            throws MinificationException;
}
