package au.com.sensis.mobile.crf.presentation;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Wraps an {@link HttpServletRequest} to provide easier interrogation of it.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public final class HttpServletRequestInterrogator {

    /**
     * See servlet-2_5-mrel2-spec.pdf, page 61: request attribute containing the URI if a
     * request is an include.
     */
    public static final String JAVAX_SERVLET_INCLUDE_REQUEST_URI
        = "javax.servlet.include.request_uri";

    private final HttpServletRequest httpServletRequest;

    /**
     * Default constructor.
     *
     * @param httpServletRequest {@link HttpServletRequest} to wrap.
     */
    public HttpServletRequestInterrogator(final HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * @return the requestUri
     */
    public String getRequestUri() {
        if (requestIsAnInclude()) {
            return transformIncludeUriToContextRelative();
        } else {
            // Path relative to the context root. eg.
            // /WEB-INF/view/jsp/crap/bdp.crap
            return getHttpServletRequest().getServletPath();
        }

    }

    /**
     * @return true if the underlying {@link HttpServletRequest} represents an include.
     */
    public boolean isInclude() {
        return requestIsAnInclude();
    }

    /**
     * @return true if the underlying {@link HttpServletRequest} represents a forward.
     */
    public boolean isForward() {
        return !requestIsAnInclude();
    }

    /**
     * @param req
     * @return
     */
    private boolean requestIsAnInclude() {
        // See servlet-2_5-mrel2-spec.pdf, page 61: peeking at request
        // attributes appears to be the only way to determine that a request
        // is an include.
        return (getIncludeUri() != null)
                && StringUtils.isNotBlank(getIncludeUri().toString());
    }

    /**
     * @return
     */
    private Object getIncludeUri() {
        return getHttpServletRequest().getAttribute(JAVAX_SERVLET_INCLUDE_REQUEST_URI);
    }

    /**
     * @return Transforms the current include URI to be relative to the webapp context root.
     */
    private String transformIncludeUriToContextRelative() {

        final String includeUri = getIncludeUri().toString();

        String includeUriWithoutContextRoot =
                StringUtils.remove(includeUri, getHttpServletRequest().getContextPath());

        if (isRelativeUri(includeUriWithoutContextRoot)) {
            // grab the URI of the original JSP requested then remove the end of
            // the path.
            final String originalPath =
                    StringUtils.substringBeforeLast(getHttpServletRequest().getServletPath(), "/");

            // add the name of the include JSP to the path to create the real
            // path for this resource.
            includeUriWithoutContextRoot = originalPath + "/" + includeUriWithoutContextRoot;
        }

        return includeUriWithoutContextRoot;
    }

    /**
     * @param includeUri
     * @return
     */
    private boolean isRelativeUri(final String includeUri) {
        return StringUtils.startsWith(includeUri, "..")
                || (StringUtils.countMatches(includeUri, "/") < 1);
    }

    /**
     * @return the httpServletRequest
     */
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("requestUri", getRequestUri());
        toStringBuilder.append("isForward", isForward());
        toStringBuilder.append("isInclude", isInclude());
        return toStringBuilder.toString();
    }

}
