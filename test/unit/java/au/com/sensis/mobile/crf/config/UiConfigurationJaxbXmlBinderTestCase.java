package au.com.sensis.mobile.crf.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import au.com.sensis.mobile.crf.config.jaxb.generated.ObjectFactory;
import au.com.sensis.mobile.crf.exception.XmlBinderRuntimeException;
import au.com.sensis.wireless.common.utils.jaxb.JaxbXMLBinderImpl;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link UiConfigurationJaxbXmlBinder}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class UiConfigurationJaxbXmlBinderTestCase extends AbstractJUnit4TestCase {

    private static final String CRF_CONFIG_IMPORT_GLOBAL_GROUPS_CLASSPATH_PATTERN
        = "/au/com/sensis/mobile/crf/config/crf-config-import-global-groups.xml";
    private static final String CRF_CONFIG_IMPORT_WITHOUT_REQUIRED_ATTRIBUTES_CLASSPATH_PATTERN
        = "/au/com/sensis/mobile/crf/config/crf-config-import-without-required-attributes.xml";

    private UiConfigurationJaxbXmlBinder objectUnderTest;
    private JaxbXMLBinderImpl jaxbXMLBinderImpl;
    private GroupTestData groupTestData;
    private GroupImportTestData groupImportTestData;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setJaxbXMLBinderImpl(new JaxbXMLBinderImpl(ObjectFactory.class.getPackage().getName()));
        setObjectUnderTest(new UiConfigurationJaxbXmlBinder(getJaxbXMLBinderImpl()));
        setGroupTestData(new GroupTestData());
        setGroupImportTestData(new GroupImportTestData());
    }

    @Test
    public void testUnmarshall() throws Throwable {
        final URL xmlUrl =
                new ClassPathResource(CRF_CONFIG_IMPORT_GLOBAL_GROUPS_CLASSPATH_PATTERN).getURL();
        final UiConfiguration uiConfiguration = getObjectUnderTest().unmarshall(xmlUrl);

        assertComplexObjectsEqual("uiConfiguration is wrong", createExpectedUiConfiguration(),
                uiConfiguration);
    }

    @Test
    public void testUnmarshallWhenImportWithoutRequiredAttributes() throws Throwable {
        final URL xmlUrl =
                new ClassPathResource(
                        CRF_CONFIG_IMPORT_WITHOUT_REQUIRED_ATTRIBUTES_CLASSPATH_PATTERN).getURL();

        try {
            getObjectUnderTest().unmarshall(xmlUrl);

            Assert.fail("XmlBinderRuntimeException expected");
        } catch (final XmlBinderRuntimeException e) {

            Assert.assertEquals("XmlBinderRuntimeException has wrong message",
                    "Error unmarshalling XML from: " + xmlUrl, e.getMessage());

            Assert.assertNotNull("XmlBinderRuntimeException should have a cause.", e.getCause());

            Assert.assertEquals("XmlBinderRuntimeException cause has wrong message",
                    "import element must have at least one of 'name', "
                            + "'fromName' or 'fromConfigPath' attributes set.", e.getCause()
                            .getMessage());
        }

    }

    private UiConfiguration createExpectedUiConfiguration() {
        final UiConfiguration uiConfiguration = new UiConfiguration();
        uiConfiguration.setConfigPath(StringUtils.EMPTY);
        uiConfiguration.setGroupsAndImports(createGroupsAndImports());
        return uiConfiguration;
    }

    private GroupsAndImports createGroupsAndImports() {
        final GroupsAndImports groupsAndImports = new GroupsAndImports();

        groupsAndImports.setGroupOrImport(createGroupOrImportArray());
        groupsAndImports.setDefaultGroup(getGroupTestData().createDefaultGroup());

        return groupsAndImports;
    }

    private GroupOrImport[] createGroupOrImportArray() {
        final List<GroupOrImport> resultList = new ArrayList<GroupOrImport>();

        resultList.add(new GroupOrImportBean(
                getGroupTestData().createIPhoneGroup()));

        resultList.add(new GroupOrImportBean(
                getGroupImportTestData().createAndroidOsImportFromDefaultNamespace()));

        resultList.add(new GroupOrImportBean(
                getGroupImportTestData().createRenamedNokia6120cImportFromDefaultNamespace()));

        resultList.add(new GroupOrImportBean(
                getGroupImportTestData().createiPadImportFromNonDefaultNamespace()));

        resultList.add(new GroupOrImportBean(
                getGroupImportTestData().createRenamedNokia6720cImportFromNonDefaultNamespace()));

        resultList.add(new GroupOrImportBean(
                getGroupTestData().createAppleWebkitGroup()));

        resultList.add(new GroupOrImportBean(
                getGroupImportTestData().createImageCategoriesGroupImport()));

        return resultList.toArray(new GroupOrImport [] {});
    }


    /**
     * @return the objectUnderTest
     */
    private UiConfigurationJaxbXmlBinder getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final UiConfigurationJaxbXmlBinder objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the jaxbXMLBinderImpl
     */
    private JaxbXMLBinderImpl getJaxbXMLBinderImpl() {
        return jaxbXMLBinderImpl;
    }

    /**
     * @param jaxbXMLBinderImpl the jaxbXMLBinderImpl to set
     */
    private void setJaxbXMLBinderImpl(final JaxbXMLBinderImpl jaxbXMLBinderImpl) {
        this.jaxbXMLBinderImpl = jaxbXMLBinderImpl;
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @param groupTestData the groupTestData to set
     */
    private void setGroupTestData(final GroupTestData groupTestData) {
        this.groupTestData = groupTestData;
    }

    /**
     * @return the groupImportTestData
     */
    private GroupImportTestData getGroupImportTestData() {
        return groupImportTestData;
    }

    /**
     * @param groupImportTestData the groupImportTestData to set
     */
    private void setGroupImportTestData(final GroupImportTestData groupImportTestData) {
        this.groupImportTestData = groupImportTestData;
    }


}
