package au.com.sensis.mobile.crf.presentation;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HttpServletBean;

import au.com.sensis.mobile.crf.service.EhcacheResourceCacheBean;

/**
 * Provides runtime access to perform CRF admin tasks. Currently only supports emptying the
 * Resource cache.
 * <p>
 * Requires two init-param values in web.xml:
 * <ul>
 *  <li>resourceCacheBeanName - the name of the Resource cache bean (crf.resourceCache)</li>
 *  <li>emptyResourceCacheAction - the path that will be used to trigger the cache-emptying,
 *   i.e. &lt;servername&gt;:&lt;port&gt;/&lt;servletpath&gt;/&lt;emptyResourceCacheAction&gt; </li>
 * </ul>
 * </p>
 * @author Tony Filipe
 */
public class AdminServlet extends HttpServletBean {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AdminServlet.class);

    private EhcacheResourceCacheBean resourceCache;
    private String resourceCacheBeanName;

    private String emptyResourceCacheAction;


    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {

        super.init(config);

        validateInitParams();

        final WebApplicationContext webApplicationContext =
            WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());

        setResourceCache((EhcacheResourceCacheBean) webApplicationContext.getBean(
                getResourceCacheBeanName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException, IOException {

        if (getRestfulAction(req).equals(getEmptyResourceCacheAction())) {

            LOGGER.debug("Performing CRF Admin task: " + getRestfulAction(req));

            emptyResourceCache();
            LOGGER.info("Emptied the resource cache.");

            resp.setCharacterEncoding("UTF-8");
            final PrintWriter response = resp.getWriter();
            response.println("Successfuly emptied the Resource cache.");
            response.flush();

        } else {
            LOGGER.warn("An unknown CRF Admin task was requested, '"
                    + getRestfulAction(req) + "'");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void emptyResourceCache() {

        getResourceCache().removeAll();
    }

    private String getRestfulAction(final HttpServletRequest req) {

        final String action = req.getPathInfo();
        return (action == null ? "" : action);
    }

    private void validateInitParams() {

        if (StringUtils.isBlank(getResourceCacheBeanName())) {
            throw new IllegalStateException(
                    "resourceCacheBeanName must be set as a non-blank Servlet init-param "
                    + "in your web.xml. Was: '" + getResourceCacheBeanName() + "'");
        }

        if (StringUtils.isBlank(getEmptyResourceCacheAction())) {
            throw new IllegalStateException(
                    "emptyResourceCacheActionName must be set as a non-blank Servlet init-param "
                    + "in your web.xml. Was: '" + getEmptyResourceCacheAction() + "'");
        }
    }


    /**
     * @return the resourceCache
     */
    public EhcacheResourceCacheBean getResourceCache() {

        return resourceCache;
    }


    /**
     * @param resourceCache  the resourceCache to set
     */
    public void setResourceCache(final EhcacheResourceCacheBean resourceCache) {

        this.resourceCache = resourceCache;
    }


    /**
     * @return the resourceCacheBeanName
     */
    public String getResourceCacheBeanName() {

        return resourceCacheBeanName;
    }


    /**
     * @param resourceCacheBeanName  the resourceCacheBeanName to set
     */
    public void setResourceCacheBeanName(final String resourceCacheBeanName) {

        this.resourceCacheBeanName = resourceCacheBeanName;
    }


    /**
     * @return the emptyResourceCacheAction
     */
    public String getEmptyResourceCacheAction() {

        return emptyResourceCacheAction;
    }


    /**
     * @param emptyResourceCacheAction  the emptyResourceCacheAction to set
     */
    public void setEmptyResourceCacheAction(final String emptyResourceCacheAction) {

        this.emptyResourceCacheAction = emptyResourceCacheAction;
    }



}
