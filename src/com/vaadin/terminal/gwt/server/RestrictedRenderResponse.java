/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * Read-only wrapper for a {@link RenderResponse}.
 * 
 * Only for use by {@link PortletApplicationContext} and
 * {@link PortletApplicationContext2}.
 */
class RestrictedRenderResponse implements RenderResponse, Serializable {

    private RenderResponse response;

    RestrictedRenderResponse(RenderResponse response) {
        this.response = response;
    }

    public void addProperty(String key, String value) {
        response.addProperty(key, value);
    }

    public PortletURL createActionURL() {
        return response.createActionURL();
    }

    public PortletURL createRenderURL() {
        return response.createRenderURL();
    }

    public String encodeURL(String path) {
        return response.encodeURL(path);
    }

    public void flushBuffer() throws IOException {
        // NOP
        // TODO throw?
    }

    public int getBufferSize() {
        return response.getBufferSize();
    }

    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    public String getContentType() {
        return response.getContentType();
    }

    public Locale getLocale() {
        return response.getLocale();
    }

    public String getNamespace() {
        return response.getNamespace();
    }

    public OutputStream getPortletOutputStream() throws IOException {
        // write forbidden
        return null;
    }

    public PrintWriter getWriter() throws IOException {
        // write forbidden
        return null;
    }

    public boolean isCommitted() {
        return response.isCommitted();
    }

    public void reset() {
        // NOP
        // TODO throw?
    }

    public void resetBuffer() {
        // NOP
        // TODO throw?
    }

    public void setBufferSize(int size) {
        // NOP
        // TODO throw?
    }

    public void setContentType(String type) {
        // NOP
        // TODO throw?
    }

    public void setProperty(String key, String value) {
        response.setProperty(key, value);
    }

    public void setTitle(String title) {
        response.setTitle(title);
    }

    public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
        // NOP
        // TODO throw?
    }

    public ResourceURL createResourceURL() {
        return response.createResourceURL();
    }

    public CacheControl getCacheControl() {
        return response.getCacheControl();
    }

    public void addProperty(Cookie cookie) {
        // NOP
        // TODO throw?
    }

    public void addProperty(String key, Element element) {
        // NOP
        // TODO throw?
    }

    public Element createElement(String tagName) throws DOMException {
        // NOP
        return null;
    }
}