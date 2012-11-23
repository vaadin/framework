package com.vaadin.tests.minitutorials.v7b9;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@PreserveOnRefresh
public class SomeUI extends UI {

    @Override
    public void init(VaadinRequest request) {

        setContent(new Label("Hello"));

        Page page = getPage();
        page.addBrowserWindowResizeListener(new BrowserWindowResizeListener() {
            public void browserWindowResized(BrowserWindowResizeEvent event) {
                Notification.show("Window width=" + event.getWidth()
                        + ", height=" + event.getHeight());
            }
        });

        page.setUriFragment(page.getUriFragment() + "foo");
        page.addUriFragmentChangedListener(new UriFragmentChangedListener() {
            public void uriFragmentChanged(UriFragmentChangedEvent event) {
                Notification.show("Fragment=" + event.getUriFragment());
            }
        });
    }
}
