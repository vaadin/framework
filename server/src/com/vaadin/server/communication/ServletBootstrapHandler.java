/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.server.communication;

import com.vaadin.server.BootstrapHandler;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class ServletBootstrapHandler extends BootstrapHandler {
    @Override
    protected String getServiceUrl(BootstrapContext context) {
        String pathInfo = context.getRequest().getPathInfo();
        if (pathInfo == null) {
            return null;
        } else {
            /*
             * Make a relative URL to the servlet by adding one ../ for each
             * path segment in pathInfo (i.e. the part of the requested path
             * that comes after the servlet mapping)
             */
            return VaadinServletService.getCancelingRelativePath(pathInfo);
        }
    }

    @Override
    public String getThemeName(BootstrapContext context) {
        String themeName = context.getRequest().getParameter(
                VaadinServlet.URL_PARAMETER_THEME);
        if (themeName == null) {
            themeName = super.getThemeName(context);
        }
        return themeName;
    }
}