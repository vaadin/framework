package com.vaadin.terminal;

import java.util.Map;

import javax.servlet.http.Cookie;

public interface WrappedRequest {
    public String getParameter(String parameter);

    public Iterable<String> getParameterNames();

    public String[] getParameterValues(String parameter);

    public Map<String, String[]> getParameterMap();

    /**
     * Gets the path of the request relative to the root url of the application.
     * 
     * @return
     */
    public String getRelativePath();

    public String getFullPath();

    public Cookie[] getCookies();
}
