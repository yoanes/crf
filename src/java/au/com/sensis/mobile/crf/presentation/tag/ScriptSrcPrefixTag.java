package au.com.sensis.mobile.crf.presentation.tag;

import javax.servlet.jsp.PageContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Render the prefix that is used for all abstract script src paths. This tag is
 * useful for exposing this prefix to JavaScript that wishes to retrieve scripts
 * itself. It then just has to tack on the abstract script path (that is normally
 * passed to the {@link ScriptTag}) to the prefix to produce the final path.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ScriptSrcPrefixTag extends AbstractSrcPrefixTag {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSrcPrefix() {
        return getClientPathPrefixEndingWithSeparator()
                + getTagDependencies().getDeploymentMetadata().getVersion() + "/javascript/";
    }

    private String getClientPathPrefixEndingWithSeparator() {
        return ensureEndsWithSeparator(getTagDependencies().getClientPathPrefix());
    }

    /**
     * @return {@link ScriptTagDependencies} of this tag.
     */
    private ScriptTagDependencies getTagDependencies() {
        final PageContext pc = (PageContext) getJspContext();

        final WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(pc.getServletContext());
        return (ScriptTagDependencies) webApplicationContext
                .getBean(ScriptTagDependencies.BEAN_NAME);
    }
}
