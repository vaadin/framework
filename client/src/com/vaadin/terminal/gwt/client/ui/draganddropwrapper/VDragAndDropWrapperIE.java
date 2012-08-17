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

package com.vaadin.terminal.gwt.client.ui.draganddropwrapper;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.VConsole;

public class VDragAndDropWrapperIE extends VDragAndDropWrapper {
    private AnchorElement anchor = null;

    @Override
    protected Element getDragStartElement() {
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
            return (Element) anchor.cast();
        } else {
            if (anchor != null) {
                div.removeChild(anchor);
                anchor = null;
            }
            return div;
        }
    }

    @Override
    protected native void hookHtml5DragStart(Element el)
    /*-{
        var me = this;

        el.attachEvent("ondragstart",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.draganddropwrapper.VDragAndDropWrapper::html5DragStart(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));
    }-*/;

    @Override
    protected native void hookHtml5Events(Element el)
    /*-{
        var me = this;

        el.attachEvent("ondragenter",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.draganddropwrapper.VDragAndDropWrapper::html5DragEnter(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondragleave",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.draganddropwrapper.VDragAndDropWrapper::html5DragLeave(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondragover",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.draganddropwrapper.VDragAndDropWrapper::html5DragOver(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondrop",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.draganddropwrapper.VDragAndDropWrapper::html5DragDrop(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));
    }-*/;

}
