package au.com.sensis.mobile.crf.service;

/**
 * Null {@link MappedResourcePath} as in Martin Fowler's "Introduce Null Object"
 * refactoring.
 *
 * <p>
 * Any method that returns a {@link MappedResourcePath} should
 * return a {@link NullMappedResourcePath} instead of null. This prevents
 * clients from having to check for nulls, in turn reducing their complexity
 * and the complexity of their unit tests. This
 * {@link NullMappedResourcePath} behaves polymorphically in a sensible way. eg.
 * {@link #exists()} always returns false.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class NullMappedResourcePath extends MappedResourcePathBean {

    /**
     * Default constructor.
     *
     * @param originalResourcePath
     *            Original path that was requested.
     */
    public NullMappedResourcePath(final String originalResourcePath) {
        super(originalResourcePath, originalResourcePath, null);
    }

    /**
     * Always returns false.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean exists() {
        return false;
    }

    /**
     * Always returns false.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean endsWithDotNull() {
        return false;
    }
}
