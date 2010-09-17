/**
 *
 */
package au.com.sensis.mobile.web.testbed;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import au.com.sensis.wireless.common.volantis.devicerepository.api.DeviceRecognition;
import au.com.sensis.wireless.web.filter.MobileContextSetUpFilter;
import au.com.sensis.wireless.web.mobile.HeaderInterpreter;

/**
 * @author w80634
 *
 */
public class ContextSetUpFIlterThatWorks extends MobileContextSetUpFilter {

	private HeaderInterpreter headerInterpreter;

	/**
     * This method is called by the server before the filter goes into service.
     *
     * @param config    the {@link FilterConfig} passed by the servlet engine.
     *
     * @throws ServletException if the config doesn't contain mandatory params.
     */
    @Override
    public void init(final FilterConfig filterConfig)
            throws ServletException {

        setServletContext(filterConfig.getServletContext());

//        setDeviceRecognition(new VolantisDeviceRecognition());
        setHeaderInterpreter(newHeaderInterpreter());
    }


    @Override
    protected HeaderInterpreter newHeaderInterpreter() {

        return getHeaderInterpreter();
        //return new WhereisMobileHeaderInterpreter();
    }

    /**
     * @param headerInterpreter the headerInterpreter to set.
     */
    @Override
    public void setHeaderInterpreter(final HeaderInterpreter headerInterpreter) {

        this.headerInterpreter = headerInterpreter;
    }

    /**
     * @return  the headerInterpreter.
     */
    @Override
    public HeaderInterpreter getHeaderInterpreter() {

        return headerInterpreter;
    }

    /* (non-Javadoc)
     * @see au.com.sensis.wireless.web.filter.MobileContextSetUpFilter#setDeviceRecognition(au.com.sensis.wireless.common.volantis.devicerepository.api.DeviceRecognition)
     */
    @Override
    public void setDeviceRecognition(final DeviceRecognition deviceRecognition) {
        super.setDeviceRecognition(deviceRecognition);
    }
}
