/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.progressindicator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Util;

public class VProgressIndicator extends Widget {

    public static final String CLASSNAME = "v-progressindicator";
    Element wrapper = DOM.createDiv();
    Element indicator = DOM.createDiv();
    protected ApplicationConnection client;
    protected final Poller poller;
    protected boolean indeterminate = false;
    private boolean pollerSuspendedDueDetach;
    protected int interval;

    public VProgressIndicator() {
        setElement(DOM.createDiv());
        getElement().appendChild(wrapper);
        setStyleName(CLASSNAME);
        wrapper.appendChild(indicator);
        indicator.setClassName(CLASSNAME + "-indicator");
        wrapper.setClassName(CLASSNAME + "-wrapper");
        poller = new Poller();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (pollerSuspendedDueDetach) {
            poller.scheduleRepeating(interval);
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (interval > 0) {
            poller.cancel();
            pollerSuspendedDueDetach = true;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            poller.cancel();
        }
    }

    class Poller extends Timer {

        @Override
        public void run() {
            if (!client.hasActiveRequest()
                    && Util.isAttachedAndDisplayed(VProgressIndicator.this)) {
                client.sendPendingVariableChanges();
            }
        }

    }
}
