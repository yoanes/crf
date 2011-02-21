package au.com.sensis.mobile.crf.config;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Holds functions we want to expose to JEXL group expressions, as well as their
 * context for evaluation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JexlGroupFunctions {

    private final Device device;
    private final Group group;

    /**
     * Constructor the functions with the passed in context variables.
     *
     * @param device
     *            Device that the functions are to be evaluated for.
     * @param group
     *            Group that the functions are to be evaluated for.
     */
    /* package */JexlGroupFunctions(final Device device, final Group group) {
        this.device = device;
        this.group = group;
    }

    /**
     * @param groupNames
     *            Names of groups to be checked against the device.
     * @return true if the given device belongs to all of the passed in groups.
     */
    public boolean inAllGroups(final String... groupNames) {
        return getGroup().inAllGroups(getDevice(), groupNames);
    }

    /**
     * @param groupNames
     *            Names of groups to be checked against the device.
     * @return true if the given device belongs to any of the passed in groups.
     */
    public boolean inAnyGroup(final String... groupNames) {
        return getGroup().inAnyGroup(getDevice(), groupNames);
    }

    /**
     * @return the device
     */
    private Device getDevice() {
        return device;
    }

    /**
     * @return the group
     */
    private Group getGroup() {
        return group;
    }
}
