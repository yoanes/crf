package au.com.sensis.mobile.crf.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link GroupsAndImports}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsAndImportsTestCase extends AbstractJUnit4TestCase {

    private GroupsAndImports objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new GroupsAndImports());
    }

    @Test
    public void testSetGroupOrImportWhenNull() throws Throwable {
        try {
            getObjectUnderTest().setGroupOrImport(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "groupOrImport must not be null.", e.getMessage());
        }

    }

    @Test
    public void testSetDefaultGroupWhenNull() throws Throwable {
        try {
            getObjectUnderTest().setDefaultGroup(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "defaultGroup must not be null.", e.getMessage());
        }

    }

    /**
     * @return the objectUnderTest
     */
    private GroupsAndImports getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final GroupsAndImports objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

}
