package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.net.URISyntaxException;

import au.com.sensis.mobile.crf.config.DeploymentMetadataTestData;
import au.com.sensis.mobile.crf.config.GroupTestData;

/**
 * Test data for resource path mapping.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourcePathTestData {

    private static final File ROOT_RESOURCES_DIR;

    private final GroupTestData groupTestData = new GroupTestData();

    static {
        try {
            ROOT_RESOURCES_DIR = new File(ResourcePathTestData.class.getResource("/").toURI());
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Error creating test data.");
        }
    }

    public String getDeploymentVersion() {
        return new DeploymentMetadataTestData().createDevDeploymentMetadata().getVersion();
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

    public Resource getMappedDotNullImageResourcePath() {
        return new ResourceBean(getRequestedImageResourcePathWithoutExtension(),
                "extrasmall/common/unmetered.null", getRootResourcesPath(),
                getGroupTestData().createExtraSmallGroup());
    }

    public Resource getMappedDefaultGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(), getJspResourcesRootServletPath()
                + "default/detail/bdp.jsp", getRootResourcesPath(), getGroupTestData()
                .createDefaultGroup());
    }

    public Resource getMappedIphoneGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(), getJspResourcesRootServletPath()
                + "iphone/detail/bdp.jsp", getRootResourcesPath(), getGroupTestData()
                .createIPhoneGroup());
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

    public String getPropertiesExtensionWithoutLeadingDot() {
        return "properties";
    }

    public String getPropertiesExtensionWithLeadingDot() {
        return "." + getPropertiesExtensionWithoutLeadingDot();
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

    public String getRequestedScaledImagePropertiesResourcePath() {
        return "common/unmetered.properties";
    }

    public String getRequestedNamedScriptResourcePath() {
        return "common/main.js";
    }

    public String getRequestedNamedScriptResourcePath2() {
        return "common/ajax.js";
    }

    public String getRequestedPackageScriptResourcePath() {
        return "util/package";
    }

    public Resource getMappedDefaultGroupCssResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                getDeploymentVersion() + "/default/common/main.css", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedDefaultGroupNamedScriptResourcePath() {
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                getDeploymentVersion() + "/default/common/main.js", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedDefaultGroupNamedScriptResourcePath2() {
        return new ResourceBean(getRequestedNamedScriptResourcePath2(),
                getDeploymentVersion() + "/default/common/ajax.js", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedDefaultGroupNamedScriptBundleResourcePath() {
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                getDeploymentVersion() + "/default/common/bundle/main.js", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public String getMappedDefaultGroupNamedScriptBundleResourceHref() {
        return getScriptClientPathPrefix()
        + getMappedDefaultGroupNamedScriptBundleResourcePath().getNewPath();
    }

    public Resource getMappedDefaultGroupPackagedScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/default/util/", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedDefaultGroupPackagedScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/default/util/util1.js", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedDefaultGroupPackagedScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/default/util/util2.js", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedDefaultGroupCssBundleResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                getDeploymentVersion() + "/default/common/bundle/main.css", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public String getMappedDefaultGroupCssBundleDirPath() {
        return getDeploymentVersion() + "/default/common/bundle/";
    }

    public Resource getMappedIphoneGroupCssResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                getDeploymentVersion() + "/iphone/common/main.css", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedIphoneGroupCssBundleResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                getDeploymentVersion() + "/iphone/common/bundle/main.css", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedIphoneGroupNamedScriptResource() {
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                getDeploymentVersion() + "/iphone/common/main.js", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedIphoneGroupNamedScriptResourcePath2() {
        return new ResourceBean(getRequestedNamedScriptResourcePath2(),
                getDeploymentVersion() + "/iphone/common/ajax.js", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public String getMappedIphoneGroupNamedScriptResourceHref() {
        return getScriptClientPathPrefix()
        + getMappedIphoneGroupNamedScriptResource().getNewPath();
    }

    public Resource getMappedIphoneGroupNamedScriptBundleResourcePath() {
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                getDeploymentVersion() + "/iphone/common/bundle/main.js", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public String getMappedIphoneGroupNamedScriptBundleResourceHref() {
        return getScriptClientPathPrefix()
        + getMappedIphoneGroupNamedScriptBundleResourcePath()
        .getNewPath();
    }

    public Resource getMappedIphoneGroupPackagedScriptBaseDirResource() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/iphone/util/", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedIphoneGroupPackagedScriptResource1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/iphone/util/util1.js", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedIphoneGroupPackagedScriptResource2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/iphone/util/util2.js", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedAndroidGroupBundledScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(), getDeploymentVersion()
                + "/android/util/bundle/bundle-all.js", getRootResourcesPath(),
                getGroupTestData().createAndroidGroup());
    }

    public Resource getMappedAndroidGroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/android/util/util1.js", getRootResourcesPath(),
                getGroupTestData().createAndroidGroup());
    }

    public Resource getMappedAndroidGroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/android/util/util2.js", getRootResourcesPath(),
                getGroupTestData().createAndroidGroup());
    }

    public Resource getMappedAppleGroupNamedScriptResource() {
        return new ResourceBean(getRequestedNamedScriptResourcePath(),
                getDeploymentVersion() + "/apple/common/main.js", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    public Resource getMappedAppleGroupPackagedScriptBaseDirResource() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/apple/util/", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    public Resource getMappedAppleGroupPackagedScriptBundleResource() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(), getDeploymentVersion()
                + "/apple/util/bundle/package.js", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    public Resource getMappedAppleGroupPackagedScriptResource1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/apple/util/util1.js", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    public Resource getMappedAppleGroupPackagedScriptResource2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/apple/util/util2.js", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    public Resource getMappedHD800GroupBundledScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(), getDeploymentVersion()
                + "/hd800/util/bundle/bundle-all.js", getRootResourcesPath(),
                getGroupTestData().createHD800Group());
    }

    public Resource getMappedHD800GroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/hd800/util/util1.js", getRootResourcesPath(),
                getGroupTestData().createHD800Group());
    }

    public Resource getMappedHD800GroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/hd800/util/util2.js", getRootResourcesPath(),
                getGroupTestData().createHD800Group());
    }

    public Resource getMappedMediumGroupBundledScriptBundleResourcePath() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(), getDeploymentVersion()
                + "/medium/util/bundle/bundle-all.js", getRootResourcesPath(),
                getGroupTestData().createMediumGroup());
    }

    public Resource getMappedMediumGroupBundledScriptResourcePath1() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/medium/util/util1.js", getRootResourcesPath(),
                getGroupTestData().createMediumGroup());
    }

    public Resource getMappedMediumGroupBundledScriptResourcePath2() {
        return new ResourceBean(getRequestedPackageScriptResourcePath(),
                getDeploymentVersion() + "/medium/util/util2.js", getRootResourcesPath(),
                getGroupTestData().createMediumGroup());
    }

    public String getMappedIphoneGroupCssBundleDirPath() {
        return getDeploymentVersion() + "/iphone/common/bundle/";
    }

    public Resource getMappedAppleGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(),
                getJspResourcesRootServletPath() + "apple/detail/bdp.jsp",
                getRootResourcesPath(),
                getGroupTestData().createAppleGroup());

    }

    public Resource getMappedAppleGroupCssResourcePath() {
        return new ResourceBean(getRequestedCssResourcePath(),
                getDeploymentVersion() + "/apple/common/main.css", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    public Resource getMappedMediumGroupResourcePath() {
        return new ResourceBean(getRequestedJspResourcePath(), getJspResourcesRootServletPath()
                + "medium/detail/bdp.jsp", getRootResourcesPath(),
                getGroupTestData().createMediumGroup());
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
        return new ResourceBean(getRequestedImageResourcePath(),
                getDeploymentVersion() + "/default/common/unmetered", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedIphoneGroupImageResourcePath() {
        return new ResourceBean(getRequestedImageResourcePath(),
                getDeploymentVersion() + "/iphone/common/unmetered", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedAppleGroupImageResourcePath() {
        return new ResourceBean(getRequestedImageResourcePath(),
                getDeploymentVersion() + "/apple/common/unmetered", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    public Resource getMappedDefaultGroupPngImageResourcePath() {
        return new ResourceBean(getRequestedImageResourcePath(),
                getDeploymentVersion() + "/default/common/unmetered.png", getRootResourcesPath(),
                getGroupTestData().createDefaultGroup());
    }

    public Resource getMappedIphoneGroupPngImageResourcePath() {
        return new ImageResourceBean(getRequestedImageResourcePath(),
                getDeploymentVersion() + "/iphone/common/unmetered.png", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedIphoneGroupDotNullImageResourcePath() {
        return new ImageResourceBean(getRequestedImageResourcePath(),
                getDeploymentVersion() + "/iphone/common/unmetered.null", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedScaledIphoneGroupGifImageResourcePath() {
        final ImageResourceBean resourceBean =
                new ImageResourceBean(getRequestedImageResourcePath(), getDeploymentVersion()
                        + "/iphone/common/scaled/w90/h60/unmetered.gif", getRootResourcesPath(),
                        getGroupTestData().createIPhoneGroup());

        resourceBean.setImageWidth(90);
        resourceBean.setImageHeight(60);

        return resourceBean;
    }

    public Resource getMappedScaledIphoneGroupPngImageResourcePath() {
        final ImageResourceBean resourceBean =
            new ImageResourceBean(getRequestedImageResourcePath(), getDeploymentVersion()
                    + "/iphone/common/scaled/w90/h60/unmetered.png", getRootResourcesPath(),
                    getGroupTestData().createIPhoneGroup());

        resourceBean.setImageWidth(90);
        resourceBean.setImageHeight(60);

        return resourceBean;
    }

    public Resource getMappedIphoneGroupGifImageResourcePath() {
        return new ResourceBean(getRequestedImageResourcePath(),
                getDeploymentVersion() + "/iphone/common/unmetered.gif", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
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

    public String getRequestedPropertiesResourcePath() {
        return "common/main.properties";
    }

    public Resource getMappedIphoneGroupPropertiesResourcePath() {
        return new ResourceBean(getRequestedPropertiesResourcePath(),
                "iphone/common/main.properties", getRootResourcesPath(),
                getGroupTestData().createIPhoneGroup());
    }

    public Resource getMappedAppleGroupPropertiesResourcePath() {
        return new ResourceBean(getRequestedPropertiesResourcePath(),
                "apple/common/main.properties", getRootResourcesPath(),
                getGroupTestData().createAppleGroup());
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

}
