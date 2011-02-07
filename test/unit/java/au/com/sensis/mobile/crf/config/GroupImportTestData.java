package au.com.sensis.mobile.crf.config;

/**
 * Test data for the {@link GroupImport} class.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupImportTestData {

    public GroupImport createImageCategoriesGroupImport() {
        final GroupImport groupImport = new GroupImport();
        groupImport.setFromConfigPath("global/imageCategories");
        return groupImport;
    }

    public GroupImport createiPadImportFromNonDefaultNamespace() {
        final GroupImport groupImport = new GroupImport();
        groupImport.setGroupName("ipad");
        groupImport.setFromConfigPath("global/extraDevices");
        return groupImport;
    }

    public GroupImport createAndroidOsImportFromDefaultNamespace() {
        final GroupImport groupImport = new GroupImport();
        groupImport.setGroupName("android-os");
        return groupImport;
    }

}
