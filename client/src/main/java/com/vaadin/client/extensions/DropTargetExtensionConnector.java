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

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
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
            onDragEnter(event, widget.getElement());
        }, DragEnterEvent.getType());

        // dragover event
        widget.sinkBitlessEvent(BrowserEvents.DRAGOVER);
        widget.addHandler(event -> {
            onDragOver(event, widget.getElement());
        }, DragOverEvent.getType());

        // dragleave event
        widget.sinkBitlessEvent(BrowserEvents.DRAGLEAVE);
        widget.addHandler(event -> {
            onDragLeave(event, widget.getElement());
        }, DragLeaveEvent.getType());

        // drop event
        widget.sinkBitlessEvent(BrowserEvents.DROP);
        widget.addHandler(this::onDrop, DropEvent.getType());
    }

    protected void onDragEnter(DragEnterEvent event, Element draggableElement) {
        addTargetIndicator(draggableElement);
    }

    protected void onDragOver(DragOverEvent event, Element draggableElement) {
        if (dragOverAllowed(event)) {
            // Set dropEffect parameter
            if (getState().dropEffect != null) {
                event.getDataTransfer().setDropEffect(
                        DataTransfer.DropEffect.valueOf(getState().dropEffect));
            }

            // Prevent default to allow drop
            event.preventDefault();
        } else {
            // Remove drop effect
            event.getDataTransfer().setDropEffect(DataTransfer.DropEffect.NONE);

            // Remove drop target indicator
            removeTargetIndicator(draggableElement);
        }
    }

    private boolean dragOverAllowed(DragOverEvent event) {
        if (getState().dragOverCriteria != null) {
            return executeScript(event.getNativeEvent(),
                    getState().dragOverCriteria);
        }

        // Allow when criteria not set
        return true;
    }

    protected void onDragLeave(DragLeaveEvent event, Element draggableElement) {
        removeTargetIndicator(draggableElement);
    }

    protected void onDrop(DropEvent event) {
        if (dropAllowed(event)) {
            event.preventDefault();

            // Initiate firing server side drop event
            JsArrayString types = getTypes(event.getDataTransfer());
            Map<String, String> data = new LinkedHashMap<>();
            for (int i = 0; i < types.length(); i++) {
                data.put(types.get(i), event.getData(types.get(i)));
            }

            getRpcProxy(DropTargetRpc.class).drop(data, getState().dropEffect);
        }
    }

    private boolean dropAllowed(DropEvent event) {
        if (getState().dropCriteria != null) {
            return executeScript(event.getNativeEvent(), getState().dropCriteria);
        }

        // Allow when criteria not set
        return true;
    }

    private void addTargetIndicator(Element element) {
        element.addClassName(CLASS_DRAG_OVER);
    }

    private void removeTargetIndicator(Element element) {
        element.removeClassName(CLASS_DRAG_OVER);
    }

    private native boolean executeScript(NativeEvent event,
            String script)/*-{
        return new Function('event', script)(event);
    }-*/;

    private native JsArrayString getTypes(DataTransfer dataTransfer)/*-{
        return dataTransfer.types;
    }-*/;

    @Override
    public DropTargetState getState() {
        return (DropTargetState) super.getState();
    }
}
