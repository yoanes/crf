package au.com.sensis.mobile.crf.service;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;

/**
 * Base test class for {@link AbstractMultipleResourceResolver}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractMultipleResourceResolverTestCase extends
        AbstractResourceResolverTestCase {

    protected void recordIsBundlingEnabled(final Boolean bundlingEnabled) {
        EasyMock.expect(getMockResourceAccumulator().isBundlingEnabled())
                .andReturn(bundlingEnabled).atLeastOnce();
    }

    protected void recordGetResourcesFromAccumulator(final List<Resource> expectedResources) {
        EasyMock.expect(getMockResourceAccumulator().getResources()).andReturn(expectedResources);
    }

    protected void recordGetResourcesFromAccumulator(final Resource expectedResource) {
        EasyMock.expect(getMockResourceAccumulator().getResources()).andReturn(
                Arrays.asList(expectedResource));
    }

}