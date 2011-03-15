package au.com.sensis.mobile.crf.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import au.com.sensis.mobile.crf.exception.MinificationException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;


/**
 * Minifies JavaScript and CSS files using the YUI Compressor.
 *
 * @author Tony Filipe
 * @author Adrian.Koh2@sensis.com.au
 */
public class YUIMinifier implements Minifier {

    private static final Logger LOGGER = Logger.getLogger(YUIMinifier.class);

    private static final int DEFAULT_LINEBREAK_POS = -1;
    private static final boolean DEFAULT_OBFUSCATE = true;
    private static final boolean DEFAULT_PRESERVE_ALL_SEMICOLONS = false;
    private static final boolean DEFAULT_DISABLE_OPTIMIZATIONS = false;

    /**
     * Creates a new YUI JavaScriptCompressor for the file read by the given Reader.
     * @param in the input file reader
     * @return a new JavaScriptCompressor for the given file (reader)
     * @throws MinificationException if unable to create a JavaScriptCompressor for the given Reader
     */
    private JavaScriptCompressor createJavaScriptCompressor(final Reader in)
        throws MinificationException {

        try {
            final JavaScriptCompressor compressor = new JavaScriptCompressor(in,
                    new YUIJavaScriptErrorReporter());

            return compressor;

        } catch (final IOException e) {
            throw new MinificationException("Unable to create JavaScriptCompressor.", e);
        }
    }

    /**
     * Uses the YUI JavaScriptCompressor to minify the file represented by the
     * given Reader, writing out the result to the given inputFilename path.
     *
     * @param inputReader
     *            a file Reader for the CSS file to be minified
     * @param outputWriter
     *            a Writer for the minified output
     *
     * @throws MinificationException
     *             if unable to perform the minification
     */
    public void minifyJavaScript(final Reader inputReader, final Writer outputWriter)
            throws MinificationException {

        Writer out = null;

        try {
            final JavaScriptCompressor compressor = createJavaScriptCompressor(inputReader);

            out = new BufferedWriter(outputWriter);

            compressor.compress(out, DEFAULT_LINEBREAK_POS, DEFAULT_OBFUSCATE, isVerbose(),
                    DEFAULT_PRESERVE_ALL_SEMICOLONS, DEFAULT_DISABLE_OPTIMIZATIONS);

        } catch (final Exception e) {
            throw new MinificationException("Unable to perform JavaScript minification", e);
        } finally {
            closeReader(inputReader);
            closeWriter(out);
        }
    }

    private boolean isVerbose() {
        return LOGGER.isDebugEnabled();
    }

    /**
     * Creates a new YUI CssCompressor for the file read in by the given Reader.
     * @param in the file reader for the input file to be minified
     * @return a new YUI CssCompressor for the input file to be minified
     * @throws MinificationException if unabled to create the CssCompressor
     */
    private CssCompressor createCSSCompressor(final Reader in) throws MinificationException {

        try {
            final CssCompressor compressor = new CssCompressor(in);

            return compressor;

        } catch (final IOException e) {
            throw new MinificationException("Unable to create CssCompressor.", e);
        }
    }

    /**
     * Minifies the CSS file read in by the given Reader using the YUI
     * CssCompressor. The resulting output is written to the given
     * outputFilename.
     *
     * @param inputReader
     *            a file Reader for the CSS file to be minified
     * @param outputWriter
     *            a Writer for the minified output
     * @throws MinificationException
     *             if unable to perform the minification
     */
    @Override
    public void minifyCss(final Reader inputReader, final Writer outputWriter)
            throws MinificationException {

        Writer out = null;

        try {
            final CssCompressor compressor = createCSSCompressor(inputReader);

            out = new BufferedWriter(outputWriter);

            compressor.compress(out, DEFAULT_LINEBREAK_POS);

        } catch (final Exception e) {
            throw new MinificationException("Unable to perform CSS minification", e);
        } finally {
            closeReader(inputReader);
            closeWriter(out);
        }
    }

    private void closeReader(final Reader reader) throws MinificationException {

        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                throw new MinificationException("Couldn't close the input file reader.", e);
            }
        }
    }

    private void closeWriter(final Writer writer) throws MinificationException {

        if (writer != null) {
            try {
                writer.close();
            } catch (final IOException e) {
                throw new MinificationException("Couldn't close the output file writer.", e);
            }
        }
    }

    private class YUIJavaScriptErrorReporter implements ErrorReporter {

        public void warning(final String message, final String sourceName,
                final int line, final String lineSource, final int lineOffset) {
            if (line < 0) {
                LOGGER.debug(message);
            } else {
                LOGGER.warn(line + ':' + lineOffset + ':' + message);
            }
        }

        public void error(final String message, final String sourceName,
                final int line, final String lineSource, final int lineOffset) {
            if (line < 0) {
                LOGGER.error(message);
            } else {
                LOGGER.error(line + ':' + lineOffset + ':' + message);
            }
        }

        public EvaluatorException runtimeError(final String message,
                final String sourceName,
                final int line, final String lineSource, final int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }
}


