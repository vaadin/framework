/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.Map;

import org.jsoup.nodes.Document;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;

/**
 * A representation of a bootstrap page being generated. The bootstrap page
 * contains of the full DOM of the HTML document as well as the HTTP headers
 * that will be included in the corresponding HTTP response.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class BootstrapPageResponse extends BootstrapResponse {

    private final Map<String, Object> headers;
    private final Document document;

    /**
     * Crate a new bootstrap page response.
     * 
     * @see BootstrapResponse#BootstrapResponse(BootstrapHandler,
     *      WrappedRequest, Application, Integer)
     * 
     * @param handler
     *            the bootstrap handler that is firing the event
     * @param request
     *            the wrapped request for which the bootstrap page should be
     *            generated
     * @param application
     *            the application for which the bootstrap page should be
     *            generated
     * @param rootId
     *            the generated id of the Root that will be displayed on the
     *            page
     * @param document
     *            the DOM document making up the HTML page
     * @param headers
     *            a map into which header data can be added
     */
    public BootstrapPageResponse(BootstrapHandler handler,
            WrappedRequest request, Application application, Integer rootId,
            Document document, Map<String, Object> headers) {
        super(handler, request, application, rootId);
        this.headers = headers;
        this.document = document;
    }

    /**
     * Sets a header value that will be added to the HTTP response. If the
     * header had already been set, the new value overwrites the previous one.
     * 
     * @see WrappedResponse#setHeader(String, String)
     * 
     * @param name
     *            the name of the header
     * @param value
     *            the header value
     */
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * Properly formats a timestamp as a date in a header that will be included
     * in the HTTP response. If the header had already been set, the new value
     * overwrites the previous one.
     * 
     * @see #setHeader(String, String)
     * @see WrappedResponse#setDateHeader(String, long)
     * 
     * @param name
     *            the name of the header
     * @param timestamp
     *            the number of milliseconds since epoch
     */
    public void setDateHeader(String name, long timestamp) {
        headers.put(name, Long.valueOf(timestamp));
    }

    /**
     * Gets the document node representing the root of the DOM hierarchy that
     * will be used to generate the HTML page. Changes to the document will be
     * reflected in the HTML.
     * 
     * @return the document node
     */
    public Document getDocument() {
        return document;
    }

}
