package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Callback interface to be passed to {@link #findJavaScriptFiles(MappedResourcePath)}.
 */
public interface JavaScriptFileFinder {

    // TODO
    List<File> findJavaScriptFiles(File dir) throws IOException;
}