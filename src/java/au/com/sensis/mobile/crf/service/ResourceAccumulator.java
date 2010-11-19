package au.com.sensis.mobile.crf.service;

import java.util.List;



/**
 * Accumulates resolved {@link Resource}s, performing any overriding or bundling required.
 *
 * @author Tony Filipe
 */
public interface ResourceAccumulator {

    /**
     * @return a list of accumulated {@link Resource}s.
     */
    List<Resource> getResources();

    /**
     * Accumulates the given list of {@link Resource}s,
     * performing any overriding or bundling required.
     *
     * @param resolvedPaths the {@link Resource}s to be accumulated.
     */
    void accumulate(final List<Resource> resolvedPaths);

}
