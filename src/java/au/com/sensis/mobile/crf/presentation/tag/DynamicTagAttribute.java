package au.com.sensis.mobile.crf.presentation.tag;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Simple bean representing a dynamic tag attribute set into a
 * {@link javax.servlet.jsp.tagext.DynamicAttributes} implementation.
 * <p>
 * Cloned from mobileComponents/core project. Such a simple and specific class
 * that it maybe needn't be centralised anywhere.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DynamicTagAttribute {

    private final String namespaceUri;
    private final String localName;
    private final Object value;

    /**
     * Constructor which assumes that {@link #getNamespaceUri()} is null (ie. default
     * namespace).
     *
     * @param localName name of the attribute being set.
     * @param value value of the attribute
     */
    public DynamicTagAttribute(final String localName, final Object value) {
        this(null, localName, value);
    }

    /**
     * Default constructor.
     *
     * @param namespaceUri namespace of the attribute, or null if in the default namespace.
     * @param localName name of the attribute being set.
     * @param value value of the attribute
     */
    public DynamicTagAttribute(final String namespaceUri,
            final String localName, final Object value) {
        this.namespaceUri = namespaceUri;
        this.localName = localName;
        this.value = value;
    }

    /**
     * @return the namespaceUri
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * @return the localName
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
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

        final DynamicTagAttribute rhs = (DynamicTagAttribute) obj;
        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(getNamespaceUri(), rhs.getNamespaceUri());
        equalsBuilder.append(getLocalName(), rhs.getLocalName());
        equalsBuilder.append(getValue(), rhs.getValue());
        return equalsBuilder.isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getNamespaceUri());
        hashCodeBuilder.append(getLocalName());
        hashCodeBuilder.append(getValue());
        return hashCodeBuilder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("namespaceUri", getNamespaceUri());
        toStringBuilder.append("localName", getLocalName());
        toStringBuilder.append("value", getValue());
        return toStringBuilder.toString();
    }


}
