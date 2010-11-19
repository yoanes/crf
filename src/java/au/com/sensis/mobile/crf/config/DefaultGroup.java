package au.com.sensis.mobile.crf.config;


/**
 * {@link DefaultGroup} that always matches.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DefaultGroup extends Group {

    private static final String ALWAYS_TRUE_EXPRESSION = "true";

    /**
     * Always returns "true".
     * {@inheritDoc}
     */
    @Override
    public String getExpr() {
        return ALWAYS_TRUE_EXPRESSION;
    }

    /**
     * Always throws {@link IllegalStateException}.
     *
     * {@inheritDoc}
     */
    @Override
    public void setExpr(final String expr) {
        throw new IllegalStateException(
                "It is illegal to set the expr for DefaultGroup.");
    }

    /**
     * Always returns true.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isDefault() {
        return true;
    }
}
