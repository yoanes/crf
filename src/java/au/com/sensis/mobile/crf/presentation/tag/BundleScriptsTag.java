package au.com.sensis.mobile.crf.presentation.tag;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.service.Resource;

/**
 * TODO.
 *
 * @author w12495
 *
 */
public class BundleScriptsTag extends AbstractTag {

    private static final Logger LOGGER = Logger
            .getLogger(BundleScriptsTag.class);

    private String id;

    private final List<Resource> resourcesToBundle = new ArrayList<Resource>();

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public void doTag() throws JspException, IOException {
        getJspBody().invoke(null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resources to bundle: " + getResourcesToBundle());
        }
    }

    private List<Resource> getResourcesToBundle() {
        return resourcesToBundle;
    }

    protected void addResourceToBundle(final Resource resource) {
        getResourcesToBundle().add(resource);
    }

    protected void addResourcesToBundle(final List<Resource> resources) {
        getResourcesToBundle().addAll(resources);
    }

}

