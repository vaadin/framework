package com.vaadin.terminal;

import java.util.Map;

public interface WrappedRequest {
    public String getParameter(String parameter);

    public Map<String, String[]> getParameterMap();

    public String getRequestPathInfo();

    public int getSessionMaxInactiveInterval();

    public Object getSessionAttribute(String name);

    public void setSessionAttribute(String name, Object attribute);

}
