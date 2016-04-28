/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.server;

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
 * {@link VaadinPortletSession}.
 */
class RestrictedRenderResponse implements RenderResponse, Serializable {

    private RenderResponse response;

    RestrictedRenderResponse(RenderResponse response) {
        this.response = response;
    }

    @Override
    public void addProperty(String key, String value) {
        response.addProperty(key, value);
    }

    @Override
    public PortletURL createActionURL() {
        return response.createActionURL();
    }

    @Override
    public PortletURL createRenderURL() {
        return response.createRenderURL();
    }

    @Override
    public String encodeURL(String path) {
        return response.encodeURL(path);
    }

    @Override
    public void flushBuffer() throws IOException {
        // NOP
        // TODO throw?
    }

    @Override
    public int getBufferSize() {
        return response.getBufferSize();
    }

    @Override
    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return response.getContentType();
    }

    @Override
    public Locale getLocale() {
        return response.getLocale();
    }

    @Override
    public String getNamespace() {
        return response.getNamespace();
    }

    @Override
    public OutputStream getPortletOutputStream() throws IOException {
        // write forbidden
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        // write forbidden
        return null;
    }

    @Override
    public boolean isCommitted() {
        return response.isCommitted();
    }

    @Override
    public void reset() {
        // NOP
        // TODO throw?
    }

    @Override
    public void resetBuffer() {
        // NOP
        // TODO throw?
    }

    @Override
    public void setBufferSize(int size) {
        // NOP
        // TODO throw?
    }

    @Override
    public void setContentType(String type) {
        // NOP
        // TODO throw?
    }

    @Override
    public void setProperty(String key, String value) {
        response.setProperty(key, value);
    }

    @Override
    public void setTitle(String title) {
        response.setTitle(title);
    }

    @Override
    public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
        // NOP
        // TODO throw?
    }

    @Override
    public ResourceURL createResourceURL() {
        return response.createResourceURL();
    }

    @Override
    public CacheControl getCacheControl() {
        return response.getCacheControl();
    }

    @Override
    public void addProperty(Cookie cookie) {
        // NOP
        // TODO throw?
    }

    @Override
    public void addProperty(String key, Element element) {
        // NOP
        // TODO throw?
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        // NOP
        return null;
    }
}
