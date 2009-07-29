package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
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
public class VUriFragmentUtility extends Widget implements Paintable,
        ValueChangeHandler<String> {

    private String fragment;
    private ApplicationConnection client;
    private String paintableId;
    private boolean immediate;

    public VUriFragmentUtility() {
        setElement(Document.get().createDivElement());
        if (BrowserInfo.get().isIE6()) {
            getElement().getStyle().setProperty("overflow", "hidden");
            getElement().getStyle().setProperty("height", "0");
        }
        History.addValueChangeHandler(this);
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

    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        fragment = historyToken;
        if (client != null) {
            client.updateVariable(paintableId, "fragment", fragment, immediate);
        }
    }

}
