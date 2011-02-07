/**
 *
 */
package au.com.sensis.mobile.crf.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <p>
 * <strong>NOTE:</strong> Serializable to support caching of these objects. Caching APIs commonly
 * require objects stored in the cache to be Serializable.
 * </p>
 * @author Adrian.Koh2@sensis.com.au
 */
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * Index of this {@link Group} in the {@link Groups} that it belongs to. This is not the same
     * as an array index because it doesn't have to be sequential.
     *
     * <p>
     * Note that this field is <b>transient</b>. This field is only
     * used privately during {@link #match(Device)} evaluation. It is anticipated
     * that any caching of {@link Group} instances will only occur to cache the result
     * of {@link #match(Device)} evaluation.
     * </p>
     */
    private transient int index;

    /**
     * {@link Groups} that this {@link Group} belongs to.
     *
     * <p>
     * Note that this field is <b>transient</b>. This field is only
     * used privately during {@link #match(Device)} evaluation. It is anticipated
     * that any caching of {@link Group} instances will only occur to cache the result
     * of {@link #match(Device)} evaluation.
     * </p>
     */
    private transient Groups parentGroups;

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
            jexl.setFunctions(createJexlFunctionsMap());

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

    private Map<String, Object> createJexlFunctionsMap() {
        final Map<String, Object> functionsMap = new HashMap<String, Object>();
        functionsMap.put(null, this);
        return functionsMap;
    }

    /**
     * @param device
     *            {@link Device} of the current request.
     * @param groupNames
     *            Names of groups to be checked against the device.
     * @return true if the given device belongs to all of the passed in groups.
     */
    public boolean inAllGroups(final Device device, final String ... groupNames) {
        validateReferencedGroups(groupNames);

        for (final String groupName : groupNames) {
            if (!getParentGroups().getGroupByName(groupName).match(device)) {
                return false;
            }
        }

        return true;
    }

    private void validateReferencedGroups(final String[] groupNames) {
        validateReferencedGroupNames(groupNames);
        validateReferencesEarlierGroupsOnly(groupNames);
    }

    private void validateReferencedGroupNames(final String[] groupNames) {
        // TODO: also validate no references to the current or later groups.
        final List<String> invalidGroupNames = new ArrayList<String>();
        for (final String groupName : groupNames) {
            if (getParentGroups().getGroupByName(groupName) == null) {
                invalidGroupNames.add(groupName);
            }
        }

        if (!invalidGroupNames.isEmpty()) {
            throw new GroupEvaluationRuntimeException("Expression references unrecognised groups: "
                    + invalidGroupNames + ".");
        }

    }

    private void validateReferencesEarlierGroupsOnly(final String[] groupNames) {
        final List<String> illegalGroupNames = new ArrayList<String>();

        for (final String groupName : groupNames) {
            if (getParentGroups().getGroupByName(groupName).getIndex() >= getIndex()) {
                illegalGroupNames.add(groupName);
            }
        }

        if (!illegalGroupNames.isEmpty()) {
            throw new GroupEvaluationRuntimeException(
                    "Illegal for expression to reference the current group or later groups "
                            + "since this may lead to a cyclic dependency: " + illegalGroupNames
                            + ".");
        }
    }

    /**
     * @param device
     *            {@link Device} of the current request.
     * @param groupNames
     *            Names of groups to be checked against the device.
     * @return true if the given device belongs to any of the passed in groups.
     */
    public boolean inAnyGroup(final Device device, final String ... groupNames) {
        validateReferencedGroups(groupNames);

        for (final String groupName : groupNames) {
            if (getParentGroups().getGroupByName(groupName).match(device)) {
                return true;
            }
        }

        return false;
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
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(final int index) {
        this.index = index;
    }


    /**
     * @return the parentGroups
     */
    public Groups getParentGroups() {
        return parentGroups;
    }

    /**
     * @param parentGroups the parentGroups to set
     */
    public void setParentGroups(final Groups parentGroups) {
        this.parentGroups = parentGroups;
    }

    /**
     * @return true if this group is a default group. The default implementation
     *         always returns false.
     */
    public boolean isDefault() {
        return false;
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

        // Ignore getParentGroups() due to cyclic dependency.

        // Don't include index. The index is really just associated with the container
        // just that we're lazy and stash it in this group rather than create another
        // wrapper. Not including the index allows us to detect if a group is a duplicate,
        // even if it has a different index.

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

        // Ignore getParentGroups() due to cyclic dependency.

        // Don't include index. The index is really just associated with the container
        // just that we're lazy and stash it in this group rather than create another
        // wrapper. Not including the index allows us to detect if a group is a duplicate,
        // even if it has a different index.


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
        toStringBuilder.append("index", getIndex());

        // Ignore getParentGroups() due to cyclic dependency.

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
