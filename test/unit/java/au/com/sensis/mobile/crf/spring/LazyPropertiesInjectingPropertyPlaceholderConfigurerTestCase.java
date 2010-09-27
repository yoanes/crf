package au.com.sensis.mobile.crf.spring;

import java.util.Properties;

import junitx.util.PrivateAccessor;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link LazyPropertiesInjectingPropertyPlaceholderConfigurer}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LazyPropertiesInjectingPropertyPlaceholderConfigurerTestCase
        extends AbstractJUnit4TestCase {

    private static final String PROPERTIES_BEAN_NAME = "my bean name";

    private LazyPropertiesInjectingPropertyPlaceholderConfigurer objectUnderTest;

    private BeanFactory mockBeanFactory;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        objectUnderTest = new LazyPropertiesInjectingPropertyPlaceholderConfigurer();
        objectUnderTest.setPropertiesBeanName(PROPERTIES_BEAN_NAME);
        objectUnderTest.setBeanFactory(getMockBeanFactory());
    }

    @Test
    public void testSetProperties() throws Throwable {
        try {
            getObjectUnderTest().setProperties(new Properties());
            Assert.fail("UnsupportedOperationException expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertEquals("UnsupportedOperationException has wrong message",
                    "setProperties is unsupported. Call setPropertiesBeanName instead, "
                    + "otherwise use a regular PropertyPlaceholderConfigurer.", e.getMessage());
        }
    }

    @Test
    public void testSetPropertiesArray() throws Throwable {
        try {
            getObjectUnderTest().setPropertiesArray(new Properties [] {});
            Assert.fail("UnsupportedOperationException expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertEquals("UnsupportedOperationException has wrong message",
                    "setPropertiesArray is unsupported. Call setPropertiesBeanName instead, "
                    + "otherwise use a regular PropertyPlaceholderConfigurer.", e.getMessage());
        }
    }

    @Test
    // TODO: naughty. Testing private method here but easiest option. Testing via
    // the public method requires a fair bit of effort to dummy up Spring classes.
    public void testLookupPropertiesBean() throws Throwable {
        final Properties expectedProperties = new Properties();
        EasyMock.expect(getMockBeanFactory().getBean(PROPERTIES_BEAN_NAME))
                .andReturn(expectedProperties);
        replay();

        final Properties actualProperties =
                (Properties) PrivateAccessor.invoke(getObjectUnderTest(),
                        "lookupPropertiesBean", null, null);
        Assert.assertSame("actualProperties is wrong", expectedProperties,
                actualProperties);
    }



    /**
     * @return the objectUnderTest
     */
    public LazyPropertiesInjectingPropertyPlaceholderConfigurer getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @return the mockBeanFactory
     */
    public BeanFactory getMockBeanFactory() {
        return mockBeanFactory;
    }

    /**
     * @param mockBeanFactory the mockBeanFactory to set
     */
    public void setMockBeanFactory(final BeanFactory mockBeanFactory) {
        this.mockBeanFactory = mockBeanFactory;
    }
}
