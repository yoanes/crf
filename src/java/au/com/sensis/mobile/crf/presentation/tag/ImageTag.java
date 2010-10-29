package au.com.sensis.mobile.crf.presentation.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceResolutionWarnLogger;
import au.com.sensis.mobile.crf.service.ResourceResolverEngine;

/**
 * Facade to an image tag that uses the Content Rendering Framework to resolve
 * the resource path set into {@link #setHref(String)}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageTag extends AbstractTag {

    private String src;

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: validate that abstract path has correct extension.
    public final void doTag() throws JspException, IOException {
        validatePathAttribute(getSrc());

        final Resource resource =
                getResourceResolverEngine().getResource(getDevice(),
                        getSrc());
        if (resource != null) {
            if (!resource.newPathEndsWithDotNull()) {
                writeSingleImageTag(getJspContext().getOut(),
                        resource);
            }
        } else {
            if (getResourceResolutionWarnLogger().isWarnEnabled()) {
                getResourceResolutionWarnLogger().warn(
                        "No resource was found for requested resource '"
                                + getSrc() + "' and device " + getDevice());
            }
        }
    }

    private ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return getTagDependencies().getResourceResolutionWarnLogger();
    }

    private void writeSingleImageTag(final JspWriter jspWriter,
            final Resource resource) throws IOException {
        jspWriter.print("<img ");

        jspWriter.print("src=\"" + getTagDependencies().getClientPathPrefix()
                + resource.getNewPath() + "\" ");

        for (final DynamicTagAttribute attribute : getDynamicAttributes()) {
            jspWriter.print(attribute.getLocalName() + "=\""
                    + attribute.getValue() + "\" ");
        }

        jspWriter.print("/>");
    }

    private ImageTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc
                        .getServletContext());
        return (ImageTagDependencies) webApplicationContext
                .getBean(ImageTagDependencies.BEAN_NAME);
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
