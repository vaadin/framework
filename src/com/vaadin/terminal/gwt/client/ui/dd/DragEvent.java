package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;

/**
 * DragEvent used by Vaadin client side engine. Supports components, items,
 * properties and custom payload (HTML5 style).
 * 
 * 
 */
public class DragEvent {

    private static int eventId = 0;

    private Transferable transferable;

    NativeEvent currentGwtEvent;

    private NativeEvent startEvent;

    private int id;

    private Date start;

    private HashMap<String, Object> dropDetails = new HashMap<String, Object>();

    DragEvent(Transferable t, NativeEvent startEvent) {
        transferable = t;
        this.startEvent = startEvent;
        id = eventId++;
        start = new Date();
    }

    public Transferable getTransferrable() {
        return transferable;
    }

    public NativeEvent getCurrentGwtEvent() {
        return currentGwtEvent;
    }

    public int getEventId() {
        return id;
    }

    public long sinceStart() {
        return new Date().getTime() - start.getTime();
    }

    /**
     * Sets the element that will be used as "drag icon".
     * 
     * TODO decide if this method should be here or in {@link Transferable} (in
     * HTML5 it is in DataTransfer) or {@link DragAndDropManager}
     * 
     * TODO should be possible to override behaviour an set to HTML5
     * DataTransfer
     * 
     * @param node
     */
    public void setDragImage(Element node) {
        DragAndDropManager.get().setDragElement(node);
    }

    /**
     * TODO consider using similar smaller (than map) api as in Transferable
     * 
     * TODO clean up when drop handler changes
     * 
     * @return
     */
    public Map<String, Object> getEventDetails() {
        return dropDetails;
    }

}
