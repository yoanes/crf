package au.com.sensis.mobile.crf.config;

/**
 * Test data for the {@link Group} class.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupTestData {

    public Group createIPhoneGroup() {
        final Group group = new Group();
        group.setName("iphone");
        group.setExpr("device.name =~ '.*iPhone.*'");
        return group;
    }

    public Group createIpadGroup() {
        final Group group = new Group();
        group.setName("ipad");
        group.setExpr("device.isA('Apple-iPad')");
        return group;
    }

    public Group createAndroidGroup() {
        final Group group = new Group();
        group.setName("android-os");
        group.setExpr("device.isA('Android-OS') or device.isA('Android-Emulator')");
        return group;
    }

    public DefaultGroup createDefaultGroup() {
        final DefaultGroup defaultGroup = new DefaultGroup();
        defaultGroup.setName("default");
        return defaultGroup;
    }

    public Group createAppleGroup() {
        final Group group = new Group();
        group.setName("apple");
        group.setExpr("device.name =~ '.*Apple.*'");
        return group;
    }

    public Group createAppleWebkitGroup() {
        final Group group = new Group();
        group.setName("applewebkit");
        group.setExpr("device.userAgent =~ '.*AppleWebKit.*'");
        return group;
    }

    public Group createHD800Group() {
        final Group group = new Group();
        group.setName("HD800");
        group.setExpr("device.imageCategory eq 'HD800'");
        return group;
    }

    public Group createLargeImageCategoryGroup() {
        final Group group = new Group();
        group.setName("L");
        group.setExpr("device.imageCategory eq 'L'");
        return group;
    }

    public Group createMediumGroup() {
        final Group group = new Group();
        group.setName("M");
        group.setExpr("device.imageCategory eq 'M'");
        return group;
    }

    public Group createExtraSmallGroup() {
        final Group group = new Group();
        group.setName("extrasmall");
        group.setExpr("device.imageCategory eq 'XS'");
        return group;
    }

}
