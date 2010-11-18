package au.com.sensis.mobile.crf.debug;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import au.com.sensis.wireless.web.framework.ServletResponseCaptureWrapper;

/**
 * Filter that outputs a plain text graph of the resolved resources for the current request.
 * Output is made in two forms:
 * <ol>
 * <li>Inserted as a comment directly under the html element in the response stream.</li>
 * <li>Log4j if debug logging is enabled.</li>
 * </ol>
 */
public class ResourceResolverDebugFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(ResourceResolverDebugFilter.class);

    private boolean enabled = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(final ServletRequest servletRequest,
            final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {

        if (isEnabled()) {

            logDebugWhenEnabled();

            filterResponseToInsertResolvedResourceTree(servletRequest, servletResponse,
                    filterChain);
        } else {

            logDebugWhenDisabled();

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void filterResponseToInsertResolvedResourceTree(final ServletRequest servletRequest,
            final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        initResourceResolutionTreeForCurrentThread();

        try {
            final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

            final ServletResponseCaptureWrapper responseWrapper =
                    captureResponseFromFilterChain(servletRequest, filterChain,
                            httpServletResponse);

            final String resolvedResourcesGraphAsPlainText =
                    ResourceResolutionTreeHolder.getResourceResolutionTree().graphAsPlainText();

            addTreeGraphToResponse(resolvedResourcesGraphAsPlainText, responseWrapper
                    .getCapturedOutput(), httpServletResponse);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Resource resolution tree:\n" + resolvedResourcesGraphAsPlainText);
            }

        } finally {
            removeResourceResolutionTreeForCurrentThread();
        }
    }

    private ServletResponseCaptureWrapper captureResponseFromFilterChain(
            final ServletRequest servletRequest, final FilterChain filterChain,
            final HttpServletResponse httpServletResponse) throws IOException, ServletException {
        final ServletResponseCaptureWrapper responseWrapper =
                new ServletResponseCaptureWrapper(httpServletResponse);

        filterChain.doFilter(servletRequest, responseWrapper);
        return responseWrapper;
    }

    private void initResourceResolutionTreeForCurrentThread() {
        ResourceResolutionTreeHolder.setResourceResolutionTree(new ResourceResolutionTree(true));
    }

    private void removeResourceResolutionTreeForCurrentThread() {
        ResourceResolutionTreeHolder.removeResourceResolutionTree();
    }

    private void addTreeGraphToResponse(final String resolvedResourcesGraph,
            final String capturedResponseOutput, final HttpServletResponse httpServletResponse)
            throws IOException {
        // Use regex replacement instead of XML parsing. We tried using StaX (ie. the Streaming API
        // for XML) but the XMLEventWriter will not gurantee that empty tags in the source
        // remain as empty tags instead of being self closing tags.
        final String modifiedResponse =
                capturedResponseOutput.replaceFirst("(<html.*?>)", "$1<!--\n"
                        + resolvedResourcesGraph + "-->");
        httpServletResponse.getWriter().print(modifiedResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final FilterConfig arg0) throws ServletException {
        // Do nothing.
    }

    /**
     * @return true if this filter is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled true if this filter is enabled.
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private void logDebugWhenDisabled() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ResourceResolverDebugFilter disabled. "
                    + "Will pass response straight through.");
        }
    }

    private void logDebugWhenEnabled() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ResourceResolverDebugFilter enabled. "
                    + "Will wrap response and insert debug graph into it.");
        }
    }
}
