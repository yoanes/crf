/**
 *
 */
package au.com.sensis.mobile.crf.config;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.mobile.crf.exception.GroupEvaluationRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class Group {

    /**
     * Name of this group. See {@link #validate(Device)} for valid values.
     */
    private String name;

    /**
     * Expression that controls which devices match this group.
     * See {@link #validate(Device)} for valid values.
     */
    private String expr;

    /**
     * Returns true if this group matches the given {@link Device}.
     *
     * @param device
     *            {@link Device} of the current request.
     * @return true if this group matches the given {@link Device}.
     * @throws GroupEvaluationRuntimeException if an error occurs.
     */
    public boolean match(final Device device)
        throws GroupEvaluationRuntimeException {
        try {
            final JexlEngine jexl = new JexlEngine();
            jexl.setLenient(false);
            jexl.setSilent(false);

            final Expression e = jexl.createExpression(getExpr());

            final JexlContext jc = new MapContext();
            jc.set("device", device);

            // Now evaluate the expression, getting the result
            final Object o = e.evaluate(jc);

            if (o instanceof Boolean) {
                return (Boolean) o;
            }

            return false;
        } catch (final Exception e) {
            throw new GroupEvaluationRuntimeException(
                    "Error evaluating expression '" + getExpr()
                            + "' for group '" + getName() + "' and device "
                            + device, e);
        }
    }

    /**
     * @return Name of this group. See {@link #validate(Device)} for valid
     *         values.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            Name of this group. See {@link #validate(Device)} for valid
     *            values.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return Expression that controls which devices match this group. See
     *         {@link #validate(Device)} for valid values.
     */
    public String getExpr() {
        return expr;
    }

    /**
     * @param expr
     *            Expression that controls which devices match this group. See
     *            {@link #validate(Device)} for valid values.
     */
    public void setExpr(final String expr) {
        this.expr = expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        final Group rhs = (Group) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getName(), rhs.getName());
        equalsBuilder.append(getExpr(), rhs.getExpr());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getName());
        hashCodeBuilder.append(getExpr());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("name", getName());
        toStringBuilder.append("expr", getExpr());
        return toStringBuilder.toString();
    }

    /**
     * Validate the state of this {@link Group}.
     *
     * @param mockDevice
     *            Device to use to evaluate the validity of {@link #getExpr()}.
     * @throws GroupEvaluationRuntimeException
     *             Thrown if any field is invalid.
     */
    public void validate(final Device mockDevice)
            throws GroupEvaluationRuntimeException {
        if (!StringUtils.isNotEmpty(getName())
                || !getName().matches("\\w(\\w|-)*")) {
            throw new GroupEvaluationRuntimeException(
                "group has invalid name: '"
                + getName()
                + "'. Must start with a letter or digit and contain only letters, "
                + "digits, underscores and hyphens.");
        }

        validateExpr(mockDevice);
    }

    private void validateExpr(final Device mockDevice) {
        match(mockDevice);
    }

}
