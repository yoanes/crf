package au.com.sensis.mobile.crf.presentation.tag;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

public class JspContextBundleTagStackBeanTestCase extends AbstractJUnit4TestCase {

    private static final String BUNDLE_TAG_REQUEST_ATTRIBUTE_NAME = "parentBundleScriptsTag";

    private JspContextBundleTagStackBean objectUnderTest;

    private Deque<AbstractBundleTag> bundleScriptsTagDeque;

    private JspContext mockJspContext;
    private AbstractBundleTag mockBundleTag;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new JspContextBundleTagStackBean(BUNDLE_TAG_REQUEST_ATTRIBUTE_NAME));
        setBundleScriptsTagDeque(new ArrayDeque<AbstractBundleTag>());
    }

    @Test
    public void testPushBundleTagWhenNoDequeAttributeSet() throws Exception {

        recordGetBundleTagDeque(null);

        recordSetNewDequeIntoJspContext();

        getBundleScriptsTagDeque().addFirst(getMockBundleTag());
        recordGetBundleTagDeque(getBundleScriptsTagDeque());

        replay();

        getObjectUnderTest().pushBundleTag(getMockJspContext(), getMockBundleTag());

        Assert.assertSame("getBundleTag returned wrong tag", getMockBundleTag(),
                getObjectUnderTest().getBundleTag(getMockJspContext()));

    }

    @Test
    public void testPushBundleTagWhenDequeAttributeSet() throws Exception {

        recordGetBundleTagDeque(new ArrayDeque<AbstractBundleTag>());

        getBundleScriptsTagDeque().addFirst(getMockBundleTag());
        recordGetBundleTagDeque(getBundleScriptsTagDeque());

        replay();

        getObjectUnderTest().pushBundleTag(getMockJspContext(), getMockBundleTag());

        Assert.assertSame("getBundleTag returned wrong tag", getMockBundleTag(),
                getObjectUnderTest().getBundleTag(getMockJspContext()));


    }

    @Test
    public void testRemoveBundleTagWhenDequeAttributeSetAndContainsOneBundleTag() throws Exception {

        getBundleScriptsTagDeque().addFirst(getMockBundleTag());
        recordGetBundleTagDeque(getBundleScriptsTagDeque());

        replay();

        getObjectUnderTest().removeBundleTag(getMockJspContext());


        Assert.assertTrue("deque should be empty", getBundleScriptsTagDeque().isEmpty());
    }

    private void recordSetNewDequeIntoJspContext() {
        getMockJspContext().setAttribute(
                EasyMock.eq(BUNDLE_TAG_REQUEST_ATTRIBUTE_NAME),
                BundleTagStackArgumentMatcher.sameStackObjects(new ArrayDeque<AbstractBundleTag>()),
                EasyMock.eq(PageContext.REQUEST_SCOPE));
    }

    private void recordGetBundleTagDeque(final Deque<AbstractBundleTag> deque) {
        EasyMock.expect(
                getMockJspContext().getAttribute(BUNDLE_TAG_REQUEST_ATTRIBUTE_NAME,
                        PageContext.REQUEST_SCOPE)).andReturn(deque);

    }

    /**
     * @return the objectUnderTest
     */
    private JspContextBundleTagStackBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final JspContextBundleTagStackBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockJspContext
     */
    public JspContext getMockJspContext() {
        return mockJspContext;
    }

    /**
     * @param mockJspContext the mockJspContext to set
     */
    public void setMockJspContext(final JspContext mockJspContext) {
        this.mockJspContext = mockJspContext;
    }

    /**
     * @return the mockBundleTag
     */
    public AbstractBundleTag getMockBundleTag() {
        return mockBundleTag;
    }

    /**
     * @param mockBundleTag the mockBundleTag to set
     */
    public void setMockBundleTag(final AbstractBundleTag mockBundleTag) {
        this.mockBundleTag = mockBundleTag;
    }

    /**
     * @return the bundleScriptsTagDeque
     */
    private Deque<AbstractBundleTag> getBundleScriptsTagDeque() {
        return bundleScriptsTagDeque;
    }

    /**
     * @param bundleScriptsTagDeque the bundleScriptsTagDeque to set
     */
    private void setBundleScriptsTagDeque(final Deque<AbstractBundleTag> bundleScriptsTagDeque) {
        this.bundleScriptsTagDeque = bundleScriptsTagDeque;
    }

}
