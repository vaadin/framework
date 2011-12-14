/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;

import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.WrappedResponse;

/**
 * Wrapper for {@link PortletResponse} and its subclasses.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see WrappedResponse
 * @see WrappedPortletRequest
 */
public class WrappedPortletResponse implements WrappedResponse {
    private static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    static {
        HTTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final PortletResponse response;
    private DeploymentConfiguration deploymentConfiguration;

    /**
     * Wraps a portlet response and an associated deployment configuration
     * 
     * @param response
     *            the portlet response to wrap
     * @param deploymentConfiguration
     *            the associated deployment configuration
     */
    public WrappedPortletResponse(PortletResponse response,
            DeploymentConfiguration deploymentConfiguration) {
        this.response = response;
        this.deploymentConfiguration = deploymentConfiguration;
    }

    public OutputStream getOutputStream() throws IOException {
        return ((MimeResponse) response).getPortletOutputStream();
    }

    /**
     * Gets the original, unwrapped portlet response.
     * 
     * @return the unwrapped portlet response
     */
    public PortletResponse getPortletResponse() {
        return response;
    }

    public void setContentType(String type) {
        ((MimeResponse) response).setContentType(type);
    }

    public PrintWriter getWriter() throws IOException {
        return ((MimeResponse) response).getWriter();
    }

    public void setStatus(int responseStatus) {
        response.setProperty(ResourceResponse.HTTP_STATUS_CODE,
                Integer.toString(responseStatus));
    }

    public void setHeader(String name, String value) {
        response.setProperty(name, value);
    }

    public void setDateHeader(String name, long timestamp) {
        response.setProperty(name, HTTP_DATE_FORMAT.format(new Date(timestamp)));
    }

    public void setCacheTime(long milliseconds) {
        WrappedHttpServletResponse.doSetCacheTime(this, milliseconds);
    }

    public void sendError(int errorCode, String message) throws IOException {
        setStatus(errorCode);
        getWriter().write(message);
    }

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }
}