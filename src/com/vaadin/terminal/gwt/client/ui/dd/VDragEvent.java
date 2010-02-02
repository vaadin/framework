package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.BrowserInfo;

/**
 * DragEvent used by Vaadin client side engine. Supports components, items,
 * properties and custom payload (HTML5 style).
 * 
 * 
 */
public class VDragEvent {

    private static final int DEFAULT_OFFSET = 10;

    private static int eventId = 0;

    private VTransferable transferable;

    NativeEvent currentGwtEvent;

    private NativeEvent startEvent;

    private int id;

    private Date start;

    private HashMap<String, Object> dropDetails = new HashMap<String, Object>();

    private Element elementOver;

    VDragEvent(VTransferable t, NativeEvent startEvent) {
        transferable = t;
        this.startEvent = startEvent;
        id = eventId++;
        start = new Date();
    }

    public VTransferable getTransferrable() {
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
     * Detecting the element on which the the event is happening may be
     * problematic during drag and drop operation. This is especially the case
     * if a drag image (often called drag proxy) is kept under the mouse cursor
     * (see {@link #createDragImage(Element, boolean)}. Drag and drop event
     * handlers (like the one provided by {@link VDragAndDropManager} ) should
     * set elmentOver field to reflect the the actual element on which the
     * pointer currently is (drag image excluded). {@link VDropHandler}s can
     * then more easily react properly on drag events by reading the element via
     * this method.
     * 
     * @return the element in {@link VDropHandler} on which mouse cursor is on
     */
    public Element getElementOver() {
        if (elementOver != null) {
            return elementOver;
        } else if (currentGwtEvent != null) {
            return currentGwtEvent.getEventTarget().cast();
        }
        return null;
    }

    public void setElementOver(Element targetElement) {
        elementOver = targetElement;
    }

    /**
     * Sets the element that will be used as "drag icon".
     * 
     * TODO decide if this method should be here or in {@link VTransferable} (in
     * HTML5 it is in DataTransfer) or {@link VDragAndDropManager}
     * 
     * TODO should be possible to override behaviour an set to HTML5
     * DataTransfer
     * 
     * @param node
     */
    public void setDragImage(Element node) {
        setDragImage(node, DEFAULT_OFFSET, DEFAULT_OFFSET);
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

    public void setDragImage(Element cloneNode, int offsetX, int offsetY) {
        cloneNode.getStyle().setMarginLeft(offsetX, Unit.PX);
        cloneNode.getStyle().setMarginTop(offsetY, Unit.PX);
        VDragAndDropManager.get().setDragElement(cloneNode);

    }

    /**
     * Automatically tries to create a proxy image from given element.
     * 
     * @param element
     * @param alignImageToEvent
     *            if true, proxy image is aligned to start event, else next to
     *            mouse cursor
     */
    public void createDragImage(Element element, boolean alignImageToEvent) {
        Element cloneNode = (Element) element.cloneNode(true);
        cloneNode.getStyle().setOpacity(0.4);
        if (BrowserInfo.get().isIE()) {
            cloneNode.getStyle().setProperty("filter", "alpha(opacity=70)");
        }
        if (alignImageToEvent) {
            int absoluteTop = element.getAbsoluteTop();
            int absoluteLeft = element.getAbsoluteLeft();
            int clientX = startEvent.getClientX();
            int clientY = startEvent.getClientY();
            int offsetX = absoluteLeft - clientX;
            int offsetY = absoluteTop - clientY;
            setDragImage(cloneNode, offsetX, offsetY);
        } else {
            setDragImage(cloneNode);
        }

    }

}
