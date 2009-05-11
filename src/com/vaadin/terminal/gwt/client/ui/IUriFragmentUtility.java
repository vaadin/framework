package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Client side implementation for UriFragmentUtility. Uses GWT's History object
 * as an implementation.
 * 
 */
public class IUriFragmentUtility extends Widget implements Paintable,
        HistoryListener {

    private String fragment;
    private ApplicationConnection client;
    private String paintableId;
    private boolean immediate;

    public IUriFragmentUtility() {
        setElement(Document.get().createDivElement());
        if (BrowserInfo.get().isIE6()) {
            getElement().getStyle().setProperty("overflow", "hidden");
            getElement().getStyle().setProperty("height", "0");
        }
        History.addHistoryListener(this);
        History.fireCurrentHistoryState();
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        String uidlFragment = uidl.getStringVariable("fragment");
        immediate = uidl.getBooleanAttribute("immediate");
        if (this.client == null) {
            // initial paint has some special logic
            this.client = client;
            paintableId = uidl.getId();
            if (!fragment.equals(uidlFragment)) {
                // initial server side fragment (from link/bookmark/typed) does
                // not equal the one on
                // server, send initial fragment to server
                History.fireCurrentHistoryState();
            }
        } else {
            if (uidlFragment != null && !uidlFragment.equals(fragment)) {
                fragment = uidlFragment;
                // normal fragment change from server, add new history item
                History.newItem(uidlFragment, false);
            }
        }
    }

    public void onHistoryChanged(String historyToken) {
        fragment = historyToken;
        if (client != null) {
            client.updateVariable(paintableId, "fragment", fragment, immediate);
        }
    }

}
