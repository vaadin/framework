package com.vaadin.tests.widgetset.client.gwtrpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Test GWT RPC in Vaadin DevMode.
 *
 * @author Vaadin Ltd
 */
public interface GwtRpcServiceTestAsync {

    /*
     * Dummy async method to verify if RPC works.
     */
    void giveMeThat(String that, String haveThis,
            AsyncCallback<String> callback);

}
