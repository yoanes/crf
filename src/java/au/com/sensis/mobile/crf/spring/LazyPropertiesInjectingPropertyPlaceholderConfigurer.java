package au.com.sensis.mobile.crf.spring;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * {@link PropertyPlaceholderConfigurer} specialisation that expects
 * {@link #setPropertiesBeanName(String)} to be called instead of
 * {@link #setProperties(Properties)}. Then, just before this bean factory post
 * processor is applied, the properties bean name is retrieved from the bean
 * factory and set into the underlying {@link PropertyPlaceholderConfigurer}.
 * <p>
 * This class is provided for the rather specialised scenario:
 * <ol>
 * <li>A PropertiesFactoryBean is configured to load a platform specific
 * properties file (eg. the file name is parameterised by the @{global.platform}
 * placeholder.</li>
 * <li>Another PropertyPlaceholderConfigurer depends on the aforementioned
 * PropertiesFactoryBean file.</li>
 * </ol>
 * By default, it appears that dependencies of
 * {@link PropertyPlaceholderConfigurer}s are never subjected to processing by
 * other {@link PropertyPlaceholderConfigurer}s.
 * </p>
 * <p>
 * Note that {@link #setProperties(Properties)} and
 * {@link #setPropertiesArray(Properties[])} throw
 * {@link UnsupportedOperationException}. If you really wish to call these,
 * chances are that you should be using a regular
 * {@link PropertyPlaceholderConfigurer}.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
// TODO: cloned from original code in mobilesComponents/core project. Possibly
// should be in somewhere more common like sdpCommon. However, sdpcommon-utils
// seems like the most logical module but it currently does not depend on
// spring and arguably shouldn't.  Furthermore, every component will depend
// on crf so maybe it should just stay in this project.
public class LazyPropertiesInjectingPropertyPlaceholderConfigurer extends
        PropertyPlaceholderConfigurer {

    private String propertiesBeanName;
    private BeanFactory beanFactory;

    /**
     * @return the propertiesBeanName
     */
    public final String getPropertiesBeanName() {
        return propertiesBeanName;
    }

    /**
     * @param propertiesBeanName
     *            the propertiesBeanName to set
     */
    public final void setPropertiesBeanName(final String propertiesBeanName) {
        this.propertiesBeanName = propertiesBeanName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        // Ensure that the superclass has access to the BeanFactory as per
        // normal.
        super.setBeanFactory(beanFactory);

        // Ensure that we also have access to the BeanFactory, since the
        // superclass
        // does not expose it.
        this.beanFactory = beanFactory;
    }

    /**
     * @return the beanFactory
     */
    private BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * {@inheritDoc}
     *
     * Overridden to lookup the bean given by {@link #getPropertiesBeanName()},
     * inject it into the superclass, then continue with normal superclass
     * processing.
     */
    @Override
    public void postProcessBeanFactory(
            final ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

        // Explicitly set the Properties into the superclass, since the local
        // method is overriden to be unsupported.
        super.setProperties(lookupPropertiesBean());

        super.postProcessBeanFactory(beanFactory);
    }

    /**
     * @return {@link Properties} bean obtained from {@link #getBeanFactory()}
     *         using the given name {@link #getPropertiesBeanName()}.
     */
    private Properties lookupPropertiesBean() {
        return (Properties) getBeanFactory().getBean(getPropertiesBeanName());
    }

    /**
     * {@inheritDoc}
     *
     * This implementation throws {@link UnsupportedOperationException}. If you
     * really wish to call this, chances are that you should be using a regular
     * {@link PropertyPlaceholderConfigurer}.
     */
    @Override
    public void setProperties(final Properties properties) {
        throw new UnsupportedOperationException(
                "setProperties is unsupported. Call setPropertiesBeanName instead, "
                        + "otherwise use a regular PropertyPlaceholderConfigurer.");
    }

    /**
     * {@inheritDoc}
     *
     * This implementation throws {@link UnsupportedOperationException}. If you
     * really wish to call this, chances are that you should be using a regular
     * {@link PropertyPlaceholderConfigurer}.
     */
    @Override
    public void setPropertiesArray(final Properties[] propertiesArray) {
        throw new UnsupportedOperationException(
                "setPropertiesArray is unsupported. Call setPropertiesBeanName instead, "
                        + "otherwise use a regular PropertyPlaceholderConfigurer.");
    }

}
