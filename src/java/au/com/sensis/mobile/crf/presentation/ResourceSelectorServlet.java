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

import au.com.sensis.mobile.crf.service.MappedResourcePath;
import au.com.sensis.mobile.crf.service.ResourceSelector;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Maps abstract JSP paths ( eg. /WEB-INF/view/jsp/detail/bdp.crf ) to concrete,
 * device specific JSP paths (eg. /WEB-INF/view/jsp/HD800/detail/bdp.jsp ), then
 * dispatches the request to the concrete path. Handles both JSP forwards and includes.
 * The actual path mapping is performed by delegating to
 * {@link ResourceSelector}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceSelectorServlet extends HttpServletBean {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(ResourceSelectorServlet.class);

    private ResourceSelector resourceSelector;

    private String resourceSelectorBeanName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        validateResourceSelectorBeanName();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils
                        .getRequiredWebApplicationContext(config
                                .getServletContext());
        setResourceSelector((ResourceSelector) webApplicationContext
                .getBean(getResourceSelectorBeanName()));
    }

    private void validateResourceSelectorBeanName() {
        if (StringUtils
                .isBlank(getResourceSelectorBeanName())) {
            throw new IllegalStateException(
                    "resourceSelectorBeanName must be set "
                            + "as a non-blank Servlet init-param in your web.xml. Was: '"
                            + getResourceSelectorBeanName()
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested URI [" + httpServletRequestInterrogator
                    + "]");
        }

        final MappedResourcePath mappedResourcePath =
                getResourceSelector().getResourcePath(
                        getDevice(httpServletRequestInterrogator
                                .getHttpServletRequest()),
                        httpServletRequestInterrogator.getRequestUri());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("mappedResourcePath [" + mappedResourcePath + "].");
        }

        if (mappedResourcePath != null) {
            return mappedResourcePath.getNewResourcePath();
        } else {
            throw new IOException("No concrete resource found for requested path: '"
                    + httpServletRequestInterrogator.getRequestUri() + "'");
        }
    }

    private Device getDevice(final HttpServletRequest req) {
        final MobileContext context =
                (MobileContext) req.getSession().getAttribute(
                        MobileContext.MOBILE_CONTEXT_KEY);
        return context.getDevice();
    }

    private ResourceSelector getResourceSelector() {
        return resourceSelector;
    }

    private void setResourceSelector(final ResourceSelector resourceSelector) {
        this.resourceSelector = resourceSelector;
    }

    /**
     * @return the resourceSelectorBeanName
     */
    public String getResourceSelectorBeanName() {
        return resourceSelectorBeanName;
    }

    /**
     * @param resourceSelectorBeanName
     *            Name of the {@link ResourceSelector} bean to obtain from the
     *            Spring context.
     */
    public void setResourceSelectorBeanName(
            final String resourceSelectorBeanName) {
        this.resourceSelectorBeanName = resourceSelectorBeanName;
    }
}
