package com.vaadin.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

public class VaadinServletTest {

    @Test
    public void testGetLastPathParameter() {
        assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com"));
        assertEquals(";a",
                VaadinServlet.getLastPathParameter("http://myhost.com;a"));
        assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com/hello"));
        assertEquals(";b=c", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;b=c"));
        assertEquals("",
                VaadinServlet.getLastPathParameter("http://myhost.com/hello/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;a/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello;a=1/"));
        assertEquals(";b", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b"));
        assertEquals(";b=1", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1"));
        assertEquals(";b=1,c=2", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1,c=2"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/hello/;b=1,c=2/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;a/"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;a=1/"));
        assertEquals(";b", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b"));
        assertEquals(";b=1", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1"));
        assertEquals(";b=1,c=2", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2"));
        assertEquals("", VaadinServlet
                .getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2/"));
    }

    @Test
    public void getStaticFilePath() {
        VaadinServlet servlet = new VaadinServlet();

        // Mapping: /VAADIN/*
        // /VAADIN
        assertNull(servlet
                .getStaticFilePath(createServletRequest("/VAADIN", null)));
        // /VAADIN/ - not really sensible but still interpreted as a resource
        // request
        assertEquals("/VAADIN/", servlet
                .getStaticFilePath(createServletRequest("/VAADIN", "/")));
        // /VAADIN/vaadinBootstrap.js
        assertEquals("/VAADIN/vaadinBootstrap.js", servlet.getStaticFilePath(
                createServletRequest("/VAADIN", "/vaadinBootstrap.js")));
        // /VAADIN/foo bar.js
        assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(
                createServletRequest("/VAADIN", "/foo bar.js")));
        // /VAADIN/.. - not normalized and disallowed in this method
        assertEquals("/VAADIN/..", servlet
                .getStaticFilePath(createServletRequest("/VAADIN", "/..")));

        // Mapping: /*
        // /
        assertNull(servlet.getStaticFilePath(createServletRequest("", null)));
        // /VAADIN
        assertNull(
                servlet.getStaticFilePath(createServletRequest("", "/VAADIN")));
        // /VAADIN/
        assertEquals("/VAADIN/", servlet
                .getStaticFilePath(createServletRequest("", "/VAADIN/")));
        // /VAADIN/foo bar.js
        assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(
                createServletRequest("", "/VAADIN/foo bar.js")));
        // /VAADIN/.. - not normalized and disallowed in this method
        assertEquals("/VAADIN/..", servlet
                .getStaticFilePath(createServletRequest("", "/VAADIN/..")));
        // /BAADIN/foo.js
        assertNull(servlet
                .getStaticFilePath(createServletRequest("", "/BAADIN/foo.js")));

        // Mapping: /myservlet/*
        // /myservlet
        assertNull(servlet
                .getStaticFilePath(createServletRequest("/myservlet", null)));
        // /myservlet/VAADIN
        assertNull(servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN")));
        // /myservlet/VAADIN/
        assertEquals("/VAADIN/", servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN/")));
        // /myservlet/VAADIN/foo bar.js
        assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN/foo bar.js")));
        // /myservlet/VAADIN/.. - not normalized and disallowed in this method
        assertEquals("/VAADIN/..", servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/VAADIN/..")));
        // /myservlet/BAADIN/foo.js
        assertNull(servlet.getStaticFilePath(
                createServletRequest("/myservlet", "/BAADIN/foo.js")));

    }

    private HttpServletRequest createServletRequest(String servletPath,
            String pathInfo) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getServletPath()).thenReturn(servletPath);
        Mockito.when(request.getPathInfo()).thenReturn(pathInfo);
        Mockito.when(request.getRequestURI()).thenReturn("/context"+pathInfo);
        Mockito.when(request.getContextPath()).thenReturn("/context");
        return request;
    }
}
