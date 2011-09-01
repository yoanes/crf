package au.com.sensis.mobile.crf.presentation.tag;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourcePathTestData;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.util.MD5Builder;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit tests for {@link RenderBundledScriptsTag}.
 *
 * @author Brendan Doyle
 */
public class RenderBundledScriptsTagTestCase
        extends AbstractJUnit4TestCase {

    private static final String TAG_ID = "myId";

    private static final String DYN_ATTR_URI = "http://www.w3.org/2002/06/xhtml2";

    private RenderBundledScriptsTag objectUnderTest;

    private final DeploymentMetadataTestData deploymentMetadataTestData
            = new DeploymentMetadataTestData();
    private BundleTagDependencies bundleScriptsTagDependencies;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    private JspWriter springMockJspWriter;
    private StringWriter stringWriter;

    private Resource mockResource1;
    private Resource mockResource2;
    private ResourceResolutionWarnLogger mockResolutionWarnLogger;
    private PageContext mockPageContext;
    private WebApplicationContext mockWebApplicationContext;
    private MockServletContext springMockServletContext;
    private BundleTagCache mockBundleScriptsTagCache;

    private File sourceBundle1NewFile;
    private String sourceBundle1NewPath;
    private File sourceBundle2NewFile;
    private String sourceBundle2NewPath;
    private File appBundlesRootDir;

    private static final String ABSOLUTE_HREF_1 = "http://www.url.com/externalJavascript1.js";
    private static final String ABSOLUTE_HREF_2 = "http://www.url.com/externalJavascript2.js";

    /**
     * Setup test data.
     *
     * @throws Exception if any error occurs.
     */
    @Before
    public void setUp()
            throws Exception {

        setObjectUnderTest(new RenderBundledScriptsTag());

        getObjectUnderTest().setJspContext(getMockPageContext());

        setStringWriter(new StringWriter());
        setSpringMockJspWriter(new MockJspWriter(getStringWriter()));
    }

    private void setupForCorrectVar()
            throws IOException {

        setSpringMockServletContext(new MockServletContext());

        setSourceBundle1NewFile(new ClassPathResource(
                "/au/com/sensis/mobile/crf/presentation/tag/sourceBundle1.js").getFile());
        setSourceBundle1NewPath(
                "default/core/util/bundle/c21f969b5f03d33d43e04f8f136e7682/utils.js");

        setSourceBundle2NewFile(new ClassPathResource(
                "/au/com/sensis/mobile/crf/presentation/tag/sourceBundle2.js").getFile());
        setSourceBundle2NewPath(
                "nonIEPC/comp/map/bundle/c0c80a0e534cf880884b00610da878aa/package.js");

        setAppBundlesRootDir(
                new ClassPathResource("/au/com/sensis/mobile/crf/presentation/tag/").getFile());

        setBundleScriptsTagDependencies(createBundleScriptsTagDependencies());
    }

    @Test
    public void testDoTagWithNoVar()
            throws Exception {

        final String var = null;
        getObjectUnderTest().setVar(var);

        try {

            getObjectUnderTest().doTag();

            Assert.fail("Expected IllegalArgumentException not thrown.");

        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("var attribute must be set.", e.getMessage());
        }

        Assert.assertEquals("Script incorrectly written", StringUtils.EMPTY, getStringWriter()
                .getBuffer().toString());
    }

    @Test
    public void testDoTagWithNoAttribute()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn(null);

        replay();

        try {

            getObjectUnderTest().doTag();

            Assert.fail("Expected IllegalArgumentException not thrown.");

        } catch (final IllegalArgumentException e) {

            Assert.assertEquals(
                    "no value: var attribute must match a previous crf:bundleScripts tag.",
                    e.getMessage());
        }

        Assert.assertEquals("Script incorrectly written", StringUtils.EMPTY,
                getStringWriter().getBuffer().toString());
    }

    @Test
    public void testDoTagWithAttributeOfIncorrectType()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn("String attribute");

        replay();

        try {

            getObjectUnderTest().doTag();

            Assert.fail("Expected IllegalArgumentException not thrown.");

        } catch (final IllegalArgumentException e) {

            Assert.assertEquals(
                    "incorrect type: var attribute must match a previous crf:bundleScripts tag.",
                    e.getMessage());
        }

        Assert.assertEquals("Script incorrectly written", StringUtils.EMPTY,
                getStringWriter().getBuffer().toString());
    }

    @Test
    public void testDoTagWhenResourcesToBundleAndNoDynamicTagAttributes()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        final BundleTagData expectedBundleTagData = new BundleTagData();
        expectedBundleTagData.setId(TAG_ID);
        expectedBundleTagData.addResourcesToBundle(Arrays.asList(getMockResource1(),
                getMockResource2()));

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn(expectedBundleTagData);

        setupForCorrectVar();

        recordGetWebApplicationContext();
        recordGetTagDependencies();
        recordCheckCachedResources(false);
        recordBehaviourForBundleCreation();
        recordBehaviourForClientSrcPathCreation();
        recordBehaviourForWritingScriptTag();
        recordUpdateCache();

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("Script incorrectly written", "<script id=\"myId\" src=\""
                + createExpectedOutputBundleClientPath() + "\" charset=\"utf-8\" "
                + "type=\"text/javascript\" ></script>", getStringWriter().getBuffer().toString());

        assertBundleFileCorrect();
    }

    @Test
    public void testDoTagWhenResourcesToBundleAndAbsoluteHrefsAndNoDynamicTagAttributes()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        final BundleTagData expectedBundleTagData = new BundleTagData();
        expectedBundleTagData.setId(TAG_ID);
        expectedBundleTagData.addResourcesToBundle(Arrays.asList(getMockResource1(),
                getMockResource2()));
        expectedBundleTagData.rememberAbsoluteHref(ABSOLUTE_HREF_1);
        expectedBundleTagData.rememberAbsoluteHref(ABSOLUTE_HREF_2);

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn(expectedBundleTagData);

        setupForCorrectVar();

        recordGetWebApplicationContext();
        recordGetTagDependencies();
        recordCheckCachedResources(false);
        recordBehaviourForBundleCreation();
        recordBehaviourForClientSrcPathCreation();
        recordBehaviourForWritingScriptTag();
        recordUpdateCache();

        replay();

        getObjectUnderTest().doTag();

        final String absoluteHrefScriptTag1 = "<script src=\"" + ABSOLUTE_HREF_1
                + "\" charset=\"utf-8\" type=\"text/javascript\" ></script>";
        final String absoluteHrefScriptTag2 = "<script src=\"" + ABSOLUTE_HREF_2
                + "\" charset=\"utf-8\" type=\"text/javascript\" ></script>";
        final String bundeledScriptTag = "<script id=\"myId\" src=\""
                + createExpectedOutputBundleClientPath() + "\" charset=\"utf-8\" "
                + "type=\"text/javascript\" ></script>";
        final String expectedOutput = absoluteHrefScriptTag1 + absoluteHrefScriptTag2
                + bundeledScriptTag;

        Assert.assertEquals("Script incorrectly written", expectedOutput,
                getStringWriter().getBuffer().toString());

        assertBundleFileCorrect();
    }

    @Test
    public void testDoTagWhenResourcesToBundleAndCharsetDynamicTagAttribute()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        final BundleTagData expectedBundleTagData = new BundleTagData();
        expectedBundleTagData.setId(TAG_ID);
        expectedBundleTagData.addResourcesToBundle(Arrays.asList(getMockResource1(),
                getMockResource2()));

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn(expectedBundleTagData);

        setupForCorrectVar();

        recordGetWebApplicationContext();
        recordGetTagDependencies();
        recordCheckCachedResources(false);
        recordBehaviourForBundleCreation();
        recordBehaviourForClientSrcPathCreation();
        recordBehaviourForWritingScriptTag();
        recordUpdateCache();

        replay();

        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI, "charset", "ascii");
        getObjectUnderTest().doTag();

        Assert.assertEquals("Script incorrectly written", "<script id=\"myId\" src=\""
                + createExpectedOutputBundleClientPath() + "\" charset=\"ascii\" "
                + "type=\"text/javascript\" ></script>", getStringWriter().getBuffer().toString());

        assertBundleFileCorrect();
    }

    @Test
    public void testDoTagWhenResourcesToBundleAndTypeDynamicTagAttribute()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        final BundleTagData expectedBundleTagData = new BundleTagData();
        expectedBundleTagData.setId(TAG_ID);
        expectedBundleTagData.addResourcesToBundle(Arrays.asList(getMockResource1(),
                getMockResource2()));

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn(expectedBundleTagData);

        setupForCorrectVar();

        recordGetWebApplicationContext();
        recordGetTagDependencies();
        recordCheckCachedResources(false);
        recordBehaviourForBundleCreation();
        recordBehaviourForClientSrcPathCreation();
        recordBehaviourForWritingScriptTag();
        recordUpdateCache();

        replay();

        getObjectUnderTest().setDynamicAttribute(DYN_ATTR_URI, "type", "text/funkyscript");
        getObjectUnderTest().doTag();

        Assert.assertEquals("Script incorrectly written", "<script id=\"myId\" src=\""
                + createExpectedOutputBundleClientPath() + "\" type=\"text/funkyscript\" "
                + "charset=\"utf-8\" ></script>", getStringWriter().getBuffer().toString());

        assertBundleFileCorrect();
    }

    @Test
    public void testDoTagWhenResourcesToBundleAreCached()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        final BundleTagData expectedBundleTagData = new BundleTagData();
        expectedBundleTagData.setId(TAG_ID);
        expectedBundleTagData.addResourcesToBundle(Arrays.asList(getMockResource1(),
                getMockResource2()));

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn(expectedBundleTagData);

        setupForCorrectVar();

        recordGetWebApplicationContext();
        recordGetTagDependencies();
        recordCheckCachedResources(true);
        recordBehaviourForWritingScriptTag();

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("Script incorrectly written", "<script id=\"myId\" src=\""
                + createExpectedOutputBundleClientPath() + "\" charset=\"utf-8\" "
                + "type=\"text/javascript\" ></script>", getStringWriter().getBuffer().toString());

        assertBundleFileCorrect();
    }

    @Test
    public void testDoTagWhenNoResourcesToBundle()
            throws Exception {

        final String var = "varName";
        getObjectUnderTest().setVar(var);

        final BundleTagData expectedBundleTagData = new BundleTagData();
        expectedBundleTagData.setId(TAG_ID);

        getMockPageContext().getAttribute(var, PageContext.REQUEST_SCOPE);
        EasyMock.expectLastCall().andReturn(expectedBundleTagData);

        setupForCorrectVar();

        EasyMock.expect(getMockPageContext().getOut()).andReturn(getSpringMockJspWriter());

        replay();

        getObjectUnderTest().doTag();

        Assert.assertEquals("Tag should not have written anything to the page", StringUtils.EMPTY,
                getStringWriter().getBuffer().toString());
    }

    private String createExpectedOutputBundleClientPath()
            throws NoSuchAlgorithmException {

        return getBundleScriptsTagDependencies().getClientPathPrefix()
                + createExpectedOutputBundleBasePath();
    }

    private File createExpectedOutputBundleFile()
            throws NoSuchAlgorithmException {

        return new File(getBundleScriptsTagDependencies().getRootResourcesDir(),
                createExpectedOutputBundleBasePath());
    }

    private String createExpectedOutputBundleBasePath()
            throws NoSuchAlgorithmException {

        // Yeah, we rely on the exact same class that the object under test will use but how else
        // would you code this test? We could encode the md5 explicitly in a string but it wouldn't
        // really convey what's supposed to happen.
        final MD5Builder md5Builder = new MD5Builder();

        md5Builder.add(getSourceBundle1NewPath());
        md5Builder.add(getSourceBundle2NewPath());

        return getBundleScriptsTagDependencies().getDeploymentMetadata().getVersion()
                + "/appBundles/myId-" + md5Builder.getSumAsHex() + "-package.js";
    }

    private void recordUpdateCache()
            throws NoSuchAlgorithmException {

        final BundleTagCacheKeyBean keyBean = createCacheKey();
        getMockBundleScriptsTagCache().put(keyBean, createExpectedOutputBundleClientPath());
    }

    private void recordCheckCachedResources(final boolean resourcesCached)
            throws NoSuchAlgorithmException {

        final BundleTagCacheKeyBean keyBean = createCacheKey();
        getMockBundleScriptsTagCache().contains(keyBean);
        EasyMock.expectLastCall().andReturn(resourcesCached);

        if (resourcesCached) {

            EasyMock.expect(getMockBundleScriptsTagCache().get(keyBean)).andReturn(
                    createExpectedOutputBundleClientPath());
        }
    }

    private BundleTagCacheKeyBean createCacheKey() {

        return new BundleTagCacheKeyBean(TAG_ID,
                new Resource[] { getMockResource1(), getMockResource2() });
    }

    private void assertBundleFileCorrect()
            throws NoSuchAlgorithmException, IOException {

        Assert.assertTrue("Output bundle file not found: '" + createExpectedOutputBundleFile()
                + "'", createExpectedOutputBundleFile().exists());

        FileReader expectedOutputFileReader = null;
        String actualOutputBundleContents;
        try {

            expectedOutputFileReader = new FileReader(createExpectedOutputBundleFile());
            actualOutputBundleContents = IOUtils.toString(expectedOutputFileReader);

        } finally {

            if (expectedOutputFileReader != null) {

                expectedOutputFileReader.close();
            }
        }

        Assert.assertTrue("Bundle content doesn't contain bundle 1",
                actualOutputBundleContents.contains("jsSourceBundle1"));
        Assert.assertTrue("Bundle content doesn't contain bundle 2",
                actualOutputBundleContents.contains("jsSourceBundle2"));
    }

    private void recordBehaviourForWritingScriptTag() {

        getMockPageContext().getOut();
        EasyMock.expectLastCall().andReturn(getSpringMockJspWriter()).atLeastOnce();
    }

    private void recordBehaviourForClientSrcPathCreation() {

        getMockResource1().getNewPath();
        EasyMock.expectLastCall().andReturn(getSourceBundle1NewPath()).atLeastOnce();

        getMockResource2().getNewPath();
        EasyMock.expectLastCall().andReturn(getSourceBundle2NewPath()).atLeastOnce();
    }

    private void recordBehaviourForBundleCreation() {

        getMockResource1().getNewFile();
        EasyMock.expectLastCall().andReturn(getSourceBundle1NewFile());

        getMockResource2().getNewFile();
        EasyMock.expectLastCall().andReturn(getSourceBundle2NewFile());
    }

    private void recordGetWebApplicationContext() {

        getMockPageContext().getServletContext();
        EasyMock.expectLastCall().andReturn(getSpringMockServletContext()).atLeastOnce();

        getSpringMockServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                getMockWebApplicationContext());
    }

    private void recordGetTagDependencies() {

        getMockWebApplicationContext().getBean("crf.bundleScriptsTagDependencies");
        EasyMock.expectLastCall().andReturn(getBundleScriptsTagDependencies()).atLeastOnce();
    }

    private BundleTagDependencies createBundleScriptsTagDependencies() {

        return new BundleTagDependencies(
                getDeploymentMetadataTestData().createDevDeploymentMetadata(),
                getResourcePathTestData().getAppBundleClientPathPrefix(),
                getMockResolutionWarnLogger(), getMockBundleScriptsTagCache(),
                getAppBundlesRootDir());
    }

    /**
     * @return  the objectUnderTest.
     */
    private RenderBundledScriptsTag getObjectUnderTest() {

        return objectUnderTest;
    }

    /**
     * @param objectUnderTest   the objectUnderTest to set.
     */
    private void setObjectUnderTest(final RenderBundledScriptsTag objectUnderTest) {

        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return  the mockJspWriter.
     */
    private JspWriter getSpringMockJspWriter() {

        return springMockJspWriter;
    }

    /**
     * @param springMockJspWriter   the springMockJspWriter to set.
     */
    private void setSpringMockJspWriter(final JspWriter springMockJspWriter) {

        this.springMockJspWriter = springMockJspWriter;
    }

    /**
     * @return  the stringWriter.
     */
    private StringWriter getStringWriter() {

        return stringWriter;
    }

    /**
     * @param stringWriter  the stringWriter to set.
     */
    private void setStringWriter(final StringWriter stringWriter) {

        this.stringWriter = stringWriter;
    }

    /**
     * @return  the mockResource.
     */
    private Resource getMockResource1() {

        return mockResource1;
    }

    /**
     * @param mockResource  the mockResource to set.
     */
    public void setMockResource1(final Resource mockResource) {

        mockResource1 = mockResource;
    }

    /**
     * @return  the deploymentMetadataTestData.
     */
    private DeploymentMetadataTestData getDeploymentMetadataTestData() {

        return deploymentMetadataTestData;
    }

    /**
     * @return  the resourcePathTestData.
     */
    private ResourcePathTestData getResourcePathTestData() {

        return resourcePathTestData;
    }

    /**
     * @return  the mockResolutionWarnLogger.
     */
    private ResourceResolutionWarnLogger getMockResolutionWarnLogger() {

        return mockResolutionWarnLogger;
    }

    /**
     * @param mockResolutionWarnLogger  the mockResolutionWarnLogger to set.
     */
    public void setMockResolutionWarnLogger(
            final ResourceResolutionWarnLogger mockResolutionWarnLogger) {

        this.mockResolutionWarnLogger = mockResolutionWarnLogger;
    }

    /**
     * @return  the sourceBundle1.
     */
    private File getSourceBundle1NewFile() {

        return sourceBundle1NewFile;
    }

    /**
     * @param sourceBundle1NewFile  the sourceBundle1 to set.
     */
    private void setSourceBundle1NewFile(final File sourceBundle1NewFile) {

        this.sourceBundle1NewFile = sourceBundle1NewFile;
    }

    /**
     * @return  the appBundlesRootDir.
     */
    private File getAppBundlesRootDir() {

        return appBundlesRootDir;
    }

    /**
     * @param appBundlesRootDir the appBundlesRootDir to set.
     */
    private void setAppBundlesRootDir(final File appBundlesRootDir) {

        this.appBundlesRootDir = appBundlesRootDir;
    }

    /**
     * @return  the mockResource2.
     */
    private Resource getMockResource2() {

        return mockResource2;
    }

    /**
     * @param mockResource2 the mockResource2 to set.
     */
    public void setMockResource2(final Resource mockResource2) {

        this.mockResource2 = mockResource2;
    }

    /**
     * @return  the sourceBundle2NewFile.
     */
    private File getSourceBundle2NewFile() {

        return sourceBundle2NewFile;
    }

    /**
     * @param sourceBundle2NewFile  the sourceBundle2NewFile to set.
     */
    private void setSourceBundle2NewFile(final File sourceBundle2NewFile) {

        this.sourceBundle2NewFile = sourceBundle2NewFile;
    }

    /**
     * @return  the sourceBundle2NewPath.
     */
    private String getSourceBundle2NewPath() {

        return sourceBundle2NewPath;
    }

    /**
     * @param sourceBundle2NewPath  the sourceBundle2NewPath to set.
     */
    private void setSourceBundle2NewPath(final String sourceBundle2NewPath) {

        this.sourceBundle2NewPath = sourceBundle2NewPath;
    }

    /**
     * @return  the sourceBundle1NewPath.
     */
    private String getSourceBundle1NewPath() {

        return sourceBundle1NewPath;
    }

    /**
     * @param sourceBundle1NewPath  the sourceBundle1NewPath to set.
     */
    private void setSourceBundle1NewPath(final String sourceBundle1NewPath) {

        this.sourceBundle1NewPath = sourceBundle1NewPath;
    }

    /**
     * @return  the mockPageContext.
     */
    private PageContext getMockPageContext() {

        return mockPageContext;
    }

    /**
     * @param mockPageContext   the mockPageContext to set.
     */
    public void setMockPageContext(final PageContext mockPageContext) {

        this.mockPageContext = mockPageContext;
    }

    /**
     * @return  the mockWebApplicationContext.
     */
    private WebApplicationContext getMockWebApplicationContext() {

        return mockWebApplicationContext;
    }

    /**
     * @param mockWebApplicationContext the mockWebApplicationContext to set.
     */
    public void setMockWebApplicationContext(
            final WebApplicationContext mockWebApplicationContext) {

        this.mockWebApplicationContext = mockWebApplicationContext;
    }

    /**
     * @return  the springMockServletContext.
     */
    private MockServletContext getSpringMockServletContext() {

        return springMockServletContext;
    }

    /**
     * @param springMockServletContext  the springMockServletContext to set.
     */
    private void setSpringMockServletContext(final MockServletContext springMockServletContext) {

        this.springMockServletContext = springMockServletContext;
    }

    /**
     * @return  the bundleScriptsTagDependencies.
     */
    private BundleTagDependencies getBundleScriptsTagDependencies() {

        return bundleScriptsTagDependencies;
    }

    /**
     * @param bundleScriptsTagDependencies  the bundleScriptsTagDependencies to set.
     */
    private void setBundleScriptsTagDependencies(
            final BundleTagDependencies bundleScriptsTagDependencies) {

        this.bundleScriptsTagDependencies = bundleScriptsTagDependencies;
    }

    /**
     * @return  the mockBundleScriptsTagCache.
     */
    private BundleTagCache getMockBundleScriptsTagCache() {

        return mockBundleScriptsTagCache;
    }

    /**
     * @param mockBundleScriptsTagCache the mockBundleScriptsTagCache to set.
     */
    public void setMockBundleScriptsTagCache(final BundleTagCache mockBundleScriptsTagCache) {

        this.mockBundleScriptsTagCache = mockBundleScriptsTagCache;
    }
}
