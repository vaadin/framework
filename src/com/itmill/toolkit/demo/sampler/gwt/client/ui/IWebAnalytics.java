package com.itmill.toolkit.demo.sampler.gwt.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IWebAnalytics extends Widget implements Paintable {

    public IWebAnalytics() {
        setElement(Document.get().createDivElement());
        if (BrowserInfo.get().isIE6()) {
            getElement().getStyle().setProperty("overflow", "hidden");
            getElement().getStyle().setProperty("height", "0");
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
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

    private native String trackPageview(String trackerId, String pageId,
            String domainName)
    /*-{
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
