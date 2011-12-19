/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ServletPortletHelper.ApplicationClassException;

/**
 * TODO Write documentation, fix JavaDoc tags.
 * 
 * @author peholmst
 */
public class ApplicationPortlet2 extends AbstractApplicationPortlet {

    private Class<? extends Application> applicationClass;

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        try {
            applicationClass = ServletPortletHelper.getApplicationClass(
                    config.getInitParameter("application"),
                    config.getInitParameter(Application.ROOT_PARAMETER),
                    getClassLoader());
        } catch (ApplicationClassException e) {
            throw new PortletException(e);
        }
    }

    @Override
    protected Class<? extends Application> getApplicationClass() {
        return applicationClass;
    }

}
