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
import au.com.sensis.mobile.web.component.core.tag.DynamicTagAttribute;

/**
 * Facade to an image tag that uses the Content Rendering Framework to resolve
 * the resource path set into {@link #setHref(String)}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
// TODO: refactor so that href attribute is src.
public class ImageTag extends AbstractTag {

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: validate that abstract path has correct extension.
    public final void doTag() throws JspException, IOException {
        validateHrefAttribute();

        final Resource resource =
                getResourceResolverEngine().getResourcePath(getDevice(),
                        getHref());
        if (resource != null) {
            if (!resource.newPathEndsWithDotNull()) {
                writeSingleImageTag(getJspContext().getOut(),
                        resource);
            }
        } else {
            if (getResourceResolutionWarnLogger().isWarnEnabled()) {
                getResourceResolutionWarnLogger().warn(
                        "No resource was found for requested resource '"
                                + getHref() + "' and device " + getDevice());
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

        jspWriter.print("/>\n");
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
}
