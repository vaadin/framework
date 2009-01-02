package com.itmill.toolkit.demo.sampler.gwt.client.ui;

import com.google.gwt.dom.client.Element;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.ILabel;

public class ICodeLabel extends ILabel {

    public ICodeLabel() {
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
