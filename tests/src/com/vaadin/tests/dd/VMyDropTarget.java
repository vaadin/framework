package com.vaadin.tests.dd;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ValueMap;
import com.vaadin.terminal.gwt.client.ui.dd.AcceptCallback;
import com.vaadin.terminal.gwt.client.ui.dd.DragAndDropManager;
import com.vaadin.terminal.gwt.client.ui.dd.DragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.DropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.HasDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.DragAndDropManager.DragEventType;

public class VMyDropTarget extends Composite implements HasDropHandler,
        DropHandler, Paintable {

    private ApplicationConnection client;

    public void dragEnter(DragEvent drag) {
        DragAndDropManager.get().visitServer(DragEventType.ENTER,
                new AcceptCallback() {
                    public void handleResponse(ValueMap responseData) {
                        // show hints, error messages etc
                    }
                });
    }

    public void dragLeave(DragEvent drag) {
        // TODO Auto-generated method stub
    }

    public void dragOver(DragEvent currentDrag) {
        // TODO Auto-generated method stub
    }

    public boolean drop(DragEvent drag) {
        // TODO Auto-generated method stub
        // return true to tell DDManager do server visit
        return false;
    }

    public Paintable getPaintable() {
        // Drophandler implemented by Paintable itself
        return this;
    }

    public DropHandler getDropHandler() {
        // Drophandler implemented by Paintable itself
        return this;
    }

    public ApplicationConnection getApplicationConnection() {
        return client;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

    }

}
