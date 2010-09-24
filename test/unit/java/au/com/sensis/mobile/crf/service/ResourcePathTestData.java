package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Test data for resource path mapping.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourcePathTestData {

    private static final File ROOT_RESOURCES_DIR;

    static {
        try {
            ROOT_RESOURCES_DIR = new File(ResourcePathTestData.class.getResource("/").toURI());
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Error creating test data.");
        }

    }

    public File getRootResourcesPath() {
        return ROOT_RESOURCES_DIR;
    }

    public String getJspResourcesRootServletPath() {
        return "/WEB-INF/view/jsp/";
    }

    public String getRequestedJspResourcePath() {
        return getJspResourcesRootServletPath() + "detail/bdp.crf";
    }

    public Resource getDotNullMappedImageResourcePath() {
        return new ResourceBean(getRequestedImageResourcePathWithoutExtension(),
                "extrasmall/common/unmetered.null", getRootResourcesPath());
    }

    public Resource getMappedDefaultGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "default/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "iPhone/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public Resource getMappedAndroidGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "Android/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public String getCrfExtensionWithoutLeadingDot() {
        return "crf";
    }

    public String getCrfExtensionWithLeadingDot() {
        return "." + getCrfExtensionWithoutLeadingDot();
    }

    public String getCssExtensionWithoutLeadingDot() {
        return "css";
    }

    public String getScriptExtensionWithoutLeadingDot() {
        return "js";
    }

    public String getCssExtensionWithLeadingDot() {
        return "." + getCssExtensionWithoutLeadingDot();
    }

    public String getScriptExtensionWithLeadingDot() {
        return "." + getScriptExtensionWithoutLeadingDot();
    }

    public String getAbstractImageExtensionWithoutLeadingDot() {
        return "image";
    }

    public String getAbstractImageExtensionWithLeadingDot() {
        return "." + getAbstractImageExtensionWithoutLeadingDot();
    }

    public String getRequestedCssResourcePath() {
        return "common/main.css";
    }

    public String getRequestedImageResourcePathWithoutExtension() {
        return "common/unmetered";
    }

    public String getRequestedImageResourcePath() {
        return "common/unmetered.image";
    }

    public String getRequestedNamedScriptResourcePath() {
        return "common/main.js";
    }

    public String getRequestedPackageScriptResourcePath() {
        return "util/package";
    }

    public Resource getMappedDefaultGroupCssResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                "default/common/main.css", getRootResourcesPath());
    }

    public Resource getMappedDefaultGroupNamedScriptResourcePath() {
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                "default/common/main.js", getRootResourcesPath());
    }

    public Resource getMappedDefaultGroupNamedScriptBundleResourcePath() {
        // TODO: should the method return the concrete ResourceBean?
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                "default/common/bundle/main.js", getRootResourcesPath());
    }

    public String getMappedDefaultGroupNamedScriptBundleResourceHref() {
        return getScriptClientPathPrefix()
        + getMappedDefaultGroupNamedScriptBundleResourcePath().getNewPath();
    }

    public Resource getMappedDefaultGroupBundledScriptBundleResourcePath() {
        // TODO: should the method return the concrete ResourceBean?
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "default/util/", getRootResourcesPath());
    }

    public Resource getMappedDefaultGroupPackagedScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "default/util/util1.js", getRootResourcesPath());
    }

    public Resource getMappedDefaultGroupPackagedScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "default/util/util2.js", getRootResourcesPath());
    }

    public Resource getMappedDefaultGroupCssBundleResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                "default/common/bundle/main.css", getRootResourcesPath());
    }

    public String getMappedDefaultGroupCssBundleDirPath() {
        return "default/common/bundle/";
    }

    public Resource getMappedIphoneGroupCssResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                "iPhone/common/main.css", getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupCssBundleResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                "iPhone/common/bundle/main.css", getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupNamedScriptResourcePath() {
        // TODO: should the method return the concrete ResourceBean?
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                "iPhone/common/main.js", getRootResourcesPath());
    }

    public String getMappedIphoneGroupNamedScriptResourceHref() {
        return getScriptClientPathPrefix()
        + getMappedIphoneGroupNamedScriptResourcePath().getNewPath();
    }

    public Resource getMappedIphoneGroupNamedScriptBundleResourcePath() {
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                "iPhone/common/bundle/main.js", getRootResourcesPath());
    }

    public String getMappedIphoneGroupNamedScriptBundleResourceHref() {
        return getScriptClientPathPrefix()
                + getMappedIphoneGroupNamedScriptBundleResourcePath()
                        .getNewPath();
    }

    public Resource getMappedIphoneGroupPackagedScriptBundleResourcePath() {
        // TODO: should the method return the concrete ResourceBean?
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "iPhone/util/", getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "iPhone/util/util1.js", getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "iPhone/util/util2.js", getRootResourcesPath());
    }

    public Resource getMappedAndroidGroupBundledScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "android/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public Resource getMappedAndroidGroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "android/util/util1.js", getRootResourcesPath());
    }

    public Resource getMappedAndroidGroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "android/util/util2.js", getRootResourcesPath());
    }

    public Resource getMappedAppleGroupBundledScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "apple/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public Resource getMappedAppleGroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "apple/util/util1.js", getRootResourcesPath());
    }

    public Resource getMappedAppleGroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "apple/util/util2.js", getRootResourcesPath());
    }

    public Resource getMappedHD800GroupBundledScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "hd800/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public Resource getMappedHD800GroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "hd800/util/util1.js", getRootResourcesPath());
    }

    public Resource getMappedHD800GroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "hd800/util/util2.js", getRootResourcesPath());
    }
    public Resource getMappedMediumGroupBundledScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "medium/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public Resource getMappedMediumGroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "medium/util/util1.js", getRootResourcesPath());
    }

    public Resource getMappedMediumGroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                "medium/util/util2.js", getRootResourcesPath());
    }

    public String getMappedIphoneGroupCssBundleDirPath() {
        return "iPhone/common/bundle/";
    }

    public Resource getMappedAppleGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "Apple/detail/bdp.jsp",
                getRootResourcesPath());

    }

    public Resource getMappedHD800GroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "HD800/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public Resource getMappedMediumGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "medium/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public String getCssClientPathPrefix() {
        return "http://localhost:8080/wl-crf-component-showcase/resources/css/";
    }

    public String getImageClientPathPrefix() {
        return "http://localhost:8080/wl-crf-component-showcase/resources/images/";
    }

    public String getScriptClientPathPrefix() {
        return "http://localhost:8080/wl-crf-component-showcase/resources/javascript/";
    }

    public String getMappedDefaultGroupCssResourceHref() {
        return getCssClientPathPrefix()
            + getMappedDefaultGroupCssResourcePath().getNewPath();
    }

    public String getMappedDefaultGroupNamedScriptResourceHref() {
        return getScriptClientPathPrefix()
            + getMappedDefaultGroupNamedScriptResourcePath().getNewPath();
    }

    public String getMappedDefaultGroupCssBundleResourceHref() {
        return getCssClientPathPrefix()
            + getMappedDefaultGroupCssBundleResourcePath().getNewPath();
    }

    public String getMappedIphoneGroupCssResourceHref() {
        return getCssClientPathPrefix()
            + getMappedIphoneGroupCssResourcePath().getNewPath();
    }

    public String getMappedIphoneGroupCssBundleResourceHref() {
        return getCssClientPathPrefix()
            + getMappedIphoneGroupCssBundleResourcePath().getNewPath();
    }

    public Resource getMappedDefaultGroupImageResourcePath() {
        // TODO: change return type to ResourceBean?
        return new ResourceBean(getRequestedImageResourcePath(),
                "default/common/unmetered", getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupImageResourcePath() {
        // TODO: change return type to ResourceBean?
        return new ResourceBean(getRequestedImageResourcePath(),
                "iPhone/common/unmetered", getRootResourcesPath());
    }

    public Resource getMappedDefaultGroupPngImageResourcePath() {
        // TODO: change return type to ResourceBean?
        return new ResourceBean(getRequestedImageResourcePath(),
                "default/common/unmetered.png", getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupPngImageResourcePath() {
        // TODO: change return type to ResourceBean?
        return new ResourceBean(getRequestedImageResourcePath(),
                "iPhone/common/unmetered.png", getRootResourcesPath());
    }

    public Resource getMappedIphoneGroupGifImageResourcePath() {
        // TODO: change return type to ResourceBean?
        return new ResourceBean(getRequestedImageResourcePath(),
                "iPhone/common/unmetered.gif", getRootResourcesPath());
    }

    public Resource getMappedAndroidGroupPngImageResourcePath() {
        return new ResourceBean(getRequestedImageResourcePath(),
                "Android/common/unmetered.png", getRootResourcesPath());
    }

    public String getMappedDefaultGroupPngImageResourceHref() {
        return getImageClientPathPrefix()
            + getMappedDefaultGroupPngImageResourcePath().getNewPath();
    }

    public String getMapComponentPathPrefix() {
        return "component/map/";
    }

    public String getMapComponentRequestedJspResourcePath() {
        return "component/map/render.jsp";
    }

    public String getMapComponentRequestedImageResourcePath() {
        return "component/map/tile_bg.image";
    }

}
