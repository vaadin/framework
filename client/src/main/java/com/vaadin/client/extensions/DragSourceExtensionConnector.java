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

import java.util.Map;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DataTransfer;
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

            // Set effectAllowed parameter
            setEffectAllowed(event.getDataTransfer(), getState().effectAllowed);

            // Set data parameter
            Map<String, String> data = getState().data;
            for (String format : data.keySet()) {
                event.setData(format, data.get(format));
            }

            // TODO: 16/01/2017 Add source object to dataTransfer
        }, DragStartEvent.getType());
    }

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
