package au.com.sensis.mobile.crf.presentation.tag;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.util.MD5Builder;

/**
 * Tag that bundles the output of any child {@link ScriptTag}s that register
 * {@link Resource}s with this {@link BundleScriptsTag} via the {@link #addResourcesToBundle(List)}
 * method.
 *
 * @author w12495
 *
 */
// TODO: should prevent duplicates of this same tag per page?
public class BundleScriptsTag extends AbstractTag {

    private static final Logger LOGGER = Logger.getLogger(BundleScriptsTag.class);

    /**
     * id to associate with the script. Should be unique to the page, just like any HTML id. This
     * tag does not enforce this uniqueness.
     */
    private String id;

    /**
     * List of resources that child tags have registered with this {@link BundleScriptsTag}
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
        // Let any child tags do their thing. If they wish to have their external JavaScript
        // resources bundled by this tag, we expect them to find us using the standard JEE
        // mechanisms, then call addResourcesToBundle.
        getJspBody().invoke(null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resources to bundle: " + getResourcesToBundle());
        }

        if (!getResourcesToBundle().isEmpty()) {
            bundleRegisteredResources();
        }
    }

    private void bundleRegisteredResources() throws IOException {
        final String outputBundleBasePath = createOutputBundleBasePath();
        createBundle(outputBundleBasePath);
        writeSingleScriptTag(createOutputBundleClientPath(outputBundleBasePath));
    }

    private void createBundle(final String outputBundleBasePath) throws IOException {
        final File outputBundleFile = new File(getTagDependencies().getRootResourcesDir(),
                outputBundleBasePath);
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
                "/appBundles/", getId(), "-", md5Builder.getSumAsHex(), "-package.js");

    }

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

    private void writeSingleScriptTag(final String src) throws IOException {
        getJspContext().getOut().print("<script id=\"");

        getJspContext().getOut().print(getId());

        getJspContext().getOut().print("\" src=\"");
        getJspContext().getOut().print(src);
        getJspContext().getOut().print("\" ");

        writeDynamicTagAttributes(getJspContext().getOut());

        getJspContext().getOut().print("></script>");
    }


    private void writeDynamicTagAttributes(final JspWriter jspWriter) throws IOException {
        boolean charsetAttributeFound = false;
        boolean typeAttributeFound = false;

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {

            if ("charset".equals(attribute.getLocalName())) {
                charsetAttributeFound = true;
            }

            if ("type".equals(attribute.getLocalName())) {
                typeAttributeFound = true;
            }

            jspWriter.print(attribute.getLocalName());
            jspWriter.print("=\"");
            jspWriter.print(attribute.getValue());
            jspWriter.print("\" ");
        }

        writeCharsetAttributeIfNotFound(jspWriter, charsetAttributeFound);
        writeTypeAttributeIfNotFound(jspWriter, typeAttributeFound);
    }

    private void writeTypeAttributeIfNotFound(final JspWriter jspWriter,
            final boolean typeAttributeFound)
            throws IOException {

        if (!typeAttributeFound) {
            jspWriter.print("type=\"text/javascript\" ");
        }
    }


    private void writeCharsetAttributeIfNotFound(final JspWriter jspWriter,
            final boolean charsetAttributeFound) throws IOException {

        if (!charsetAttributeFound) {
            jspWriter.print("charset=\"utf-8\" ");
        }
    }

    private BundleScriptsTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc
                        .getServletContext());
        return (BundleScriptsTagDependencies) webApplicationContext
                .getBean(BundleScriptsTagDependencies.BEAN_NAME);
    }

    /**
     * @return List of resources that a child tag wants to register with this
     *         {@link BundleScriptsTag} to be bundled into a single script.
     */
    private List<Resource> getResourcesToBundle() {
        return resourcesToBundle;
    }

    /**
     * @param resources
     *            List of resources that a child tag wants to register with this
     *            {@link BundleScriptsTag} to be bundled into a single script.
     */
    protected void addResourcesToBundle(final List<Resource> resources) {
        getResourcesToBundle().addAll(resources);
    }

}

