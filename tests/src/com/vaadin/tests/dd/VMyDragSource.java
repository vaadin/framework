package com.vaadin.tests.dd;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager;
import com.vaadin.terminal.gwt.client.ui.dd.VTransferable;

/**
 * Example code to implement Component that has something to drag.
 */
public class VMyDragSource extends Composite implements Paintable,
        MouseDownHandler, MouseMoveHandler, MouseOutHandler {

    private boolean mouseDown;
    private MouseDownEvent mDownEvent;
    @SuppressWarnings("unused")
    private ApplicationConnection client;

    public VMyDragSource() {
        FlowPanel fp = new FlowPanel();
        initWidget(fp);

        HTML html = new HTML("DragThis");

        fp.add(html);

        html.addMouseDownHandler(this);
        html.addMouseMoveHandler(this);
        html.addMouseOutHandler(this);

    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        this.client = client;
    }

    /*
     * Below a sophisticated drag start implementation. Drag event is started if
     * mouse is moved 5 pixels with left mouse key down.
     */

    public void onMouseDown(MouseDownEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            mouseDown = true;
            mDownEvent = event;
        }
    }

    public void onMouseMove(MouseMoveEvent event) {
        if (mouseDown) {
            int deltaX = Math.abs(mDownEvent.getClientX() - event.getClientX());
            int deltaY = Math.abs(mDownEvent.getClientY() - event.getClientY());
            if (deltaX > 5 || deltaY > 5) {
                // Start the drag and drop operation

                // create Transferable, that contains the payload
                VTransferable transferable = new VTransferable();
                transferable.setData("Text", "myPayload");

                // Tell DragAndDropManager to start a drag and drop operation.
                // Also let it handle all events (last parameter true). Could
                // also do all event handling here too.
                VDragAndDropManager.get().startDrag(transferable,
                        mDownEvent.getNativeEvent(), true);

                mouseDown = false;
                mDownEvent = null;
            }
        }

    }

    public void onMouseOut(MouseOutEvent event) {
        mouseDown = false;
        mDownEvent = null;
    }

}
