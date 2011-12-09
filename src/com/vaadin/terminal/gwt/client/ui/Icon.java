/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.UIObject;
import com.vaadin.terminal.gwt.client.ApplicationConnection;

public class Icon extends UIObject {
    public static final String CLASSNAME = "v-icon";
    private final ApplicationConnection client;
    private String myUri;

    public Icon(ApplicationConnection client) {
        setElement(DOM.createImg());
        DOM.setElementProperty(getElement(), "alt", "");
        setStyleName(CLASSNAME);
        this.client = client;
    }

    public Icon(ApplicationConnection client, String uidlUri) {
        this(client);
        setUri(uidlUri);
    }

    public void setUri(String uidlUri) {
        if (!uidlUri.equals(myUri)) {
            /*
             * Start sinking onload events, widgets responsibility to react. We
             * must do this BEFORE we set src as IE fires the event immediately
             * if the image is found in cache (#2592).
             */
            sinkEvents(Event.ONLOAD);

            String uri = client.translateVaadinUri(uidlUri);
            DOM.setElementProperty(getElement(), "src", uri);
            myUri = uidlUri;
        }
    }

}
