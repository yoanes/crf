package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * {@link ResourcePathMapper} that delegates to a chain of responsibility list of other
 * {@link ResourcePathMapper}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ChainedResourcePathMapper implements ResourcePathMapper {

    // logger not final to allow it to be swapped out during unit testing.
    private static Logger logger = Logger.getLogger(ChainedResourcePathMapper.class);

    private final List<ResourcePathMapper> resourcePathMappers;

    /**
     * @param resourcePathMappers
     *            Chain of responsibility list of {@link ResourcePathMapper}s.
     */
    public ChainedResourcePathMapper(
            final List<ResourcePathMapper> resourcePathMappers) {
        if (resourcePathMappers == null) {
            throw new IllegalArgumentException(
                    "resourcePathMappers must not be null");
        }

        if (resourcePathMappers.isEmpty() && logger.isEnabledFor(Level.WARN)) {
            logger.warn("resourcePathMappers is empty. "
                    + "This ChainedResourcePathMapper will always "
                    + "return a NullMappedResourcePath.");
        }
        this.resourcePathMappers = resourcePathMappers;
    }


    /**
     * Delegates to each {@link ResourcePathMapper} from a chain of
     * responsibility. The chain stops when either of the following occurs:
     * <ol>
     * <li>A non-null {@link MappedResourcePath}is found.</li>
     * <li>The end of the chain is reached without the previous condition being
     * reached. In this case, null is returned.</li>
     * </ol>
     *
     * {@inheritDoc}
     */
    @Override
    public List<MappedResourcePath> resolve(
            final String requestedResourcePath, final Group group) throws IOException {
        for (final ResourcePathMapper resourcePathMapper : getResourcePathMappers()) {
            // TODO: needs to check if resourcePathMapper is interested instead of checking
            // foundResourcePaths.isEmpty?.
            final List<MappedResourcePath> foundResourcePaths
                = resourcePathMapper.resolve(requestedResourcePath, group);
            if (!foundResourcePaths.isEmpty()) {
                return foundResourcePaths;
            }
        }
        return new ArrayList<MappedResourcePath>();
    }


    /**
     * @return the resourcePathMappers
     */
    private List<ResourcePathMapper> getResourcePathMappers() {
        return resourcePathMappers;
    }
}
