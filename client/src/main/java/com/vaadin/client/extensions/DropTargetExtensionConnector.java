package com.vaadin.client.extensions;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DropTargetState;

@Connect(DropTargetExtension.class)
public class DropTargetExtensionConnector extends AbstractExtensionConnector {

    private static final String CLASS_DRAG_OVER = "v-drag-over";

    @Override
    protected void extend(ServerConnector target) {
        Widget widget = ((ComponentConnector) target).getWidget();

        // dragenter event
        widget.sinkBitlessEvent(BrowserEvents.DRAGENTER);
        widget.addHandler(event -> {
            widget.getElement().addClassName(CLASS_DRAG_OVER);
        }, DragEnterEvent.getType());

        // dragover event
        widget.sinkBitlessEvent(BrowserEvents.DRAGOVER);
        widget.addHandler(event -> {

            // Set dropEffect parameter
            if (getState().dropEffect != null) {
                event.getDataTransfer().setDropEffect(
                        DataTransfer.DropEffect.valueOf(getState().dropEffect));
            }

            // Prevent default to allow drop
            event.preventDefault();
        }, DragOverEvent.getType());

        // dragleave event
        widget.sinkBitlessEvent(BrowserEvents.DRAGLEAVE);
        widget.addHandler(event -> {
            widget.getElement().removeClassName(CLASS_DRAG_OVER);
        }, DragLeaveEvent.getType());

        // drop event
        widget.sinkBitlessEvent(BrowserEvents.DROP);
        widget.addHandler(event -> {
            event.preventDefault();
        }, DropEvent.getType());
    }

    @Override
    public DropTargetState getState() {
        return (DropTargetState) super.getState();
    }
}
