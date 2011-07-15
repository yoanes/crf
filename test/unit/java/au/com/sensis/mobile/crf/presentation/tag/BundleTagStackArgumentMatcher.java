package au.com.sensis.mobile.crf.presentation.tag;

import java.util.Deque;
import java.util.Iterator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

/**
 * {@link IArgumentMatcher} implementation to check that two {@link Deque}s contain
 * the same object references.
 *
 * @author Adrian Koh
 */
public class BundleTagStackArgumentMatcher
        implements IArgumentMatcher {

    private final Deque<? extends AbstractBundleTag> expectedStack;

    /**
     * @param expectedStack
     *            the {@link Deque} expected to have been passed in the test.
     */
    public BundleTagStackArgumentMatcher(
            final Deque<? extends AbstractBundleTag> expectedStack) {

        this.expectedStack = expectedStack;
    }

    /**
     * @param actual    the actual {@link Object} passed during test.
     *
     * @return true if the actual object equals the expected object.
     */
    public boolean matches(final Object actual) {

        if (actual == getExpectedStack()) {

            return true;

        } else if ((actual == null) || !Deque.class.isAssignableFrom(actual.getClass())) {

            return false;

        } else {

            final Deque actualStack = (Deque) actual;
            return compareStacks(actualStack);
        }
    }

    private boolean compareStacks(final Deque<Object> actual) {

        if (getExpectedStack().size() != actual.size()) {
            return false;
        }

        final Iterator<Object> itActualObject = actual.iterator();
        for (final Object expectedObject : getExpectedStack()) {
            // Yes, we really do mean !=
            if (expectedObject != itActualObject.next()) {
                return false;
            }
        }
        return true;
    }

    /***
     * See {@link IArgumentMatcher#appendTo(StringBuffer)}.
     *
     * @param buffer    the {@link StringBuffer} to append a message to.
     */
    public void appendTo(final StringBuffer buffer) {

        buffer.append("sameStackObjects(");
        buffer.append(ToStringBuilder.reflectionToString(getExpectedStack()));
        buffer.append(")");

    }

    /**
     * A utility method to allow this {@link IArgumentMatcher} to be used in the same way that
     * methods like {@link EasyMock#same(Object)} can be used.
     *
     * @param in
     *            the expected {@link MapCriteria}.
     *
     * @return same as passed in arg.
     */
    public static Deque<? extends AbstractBundleTag> sameStackObjects(
            final Deque<? extends AbstractBundleTag> in) {

        EasyMock.reportMatcher(new BundleTagStackArgumentMatcher(in));
        return null;
    }

    /**
     * @return  the expectedStack.
     */
    public Deque<? extends AbstractBundleTag> getExpectedStack() {

        return expectedStack;
    }
}
