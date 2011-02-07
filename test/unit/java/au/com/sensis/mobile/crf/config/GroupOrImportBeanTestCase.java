package au.com.sensis.mobile.crf.config;

import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link GroupOrImportBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupOrImportBeanTestCase extends AbstractJUnit4TestCase {

    @Test
    public void testConstructorWhenNullGroup() throws Throwable {
        try {
            new GroupOrImportBean((Group) null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "group must not be null.", e.getMessage());
        }

    }

    @Test
    public void testConstructorWhenNullGroupImport() throws Throwable {
        try {
            new GroupOrImportBean((GroupImport) null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "groupImport must not be null.", e.getMessage());
        }

    }
}
