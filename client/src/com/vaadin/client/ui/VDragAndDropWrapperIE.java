/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.client.ui;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.VConsole;

public class VDragAndDropWrapperIE extends VDragAndDropWrapper {
    private AnchorElement anchor = null;

    @Override
    protected com.google.gwt.user.client.Element getDragStartElement() {
        VConsole.log("IE get drag start element...");
        Element div = getElement();
        if (dragStartMode == HTML5) {
            if (anchor == null) {
                anchor = Document.get().createAnchorElement();
                anchor.setHref("#");
                anchor.setClassName("drag-start");
                div.appendChild(anchor);
            }
            VConsole.log("IE get drag start element...");
            return anchor.cast();
        } else {
            if (anchor != null) {
                div.removeChild(anchor);
                anchor = null;
            }
            return DOM.asOld(div);
        }
    }

    @Deprecated
    @Override
    protected native void hookHtml5DragStart(
            com.google.gwt.user.client.Element el)
    /*-{
        var me = this;

        el.attachEvent("ondragstart",  $entry(function(ev) {
            return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragStart(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
        }));
    }-*/;

    @Override
    protected void hookHtml5DragStart(Element el) {
        hookHtml5DragStart(DOM.asOld(el));
    }

    @Deprecated
    @Override
    protected native void hookHtml5Events(com.google.gwt.user.client.Element el)
    /*-{
        var me = this;

        el.attachEvent("ondragenter",  $entry(function(ev) {
            return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragEnter(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondragleave",  $entry(function(ev) {
            return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragLeave(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondragover",  $entry(function(ev) {
            return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragOver(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondrop",  $entry(function(ev) {
            return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragDrop(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
        }));
    }-*/;

    @Override
    protected void hookHtml5Events(Element el) {
        hookHtml5Events(DOM.asOld(el));
    }

}
