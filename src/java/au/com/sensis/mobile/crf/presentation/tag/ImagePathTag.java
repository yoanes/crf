package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;

/**
 * Facade to a custom tag that uses the Content Rendering Framework to resolve
 * the image resource path set into {@link #setSrc(String)}.
 *
 * It outputs the path to the resolved image to the calling JSP.
 *
 * @author Adrian.Koh2@sensis.com.au
 * @author Tony Filipe
 */
public class ImagePathTag extends AbstractTag {

    private String src;

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: validate that abstract path has correct extension.
    public final void doTag() throws JspException, IOException {

        validatePathAttribute(getSrc());

        final Resource resource =
            getResourceResolverEngine().getResource(getDevice(), getSrc());

        if (resource != null) {
            doTagWhenResourceFound(resource);
        } else {
            doTagWhenResourceNotFound();
        }
    }

    private void doTagWhenResourceFound(final Resource resource) throws IOException, JspException {

        if (resource.newPathEndsWithDotNull()) {
            getJspContext().getOut().write(StringUtils.EMPTY);
        } else {
            writeImagePath(getJspContext().getOut(), resource);
        }
    }

    private void doTagWhenResourceNotFound() throws IOException {

        writeBrokenImagePath(getJspContext().getOut());
    }

    private void writeImagePath(final JspWriter jspWriter,
            final Resource resource) throws IOException {

        jspWriter.print(getTagDependencies().getClientPathPrefix() + resource.getNewPath()
                + getUniqueRequestParam());
    }

    private void writeBrokenImagePath(final JspWriter jspWriter) throws IOException {

        jspWriter.print(getTagDependencies().getClientPathPrefix()
                + getTagDependencies().getDeploymentMetadata().getVersion() + "/" + getSrc());
    }

    private ImageTagDependencies getTagDependencies() {

        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
            WebApplicationContextUtils.getRequiredWebApplicationContext(pc.getServletContext());

        return (ImageTagDependencies) webApplicationContext.getBean(ImageTagDependencies.BEAN_NAME);
    }

    private String getUniqueRequestParam() {

        String uniqueRequestParam = StringUtils.EMPTY;

        if (!getTagDependencies().getDeploymentMetadata().isDownstreamCachingEnabled()) {
            uniqueRequestParam = "?" + new Date().getTime();
        }

        return uniqueRequestParam;
    }

    /**
     * @return the {@link ResourceResolverEngine}.
     */
    private ResourceResolverEngine getResourceResolverEngine() {
        return getTagDependencies().getResourceResolverEngine();
    }

    /**
     * @return the src
     */
    public final String getSrc() {
        return src;
    }

    /**
     * @param src the src to set.
     */
    public final void setSrc(final String src) {
        this.src = src;
    }

}
