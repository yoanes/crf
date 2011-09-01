package au.com.sensis.mobile.crf.presentation.tag;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.util.MD5Builder;

/**
 * Delegate which implements the writing of output for classes inheriting from
 * {@link AbstractBundleTag}.
 *
 * @author w12495 (author of original AbstractBundleTag)
 * @author Brendan Doyle
 */
public abstract class BundleTagDelegate {

    private static final Logger LOGGER = Logger.getLogger(BundleTagDelegate.class);

    private final JspContext jspContext;

    /**
     * Data that child tags have registered with this {@link BundleTagDelegate}.
     */
    private final BundleTagData bundleTagData;

    public BundleTagDelegate(final JspContext jspContext, final BundleTagData bundleTagData) {

        // TODO: now we have the context, we can get the writer from it - should we do that?
        this.jspContext = jspContext;
        this.bundleTagData = bundleTagData;
    }

    /**
     * @return  the id.
     */
    protected String getId() {

        return getBundleTagData().getId();
    }

    /**
     * Write out the tags to the page.
     *
     * @param jspWriter             the {@link JspWriter} to print output to.
     * @param dynamicTagAttributes  the {@link List} of {@link DynamicTagAttribute}s.
     *
     * @throws IOException  if any error occurs.
     */
    public void writeTags(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes)
            throws IOException {

        if (childTagsHaveRegisteredAbsoluteHrefs()) {

            writeTagsForRegisteredHrefsToPage(jspWriter, dynamicTagAttributes);
        }

        if (childTagsHaveRegisteredResourcesToBundle()) {

            bundleRegisteredResourcesAndWriteTagToPage(jspWriter, dynamicTagAttributes);
        }
    }

    /**
     * Write out the tag to the page.
     *
     * @param jspWriter             the {@link JspWriter} to print output to.
     * @param path                  the client path to the bundle resource.
     * @param dynamicTagAttributes  the {@link List} of {@link DynamicTagAttribute}s.
     *
     * @throws IOException  if any error occurs.
     */
    protected abstract void writeTag(JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes, final String path)
            throws IOException;

    /**
     * Write out the tag to the page.
     *
     * @param jspWriter             the {@link JspWriter} to print output to.
     * @param path                  absolute href.
     * @param dynamicTagAttributes  the {@link List} of {@link DynamicTagAttribute}s.
     *
     * @throws IOException  if any error occurs.
     */
    protected abstract void writeAbsoluteHrefTag(JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes, final String path)
            throws IOException;

    /**
     * @return  the file extension (without the ".") to be used for the created bundle.
     */
    protected abstract String getBundleFileExtension();

    private boolean childTagsHaveRegisteredAbsoluteHrefs() {

        return ! getAbsoluteHrefsToRemember().isEmpty();
    }

    private void writeTagsForRegisteredHrefsToPage(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes)
            throws IOException {

        for (final String absoluteHref : getAbsoluteHrefsToRemember()) {

            writeAbsoluteHrefTag(jspWriter, dynamicTagAttributes, absoluteHref);
        }
    }

    private boolean childTagsHaveRegisteredResourcesToBundle() {

        return ! getResourcesToBundle().isEmpty();
    }

    private void bundleRegisteredResourcesAndWriteTagToPage(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes)
            throws IOException {

        final BundleTagCacheKey cacheKey = createCacheKey();

        debugLogCheckingCache(cacheKey);

        if (getCache().contains(cacheKey)) {

            writeTagForCachedBundle(jspWriter, dynamicTagAttributes, cacheKey);

        } else {

            writeTagForNonCachedBundle(jspWriter, dynamicTagAttributes, cacheKey);
        }
    }

    private void writeTagForCachedBundle(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes, final BundleTagCacheKey cacheKey)
            throws IOException {

        final String cachedBundleClientPath = getCache().get(cacheKey);
        debugLogCachedClientBundlePathFound(cachedBundleClientPath);
        writeTag(jspWriter, dynamicTagAttributes, cachedBundleClientPath);
    }

