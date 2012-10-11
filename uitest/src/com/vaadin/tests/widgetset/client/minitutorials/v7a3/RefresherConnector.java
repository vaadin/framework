package com.vaadin.tests.widgetset.client.minitutorials.v7a3;

import com.google.gwt.user.client.Timer;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7a3.Refresher;

@Connect(Refresher.class)
public class RefresherConnector extends AbstractExtensionConnector {

    private RefresherRpc rpc = RpcProxy.create(RefresherRpc.class, this);

    private Timer timer = new Timer() {
        @Override
        public void run() {
            rpc.refresh();
        }
    };

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);
        timer.cancel();
        if (isEnabled()) {
            timer.scheduleRepeating(getState().interval);
        }
    }

    @Override
    public void onUnregister() {
        timer.cancel();
    }

    @Override
    public RefresherState getState() {
        return (RefresherState) super.getState();
    }
}