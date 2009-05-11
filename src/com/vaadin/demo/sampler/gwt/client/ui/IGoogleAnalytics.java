package com.vaadin.demo.sampler.gwt.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class IGoogleAnalytics extends Widget implements Paintable {

    public IGoogleAnalytics() {
        setElement(Document.get().createDivElement());
        if (BrowserInfo.get().isIE6()) {
            getElement().getStyle().setProperty("overflow", "hidden");
            getElement().getStyle().setProperty("height", "0");
            getElement().getStyle().setProperty("width", "0");
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (isLocalHostUrl()) {
            // Do not track localhost page views
            return;
        }
        String trackerId = uidl.getStringAttribute("trackerid");
        String pageId = uidl.getStringAttribute("pageid");
        String domainName = uidl.getStringAttribute("domain");

        String res = trackPageview(trackerId, pageId, domainName);
        if (null != res) {
            ApplicationConnection.getConsole().log(
                    "WebAnalytics.trackPageview(" + trackerId + "," + pageId
                            + "," + domainName + ") FAILED: " + res);
        } else {
            ApplicationConnection.getConsole().log(
                    "WebAnalytics.trackPageview(" + trackerId + "," + pageId
                            + "," + domainName + ") SUCCESS.");
        }
    }

    private native boolean isLocalHostUrl()
    /*-{
        var location = $wnd.location;
        var re = /^http:\/\/(localhost|127.0.0.1)/;
        return re.test(location);
    }-*/;

    private native String trackPageview(String trackerId, String pageId,
            String domainName)
    /*-{
        if (!$wnd._gat) {
            return "Tracker not found (running offline?)";
        }
        try {
            var pageTracker = $wnd._gat._getTracker(trackerId);
            if (domainName) {
                pageTracker._setDomainName(domainName);
            }
            if (pageId) {
                pageTracker._trackPageview(pageId);
            } else {
                pageTracker._trackPageview();
            }
            return null;
        } catch(err) {
            return ""+err;
        }
    }-*/;
}
