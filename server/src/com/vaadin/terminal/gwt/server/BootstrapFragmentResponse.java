/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.List;

import org.jsoup.nodes.Node;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;

public class BootstrapFragmentResponse extends BootstrapResponse {
    private final List<Node> fragmentNodes;

    public BootstrapFragmentResponse(BootstrapHandler handler,
            WrappedRequest request, List<Node> fragmentNodes,
            Application application, Integer rootId) {
        super(handler, request, application, rootId);
        this.fragmentNodes = fragmentNodes;
    }

    public List<Node> getFragmentNodes() {
        return fragmentNodes;
    }

}
