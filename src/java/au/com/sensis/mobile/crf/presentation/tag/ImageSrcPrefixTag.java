package au.com.sensis.mobile.crf.presentation.tag;

import javax.servlet.jsp.PageContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Render the prefix that is used for all abstract image src paths. This tag is
 * useful for exposing this prefix to JavaScript that wishes to retrieve images
 * itself. It then just has to tack on the abstract image path (that is normally
 * passed to the {@link ImageTag}) to the prefix to produce the final path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ImageSrcPrefixTag extends AbstractSrcPrefixTag {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSrcPrefix() {
        return getClientPathPrefixEndingWithSeparator()
                + getTagDependencies().getDeploymentMetadata().getVersion() + "/images/";
    }

    private String getClientPathPrefixEndingWithSeparator() {
        return ensureEndsWithSeparator(getTagDependencies().getClientPathPrefix());
    }

    /**
     * @return {@link ImageTagDependencies} of this tag.
     */
    private ImageTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc.getServletContext());
        return (ImageTagDependencies) webApplicationContext.getBean(ImageTagDependencies.BEAN_NAME);
    }
}
