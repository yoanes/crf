package au.com.sensis.mobile.crf.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
 */
public class YUIMinifier implements Minifier {

    private static final Logger LOGGER = Logger.getLogger(YUIMinifier.class);

    private static final int DEFAULT_LINEBREAK_POS = -1;
    private static final boolean DEFAULT_OBFUSCATE = true;
    private static final boolean DEFAULT_PRESERVE_ALL_SEMICOLONS = false;
    private static final boolean DEFAULT_DISABLE_OPTIMIZATIONS = false;
    private boolean verbose;

    /**
     * {@inheritDoc}
     */
    @Override
    public void minify(final String inputFilename) throws MinificationException {

        final String type = getFileType(inputFilename);
        validateFileType(type);

        if (LOGGER.isDebugEnabled()) {
            verbose = true;
        }

        Reader in = null;

        try {
            in = new BufferedReader(new FileReader(inputFilename));

            if (isJavaScript(type)) {

                minifyJavaScript(in, inputFilename);

            } else {

                minifyCss(in, inputFilename);
            }

        } catch (final FileNotFoundException e) {

            throw new MinificationException(
                    "Couldn't find the specified file '" + inputFilename + "'.", e);
        } finally {
            closeReader(in);
        }
    }

    /**
     * Determines the file type based on the file's extension.
     * @param inputFilename from which to extract the file extension
     * @return the extension of the given filename
     * @throws MinificationException if the given filename doesn't contain an extension
     */
    private String getFileType(final String inputFilename) throws MinificationException {

        String type = null;

        final int idx = inputFilename.lastIndexOf('.');
        if ((idx >= 0) && (idx < inputFilename.length() - 1)) {
            type = inputFilename.substring(idx + 1);
        }

        if (type == null) {
            throw new MinificationException(
                    "Unable to determine file type from the extension of the input file '"
                    + inputFilename + "'");
        }

        return type;
    }

    /**
     * Validates that the file is of accepted type, (either JavaScript or CSS).
     * @param type the extension of the file
     * @throws MinificationException if the given type is not a CSS or JavaScript file type
     */
    private void validateFileType(final String type) throws MinificationException {

        if (!isJavaScript(type) && !isCSS(type))  {

            throw new MinificationException(
                    "Invalid file type, '" + type + "'. Can only minify CSS and JavaScript files");
        }
    }

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
     * Uses the YUI JavaScriptCompressor to minify the file represented by the given Reader, writing
     * out the result to the given inputFilename path.
     * @param in the file reader for the input file to be minified
     * @param outputFilename the full path for the minified output file
     * @throws MinificationException if unable to perform the minification
     */
    private void minifyJavaScript(final Reader in, final String outputFilename)
    throws MinificationException {

        final JavaScriptCompressor compressor = createJavaScriptCompressor(in);

        Writer out = null;

        try {
            // Close the input stream first, and then open the output stream,
            // because the output file overrides the input file.
            in.close();

            out = new BufferedWriter(new FileWriter(outputFilename, false));

            compressor.compress(out, DEFAULT_LINEBREAK_POS, DEFAULT_OBFUSCATE, verbose,
                    DEFAULT_PRESERVE_ALL_SEMICOLONS, DEFAULT_DISABLE_OPTIMIZATIONS);

        } catch (final IOException e) {
            throw new MinificationException("Unable to perform JavaScript minification", e);
        } finally {
            closeWriter(out);
        }
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
     * Minifies the CSS file read in by the given Reader using the YUI CssCompressor. The
     * resulting output is written to the given outputFilename.
     * @param in a file Reader for the CSS file to be minified
     * @param outputFilename the full path to the minified output file
     * @throws MinificationException if unable to perform the minification
     */
    private void minifyCss(final Reader in, final String outputFilename)
    throws MinificationException {

        Writer out = null;

        try {
            final CssCompressor compressor = createCSSCompressor(in);

            // Close the input stream first, and then open the output stream,
            // because the output file overrides the input file.
            in.close();

            out = new BufferedWriter(new FileWriter(outputFilename, false));

            compressor.compress(out, DEFAULT_LINEBREAK_POS);

        } catch (final IOException e) {
            throw new MinificationException("Unable to perform CSS minification", e);
        } finally {
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

    private boolean isJavaScript(final String type) {

        return type.equalsIgnoreCase("js");
    }

    private boolean isCSS(final String type) {

        return type.equalsIgnoreCase("css");
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


