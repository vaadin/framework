package com.vaadin.tests.widgetset.client.gwtrpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Test GWT RPC in Vaadin DevMode.
 *
 * @author Vaadin Ltd
 */
@RemoteServiceRelativePath("GwtRpcTest")
public interface GwtRpcServiceTest extends RemoteService {

    /*
     * Dummy method to verify if RPC works.
     */
    String giveMeThat(String that, String haveThis);

}
