package com.vaadin.tests.dd;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler;

public class VMyDropTarget extends Composite implements VHasDropHandler,
        VDropHandler {

    ApplicationConnection client;

    public void dragEnter(VDragEvent drag) {
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

    public VDropHandler getDropHandler() {
        // Drophandler implemented by widget itself
        return this;
    }

    public ComponentConnector getPaintable() {
        // TODO Auto-generated method stub
        return null;
    }

    public ApplicationConnection getApplicationConnection() {
        return client;
    }

}
