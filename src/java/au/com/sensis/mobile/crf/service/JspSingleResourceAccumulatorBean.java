package au.com.sensis.mobile.crf.service;

import java.util.List;


/**
 * {@link SingleResourceAccumulatorBean} for JSPs.
 */
public class JspSingleResourceAccumulatorBean extends SingleResourceAccumulatorBean {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getResources() {
        // Override so that no updates to ResourceResolutionTree are made. These are
        // done in the ResourceResolverServlet because only that will now what resource requests
        // are spawned from the JSP.
        return doGetResources();
    }


}
