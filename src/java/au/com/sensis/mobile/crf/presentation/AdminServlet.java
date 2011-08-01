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

import au.com.sensis.mobile.crf.presentation.tag.BundleTagCache;
import au.com.sensis.mobile.crf.service.ResourceCache;

/**
 * Provides runtime access to perform CRF admin tasks. Currently only supports emptying the
 * Resource caches (which include the {@link BundleTagCache}s).
 * <p>
 * Requires four init-param values in web.xml:
 * <ul>
 *  <li>resourceCacheBeanName - the name of the Resource cache bean (crf.resourceCache)</li>
 *  <li>bundleScriptsTagCacheBeanName - the name of the {@link BundlesTagCache} cache bean for
 *      bundled scripts (crf.bundleScriptsTagCache)</li>
 *  <li>bundleLinksTagCacheBeanName - the name of the {@link BundlesTagCache} cache bean for
 *      bundled links (crf.bundleLinksTagCache)</li>
 *  <li>emptyResourceCacheAction - the path that will be used to trigger the cache-emptying,
 *   i.e. &lt;servername&gt;:&lt;port&gt;/&lt;servletpath&gt;/&lt;emptyResourceCacheAction&gt; </li>
 * </ul>
 * </p>
 * @author Tony Filipe
 */
public class AdminServlet extends HttpServletBean {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AdminServlet.class);

    private ResourceCache resourceCache;
    private BundleTagCache bundleScriptsTagCache;
    private BundleTagCache bundleLinksTagCache;

    private String resourceCacheBeanName;
    private String bundleScriptsTagCacheBeanName;
    private String bundleLinksTagCacheBeanName;

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

        setResourceCache((ResourceCache) webApplicationContext.getBean(
                getResourceCacheBeanName()));
        setBundleScriptsTagCache((BundleTagCache) webApplicationContext.getBean(
                getBundleScriptsTagCacheBeanName()));
        setBundleLinksTagCache((BundleTagCache) webApplicationContext.getBean(
                getBundleLinksTagCacheBeanName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException, IOException {

        if (getRestfulAction(req).equals(getEmptyResourceCacheAction())) {

            LOGGER.info("Performing CRF Admin task: " + getRestfulAction(req));

            emptyResourceCaches();
            LOGGER.info("Emptied the resource caches.");

            resp.setCharacterEncoding("UTF-8");
            final PrintWriter response = resp.getWriter();
            response.println("Successfuly emptied the Resource caches.");
            response.flush();

        } else {
            LOGGER.warn("An unknown CRF Admin task was requested, '"
                    + getRestfulAction(req) + "'");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void emptyResourceCaches() {

        getResourceCache().removeAll();
        getBundleScriptsTagCache().removeAll();
        getBundleLinksTagCache().removeAll();
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

        if (StringUtils.isBlank(getBundleScriptsTagCacheBeanName())) {
            throw new IllegalStateException(
                    "bundleScriptsTagCacheBeanName must be set as a non-blank Servlet init-param "
                    + "in your web.xml. Was: '" + getBundleScriptsTagCacheBeanName() + "'");
        }

        if (StringUtils.isBlank(getBundleLinksTagCacheBeanName())) {
            throw new IllegalStateException(
                    "bundleLinksTagCacheBeanName must be set as a non-blank Servlet init-param "
                    + "in your web.xml. Was: '" + getBundleLinksTagCacheBeanName() + "'");
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
    public ResourceCache getResourceCache() {

        return resourceCache;
    }


    /**
     * @param resourceCache  the resourceCache to set
     */
    public void setResourceCache(final ResourceCache resourceCache) {

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

    /**
     * @return the bundleScriptsTagCache
     */
    public BundleTagCache getBundleScriptsTagCache() {
        return bundleScriptsTagCache;
    }

    /**
     * @param bundleScriptsTagCache the bundleScriptsTagCache to set
     */
    public void setBundleScriptsTagCache(final BundleTagCache bundleScriptsTagCache) {
        this.bundleScriptsTagCache = bundleScriptsTagCache;
    }

    /**
     * @return the bundleLinksTagCache
     */
    public BundleTagCache getBundleLinksTagCache() {
        return bundleLinksTagCache;
    }

    /**
     * @param bundleLinksTagCache the bundleLinksTagCache to set
     */
    public void setBundleLinksTagCache(final BundleTagCache bundleLinksTagCache) {
        this.bundleLinksTagCache = bundleLinksTagCache;
    }

    /**
     * @return the bundleScriptsTagCacheBeanName
     */
    public String getBundleScriptsTagCacheBeanName() {
        return bundleScriptsTagCacheBeanName;
    }

    /**
     * @param bundleScriptsTagCacheBeanName the bundleScriptsTagCacheBeanName to set
     */
    public void setBundleScriptsTagCacheBeanName(final String bundleScriptsTagCacheBeanName) {
        this.bundleScriptsTagCacheBeanName = bundleScriptsTagCacheBeanName;
    }

    /**
     * @return the bundleLinksTagCacheBeanName
     */
    public String getBundleLinksTagCacheBeanName() {
        return bundleLinksTagCacheBeanName;
    }

    /**
     * @param bundleLinksTagCacheBeanName the bundleLinksTagCacheBeanName to set
     */
    public void setBundleLinksTagCacheBeanName(final String bundleLinksTagCacheBeanName) {
        this.bundleLinksTagCacheBeanName = bundleLinksTagCacheBeanName;
    }

}
