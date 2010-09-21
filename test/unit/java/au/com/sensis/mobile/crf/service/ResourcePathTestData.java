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

    public MappedResourcePath getDotNullMappedImageResourcePath() {
        return new MappedResourcePathBean(getRequestedImageResourcePathWithoutExtension(),
                "extrasmall/common/unmetered.null", getRootResourcesPath());
    }

    public MappedResourcePath getMappedDefaultGroupResourcePath() {
        return new MappedResourcePathBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "default/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupResourcePath() {
        return new MappedResourcePathBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "iPhone/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public MappedResourcePath getMappedAndroidGroupResourcePath() {
        return new MappedResourcePathBean(getRequestedJspResourcePath(),
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

    public String getRequestedBundledScriptResourcePath() {
        return "util/bundle";
    }

    public MappedResourcePath getMappedDefaultGroupCssResourcePath() {
        return new MappedResourcePathBean(getRequestedCssResourcePath(),
                "default/common/main.css", getRootResourcesPath());
    }

    public MappedResourcePath getMappedDefaultGroupNamedScriptResourcePath() {
        return new MappedResourcePathBean(getRequestedNamedScriptResourcePath(),
                "default/common/main.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedDefaultGroupNamedScriptBundleResourcePath() {
        // TODO: should the method return the concrete MappedResourcePathBean?
        return new MappedResourcePathBean(getRequestedNamedScriptResourcePath(),
                "default/common/bundle/main.js", getRootResourcesPath());
    }

    public String getMappedDefaultGroupNamedScriptBundleResourceHref() {
        return getScriptClientPathPrefix()
        + getMappedDefaultGroupNamedScriptBundleResourcePath().getNewResourcePath();
    }

    public MappedResourcePath getMappedDefaultGroupBundledScriptBundleResourcePath() {
        // TODO: should the method return the concrete MappedResourcePathBean?
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "default/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedDefaultGroupBundledScriptResourcePath1() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "default/util/util1.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedDefaultGroupBundledScriptResourcePath2() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "default/util/util2.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedDefaultGroupCssBundleResourcePath() {
        return new MappedResourcePathBean(getRequestedCssResourcePath(),
                "default/common/bundle/main.css", getRootResourcesPath());
    }

    public String getMappedDefaultGroupCssBundleDirPath() {
        return "default/common/bundle/";
    }

    public MappedResourcePath getMappedIphoneGroupCssResourcePath() {
        return new MappedResourcePathBean(getRequestedCssResourcePath(),
                "iPhone/common/main.css", getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupCssBundleResourcePath() {
        return new MappedResourcePathBean(getRequestedCssResourcePath(),
                "iPhone/common/bundle/main.css", getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupNamedScriptResourcePath() {
        // TODO: should the method return the concrete MappedResourcePathBean?
        return new MappedResourcePathBean(getRequestedNamedScriptResourcePath(),
                "iPhone/common/main.js", getRootResourcesPath());
    }

    public String getMappedIphoneGroupNamedScriptResourceHref() {
        return getScriptClientPathPrefix()
        + getMappedIphoneGroupNamedScriptResourcePath().getNewResourcePath();
    }

    public MappedResourcePath getMappedIphoneGroupNamedScriptBundleResourcePath() {
        return new MappedResourcePathBean(getRequestedNamedScriptResourcePath(),
                "iPhone/common/bundle/main.js", getRootResourcesPath());
    }

    public String getMappedIphoneGroupNamedScriptBundleResourceHref() {
        return getScriptClientPathPrefix()
                + getMappedIphoneGroupNamedScriptBundleResourcePath()
                        .getNewResourcePath();
    }

    public MappedResourcePath getMappedIphoneGroupBundledScriptBundleResourcePath() {
        // TODO: should the method return the concrete MappedResourcePathBean?
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "iPhone/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupBundledScriptResourcePath1() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "iPhone/util/util1.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupBundledScriptResourcePath2() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "iPhone/util/util2.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedAndroidGroupBundledScriptBundleResourcePath() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "android/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedAndroidGroupBundledScriptResourcePath1() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "android/util/util1.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedAndroidGroupBundledScriptResourcePath2() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "android/util/util2.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedAppleGroupBundledScriptBundleResourcePath() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "apple/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedAppleGroupBundledScriptResourcePath1() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "apple/util/util1.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedAppleGroupBundledScriptResourcePath2() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "apple/util/util2.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedHD800GroupBundledScriptBundleResourcePath() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "hd800/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedHD800GroupBundledScriptResourcePath1() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "hd800/util/util1.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedHD800GroupBundledScriptResourcePath2() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "hd800/util/util2.js", getRootResourcesPath());
    }
    public MappedResourcePath getMappedMediumGroupBundledScriptBundleResourcePath() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "medium/util/bundle/bundle-all.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedMediumGroupBundledScriptResourcePath1() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "medium/util/util1.js", getRootResourcesPath());
    }

    public MappedResourcePath getMappedMediumGroupBundledScriptResourcePath2() {
        return new MappedResourcePathBean(getRequestedBundledScriptResourcePath(),
                "medium/util/util2.js", getRootResourcesPath());
    }

    public String getMappedIphoneGroupCssBundleDirPath() {
        return "iPhone/common/bundle/";
    }

    public MappedResourcePath getMappedAppleGroupResourcePath() {
        return new MappedResourcePathBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "Apple/detail/bdp.jsp",
                getRootResourcesPath());

    }

    public MappedResourcePath getMappedHD800GroupResourcePath() {
        return new MappedResourcePathBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "HD800/detail/bdp.jsp",
                getRootResourcesPath());
    }

    public MappedResourcePath getMappedMediumGroupResourcePath() {
        return new MappedResourcePathBean(getRequestedJspResourcePath(),
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
            + getMappedDefaultGroupCssResourcePath().getNewResourcePath();
    }

    public String getMappedDefaultGroupNamedScriptResourceHref() {
        return getScriptClientPathPrefix()
            + getMappedDefaultGroupNamedScriptResourcePath().getNewResourcePath();
    }

    public String getMappedDefaultGroupCssBundleResourceHref() {
        return getCssClientPathPrefix()
            + getMappedDefaultGroupCssBundleResourcePath().getNewResourcePath();
    }

    public String getMappedIphoneGroupCssResourceHref() {
        return getCssClientPathPrefix()
            + getMappedIphoneGroupCssResourcePath().getNewResourcePath();
    }

    public String getMappedIphoneGroupCssBundleResourceHref() {
        return getCssClientPathPrefix()
            + getMappedIphoneGroupCssBundleResourcePath().getNewResourcePath();
    }

    public MappedResourcePath getMappedDefaultGroupImageResourcePath() {
        // TODO: change return type to ImageMappedResourcePathBean?
        return new ImageMappedResourcePathBean(getRequestedImageResourcePath(),
                "default/common/unmetered", getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupImageResourcePath() {
        // TODO: change return type to ImageMappedResourcePathBean?
        return new ImageMappedResourcePathBean(getRequestedImageResourcePath(),
                "iPhone/common/unmetered", getRootResourcesPath());
    }

    public MappedResourcePath getMappedDefaultGroupPngImageResourcePath() {
        // TODO: change return type to ImageMappedResourcePathBean?
        return new ImageMappedResourcePathBean(getRequestedImageResourcePath(),
                "default/common/unmetered.png", getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupPngImageResourcePath() {
        // TODO: change return type to ImageMappedResourcePathBean?
        return new ImageMappedResourcePathBean(getRequestedImageResourcePath(),
                "iPhone/common/unmetered.png", getRootResourcesPath());
    }

    public MappedResourcePath getMappedIphoneGroupGifImageResourcePath() {
        // TODO: change return type to ImageMappedResourcePathBean?
        return new ImageMappedResourcePathBean(getRequestedImageResourcePath(),
                "iPhone/common/unmetered.gif", getRootResourcesPath());
    }

    public MappedResourcePath getMappedAndroidGroupPngImageResourcePath() {
        return new MappedResourcePathBean(getRequestedImageResourcePath(),
                "Android/common/unmetered.png", getRootResourcesPath());
    }

    public String getMappedDefaultGroupPngImageResourceHref() {
        return getImageClientPathPrefix()
            + getMappedDefaultGroupPngImageResourcePath().getNewResourcePath();
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
