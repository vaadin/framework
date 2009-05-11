package com.vaadin.demo.sampler.gwt.client.ui;

import com.google.gwt.dom.client.Element;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VLabel;

public class VCodeLabel extends VLabel {

    public VCodeLabel() {
        super();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        Element pre = getElement().getFirstChildElement();
        if (null != pre) {
            pre.setClassName("prettyprint");
            prettyPrint();
        }
    }

    private native void prettyPrint()
    /*-{
         $wnd.prettyPrint();
     }-*/;

}
