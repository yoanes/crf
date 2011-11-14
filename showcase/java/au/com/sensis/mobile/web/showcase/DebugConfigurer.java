package au.com.sensis.mobile.web.showcase;

import javax.servlet.ServletContext;


/**
 * Supports making special config tweaks.  Typically to help with testing / debugging.
 *
 * @author Boyd Sharrock
 *
 */
public interface DebugConfigurer {

    /**
     * @param debugInfo Provides the parameter information from the request parameter.
     * @param servletContext The {@link ServletContext}.
     */
    void applyDebugSetup(final String[] debugInfo, final ServletContext servletContext);

}
