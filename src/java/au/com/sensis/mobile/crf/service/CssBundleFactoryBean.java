package au.com.sensis.mobile.crf.service;

import java.util.List;


/**
 * Default {@link CssBundleFactory} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class CssBundleFactoryBean implements CssBundleFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public MappedResourcePath getBundle(final List<MappedResourcePath> resourcePathsToInclude) {
        //TODO
        return null;
    }

// TODO: these methods may be resurrected. They were originally in
//    LinkTagWriter but
// TDD applied to LinkTagWriter caused them to be simplified out.
// I think they will belong in this CssBundleFactoryBean class instead.
//    private String createBundleOutputDirPath(final List<MappedResourcePath> list) {
//        final String baseResourcePath = getLastElement(list);
//
//        return StringUtils.substringBeforeLast(baseResourcePath, "/")
//                + "/bundle/";
//    }
//
//    private String getLastElement(final List<MappedResourcePath> list) {
//        String foundResourcePath = null;
//        for (final MappedResourcePath resourcePath : list) {
//            foundResourcePath = resourcePath.getNewResourcePath();
//        }
//        return foundResourcePath;
//    }

}
