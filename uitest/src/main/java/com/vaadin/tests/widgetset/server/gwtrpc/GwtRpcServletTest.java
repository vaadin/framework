package com.vaadin.tests.widgetset.server.gwtrpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.vaadin.tests.widgetset.client.gwtrpc.GwtRpcServiceTest;

/**
 * Test GWT RPC in Vaadin DevMode.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class GwtRpcServletTest extends RemoteServiceServlet
        implements GwtRpcServiceTest {

    @Override
    public String giveMeThat(String that, String haveThis) {
        return "Take " + that + " for " + haveThis;
    }

}
