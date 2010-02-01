package com.vaadin.terminal.gwt.client.ui;

import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VHtml5DragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VTransferable;

public class VDragDropPane extends VAbsoluteLayout implements Container,
        VHasDropHandler {

    private String paintableId;

    /**
     * DragEvent is stored here in case of HTML5 drag event.
     */
    private VDragEvent vaadinDragEvent;

    public VDragDropPane() {
        super();
        addDomHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                EventTarget eventTarget = event.getNativeEvent()
                        .getEventTarget();
                Paintable paintable = client.getPaintable((Element) eventTarget
                        .cast());
                VTransferable transferable = new VTransferable();
                transferable.setComponent(paintable);
                VDragEvent drag = VDragAndDropManager.get().startDrag(
                        transferable, event.getNativeEvent(), true);
                Element cloneNode = (Element) ((Widget) paintable).getElement()
                        .cloneNode(true);
                cloneNode.getStyle().setBackgroundColor("#999");
                cloneNode.getStyle().setOpacity(0.4);
                drag.setDragImage(cloneNode);
                drag.getEventDetails().put(
                        "mouseDown",
                        new MouseEventDetails(event.getNativeEvent())
                                .serialize());
                event.preventDefault(); // prevent text selection
            }
        }, MouseDownEvent.getType());

        hookHtml5Events(getElement());
        getStyleElement().getStyle().setBackgroundColor("yellow");

    }

    /**
     * Prototype code, memory leak risk.
     * 
     * @param el
     */
    private native void hookHtml5Events(Element el)
    /*-{

        var me = this;
         
        el.addEventListener("dragenter",  function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragDropPane::html5DragEnter(Lcom/vaadin/terminal/gwt/client/ui/dd/Html5DragEvent;)(ev);
        }, false);
        
        el.addEventListener("dragleave",  function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragDropPane::html5DragLeave(Lcom/vaadin/terminal/gwt/client/ui/dd/Html5DragEvent;)(ev);
        }, false);

        el.addEventListener("dragover",  function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragDropPane::html5DragOver(Lcom/vaadin/terminal/gwt/client/ui/dd/Html5DragEvent;)(ev);
        }, false);

        el.addEventListener("drop",  function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragDropPane::html5DragDrop(Lcom/vaadin/terminal/gwt/client/ui/dd/Html5DragEvent;)(ev);
        }, false);
        
    }-*/;

    public boolean html5DragEnter(VHtml5DragEvent event) {
        ApplicationConnection.getConsole().log("HTML 5 Drag Enter");
        VTransferable transferable = new VTransferable();

        // TODO refine api somehow so that we will now not use the event preview
        // method provided by manager
        vaadinDragEvent = VDragAndDropManager.get().startDrag(transferable,
                event, false);
        event.preventDefault();
        event.stopPropagation();
        return false;
    }

    public boolean html5DragLeave(VHtml5DragEvent event) {
        ApplicationConnection.getConsole().log("HTML 5 Drag Leave posponed...");
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                // Yes, dragleave happens before drop. Makes no sense to me.
                // IMO shouldn't fire leave at all if drop happens (I guess this
                // is what IE does).
                // In Vaadin we fire it only if drop did not happen.
                if (vaadinDragEvent != null) {
                    ApplicationConnection.getConsole().log(
                            "...HTML 5 Drag Leave");
                    getDropHandler().dragLeave(vaadinDragEvent);
                }
            }
        });
        event.preventDefault();
        event.stopPropagation();
        return false;
    }

    public boolean html5DragOver(VHtml5DragEvent event) {
        ApplicationConnection.getConsole().log("HTML 5 Drag Over");
        getDropHandler().dragOver(vaadinDragEvent);
        // needed to be set for Safari, otherwise drop will not happen
        String s = event.getEffectAllowed();
        if ("all".equals(s) || s.contains("opy")) {
            event.setDragEffect("copy");
        } else {
            event.setDragEffect(s);
            ApplicationConnection.getConsole().log("Drag effect set to " + s);
        }
        event.preventDefault();
        event.stopPropagation();
        return false;
    }

    public boolean html5DragDrop(VHtml5DragEvent event) {
        ApplicationConnection.getConsole().log("HTML 5 Drag Drop");
        VTransferable transferable = vaadinDragEvent.getTransferrable();

        JsArrayString types = event.getTypes();
        for (int i = 0; i < types.length(); i++) {
            String type = types.get(i);
            ApplicationConnection.getConsole().log("Type: " + type);
            if ("text/plain".equals(type)) {
                String data = event.getDataAsText(type);
                ApplicationConnection.getConsole().log(type + " : " + data);
                transferable.setData("text/plain", data);
            }
        }

        String fileAsString = event.getFileAsString(0);
        if (fileAsString != null) {
            ApplicationConnection.getConsole().log(fileAsString);
            transferable.setData("fileContents", fileAsString);
        }

        VDragAndDropManager.get().endDrag();
        vaadinDragEvent = null;
        event.preventDefault();
        event.stopPropagation();

        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!uidl.hasAttribute("cached")) {
            int childCount = uidl.getChildCount();
            UIDL childUIDL = uidl.getChildUIDL(childCount - 1);
            getDropHandler().updateRules(childUIDL);
        }
    }

    private VAbstractDropHandler dropHandler;

    public VAbstractDropHandler getDropHandler() {
        if (dropHandler == null) {
            dropHandler = new VAbstractDropHandler() {

                @Override
                public Paintable getPaintable() {
                    return VDragDropPane.this;
                }

                @Override
                public void dragLeave(VDragEvent drag) {
                    ApplicationConnection.getConsole().log("DragLeave");
                    getStyleElement().getStyle().setBackgroundColor("yellow");
                }

                @Override
                public boolean drop(VDragEvent drag) {
                    ApplicationConnection.getConsole().log(
                            "Drop" + drag.sinceStart());

                    if (getStyleElement().getStyle().getBackgroundColor()
                            .equals("yellow")) {
                        // not accepted
                        ApplicationConnection.getConsole().log(
                                "Drop was not accepted");
                        return false;
                    }

                    Map<String, Object> transferable = drag.getEventDetails();

                    // this is absolute layout based, and we may want to set
                    // component
                    // relatively to where the drag ended.
                    // need to add current location of the drop area

                    int absoluteLeft = getAbsoluteLeft();
                    int absoluteTop = getAbsoluteTop();

                    transferable.put("absoluteLeft", absoluteLeft);
                    transferable.put("absoluteTop", absoluteTop);

                    getStyleElement().getStyle().setBackgroundColor("yellow");
                    return super.drop(drag);
                }

                @Override
                protected void dragAccepted(VDragEvent drag) {
                    getStyleElement().getStyle().setBackgroundColor("green");
                }

                public ApplicationConnection getApplicationConnection() {
                    return client;
                }
            };
        }
        return dropHandler;
    }

    public Paintable getPaintable() {
        return this;
    }

}
