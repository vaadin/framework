/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Util;

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

    private NativeEvent currentGwtEvent;

    private NativeEvent startEvent;

    private int id;

    private HashMap<String, Object> dropDetails = new HashMap<String, Object>();

    private Element elementOver;

    VDragEvent(VTransferable t, NativeEvent startEvent) {
        transferable = t;
        this.startEvent = startEvent;
        id = eventId++;
    }

    public VTransferable getTransferable() {
        return transferable;
    }

    /**
     * Returns the the latest {@link NativeEvent} that relates to this drag and
     * drop operation. For example on {@link VDropHandler#dragEnter(VDragEvent)}
     * this is commonly a {@link MouseOverEvent}.
     * 
     * @return
     */
    public NativeEvent getCurrentGwtEvent() {
        return currentGwtEvent;
    }

    public void setCurrentGwtEvent(NativeEvent event) {
        currentGwtEvent = event;
    }

    int getEventId() {
        return id;
    }

    /**
     * Detecting the element on which the the event is happening may be
     * problematic during drag and drop operation. This is especially the case
     * if a drag image (often called also drag proxy) is kept under the mouse
     * cursor (see {@link #createDragImage(Element, boolean)}. Drag and drop
     * event handlers (like the one provided by {@link VDragAndDropManager} )
     * should set elmentOver field to reflect the the actual element on which
     * the pointer currently is (drag image excluded). {@link VDropHandler}s can
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
     * Sets the drag image used for current drag and drop operation. Drag image
     * is displayed next to mouse cursor during drag and drop.
     * <p>
     * The element to be used as drag image will automatically get CSS style
     * name "v-drag-element".
     * 
     * TODO decide if this method should be here or in {@link VTransferable} (in
     * HTML5 it is in DataTransfer) or {@link VDragAndDropManager}
     * 
     * TODO should be possible to override behavior. Like to proxy the element
     * to HTML5 DataTransfer
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
    public Map<String, Object> getDropDetails() {
        return dropDetails;
    }

    /**
     * Sets the drag image used for current drag and drop operation. Drag image
     * is displayed next to mouse cursor during drag and drop.
     * <p>
     * The element to be used as drag image will automatically get CSS style
     * name "v-drag-element".
     * 
     * @param element
     *            the dom element to be positioned next to mouse cursor
     * @param offsetX
     *            the horizontal offset of drag image from mouse cursor
     * @param offsetY
     *            the vertical offset of drag image from mouse cursor
     */
    public void setDragImage(Element element, int offsetX, int offsetY) {
        element.getStyle().setMarginLeft(offsetX, Unit.PX);
        element.getStyle().setMarginTop(offsetY, Unit.PX);
        VDragAndDropManager.get().setDragElement(element);
    }

    /**
     * @return the current Element used as a drag image (aka drag proxy) or null
     *         if drag image is not currently set for this drag operation.
     */
    public Element getDragImage() {
        return (Element) VDragAndDropManager.get().getDragElement();
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
        if (BrowserInfo.get().isIE()) {
            if (cloneNode.getTagName().toLowerCase().equals("tr")) {
                TableElement table = Document.get().createTableElement();
                TableSectionElement tbody = Document.get().createTBodyElement();
                table.appendChild(tbody);
                tbody.appendChild(cloneNode);
                cloneNode = table.cast();
            }
        }
        if (alignImageToEvent) {
            int absoluteTop = element.getAbsoluteTop();
            int absoluteLeft = element.getAbsoluteLeft();
            int clientX = Util.getTouchOrMouseClientX(startEvent);
            int clientY = Util.getTouchOrMouseClientY(startEvent);
            int offsetX = absoluteLeft - clientX;
            int offsetY = absoluteTop - clientY;
            setDragImage(cloneNode, offsetX, offsetY);
        } else {
            setDragImage(cloneNode);
        }

    }

}
