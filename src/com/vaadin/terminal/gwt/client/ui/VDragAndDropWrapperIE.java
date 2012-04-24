/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

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
            return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragStart(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));
    }-*/;

    @Override
    protected native void hookHtml5Events(Element el)
    /*-{
        var me = this;

        el.attachEvent("ondragenter",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragEnter(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondragleave",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragLeave(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondragover",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragOver(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));

        el.attachEvent("ondrop",  $entry(function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragDrop(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }));
    }-*/;

}
