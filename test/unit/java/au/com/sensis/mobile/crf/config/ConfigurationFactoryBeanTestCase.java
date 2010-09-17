package au.com.sensis.mobile.crf.config;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.exception.ContentRenderingFrameworkRuntimeException;
import au.com.sensis.mobile.crf.exception.GroupEvaluationRuntimeException;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ConfigurationFactoryBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ConfigurationFactoryBeanTestCase extends
        AbstractJUnit4TestCase {

    private static final String CRF_CONFIG_MULTIPLE_VALID_GROUPS
        = "/au/com/sensis/mobile/crf/config/crf-config-multiple-valid-groups.xml";

    private static final String CRF_CONFIG_ONE_INVALID_EXPR
        = "/au/com/sensis/mobile/crf/config/crf-config-one-invalid-expr.xml";
    private static final String CRF_CONFIG_MULTIPLE_INVALID_EXPR
        = "/au/com/sensis/mobile/crf/config/crf-config-multiple-invalid-expr.xml";

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testConstructorWhenFileNotFound() throws Throwable {
        final String mappingConfigurationClasspath =
                "/file does not exist on classpath";
        try {
            new ConfigurationFactoryBean(
                    mappingConfigurationClasspath);
            Assert.fail("ContentRenderingFrameworkRuntimeException expected");
        } catch (final ContentRenderingFrameworkRuntimeException e) {

            Assert
                    .assertEquals(
                            "ContentRenderingFrameworkRuntimeException has wrong message",
                            "Error loading config from classpath: '"
                                    + mappingConfigurationClasspath + "'", e
                                    .getMessage());
        }
    }

    @Test
    public void testConstructorWhenSchemaValidationFails() throws Throwable {
        for (final TestData testData : TestData.createTestDataForSchemaValidationFailure()) {
            doTestConstructorWhenSchemaValidationFails(testData);
        }
    }

    private void doTestConstructorWhenSchemaValidationFails(final TestData testData) {
        try {
            new ConfigurationFactoryBean(
                    testData.getConfigFileClasspathLocation());
            Assert.fail("ContentRenderingFrameworkRuntimeException expected for testData: "
                    + testData);
        } catch (final ContentRenderingFrameworkRuntimeException e) {

            Assert.assertEquals("ContentRenderingFrameworkRuntimeException has wrong "
                    + "message for testData: " + testData, "Error loading config from classpath: '"
                    + testData.getConfigFileClasspathLocation() + "'", e.getMessage());

            Assert.assertNotNull("ContentRenderingFrameworkRuntimeException should have a "
                    + "cause for testData: " + testData, e.getCause());

            Assert.assertNotNull("Cause should have a non-null message for testData: " + testData,
                    e.getCause().getMessage());
            Assert.assertTrue("Cause does not contain message matching expected regex: ["
                    + testData.getExpectedExceptionCauseMessage() + "]. Message was: ["
                    + e.getCause().getMessage() + "]",
                    e.getCause().getMessage().matches(
                            testData.getExpectedExceptionCauseMessage()));
        }
    }

    @Test(expected = GroupEvaluationRuntimeException.class)
    public void testConstructurWhenOneGroupExpressionInvalid() throws Throwable {
        // Don't assert the contents of the exception, as it is thrown by the Group
        // class (which we can't mock). The GroupTestCase should cover its operation.
        new ConfigurationFactoryBean(CRF_CONFIG_ONE_INVALID_EXPR);
    }

    @Test(expected = GroupEvaluationRuntimeException.class)
    public void testConstructurWhenMultipleGroupExpressionsInvalid() throws Throwable {
        // Don't assert the contents of the exception, as it is thrown by the Group
        // class (which we can't mock). The GroupTestCase should cover its operation.
        new ConfigurationFactoryBean(CRF_CONFIG_MULTIPLE_INVALID_EXPR);
    }

    @Test
    public void testConstructorWhenSchemaValidationSucceeds() throws Throwable {
        for (final TestData testData : TestData.createTestDataForSchemaValidationSuccess()) {
            new ConfigurationFactoryBean(
                    testData.getConfigFileClasspathLocation());
        }
    }

    @Test
    public void testConfigurationLoaded() throws Throwable {
        final ConfigurationFactory configurationFactory =
                new ConfigurationFactoryBean(
                        CRF_CONFIG_MULTIPLE_VALID_GROUPS);

        final UiConfiguration expectedUiConfiguration =
                createUiConfigurationWithMultipleValidGroups();

        assertComplexObjectsEqual("uiConfiguration is wrong",
                expectedUiConfiguration, configurationFactory
                        .getUiConfiguration());
    }

    private UiConfiguration createUiConfigurationWithMultipleValidGroups() {
        final UiConfiguration uiConfiguration = new UiConfiguration();

        final Group group1 = new Group();
        group1.setName("iphone");
        group1.setExpr("device.name =~ '.*iPhone.*'");

        final Group group2 = new Group();
        group2.setName("iPad");
        group2.setExpr("device.name =~ '.*iPad.*'");

        final Groups groups = new Groups();
        groups.setGroups(new Group [] {group1, group2});
        groups.setDefaultGroup(createDefaultGroup());

        uiConfiguration.setGroups(groups);

        return uiConfiguration;
    }

    private DefaultGroup createDefaultGroup() {
        final DefaultGroup defaultGroup = new DefaultGroup();
        defaultGroup.setName("default");
        return defaultGroup;
    }

    private static final class TestData {

        private final String configFileClasspathLocation;
        private String expectedExceptionCauseMessage;

        private TestData(final String configFileClasspathLocation,
                final String expectedExceptionCauseMessage) {
            this.configFileClasspathLocation = configFileClasspathLocation;
            this.expectedExceptionCauseMessage = expectedExceptionCauseMessage;
        }

        private TestData(final String configFileClasspathLocation) {
            this.configFileClasspathLocation = configFileClasspathLocation;
        }

        private static TestData [] createTestDataForSchemaValidationFailure() {
            // NOTE: the expectedExceptionCauseMessage is tied to the schema validator
            // implementation. We'd rather not do this but testing against the explicit
            // messages produces stronger tests than simply testing that an exception
            // was thrown.
            return new TestData [] {
                new TestData("/au/com/sensis/mobile/crf/config/crf-config-no-namespace.xml",
                        ".*Cannot find the declaration of element 'ui-configuration'.*"),
                new TestData("/au/com/sensis/mobile/crf/config/crf-config-no-groups-element.xml",
                        ".*The content of element 'crf:ui-configuration' "
                            + "is not complete. One of '\\{.*groups\\}' is expected.*"),
                new TestData("/au/com/sensis/mobile/crf/config/crf-config-groups-element-empty.xml",
                        ".*content of element 'groups' is not "
                        + "complete. One of '\\{.*group,.*default-group\\}' is expected.*"),
            };
        }

        private static TestData [] createTestDataForSchemaValidationSuccess() {
            return new TestData [] {
                    new TestData(
                        "/au/com/sensis/mobile/crf/config/crf-config-only-default-group.xml"),
                    new TestData(CRF_CONFIG_MULTIPLE_VALID_GROUPS),
            };
        }

        /**
         * @return the configFileClasspathLocation
         */
        private String getConfigFileClasspathLocation() {
            return configFileClasspathLocation;
        }

        /**
         * @return the expectedExceptionCauseMessage
         */
        private String getExpectedExceptionCauseMessage() {
            return expectedExceptionCauseMessage;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .append("configFileClasspathLocation", getConfigFileClasspathLocation())
                .append("expectedExceptionCauseMessage", getExpectedExceptionCauseMessage())
                .toString();
        }




    }
}
