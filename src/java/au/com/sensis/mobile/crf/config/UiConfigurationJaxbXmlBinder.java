package au.com.sensis.mobile.crf.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.UrlResource;

import au.com.sensis.mobile.crf.exception.XmlBinderRuntimeException;
import au.com.sensis.mobile.crf.util.XmlBinder;
import au.com.sensis.wireless.common.utils.jaxb.JaxbXMLBinderImpl;

/**
 * {@link XmlBinder} that uses JAXB to slurp in the XML, then programatically copies the data
 * into a {@link UiConfiguration}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class UiConfigurationJaxbXmlBinder implements XmlBinder {

    /**
     * We use the explicit {@link JaxbXMLBinderImpl} since this is a hard contraint.
     * Not just any binder will do.
     */
    private final JaxbXMLBinderImpl binder;

    /**
     * Constructor.
     *
     * @param binder {@link JaxbXMLBinderImpl} to use to convert source XML to Java objects.
     */
    public UiConfigurationJaxbXmlBinder(final JaxbXMLBinderImpl binder) {
        this.binder = binder;
    }

    /**
     * {@inheritDoc}
     *
     * Note that the returned object will not have the following fields set, as
     * this data is not part of the source XML. Callers should set these fields
     * themselves.
     * <ul>
     * <li>{@link UiConfiguration#getSourceUrl()}</li>
     * <li>{@link UiConfiguration#getSourceTimestamp()}</li>
     * </ul>
     */
    @Override
    public UiConfiguration unmarshall(final URL xml) throws XmlBinderRuntimeException {
        try {

            final UrlResource urlResource = new UrlResource(xml);
            final String xmlString = IOUtils.toString(urlResource.getInputStream());

            final au.com.sensis.mobile.crf.config.jaxb.generated.UiConfiguration uiConfiguration =
                    (au.com.sensis.mobile.crf.config.jaxb.generated.UiConfiguration) getBinder()
                            .unmarshall(xmlString);

            return transformJaxbUiConfiguration(uiConfiguration);

        } catch (final Exception e) {
            throw new XmlBinderRuntimeException("Error unmarshalling XML from: " + xml, e);
        }
    }

    private UiConfiguration transformJaxbUiConfiguration(
            final au.com.sensis.mobile.crf.config.jaxb.generated.UiConfiguration
                jaxbUiConfiguration) {

        final UiConfiguration result = new UiConfiguration();

        result.setConfigPath(jaxbUiConfiguration.getConfigPath());
        result.setGroupsAndImports(transformJaxbGroups(jaxbUiConfiguration.getGroups()));
        return result;
    }

    private GroupsAndImports transformJaxbGroups(
            final au.com.sensis.mobile.crf.config.jaxb.generated.Groups jaxbGroups) {

        final GroupsAndImports groupsAndImports = new GroupsAndImports();
        groupsAndImports.setDefaultGroup(transformJaxbDefaultGroup(jaxbGroups.getDefaultGroup()));
        groupsAndImports.setGroupOrImport(transformJaxbGroupOrImportList(jaxbGroups
                .getGroupOrImport()));

        return groupsAndImports;
    }

    private GroupOrImport[] transformJaxbGroupOrImportList(final List<Object> groupOrImport) {

        final List<GroupOrImport> groupOrImports = new ArrayList<GroupOrImport>();

        for (final Object currGroupOrImport : groupOrImport) {
            groupOrImports.add(transformGroupOrImport(currGroupOrImport));
        }

        return groupOrImports.toArray(new GroupOrImport [] {});
    }

    private GroupOrImport transformGroupOrImport(final Object currGroupOrImport) {

        if (au.com.sensis.mobile.crf.config.jaxb.generated.Group.class.equals(currGroupOrImport
                .getClass())) {

            final au.com.sensis.mobile.crf.config.jaxb.generated.Group jaxbGroup =
                    (au.com.sensis.mobile.crf.config.jaxb.generated.Group) currGroupOrImport;
            return createGroupOrImportBean(jaxbGroup);

        } else if (au.com.sensis.mobile.crf.config.jaxb.generated.Import.class
                .equals(currGroupOrImport.getClass())) {

            final au.com.sensis.mobile.crf.config.jaxb.generated.Import jaxbImport =
                    (au.com.sensis.mobile.crf.config.jaxb.generated.Import) currGroupOrImport;
            return createGroupOrImportBean(jaxbImport);

        } else {
            throw new IllegalStateException(
                    "currGroupOrImport should be either a Group or Import but was: "
                            + currGroupOrImport
                            + ". Looks like you generated new JAXB classes but "
                            + "forgot to update this code.");
        }
    }

    private GroupOrImport createGroupOrImportBean(
            final au.com.sensis.mobile.crf.config.jaxb.generated.Import jaxbImport) {

        validateJaxbImport(jaxbImport);

        final GroupImport groupImport = new GroupImport();

        if (StringUtils.isNotBlank(jaxbImport.getName())) {
            groupImport.setGroupName(jaxbImport.getName());
        }

        if (StringUtils.isNotBlank(jaxbImport.getFromName())) {
            groupImport.setFromGroupName(jaxbImport.getFromName());
        }

        // The "fromConfigPath" field may be blank, since it is valid for a UiConfiguration to have
        // a blank config-path (which reperesents the default config).
        if (jaxbImport.getFromConfigPath() != null) {
            groupImport.setFromConfigPath(jaxbImport.getFromConfigPath());
        }

        return new GroupOrImportBean(groupImport);
    }

    private void validateJaxbImport(
            final au.com.sensis.mobile.crf.config.jaxb.generated.Import jaxbImport) {

        if (StringUtils.isBlank(jaxbImport.getName())
                && StringUtils.isBlank(jaxbImport.getFromName())
                && StringUtils.isBlank(jaxbImport.getFromConfigPath())) {
            throw new XmlBinderRuntimeException(
                    "import element must have at least one of 'name', "
                        + "'fromName' or 'fromConfigPath' attributes set.");
        }
    }

    private GroupOrImport createGroupOrImportBean(
            final au.com.sensis.mobile.crf.config.jaxb.generated.Group jaxbGroup) {
        final Group group = new Group();
        group.setName(jaxbGroup.getName());
        group.setExpr(jaxbGroup.getExpr());
        return new GroupOrImportBean(group);
    }

    private DefaultGroup transformJaxbDefaultGroup(
            final au.com.sensis.mobile.crf.config.jaxb.generated.DefaultGroup jaxbDefaultGroup) {
        final DefaultGroup defaultGroup = new DefaultGroup();
        defaultGroup.setName(jaxbDefaultGroup.getName());
        return defaultGroup;
    }

    /**
     * @return the binder
     */
    private JaxbXMLBinderImpl getBinder() {
        return binder;
    }

}
