/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

public interface WrappedResponse extends Serializable {

    public void setStatus(int statusCode);

    public void setContentType(String contentType);

    public void setHeader(String name, String value);

    public void setDateHeader(String name, long timestamp);

    public OutputStream getOutputStream() throws IOException;

    public PrintWriter getWriter() throws IOException;

    /**
     * Sets time in milliseconds, -1 means no cache at all. All required headers
     * related to caching in the response are set based on the time.
     * 
     * @param milliseconds
     *            Cache time in milliseconds
     */
    public void setCacheTime(long milliseconds);

}
