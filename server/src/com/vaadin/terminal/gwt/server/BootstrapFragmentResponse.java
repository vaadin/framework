/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.List;

import org.jsoup.nodes.Node;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;

/**
 * A representation of a bootstrap fragment being generated. The bootstrap
 * fragment is the HTML code that will make up the actual application. This also
 * includes the JavaScript that initializes the application.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class BootstrapFragmentResponse extends BootstrapResponse {
    private final List<Node> fragmentNodes;

    /**
     * Crate a new bootstrap fragment response.
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
     * @param fragmentNodes
     *            a mutable list containing the DOM nodes that will make up the
     *            application HTML
     */
    public BootstrapFragmentResponse(BootstrapHandler handler,
            WrappedRequest request, Application application, Integer rootId,
            List<Node> fragmentNodes) {
        super(handler, request, application, rootId);
        this.fragmentNodes = fragmentNodes;
    }

    /**
     * Gets the list of DOM nodes that will be used to generate the fragment
     * HTML. Changes to the returned list will be reflected in the generated
     * HTML.
     * 
     * @return the current list of DOM nodes that makes up the application
     *         fragment
     */
    public List<Node> getFragmentNodes() {
        return fragmentNodes;
    }

}