    private void writeTagForNonCachedBundle(final JspWriter jspWriter,
            final List<DynamicTagAttribute> dynamicTagAttributes, final BundleTagCacheKey cacheKey)
            throws IOException {

        final String outputBundleBasePath = createOutputBundleBasePath(getBundleFileExtension());
        createBundle(outputBundleBasePath);

        final String outputBundleClientPath = createOutputBundleClientPath(outputBundleBasePath);
        writeTag(jspWriter, dynamicTagAttributes, outputBundleClientPath);

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

    private void createBundle(final String outputBundleBasePath)
            throws IOException {

        final File outputBundleFile = new File(getBundleTagDependencies().getRootResourcesDir(),
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

                // Preserve newlines in case minification was disabled for each resource and there
                // are single-line comments in the file. This is unnecessary if minification is
                // enabled but does not add any significant overhead.
                outputBundleFileWriter.write("\n");

            } finally {

                resourceReader.close();
            }
        }
    }

    private String createOutputBundleClientPath(final String outputBundleBasePath) {

        return getBundleTagDependencies().getClientPathPrefix() + outputBundleBasePath
                + getUniqueRequestParam();

    }

    private String createOutputBundleBasePath(final String bundleFileExtension) {

        final MD5Builder md5Builder = createMD5Builder();

        for (final Resource resource : getResourcesToBundle()) {

            md5Builder.add(resource.getNewPath());
        }

        return concatStrings(getBundleTagDependencies().getDeploymentMetadata().getVersion(),
                "/appBundles/", getId(), "-", md5Builder.getSumAsHex(), "-package.",
                bundleFileExtension);
    }

    private String concatStrings(final String ... stringsToConcat) {

        final StringBuilder stringBuilder = new StringBuilder();

        for (final String currStr : stringsToConcat) {

            stringBuilder.append(currStr);
        }

        return stringBuilder.toString();
    }

    private MD5Builder createMD5Builder() {

        try {

            return new MD5Builder();

        } catch (final NoSuchAlgorithmException e) {

            throw new IllegalStateException("The MD5 algorithm is not available in yor JVM. "
                    + "See the Javadoc for MessageDigest.getInstance(String) for further details.",
                    e);
        }
    }

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

        return getBundleTagDependencies().getBundleTagCache();
    }

    /**
     * @return  a {@link List} of {@link Resource}s that a child tag wants to register with this
     *          {@link BundleTagDelegate} to be bundled into a single script.
     */
    private List<Resource> getResourcesToBundle() {

        return getBundleTagData().getResourcesToBundle();
    }

    /**
     * @return  the absoluteHrefsToRemember.
     */
    private List<String> getAbsoluteHrefsToRemember() {

        return getBundleTagData().getAbsoluteHrefsToRemember();
    }

    /**
     * @return  a unique request parameter. Only non-empty if downstream caching is disabled.
     */
    protected final String getUniqueRequestParam() {

        String uniqueRequestParam = StringUtils.EMPTY;

        if (! getBundleTagDependencies().getDeploymentMetadata().isDownstreamCachingEnabled()) {

            uniqueRequestParam = "?" + new Date().getTime();
        }

        return uniqueRequestParam;
    }

    protected BundleTagDependencies getBundleTagDependencies() {

        return (BundleTagDependencies)
                getWebApplicationContext().getBean(getTagDependenciesBeanName());
    }

    /**
     * @return  the name of the {@link BundleTagDependencies} bean to be obtained from the Spring
     *          context.
     */
    protected abstract String getTagDependenciesBeanName();

    protected WebApplicationContext getWebApplicationContext() {

        final PageContext pc = (PageContext) getJspContext();

        return WebApplicationContextUtils.getRequiredWebApplicationContext(pc.getServletContext());
    }

    /**
     * @return  the jspContext.
     */
    protected JspContext getJspContext() {

        return jspContext;
    }

    /**
     * @return  the bundleTagData.
     */
    protected BundleTagData getBundleTagData() {

        return bundleTagData;
    }
}

