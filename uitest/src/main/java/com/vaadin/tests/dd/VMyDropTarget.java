package com.vaadin.tests.dd;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.client.ui.dd.VDropHandler;
import com.vaadin.client.ui.dd.VHasDropHandler;

public class VMyDropTarget extends Composite implements VHasDropHandler,
        VDropHandler {

    ApplicationConnection client;

    @Override
    public void dragEnter(VDragEvent drag) {
    }

    @Override
    public void dragLeave(VDragEvent drag) {
        // TODO Auto-generated method stub
    }

    @Override
    public void dragOver(VDragEvent currentDrag) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean drop(VDragEvent drag) {
        // TODO Auto-generated method stub
        // return true to tell DDManager do server visit
        return false;
    }

    @Override
    public VDropHandler getDropHandler() {
        // Drophandler implemented by widget itself
        return this;
    }

    @Override
    public ComponentConnector getConnector() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ApplicationConnection getApplicationConnection() {
        return client;
    }

}
