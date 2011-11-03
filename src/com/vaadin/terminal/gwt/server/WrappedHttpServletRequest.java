package com.vaadin.terminal.gwt.server;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.terminal.WrappedRequest;

public class WrappedHttpServletRequest implements WrappedRequest {

    private final HttpServletRequest request;

    public WrappedHttpServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    public String getParameter(String parameter) {
        return request.getParameter(parameter);
    }

    public Iterable<String> getParameterNames() {
        return new Iterable<String>() {
            public Iterator<String> iterator() {
                final Enumeration<String> parameterNames = request
                        .getParameterNames();
                return new Iterator<String>() {

                    public boolean hasNext() {
                        return parameterNames.hasMoreElements();
                    }

                    public String next() {
                        return parameterNames.nextElement();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public String[] getParameterValues(String parameter) {
        return request.getParameterValues(parameter);
    }

    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public String getRelativePath() {
        return request.getPathInfo();
    }

    public String getFullPath() {
        return request.getRequestURI();
    }

    public Cookie[] getCookies() {
        return request.getCookies();
    }

}
