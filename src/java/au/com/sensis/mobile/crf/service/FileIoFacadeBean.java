package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFilter;


/**
 * FileIoFacade implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class FileIoFacadeBean implements FileIoFacade {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fileExists(final String path) {
        return new File(path).exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fileExists(final File parentDirectory, final String path) {
        return new File(parentDirectory , path).exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] list(final File parentDirectory, final String path,
            final String[] extensions) {
        // The path may contain more than just a file name. eg.
        // default/common/unmetered. So get just the last part.
        final String pathFileName = new File(path).getName();

        // Similarly, strip the last part of the parentDirectory + path
        // to get the directory to look in.
        final File baseDirForListings =
                new File(parentDirectory, path).getParentFile();

        final File[] foundFiles = baseDirForListings.listFiles(createWildcardFilter(pathFileName,
                extensions));
        if (foundFiles != null) {
            return foundFiles;
        } else {
            return new File [] {};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] list(final File directory, final String[] wildcards) {
        final File[] foundFiles = directory.listFiles(
                createWildcardFilter(wildcards));
        if (foundFiles != null) {
            return foundFiles;
        } else {
            return new File [] {};
        }

    }

    private FileFilter createWildcardFilter(final String fileName,
            final String[] extensions) {
        return new WildcardFilter(createWildcards(fileName,
                extensions));
    }

    private FileFilter createWildcardFilter(final String[] wildcards) {
        return new WildcardFilter(wildcards);
    }

    private String[] createWildcards(final String fileName,
            final String[] extensions) {
        final List<String> fileNameWildcards = new ArrayList<String>();

        for (final String extension : extensions) {
            fileNameWildcards
                    .add(insertExtensionSeparator(fileName, extension));
        }

        return fileNameWildcards.toArray(new String[] {});
    }

    private String insertExtensionSeparator(final String fileName,
            final String extension) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileName);
        stringBuilder.append(".");
        stringBuilder.append(extension);
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFileAndCloseStream(final File inputFile,
            final OutputStream outputStream) throws IOException {

        try {
            final FileInputStream fileInputStream = new FileInputStream(inputFile);
            IOUtils.copy(fileInputStream, outputStream);
        } finally {
            outputStream.close();
        }
    }
}
