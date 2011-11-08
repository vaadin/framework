package com.vaadin.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface WrappedRequest {
    /**
     * Get the named HTTP or portlet request parameter.
     * 
     * @see javax.servlet.ServletRequest#getParameter(String)
     * @see javax.portlet.PortletRequest#getParameter(String)
     * 
     * @param parameter
     * @return
     */
    public String getParameter(String parameter);

    public Map<String, String[]> getParameterMap();

    /**
     * Returns the length of the request content that can be read from the input
     * stream returned by {@link #getInputStream()}.
     * 
     * @return content length in bytes
     */
    public int getContentLength();

    /**
     * Returns an input stream from which the request content can be read. The
     * request content length can be obtained with {@link #getContentLength()}
     * without reading the full stream contents.
     * 
     * @return
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException;

    /**
     * @see javax.servlet.ServletRequest#getAttribute(String)
     * @see javax.portlet.PortletRequest#getAttribute(String)
     */
    public Object getAttribute(String name);

    /**
     * @see javax.servlet.ServletRequest#setAttribute(String, Object)
     * @see javax.portlet.PortletRequest#setAttribute(String, Object)
     */
    public void setAttribute(String name, Object value);

    public String getRequestPathInfo();

    public int getSessionMaxInactiveInterval();

    public Object getSessionAttribute(String name);

    public void setSessionAttribute(String name, Object attribute);

    public String getContentType();

}
