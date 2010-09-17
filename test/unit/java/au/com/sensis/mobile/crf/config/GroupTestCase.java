package au.com.sensis.mobile.crf.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.GroupEvaluationRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.common.volantis.devicerepository.api.ImageCategory;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link Group}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupTestCase extends AbstractJUnit4TestCase {

    private Device mockDevice;

    @Test
    public void testValidateWhenNameIsInvalid() throws Throwable {
        for (final String invalidFilename : generateInvalidGroupNames()) {
            final Group group = new Group();
            group.setName(invalidFilename);

            replay();

            try {
                group.validate(getMockDevice());

                Assert.fail("GroupEvaluationRuntimeException expected for test name: '"
                        + invalidFilename + "");
            } catch (final GroupEvaluationRuntimeException e) {

                Assert.assertEquals(
                    "GroupEvaluationRuntimeException has wrong message",
                    "group has invalid name: '" + invalidFilename
                    + "'. Must start with a letter or digit and contain only letters, "
                    + "digits, underscores and hyphens.", e.getMessage());
            }

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
            final Group group = new Group();
            group.setName("iPhone");
            group.setExpr(invalidExpr);


            EasyMock.expect(getMockDevice().getImageCategory()).andReturn(
                    ImageCategory.HD480).anyTimes();
            replay();

            try {
                group.validate(getMockDevice());

                Assert.fail("GroupEvaluationRuntimeException expected for test expr: '"
                        + invalidExpr + "");
            } catch (final GroupEvaluationRuntimeException e) {

                Assert.assertEquals(
                    "GroupEvaluationRuntimeException has wrong message",
                    "Error evaluating expression '" + invalidExpr
                    + "' for group 'iPhone' and device "
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
            final Group group = new Group();
            group.setName(testName);
            group.setExpr("device.name =~ '.*iPhone.*'");

            EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPhone");

            replay();

            group.validate(getMockDevice());

            // Explicit verify since we are in a loop
            verify();

            // Reset mocks prior to next iteration.
            reset();
        }
    }

    @Test
    public void testMatchWhenExprReturnsTrue() throws Throwable {

        final Group group = new Group();
        group.setName("iPhone");
        group.setExpr("device.name =~ '.*iPhone.*'");

        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPhone");

        replay();

        Assert.assertTrue("match should be true", group.match(getMockDevice()));
    }

    @Test
    public void testMatchWhenExprReturnsFalse() throws Throwable {

        final Group group = new Group();
        group.setName("iPhone");
        group.setExpr("device.name =~ '.*iPhone.*'");

        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPad");

        replay();

        Assert.assertFalse("match should be false", group.match(getMockDevice()));
    }

    @Test
    public void testMatchWhenExprDoesNotEquateToBoolean() throws Throwable {

        final Group group = new Group();
        group.setName("iPhone");
        group.setExpr("device.name");

        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPad");

        replay();

        Assert.assertFalse("match should be false", group.match(getMockDevice()));
    }

    @Test
    public void testMatchWhenExprIsInvalid() throws Throwable {
        final String[] testExpressions =
                { "device.propertyDoesNotExist eq 'HD800'",
                        "device.imageCategory dummyOperator 'M'" };

        for (final String testExpr : testExpressions) {
            final Group group = new Group();
            group.setName("iPhone");
            group.setExpr(testExpr);

            try {
                group.match(getMockDevice());
                Assert.fail("GroupEvaluationRuntimeException expected for test expr: '"
                      + testExpr + "");
            } catch (final GroupEvaluationRuntimeException e) {
                Assert.assertEquals(
                    "GroupEvaluationRuntimeException has wrong message",
                    "Error evaluating expression '" + testExpr + "' for group 'iPhone' and device "
                    + getMockDevice(),
                    e.getMessage());
                Assert.assertNotNull("GroupEvaluationRuntimeException has no cause",
                        e.getCause());
            }

        }
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
}
