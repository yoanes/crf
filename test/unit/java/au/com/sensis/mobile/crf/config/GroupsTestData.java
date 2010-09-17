package au.com.sensis.mobile.crf.config;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.Groups;

/**
 * Test data for the {@link Group} class.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsTestData {

    private final GroupTestData groupTestData = new GroupTestData();

    public Groups createGroups1() {
        final Groups groups = new Groups();

        groups.setGroups(new Group[] {
                getGroupTestData().createIPhoneGroup(),
                getGroupTestData().createDefaultGroup() });

        return groups;
    }

    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

}
