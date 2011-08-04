package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;



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
    public File[] list(final File parentDirectory, final String path, final String[] extensions) {
        // The path may contain more than just a file name. eg.
        // default/common/unmetered. So get just the last part.
        final String pathFileName = new File(path).getName();

        return doListByExtensions(parentDirectory, path, createWildcardFilter(pathFileName,
                extensions));
    }

    private File[] doListByExtensions(final File parentDirectory, final String path,
            final FileFilter fileFilter) {

        // Similarly, strip the last part of the parentDirectory + path
        // to get the directory to look in.
        final File baseDirForListings = new File(parentDirectory, path).getParentFile();

        final File[] foundFiles = baseDirForListings.listFiles(fileFilter);
        if (foundFiles != null) {
            return foundFiles;
        } else {
            return new File[] {};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] list(final File parentDirectory, final String path,
            final String[] matchedExtensions, final String[] excludedExtensions) {
        // The path may contain more than just a file name. eg.
        // default/common/unmetered. So get just the last part.
        final String pathFileName = new File(path).getName();

        final IOFileFilter matchedExtensionsWildcardFilter =
                createWildcardFilter(pathFileName, matchedExtensions);
        final IOFileFilter excludedExtensionsWildcardFilter =
                new NotFileFilter(createWildcardFilter(pathFileName, excludedExtensions));

        final FileFilter fileFilter =
                new AndFileFilter(matchedExtensionsWildcardFilter,
                        excludedExtensionsWildcardFilter);

        return doListByExtensions(parentDirectory, path, fileFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] list(final File directory, final String[] wildcards) {
        return list(directory, createWildcardFilter(wildcards));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] list(final File directory, final FileFilter fileFilter) {
        final File[] foundFiles = directory.listFiles(fileFilter);
        if (foundFiles != null) {
            return foundFiles;
        } else {
            return new File[] {};
        }
    }

    private IOFileFilter createWildcardFilter(final String fileName,
            final String[] extensions) {
        return new WildcardFileFilter(createWildcards(fileName,
                extensions));
    }

    private FileFilter createWildcardFilter(final String[] wildcards) {
        return new WildcardFileFilter(wildcards);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void mkdirs(final File dir) throws IOException {
        if ((!dir.exists() || !dir.isDirectory()) && !dir.mkdirs()) {
            throw new IOException("Failed to create directory or one of its parent directories: "
                    + dir);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] listByFilenameAndDirnameWildcardPatterns(final File directory,
            final String [] filenameWildcardPatterns,
            final String [] dirnameWildcardPatterns) {
        @SuppressWarnings("unchecked")
        final Collection<File> foundFiles =
                FileUtils.listFiles(directory, new WildcardFileFilter(filenameWildcardPatterns),
                        new WildcardFileFilter(dirnameWildcardPatterns));
        if (foundFiles.isEmpty()) {
            return new File[] {};
        } else {
            return foundFiles.toArray(new File[] {});
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fileExists(final File file) {
        return file.exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirectory(final File dir) {
        return dir.isDirectory();
    }
}
