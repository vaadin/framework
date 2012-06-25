/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.widgetset.server;

import com.vaadin.terminal.AbstractExtension;
import com.vaadin.tests.widgetset.client.ComplexTestBean;
import com.vaadin.tests.widgetset.client.SerializerTestRpc;

public class SerializerTestExtension extends AbstractExtension {

    @Override
    public ComplexTestBean getState() {
        return (ComplexTestBean) super.getState();
    }

    public void registerRpc(SerializerTestRpc rpc) {
        super.registerRpc(rpc);
    }

}
