package com.vaadin.terminal;

import java.util.Map;

public interface WrappedRequest {
    public String getParameter(String parameter);

    public Map<String, String[]> getParameterMap();

}
