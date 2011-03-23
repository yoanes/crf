package au.com.sensis.mobile.crf.presentation.tag;

import javax.servlet.jsp.PageContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Render the prefix that is used for all abstract link href paths. This tag is
 * useful for exposing this prefix to JavaScript that wishes to retrieve CSS
 * itself. It then just has to tack on the abstract link path (that is normally
 * passed to the {@link LinkTag}) to the prefix to produce the final path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class LinkHrefPrefixTag extends AbstractSrcPrefixTag {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSrcPrefix() {
        return getClientPathPrefixEndingWithSeparator()
                + getTagDependencies().getDeploymentMetadata().getVersion() + "/css/";
    }

    private String getClientPathPrefixEndingWithSeparator() {
        return ensureEndsWithSeparator(getTagDependencies().getClientPathPrefix());
    }

    /**
     * @return {@link LinkTagDependencies} of this tag.
     */
    private LinkTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc.getServletContext());
        return (LinkTagDependencies) webApplicationContext.getBean(LinkTagDependencies.BEAN_NAME);
    }
}
