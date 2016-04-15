package com.vaadin.tests.minitutorials.v7a3;

import java.lang.reflect.Method;
import java.util.EventObject;

import com.vaadin.server.AbstractExtension;
import com.vaadin.tests.widgetset.client.minitutorials.v7a3.RefresherRpc;
import com.vaadin.tests.widgetset.client.minitutorials.v7a3.RefresherState;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

public class Refresher extends AbstractExtension {
    public interface RefreshListener {
        static Method METHOD = ReflectTools.findMethod(RefreshListener.class,
                "refresh", RefreshEvent.class);

        public void refresh(RefreshEvent refreshEvent);
    }

    public class RefreshEvent extends EventObject {

        public RefreshEvent(Refresher refresher) {
            super(refresher);
        }

        public Refresher getRefresher() {
            return (Refresher) getSource();
        }

    }

    public Refresher(UI ui) {
        registerRpc(new RefresherRpc() {
            @Override
            public void refresh() {
                fireEvent(new RefreshEvent(Refresher.this));
            }
        });
        extend(ui);
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

    public void addRefreshListener(RefreshListener listener) {
        super.addListener(RefreshEvent.class, listener, RefreshListener.METHOD);
    }

    public void removeRefreshListener(RefreshListener listener) {
        super.removeListener(RefreshEvent.class, listener,
                RefreshListener.METHOD);
    }
}
