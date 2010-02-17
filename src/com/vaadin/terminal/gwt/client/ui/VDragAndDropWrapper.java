package com.vaadin.terminal.gwt.client.ui;

import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.dd.HorizontalDropLocation;
import com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCallback;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VHtml5DragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VTransferable;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;

/**
 * 
 * Must have features:
 * 
 * stylenames to root element depending on place on component
 * 
 * 
 * allow size to change on emphasis/deemphasis (behave well for vaadin layout
 * system)
 * 
 * html5 drops
 * 
 * drop details: locations + sizes in document hierarchy up to wrapper
 * 
 * 
 */
public class VDragAndDropWrapper extends VCustomComponent implements
        VHasDropHandler {

    private static final String CLASSNAME = "v-ddwrapper";

    public VDragAndDropWrapper() {
        super();
        hookHtml5Events(getElement());
        setStyleName(CLASSNAME);
        addDomHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                if (dragStarMode > 0) {
                    // TODO should support drag mode WRAPPER too, now works for
                    // COMPONENT
                    VTransferable transferable = new VTransferable();
                    transferable.setDragSource(VDragAndDropWrapper.this);
                    Paintable paintable = client.getPaintable((Element) event
                            .getNativeEvent().getEventTarget().cast());

                    transferable.setData("component", paintable);
                    VDragEvent startDrag = VDragAndDropManager.get().startDrag(
                            transferable, event.getNativeEvent(), true);
                    if (dragStarMode == WRAPPER) {
                        paintable = VDragAndDropWrapper.this;
                    }

                    startDrag.createDragImage(
                            ((Widget) paintable).getElement(), true);
                    event.preventDefault(); // prevent text selection

                }
            }
        }, MouseDownEvent.getType());
    }

    private ApplicationConnection client;
    private VAbstractDropHandler dropHandler;
    private VDragEvent vaadinDragEvent;

    private final static int NONE = 0;
    private final static int COMPONENT = 1;
    private final static int WRAPPER = 2;
    private int dragStarMode;

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        super.updateFromUIDL(uidl, client);
        if (!uidl.hasAttribute("cached") && !uidl.hasAttribute("hidden")) {
            int childCount = uidl.getChildCount();
            if (childCount > 1) {
                UIDL childUIDL = uidl.getChildUIDL(1);
                if (dropHandler == null) {
                    dropHandler = new CustomDropHandler();
                }
                dropHandler.updateAcceptRules(childUIDL);
            } else {
                dropHandler = null;
            }

            dragStarMode = uidl.getIntAttribute("dragStartMode");
        }
    }

    public boolean html5DragEnter(VHtml5DragEvent event) {
        if (dropHandler == null) {
            return true;
        }
        ApplicationConnection.getConsole().log("HTML 5 Drag Enter");
        VTransferable transferable = new VTransferable();

        vaadinDragEvent = VDragAndDropManager.get().startDrag(transferable,
                event, false);
        event.preventDefault();
        event.stopPropagation();
        return false;
    }

    public boolean html5DragLeave(VHtml5DragEvent event) {
        if (dropHandler == null) {
            return true;
        }

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
        if (dropHandler == null) {
            return true;
        }

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
        if (dropHandler == null) {
            return true;
        }

        ApplicationConnection.getConsole().log("HTML 5 Drag Drop");
        VTransferable transferable = vaadinDragEvent.getTransferable();

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

    public VDropHandler getDropHandler() {
        return dropHandler;
    }

    private class CustomDropHandler extends VAbstractDropHandler {

        private static final String OVER_STYLE = "v-ddwrapper-over";
        private VerticalDropLocation verticalDropLocation;
        private HorizontalDropLocation horizontalDropLocation;

        @Override
        public void dragEnter(VDragEvent drag) {
            ApplicationConnection.getConsole().log("DDWrapper DragEnter");
            super.dragEnter(drag);
        }

        @Override
        public void dragLeave(VDragEvent drag) {
            ApplicationConnection.getConsole().log("DDWrapper DragLeave");
            deEmphasis();
        }

        @Override
        public void dragOver(final VDragEvent drag) {
            validate(new VAcceptCallback() {
                public void accepted(VDragEvent event) {
                    dragAccepted(drag);
                }
            }, drag);
        }

        @Override
        public boolean drop(VDragEvent drag) {
            ApplicationConnection.getConsole().log("Drop" + drag.sinceStart());
            deEmphasis();

            Map<String, Object> dd = drag.getDropDetails();

            // this is absolute layout based, and we may want to set
            // component
            // relatively to where the drag ended.
            // need to add current location of the drop area

            int absoluteLeft = getAbsoluteLeft();
            int absoluteTop = getAbsoluteTop();

            dd.put("absoluteLeft", absoluteLeft);
            dd.put("absoluteTop", absoluteTop);

            dd.put("verticalLocation", verticalDropLocation.toString());
            dd.put("horizontalLocation", horizontalDropLocation.toString());

            return super.drop(drag);
        }

        private void deEmphasis() {
            if (verticalDropLocation != null) {
                VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE,
                        false);
                VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                        + verticalDropLocation.toString().toLowerCase(), false);
                VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                        + horizontalDropLocation.toString().toLowerCase(),
                        false);
            }
        }

        @Override
        protected void dragAccepted(VDragEvent drag) {
            emphasis(drag);
        }

        private void emphasis(VDragEvent drag) {
            deEmphasis();
            verticalDropLocation = VerticalDropLocation.get(getElement(), drag
                    .getCurrentGwtEvent().getClientY(), 0.2);
            horizontalDropLocation = HorizontalDropLocation.get(getElement(),
                    drag.getCurrentGwtEvent().getClientX(), 0.2);
            VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE, true);
            VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                    + verticalDropLocation.toString().toLowerCase(), true);
            VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                    + horizontalDropLocation.toString().toLowerCase(), true);

            // TODO build (to be an example) an emphasis mode where drag image
            // is fitted before or after the content

        }

        @Override
        public Paintable getPaintable() {
            return VDragAndDropWrapper.this;
        }

        public ApplicationConnection getApplicationConnection() {
            return client;
        }

    }

    /**
     * Prototype code, memory leak risk.
     * 
     * @param el
     */
    private native void hookHtml5Events(Element el)
    /*-{

            var me = this;
            
            if(el.addEventListener) {
                el.addEventListener("dragenter",  function(ev) {
                    return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragEnter(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                }, false);
                
                el.addEventListener("dragleave",  function(ev) {
                    return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragLeave(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                }, false);
        
                el.addEventListener("dragover",  function(ev) {
                    return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragOver(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                }, false);
        
                el.addEventListener("drop",  function(ev) {
                    return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragDrop(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                }, false);
            
            } else {
                el.attachEvent("ondragenter",  function(ev) {
                            return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragEnter(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                });
                
                el.attachEvent("ondragleave",  function(ev) {
                        return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragLeave(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                });
        
                el.attachEvent("ondragover",  function(ev) {
                    return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragOver(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                });
        
                el.attachEvent("ondrop",  function(ev) {
                    return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragDrop(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
                });
            }
        
    }-*/;

}
