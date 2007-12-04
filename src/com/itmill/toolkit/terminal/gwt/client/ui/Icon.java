/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.UIObject;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;

public class Icon extends UIObject {
    private final ApplicationConnection client;
    private String myUri;

    public Icon(ApplicationConnection client) {
        setElement(DOM.createImg());
        DOM.setElementProperty(getElement(), "alt", "icon");
        setStyleName("i-icon");
        this.client = client;
    }

    public Icon(ApplicationConnection client, String uidlUri) {
        this(client);
        setUri(uidlUri);
    }

    public void setUri(String uidlUri) {
        if (!uidlUri.equals(myUri)) {
            DOM.setElementProperty(getElement(), "src", client
                    .translateToolkitUri(uidlUri));
            myUri = uidlUri;
        }
    }
}
