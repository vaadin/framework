package com.vaadin.client.extensions;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;

@Connect(DragSourceExtension.class)
public class DragSourceExtensionConnector extends AbstractExtensionConnector {

    private static final String CLASS_DRAGGABLE = "v-draggable";

    @Override
    protected void extend(ServerConnector target) {
        Widget widget = ((ComponentConnector) target).getWidget();

        widget.getElement().setDraggable(Element.DRAGGABLE_TRUE);
        widget.getElement().addClassName(CLASS_DRAGGABLE);

        widget.sinkBitlessEvent(BrowserEvents.DRAGSTART);
        widget.addHandler(event -> {
            // TODO add actions here
        }, DragStartEvent.getType());
    }

    @Override
    public DragSourceState getState() {
        return (DragSourceState) super.getState();
    }
}
