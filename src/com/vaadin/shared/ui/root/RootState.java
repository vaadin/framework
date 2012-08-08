/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.root;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.Connector;

public class RootState extends ComponentState {
    private Connector content;
    private int heartbeatInterval;

    public Connector getContent() {
        return content;
    }

    public void setContent(Connector content) {
        this.content = content;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
}