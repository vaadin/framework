package com.vaadin.tests.minitutorials.v7a3;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.AbstractExtension;
import com.vaadin.tests.widgetset.client.minitutorials.v7a3.RefresherRpc;
import com.vaadin.tests.widgetset.client.minitutorials.v7a3.RefresherState;
import com.vaadin.ui.UI;

public class Refresher extends AbstractExtension {
    public interface RefreshListener {
        public void refresh(Refresher source);
    }

    private List<RefreshListener> listeners = new ArrayList<RefreshListener>();

    public Refresher() {
        registerRpc(new RefresherRpc() {
            @Override
            public void refresh() {
                for (RefreshListener listener : listeners) {
                    listener.refresh(Refresher.this);
                }
            }
        });
    }

    @Override
    public RefresherState getState() {
        return (RefresherState) super.getState();
    }

    public void setInterval(int millis) {
        getState().interval = millis;
    }

    public int getInterval() {
        return getState().interval;
    }

    public void setEnabled(boolean enabled) {
        getState().enabled = enabled;
    }

    public boolean isEnabled() {
        return getState().enabled;
    }

    public void addListener(RefreshListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RefreshListener listener) {
        listeners.remove(listener);
    }

    public void extend(UI target) {
        super.extend(target);
    }
}