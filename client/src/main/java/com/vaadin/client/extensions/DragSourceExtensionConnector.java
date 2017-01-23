/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.extensions;

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;

/**
 * Extension to add drag source functionality to a widget for using HTML5 drag
 * and drop. Client side counterpart of {@link DragSourceExtension}.
 */
@Connect(DragSourceExtension.class)
public class DragSourceExtensionConnector extends AbstractExtensionConnector {

    private static final String CLASS_DRAGGABLE = "v-draggable";

    @Override
    protected void extend(ServerConnector target) {
        Element dragSourceElement = getDraggableElement();

        dragSourceElement.setDraggable(Element.DRAGGABLE_TRUE);
        dragSourceElement.addClassName(CLASS_DRAGGABLE);

        addEventListener(dragSourceElement, BrowserEvents.DRAGSTART,
                this::onDragStart);

        // TODO: 23/01/2017 Consider removing event listener on detach
    }

    /**
     * Event handler for the {@code dragstart} event. Called when {@code
     * dragstart} event occurs.
     *
     * @param event
     *         browser event to be handled
     */
    protected void onDragStart(Event event) {
        // Set effectAllowed parameter
        setEffectAllowed(event.getDataTransfer(), getState().effectAllowed);

        // Set data parameter
        List<String> types = getState().types;
        Map<String, String> data = getState().data;
        for (String format : types) {
            event.getDataTransfer().setData(format, data.get(format));
        }
    }

    /**
     * Finds the draggable element within the widget. By default, returns the
     * topmost element.
     *
     * @return the draggable element in the parent widget.
     */
    protected Element getDraggableElement() {
        return ((ComponentConnector) getParent()).getWidget().getElement();
    }

    private native void addEventListener(Element element, String eventName,
            EventListener listener)/*-{
        var listenerFunction = function (event) {
            listener.@com.google.gwt.user.client.EventListener::onBrowserEvent(*)(event);
        }

        element.addEventListener(eventName, listenerFunction, false);
    }-*/;

    private native void setEffectAllowed(DataTransfer dataTransfer,
            String effectAllowed)/*-{
        if (effectAllowed) {
            dataTransfer.effectAllowed = effectAllowed;
        }
    }-*/;

    @Override
    public DragSourceState getState() {
        return (DragSourceState) super.getState();
    }
}
