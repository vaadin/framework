/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class IProgressIndicator extends Widget implements Paintable {

    private static final String CLASSNAME = "i-progressindicator";
    Element wrapper = DOM.createDiv();
    Element indicator = DOM.createDiv();
    private ApplicationConnection client;
    private final Poller poller;
    private boolean indeterminate = false;
    private boolean pollerSuspendedDueDetach;

    public IProgressIndicator() {
        setElement(DOM.createDiv());
        getElement().appendChild(wrapper);
        setStyleName(CLASSNAME);
        wrapper.appendChild(indicator);
        indicator.setClassName(CLASSNAME + "-indicator");
        wrapper.setClassName(CLASSNAME + "-wrapper");
        poller = new Poller();
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        poller.cancel();
        this.client = client;
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        indeterminate = uidl.getBooleanAttribute("indeterminate");

        if (indeterminate) {
            String basename = CLASSNAME + "-indeterminate";
            IProgressIndicator.setStyleName(getElement(), basename, true);
            IProgressIndicator.setStyleName(getElement(), basename
                    + "-disabled", uidl.getBooleanAttribute("disabled"));
        } else {
            try {
                final float f = Float.parseFloat(uidl
                        .getStringAttribute("state"));
                final int size = Math.round(100 * f);
                DOM.setStyleAttribute(indicator, "width", size + "%");
            } catch (final Exception e) {
            }
        }

        if (!uidl.getBooleanAttribute("disabled")) {
            poller.scheduleRepeating(uidl.getIntAttribute("pollinginterval"));
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (pollerSuspendedDueDetach) {
            poller.run();
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        poller.cancel();
        pollerSuspendedDueDetach = true;
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
            client.sendPendingVariableChanges();
        }

    }

}
