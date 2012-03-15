package au.com.sensis.mobile.crf.presentation;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HttpServletBean;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTree;
import au.com.sensis.mobile.crf.debug.ResourceResolutionTreeHolder;
import au.com.sensis.mobile.crf.debug.ResourceTreeNodeBean;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Maps abstract JSP paths ( eg. /WEB-INF/view/jsp/detail/bdp.crf ) to concrete,
 * device specific JSP paths (eg. /WEB-INF/view/jsp/HD800/detail/bdp.jsp ), then
 * dispatches the request to the concrete path. Handles both JSP forwards and includes.
 * The actual path mapping is performed by delegating to
 * {@link ResourceResolverEngine}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceResolverServlet extends HttpServletBean {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(ResourceResolverServlet.class);

    private ResourceResolverEngine resourceResolverEngine;

    private String resourceResolverEngineBeanName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        validateResourceResolverEngineBeanName();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils
                .getRequiredWebApplicationContext(config
                        .getServletContext());
        setResourceResolverEngine((ResourceResolverEngine) webApplicationContext
                .getBean(getResourceResolverEngineBeanName()));
    }

    private void validateResourceResolverEngineBeanName() {
        if (StringUtils
                .isBlank(getResourceResolverEngineBeanName())) {
            throw new IllegalStateException(
                    "resourceResolverEngineBeanName must be set "
                            + "as a non-blank Servlet init-param in your web.xml. Was: '"
                            + getResourceResolverEngineBeanName()
                            + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {

        serviceGetOrPost(req, resp);
    }

    private void serviceGetOrPost(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        final HttpServletRequestInterrogator httpServletRequestInterrogator =
                new HttpServletRequestInterrogator(req);

        final String rewrittenUri = rewriteUri(httpServletRequestInterrogator);

        final RequestDispatcher requestDispatcher =
                req.getRequestDispatcher(rewrittenUri);
        if (httpServletRequestInterrogator.isInclude()) {
            requestDispatcher.include(req, resp);
        } else {
            requestDispatcher.forward(req, resp);
        }

        getResourceResolutionTree().promoteParentToCurrent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        serviceGetOrPost(req, resp);
    }

    /**
     * @param httpServletRequestInterrogator
     *            {@link HttpServletRequestInterrogator} wrapping the request.
     * @return rewrites the requested URI to a device specific resource.
     * @throws IOException Thrown if the URI cannot be rewritten.
     */
    private String rewriteUri(
            final HttpServletRequestInterrogator httpServletRequestInterrogator)
                    throws IOException {

        logDebugRewriteUrlEntry(httpServletRequestInterrogator);

        final Resource resource =
                getResourceResourceResolverEngine().getResource(
                        getDevice(httpServletRequestInterrogator
                                .getHttpServletRequest()),
                                httpServletRequestInterrogator.getRequestUri());

        addResourceToResourceResolutionTreeIfEnabled(resource);

        logDebugRewriteUrlExit(resource);

        if (resource != null) {
            return resource.getNewPath();
        } else {
            throw new IOException("No concrete resource found for requested path: '"
                    + httpServletRequestInterrogator.getRequestUri() + "'");
        }
    }

    private void addResourceToResourceResolutionTreeIfEnabled(final Resource resource) {
        if (getResourceResolutionTree().isEnabled()) {
            getResourceResolutionTree()
            .addChildToCurrentNodeAndPromoteToCurrent(new ResourceTreeNodeBean(resource));
        }
    }

    private void logDebugRewriteUrlEntry(
            final HttpServletRequestInterrogator httpServletRequestInterrogator) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested URI [" + httpServletRequestInterrogator
                    + "]");
        }
    }

    private void logDebugRewriteUrlExit(final Resource resource) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resource [" + resource + "].");
        }
    }


    private ResourceResolutionTree getResourceResolutionTree() {
        return ResourceResolutionTreeHolder.getResourceResolutionTree();
    }

    private Device getDevice(final HttpServletRequest req) {
        final MobileContext context =
                (MobileContext) req.getSession().getAttribute(
                        MobileContext.MOBILE_CONTEXT_KEY);
        return context.getDevice();
    }

    private ResourceResolverEngine getResourceResourceResolverEngine() {
        return resourceResolverEngine;
    }

    private void setResourceResolverEngine(final ResourceResolverEngine resourceResolverEngine) {
        this.resourceResolverEngine = resourceResolverEngine;
    }

    /**
     * @return the resourceResolverEngineBeanName
     */
    public String getResourceResolverEngineBeanName() {
        return resourceResolverEngineBeanName;
    }

    /**
     * @param resourceResolverEngineBeanName
     *            Name of the {@link ResourceResolverEngine} bean to obtain from the
     *            Spring context.
     */
    public void setResourceResolverEngineBeanName(
            final String resourceResolverEngineBeanName) {
        this.resourceResolverEngineBeanName = resourceResolverEngineBeanName;
    }
}
