package au.com.sensis.mobile.crf.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.jexl2.JexlException;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.exception.GroupEvaluationRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.common.volantis.devicerepository.api.ImageCategory;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;
import au.com.sensis.wireless.web.mobile.ThreadLocalContextObjectsHolder;

/**
 * Unit test {@link Group}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupTestCase extends AbstractJUnit4TestCase {

    private static final String GROUP_NAME = "myGroupName";

    private Device mockDevice;

    private Group objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new Group());
        getObjectUnderTest().setName(GROUP_NAME);

        final Groups parentGroups = createGroups(getObjectUnderTest());
        getObjectUnderTest().setParentGroups(parentGroups);
    }

    @Test
    public void testValidateWhenNameIsInvalid() throws Throwable {
        for (final String invalidFilename : generateInvalidGroupNames()) {
            getObjectUnderTest().setName(invalidFilename);

            replay();

            try {
                getObjectUnderTest().validate(getMockDevice());

                Assert.fail("GroupEvaluationRuntimeException expected for test name: '"
                        + invalidFilename + "");
            } catch (final GroupEvaluationRuntimeException e) {

                Assert.assertEquals(
                    "GroupEvaluationRuntimeException has wrong message",
                    "group has invalid name: '" + invalidFilename
                    + "'. Must start with a letter or digit and contain only letters, "
                    + "digits, underscores and hyphens.", e.getMessage());
            }

            // Explicit verify since we are in a loop
            verify();

            // Reset mocks prior to next iteration.
            reset();
        }
    }

    private String[] generateInvalidGroupNames() {
        final List<String> invalidFilenames = new ArrayList<String>();
        invalidFilenames.addAll(Arrays.asList(null, StringUtils.EMPTY, " ", "  "));

        final String invalidCharacters = "+^#$()&@%=,.:?'\";\\|<{*[!]>}/~`";
        for (int i = 0; i < invalidCharacters.length(); i++) {
            invalidFilenames.add(invalidCharacters.charAt(i) + "myFile");
            invalidFilenames.add("myFile" + invalidCharacters.charAt(i));
        }

        return invalidFilenames.toArray(new String[] {});
    }

    @Test
    public void testValidateWhenExprIsInvalid() throws Throwable {
        final String[] testExpressions =
            { "device.propertyDoesNotExist eq 'HD800'",
                    "device.imageCategory dummyOperator 'M'" };

        for (final String invalidExpr : testExpressions) {
            getObjectUnderTest().setExpr(invalidExpr);


            EasyMock.expect(getMockDevice().getImageCategory()).andReturn(
                    ImageCategory.HD480).anyTimes();
            replay();

            try {
                getObjectUnderTest().validate(getMockDevice());

                Assert.fail("GroupEvaluationRuntimeException expected for test expr: '"
                        + invalidExpr + "");
            } catch (final GroupEvaluationRuntimeException e) {

                Assert.assertEquals(
                    "GroupEvaluationRuntimeException has wrong message",
                    "Error evaluating expression '" + invalidExpr
                    + "' for group '" + GROUP_NAME + "' and device "
                    + getMockDevice(), e.getMessage());
            }

            // Explicit verify since we are in a loop
            verify();

            // Reset mocks prior to next iteration.
            reset();
        }
    }

    @Test
    public void testValidateWhenValid() throws Throwable {
        final String [] testNames = { "w", "w1", "w-1", "width", "width-100" };
        for (final String testName : testNames) {
            getObjectUnderTest().setName(testName);
            getObjectUnderTest().setExpr("device.name =~ '.*iPhone.*'");

            EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPhone");

            replay();

            getObjectUnderTest().validate(getMockDevice());

            // Explicit verify since we are in a loop
            verify();

            // Reset mocks prior to next iteration.
            reset();
        }
    }

    @Test
    public void testMatchWhenExprReturnsTrue() throws Throwable {

        getObjectUnderTest().setExpr("device.name =~ '.*iPhone.*'");

        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPhone");

        replay();

        Assert.assertTrue("match should be true", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenExprReturnsFalse() throws Throwable {

        getObjectUnderTest().setExpr("device.name =~ '.*iPhone.*'");

        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPad");

        replay();

        Assert.assertFalse("match should be false", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenExprDoesNotEquateToBoolean() throws Throwable {

        getObjectUnderTest().setExpr("device.name");

        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPad");

        replay();

        Assert.assertFalse("match should be false", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenExprIsInvalid() throws Throwable {
        final String[] testExpressions =
                { "device.propertyDoesNotExist eq 'HD800'",
                        "device.imageCategory dummyOperator 'M'" };

        for (final String testExpr : testExpressions) {
            getObjectUnderTest().setExpr(testExpr);

            replay();

            try {
                getObjectUnderTest().match(getMockDevice());
                Assert.fail("GroupEvaluationRuntimeException expected for test expr: '" + testExpr
                        + "");
            } catch (final GroupEvaluationRuntimeException e) {
                Assert.assertEquals("GroupEvaluationRuntimeException has wrong message",
                        "Error evaluating expression '" + testExpr + "' for group '" + GROUP_NAME
                                + "' and device " + getMockDevice(), e.getMessage());
                Assert.assertNotNull("GroupEvaluationRuntimeException has no cause", e.getCause());
            }

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();

        }
    }

    @Test
    public void testMatchWhenInAllGroupsInvokedAndTrue() throws Throwable {

        getObjectUnderTest().setExpr("inAllGroups('trueGroup1', 'trueGroup2')");

        replay();

        Assert.assertTrue("match should be true", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenInAllGroupsInvokedAndFalse() throws Throwable {

        getObjectUnderTest().setExpr(
                "inAllGroups('trueGroup1', 'falseGroup1', 'trueGroup2')");

        replay();

        Assert.assertFalse("match should be false", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenInAnyGroupInvokedAndTrue() throws Throwable {
        getObjectUnderTest().setExpr(
                "inAnyGroup('trueGroup1', 'falseGroup1', 'trueGroup2')");

        replay();

        Assert.assertTrue("match should be true", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenInAnyGroupInvokedAndFalse() throws Throwable {
        getObjectUnderTest().setExpr("inAnyGroup('falseGroup1', 'falseGroup2')");

        replay();

        Assert.assertFalse("match should be false", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenGroupMembershipFunctionsInvokedAndReferenceUnknownGroups()
            throws Throwable {

        final String[] expressions =
                { "inAllGroups('trueGroup1', 'unknownGroup1', 'unknownGroup2', 'default')",
                        "inAnyGroup('trueGroup1', 'unknownGroup1', 'unknownGroup2', 'default')" };

        for (final String expr : expressions) {

            getObjectUnderTest().setExpr(expr);

            replay();

            try {
                getObjectUnderTest().match(getMockDevice());

                Assert.fail("GroupEvaluationRuntimeException expected for expr: [" + expr + "]");
            } catch (final GroupEvaluationRuntimeException e) {
                final String errorSuffix = " for expr: [" + expr + "]";

                Assert.assertEquals("GroupEvaluationRuntimeException has wrong message"
                        + errorSuffix, "Error evaluating expression '" + expr + "' for group '"
                        + GROUP_NAME + "' and device " + getMockDevice(), e.getMessage());

                Assert.assertNotNull("GroupEvaluationRuntimeException should have a cause"
                        + errorSuffix, e.getCause());

                Assert.assertTrue("GroupEvaluationRuntimeException cause is wrong type"
                        + errorSuffix, JexlException.class.equals(e.getCause().getClass()));

                Assert.assertNotNull("JexlException should have a cause" + errorSuffix, e
                        .getCause().getCause());
                Assert.assertTrue("JexlException cause is wrong type" + errorSuffix,
                        GroupEvaluationRuntimeException.class.equals(e.getCause().getCause()
                                .getClass()));

                Assert.assertEquals(
                        "GroupEvaluationRuntimeException cause has wrong message + errorSuffix",
                        "Expression references unrecognised groups: "
                                + "[unknownGroup1, unknownGroup2].", e.getCause().getCause()
                                .getMessage());
            }

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }
    }

    @Test
    public void testMatchWhenGroupMembershipFunctionsInvokedAndReferenceCurrentOrLaterGroups()
            throws Throwable {

        final String[] expressions = {
                "inAllGroups('trueGroup1', 'laterGroup1', 'laterGroup2', 'default')",
                "inAnyGroup('trueGroup1', 'laterGroup1', 'laterGroup2', 'default')",
                "inAllGroups('trueGroup1', '" + GROUP_NAME + "', 'laterGroup1')",
                "inAnyGroup('trueGroup1', '" + GROUP_NAME + "', 'laterGroup1')"
        };

        final String[] illegalGroups =
                { "[laterGroup1, laterGroup2, default]",
                        "[laterGroup1, laterGroup2, default]",
                        "[" + GROUP_NAME + ", laterGroup1]",
                        "[" + GROUP_NAME + ", laterGroup1]" };

        for (int i = 0; i < expressions.length; i++) {

            getObjectUnderTest().setExpr(expressions[i]);

            replay();

            try {
                getObjectUnderTest().match(getMockDevice());

                Assert.fail("GroupEvaluationRuntimeException expected for expr: [" + expressions[i]
                        + "]");
            } catch (final GroupEvaluationRuntimeException e) {
                final String errorSuffix = " for expr: [" + expressions[i] + "]";

                Assert.assertEquals("GroupEvaluationRuntimeException has wrong message"
                        + errorSuffix, "Error evaluating expression '" + expressions[i]
                        + "' for group '" + GROUP_NAME + "' and device " + getMockDevice(), e
                        .getMessage());

                Assert.assertNotNull("GroupEvaluationRuntimeException should have a cause"
                        + errorSuffix, e.getCause());

                Assert.assertTrue("GroupEvaluationRuntimeException cause is wrong type"
                        + errorSuffix, JexlException.class.equals(e.getCause().getClass()));

                Assert.assertNotNull("JexlException should have a cause" + errorSuffix, e
                        .getCause().getCause());
                Assert.assertTrue("JexlException cause is wrong type" + errorSuffix,
                        GroupEvaluationRuntimeException.class.equals(e.getCause().getCause()
                                .getClass()));

                Assert.assertEquals(
                        "GroupEvaluationRuntimeException cause has wrong message" + errorSuffix,
                        "Illegal for expression to reference the current group or later groups "
                                + "since this may lead to a cyclic dependency: " + illegalGroups[i]
                                + ".", e.getCause().getCause().getMessage());
            }

            // Explicit verify and reset since we are in a loop.
            verify();
            reset();
        }
    }

    @Test
    public void testMatchWhenThreadLocalContainsExtraObjects() throws Throwable {

        final HashMap<String, Object> objectMap = new HashMap<String, Object>();
        objectMap.put("iPhoneRegex", ".*iPhone.*");
        ThreadLocalContextObjectsHolder.setObjectMap(objectMap);

        getObjectUnderTest().setExpr("device.name =~ iPhoneRegex");

        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPhone");

        replay();

        Assert.assertTrue("match should be true", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenGroupIsAnImport() throws Exception {
        final Groups groupsContainingGroupToImport = createGroupsToImport();
        final Group groupToImport = groupsContainingGroupToImport.getGroupByName("groupToImport");

        getObjectUnderTest().setExpr(StringUtils.EMPTY);
        getObjectUnderTest().setImportedGroup(groupToImport);

        Assert.assertTrue("match should be true", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenImportedGroupInvokesInAnyGroupFunction() throws Exception {
        final Groups groupsContainingGroupToImport = createGroupsToImport();
        final Group groupToImport = groupsContainingGroupToImport
                .getGroupByName("groupToImportWithInAnyGroupFunction");

        getObjectUnderTest().setExpr(StringUtils.EMPTY);
        getObjectUnderTest().setImportedGroup(groupToImport);

        Assert.assertTrue("match should be true", getObjectUnderTest().match(getMockDevice()));
    }

    @Test
    public void testMatchWhenImportedGroupInvokesInAllGroupsFunction() throws Exception {
        final Groups groupsContainingGroupToImport = createGroupsToImport();
        final Group groupToImport = groupsContainingGroupToImport
                .getGroupByName("groupToImportWithInAllGroupsFunction");

        getObjectUnderTest().setExpr(StringUtils.EMPTY);
        getObjectUnderTest().setImportedGroup(groupToImport);

        Assert.assertTrue("match should be true", getObjectUnderTest().match(getMockDevice()));
    }

    private Groups createGroups(final Group groupToInsert) {
        final Groups groups = new Groups();
        final Group trueGroup1 = new Group();
        trueGroup1.setName("trueGroup1");
        trueGroup1.setExpr("true");

        final Group falseGroup1 = new Group();
        falseGroup1.setName("falseGroup1");
        falseGroup1.setExpr("false");

        final Group trueGroup2 = new Group();
        trueGroup2.setName("trueGroup2");
        trueGroup2.setExpr("true");

        final Group falseGroup2 = new Group();
        falseGroup2.setName("falseGroup2");
        falseGroup2.setExpr("false");

        final Group laterGroup1 = new Group();
        laterGroup1.setName("laterGroup1");
        laterGroup1.setExpr("false");

        final Group laterGroup2 = new Group();
        laterGroup2.setName("laterGroup2");
        laterGroup2.setExpr("false");

        final DefaultGroup defaultGroup = new DefaultGroup();
        defaultGroup.setName("default");

        groups.setGroups(new Group[] { trueGroup1, falseGroup1, trueGroup2, falseGroup2,
                groupToInsert, laterGroup1, laterGroup2 });
        groups.setDefaultGroup(defaultGroup);

        return groups;
    }

    private Groups createGroupsToImport() {
        final Groups groups = new Groups();
        final Group importedTrueGroup1 = new Group();
        importedTrueGroup1.setName("importedTrueGroup1");
        importedTrueGroup1.setExpr("true");

        final Group importedFalseGroup1 = new Group();
        importedFalseGroup1.setName("importedFalseGroup1");
        importedFalseGroup1.setExpr("false");

        final Group importedTrueGroup2 = new Group();
        importedTrueGroup2.setName("importedTrueGroup2");
        importedTrueGroup2.setExpr("true");

        final Group groupToImportWithoutGroupFunction = new Group();
        groupToImportWithoutGroupFunction.setName("groupToImport");
        groupToImportWithoutGroupFunction.setExpr("true");

        final Group groupToImportWithInAnyGroupFunction = new Group();
        groupToImportWithInAnyGroupFunction.setName("groupToImportWithInAnyGroupFunction");
        groupToImportWithInAnyGroupFunction.setExpr(
                "inAnyGroup('importedFalseGroup1', 'importedTrueGroup1')");

        final Group groupToImportWithInAllGroupsFunction = new Group();
        groupToImportWithInAllGroupsFunction.setName("groupToImportWithInAllGroupsFunction");
        groupToImportWithInAllGroupsFunction.setExpr(
                "inAllGroups('importedTrueGroup2', 'importedTrueGroup1')");

        final DefaultGroup defaultGroup = new DefaultGroup();
        defaultGroup.setName("default");

        groups.setGroups(new Group[] { importedTrueGroup1, importedFalseGroup1, importedTrueGroup2,
                groupToImportWithoutGroupFunction, groupToImportWithInAnyGroupFunction,
                groupToImportWithInAllGroupsFunction });

        groups.setDefaultGroup(defaultGroup);

        return groups;
    }

    @Test
    public void testIsDefault() throws Throwable {
        final Group group = new Group();

        Assert.assertFalse("isDefault() should be false",
                group.isDefault());
    }

    @Test
    public void testEqualsWhenImportedGroupNullOnBothSides() throws Exception {
        final Group group1 = new Group();
        final Group group2 = new Group();

        Assert.assertEquals("Groups should be equal", group1, group2);

    }

    @Test
    public void testEqualsWhenImportedGroupNullOnLeftSideOnly() throws Exception {
        final Group group1 = new Group();
        final Group group2 = new Group();
        group2.setImportedGroup(new Group());

        Assert.assertFalse("Groups should not be equal", group1.equals(group2));

    }

    @Test
    public void testEqualsWhenImportedGroupNullOnRightSideOnly() throws Exception {
        final Group group1 = new Group();
        group1.setImportedGroup(new Group());

        final Group group2 = new Group();

        Assert.assertFalse("Groups should not be equal", group1.equals(group2));

    }

    /**
     * @return the mockDevice
     */
    public Device getMockDevice() {
        return mockDevice;
    }

    /**
     * @param mockDevice the mockDevice to set
     */
    public void setMockDevice(final Device mockDevice) {
        this.mockDevice = mockDevice;
    }

    /**
     * @return the objectUnderTest
     */
    private Group getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final Group objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}
