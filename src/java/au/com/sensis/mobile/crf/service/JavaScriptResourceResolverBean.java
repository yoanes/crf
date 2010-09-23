package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourceResolver} that maps abstract Script paths to real Script paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourceResolverBean extends AbstractResourceResolver {

    private static final Logger LOGGER = Logger.getLogger(JavaScriptResourceResolverBean.class);

    /**
     * Separator character expected to be used in {@link #getOriginalPath()}
     * and {@link #getNewFile()}.
     */
    protected static final String SEPARATOR = "/";

    private JavaScriptFileFinder javaScriptFileFinder;

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Abstract extension for resources.
     * @param rootResourcesDir
     *            Root directory where the concrete resources are stored.
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger}.
     */
    public JavaScriptResourceResolverBean(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        super(abstractResourceExtension, rootResourcesDir,
                resourceResolutionWarnLogger);

        javaScriptFileFinder =
            new JavaScriptBundlePathExpander(new PropertiesLoaderBean(),
                    "bundles.properties", "order");

    }

    // TODO: temp hack for testing. JavaScriptFileFinder will be refactored into oblivion, anyway.
    /**
     * @param javaScriptFileFinder the JavaScriptFileFinder.
     */
    public void setPathExpander(final JavaScriptFileFinder javaScriptFileFinder) {
        this.javaScriptFileFinder = javaScriptFileFinder;
    }

    private JavaScriptFileFinder getPathExpander() {
        return javaScriptFileFinder;
    }


    /**
     * Template method for mapping requested resource paths to real resource
     * paths.
     *
     * {@inheritDoc}
     * @throws IOException Thrown if an IO error occurs.
     */
    @Override
    public List<Resource> resolve(
            final String requestedResourcePath, final Group group)
                throws IOException {
        if (isRecognisedAbstractResourceRequest(requestedResourcePath)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "Mapping '"
                                + requestedResourcePath
                                + "' to '"
                                + doMapResourcePath(requestedResourcePath,
                                        group) + "'");
            }
            final Resource resource =
                createResource(requestedResourcePath,
                    doMapResourcePath(requestedResourcePath, group));

            if (resource.isBundlePath()) {
                return createResults(requestedResourcePath,
                        resource.getBundleParentDirFile());
            } else {
                if (exists(resource)) {
                    return Arrays.asList(resource);
                } else {
                    return new ArrayList<Resource>();
                }
            }
        } else {

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "Requested resource '" + requestedResourcePath
                                + "' is not for a " + getDebugResourceTypeName()
                                + " file. Returning an empty list.");
            }

            return new ArrayList<Resource>();
        }
    }

    /**
     * @param resource {@link Resource} to test the existence of.
     * @return true if the mapped path given by {@link #getNewPath()}
     *         exists in {@link #getRootResourceDir()}.
     */
    protected boolean exists(final Resource resource) {
        // TODO: possibly cache the result since we are accessing the file system?
        return FileIoFacadeFactory.getFileIoFacadeSingleton().fileExists(
                getRootResourcesDir(), resource.getNewPath());
    }

    private List<Resource> createResults(
            final String requestedResourcePath,
            final File javascriptFilesBaseDir) throws IOException {
        final List<Resource> result =
            new ArrayList<Resource>();

      final List<File> foundFiles = getPathExpander().findJavaScriptFiles(javascriptFilesBaseDir);
      if (foundFiles != null) {
        for (final File file : foundFiles) {
            final Resource currResource =
                    new ResourceBean(
                            requestedResourcePath,
                            getRootResourceDirRelativePath(file),
                            getRootResourcesDir());
            result.add(currResource);
        }
      }
        return result;
    }

    private String getRootResourceDirRelativePath(final File file) {
        final String rootResourceDirRelativePath = StringUtils.substringAfter(file.getPath(),
                getRootResourcesDir().getPath()).replace(File.separator, SEPARATOR);
        return StringUtils.substringAfter(rootResourceDirRelativePath, SEPARATOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doMapResourcePath(final String requestedResourcePath, final Group group) {
        // TODO: "bundle" name creation will probably change. We want "package" or something
        // instead? Hard code for now.
        if (isBundleRequested(requestedResourcePath)) {
            return insertGroupNameIntoPath(requestedResourcePath, group) + "/bundle-all.js";
        } else {
            return super.doMapResourcePath(requestedResourcePath, group);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isRecognisedAbstractResourceRequest(
            final String requestedResourcePath) {
        return super.isRecognisedAbstractResourceRequest(requestedResourcePath)
            || isBundleRequested(requestedResourcePath);
    }



    private boolean isBundleRequested(final String requestedResourcePath) {
        // TODO: "bundle" checking will probably change. We want "package" or something instead?
        // Hard code for now.
        return requestedResourcePath.endsWith("bundle");
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRealResourcePathExtension() {
        return ".js";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDebugResourceTypeName() {
        return "JavaScript";
    }
}
