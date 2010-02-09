package com.vaadin.tests.dd;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCallback;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler;

public class VMyDropTarget extends Composite implements VHasDropHandler,
        VDropHandler, Paintable {

    private ApplicationConnection client;

    public void dragEnter(VDragEvent drag) {
        VDragAndDropManager.get().visitServer(new VAcceptCallback() {
            public void accepted() {
                // show drag hints here
            }
        });
    }

    public void dragLeave(VDragEvent drag) {
        // TODO Auto-generated method stub
    }

    public void dragOver(VDragEvent currentDrag) {
        // TODO Auto-generated method stub
    }

    public boolean drop(VDragEvent drag) {
        // TODO Auto-generated method stub
        // return true to tell DDManager do server visit
        return false;
    }

    public Paintable getPaintable() {
        // Drophandler implemented by Paintable itself
        return this;
    }

    public VDropHandler getDropHandler() {
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
