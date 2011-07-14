package au.com.sensis.mobile.crf.presentation.tag;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.util.MD5Builder;

/**
 * Tag that bundles the output of any child tags that register {@link Resource}s with
 * this {@link AbstractBundleTag} via the {@link #addResourcesToBundle(List)} method.
 *
 * <p>
 * This tag has no need need to inherit the complexity of {@link AbstractDuplicatePreventingTag}
 * because any child tags are assumed to already have this protection. So
 * if this {@link AbstractBundleTag} ends up with a non-empty {@link #getResourcesToBundle()},
 * this is because there was a child tag that has not occurred in the request before.
 * </p>
 *
 * @author w12495
 */
public abstract class AbstractBundleTag extends AbstractTag {

    private static final Logger LOGGER = Logger.getLogger(AbstractBundleTag.class);

    /**
     * id to associate with the script. Should be unique to the page, just like any HTML id. This
     * tag does not enforce this uniqueness.
     */
    private String id;

    /**
     * List of resources that child tags have registered with this {@link AbstractBundleTag}
     * to be bundled into a single script.
     */
    private final List<Resource> resourcesToBundle = new ArrayList<Resource>();

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public void doTag() throws JspException, IOException {
        // Let any child tags do their thing. If they wish to have their external
        // resources bundled by this tag, we expect them to find us using the standard JEE
        // findAncestorWithClass method, then call addResourcesToBundle.
        getJspBody().invoke(null);

        if (!getResourcesToBundle().isEmpty()) {
            bundleRegisteredResourcesAndWriteTagToPage();
        }
    }

    private void bundleRegisteredResourcesAndWriteTagToPage() throws IOException {
        final BundleTagCacheKey cacheKey = createCacheKey();

        debugLogCheckingCache(cacheKey);
        if (getCache().contains(cacheKey)) {
            writeTagForCachedBundle(cacheKey);
        } else {
            writeTagForNonCachedBundle(cacheKey);
        }
    }

    private void writeTagForCachedBundle(final BundleTagCacheKey cacheKey)
            throws IOException {

        final String cachedBundleClientPath = getCache().get(cacheKey);
        debugLogCachedClientBundlePathFound(cachedBundleClientPath);
        writeTag(cachedBundleClientPath);
    }

    private void writeTagForNonCachedBundle(final BundleTagCacheKey cacheKey)
        throws IOException {

        final String outputBundleBasePath = createOutputBundleBasePath();
        createBundle(outputBundleBasePath);

        final String outputBundleClientPath = createOutputBundleClientPath(outputBundleBasePath);
        writeTag(outputBundleClientPath);

        updateCache(cacheKey, outputBundleClientPath);
    }

    private void updateCache(final BundleTagCacheKey cacheKey,
            final String outputBundleClientPath) {

        debugLogUpdatingCache(cacheKey, outputBundleClientPath);

        getCache().put(cacheKey, outputBundleClientPath);
    }

    private BundleTagCacheKey createCacheKey() {
        return new BundleTagCacheKeyBean(getId(),
                getResourcesToBundle().toArray(new Resource [] {}));
    }

    private void createBundle(final String outputBundleBasePath) throws IOException {
        final File outputBundleFile = new File(getTagDependencies().getRootResourcesDir(),
                outputBundleBasePath);

        debugLogCreatingBundle(outputBundleFile);

        createFileAndParentDirsIfNecessary(outputBundleFile);
        final FileWriter outputBundleFileWriter = new FileWriter(outputBundleFile);

        try {
            concatenateResources(outputBundleFileWriter);
        } finally {
            outputBundleFileWriter.close();
        }
    }

    private void createFileAndParentDirsIfNecessary(final File outputBundleFile)
        throws IOException {

        if (!outputBundleFile.getParentFile().exists()
                && !outputBundleFile.getParentFile().mkdirs()) {
            throw new IOException("Error creating directories for '" + outputBundleFile + "'");
        }
        outputBundleFile.createNewFile();
    }

    private void concatenateResources(final FileWriter outputBundleFileWriter)
            throws IOException {
        for (final Resource resource : getResourcesToBundle()) {
            final FileReader resourceReader = new FileReader(resource.getNewFile());
            try {
                IOUtils.copy(resourceReader, outputBundleFileWriter);

                // Preserve newlines in case minification was disabled for each resource
                // and there are single-line comments in the file. This is unnecessary if
                // minification is enabled but does not add any significant overhead.
                outputBundleFileWriter.write("\n");
            } finally {
                resourceReader.close();
            }
        }
    }

    private String createOutputBundleClientPath(final String outputBundleBasePath) {
        return getTagDependencies().getClientPathPrefix() + outputBundleBasePath;

    }

    private String createOutputBundleBasePath() {
        final MD5Builder md5Builder = createMD5Builder();

        for (final Resource resource : getResourcesToBundle()) {
            md5Builder.add(resource.getNewPath());
        }
        return concatStrings(getTagDependencies().getDeploymentMetadata().getVersion(),
                "/appBundles/", getId(), "-", md5Builder.getSumAsHex(), "-package.",
                getBundleFileExtension());

    }

    /**
     * @return File extension (without the ".") to be used for the created bundle.
     */
    protected abstract String getBundleFileExtension();

    private String concatStrings(final String ... stringsToConcat) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String currStr : stringsToConcat) {
            stringBuilder.append(currStr);
        }

        return stringBuilder.toString();
    }

    private MD5Builder createMD5Builder() {
        MD5Builder md5Builder;
        try {
            md5Builder = new MD5Builder();
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("The MD5 algorithm is not available in yor JVM. "
                    + "See the Javadoc for MessageDigest.getInstance(String) for further details.",
                    e);
        }
        return md5Builder;
    }

    /**
     * Write out the tag to the page.
     *
     * @param path Client path to the bundle resource.
     * @throws IOException Thrown if any error occurs.
     */
    protected abstract void writeTag(final String path) throws IOException;

    private BundleTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc
                        .getServletContext());
        return (BundleTagDependencies) webApplicationContext
                .getBean(getTagDependenciesBeanName());
    }

    /**
     * @return Name of the {@link BundleTagDependencies} bean to be obtained from the Spring
     *         context.
     */
    protected abstract String getTagDependenciesBeanName();

    private void debugLogCheckingCache(final BundleTagCacheKey cacheKey) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Checking cache for key='" + cacheKey + "'");
        }
    }

    private void debugLogCachedClientBundlePathFound(final String cachedBundleClientPath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Found cached bundle client path: '" + cachedBundleClientPath + "'");
        }
    }

    private void debugLogCreatingBundle(final File outputBundleFile) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating bundle '" + outputBundleFile + "' from resources: '"
                    + getResourcesToBundle() + "'");
        }
    }

    private void debugLogUpdatingCache(final BundleTagCacheKey cacheKey,
            final String outputBundleClientPath) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating cache with key='" + cacheKey + "' and value='"
                    + outputBundleClientPath + "'");
        }
    }

    private BundleTagCache getCache() {
        return getTagDependencies().getBundleTagCache();
    }

    /**
     * @return List of resources that a child tag wants to register with this
     *         {@link AbstractBundleTag} to be bundled into a single script.
     */
    private List<Resource> getResourcesToBundle() {
        return resourcesToBundle;
    }

    /**
     * @param resources
     *            List of resources that a child tag wants to register with this
     *            {@link AbstractBundleTag} to be bundled into a single script.
     */
    protected void addResourcesToBundle(final List<Resource> resources) {
        getResourcesToBundle().addAll(resources);
    }

}

