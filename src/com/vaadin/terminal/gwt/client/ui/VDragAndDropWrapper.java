/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.dd.DDUtil;
import com.vaadin.terminal.gwt.client.ui.dd.HorizontalDropLocation;
import com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCallback;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VHtml5DragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VHtml5File;
import com.vaadin.terminal.gwt.client.ui.dd.VHtml5File.Callback;
import com.vaadin.terminal.gwt.client.ui.dd.VTransferable;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;

/**
 * 
 * Must have features pending:
 * 
 * drop details: locations + sizes in document hierarchy up to wrapper
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
                    VTransferable transferable = new VTransferable();
                    transferable.setDragSource(VDragAndDropWrapper.this);

                    Paintable paintable;
                    Widget w = Util.findWidget((Element) event.getNativeEvent()
                            .getEventTarget().cast(), null);
                    while (w != null && !(w instanceof Paintable)) {
                        w = w.getParent();
                    }
                    paintable = (Paintable) w;

                    transferable.setData("component", paintable);
                    VDragEvent startDrag = VDragAndDropManager.get().startDrag(
                            transferable, event.getNativeEvent(), true);

                    transferable.setData("mouseDown", new MouseEventDetails(
                            event.getNativeEvent()).serialize());

                    if (dragStarMode == WRAPPER) {
                        startDrag.createDragImage(getElement(), true);
                    } else {
                        startDrag.createDragImage(
                                ((Widget) paintable).getElement(), true);
                    }
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
    private int filecounter = 0;
    private boolean dragLeavPostponed;

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
        if (dragLeavPostponed) {
            // returned quickly back to wrapper
            dragLeavPostponed = false;
            return false;
        }
        VTransferable transferable = new VTransferable();
        transferable.setDragSource(this);

        vaadinDragEvent = VDragAndDropManager.get().startDrag(transferable,
                event, false);
        VDragAndDropManager.get().setCurrentDropHandler(getDropHandler());
        event.preventDefault();
        event.stopPropagation();
        return false;
    }

    public boolean html5DragLeave(VHtml5DragEvent event) {
        if (dropHandler == null) {
            return true;
        }

        dragLeavPostponed = true;
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                // Yes, dragleave happens before drop. Makes no sense to me.
                // IMO shouldn't fire leave at all if drop happens (I guess this
                // is what IE does).
                // In Vaadin we fire it only if drop did not happen.
                if (dragLeavPostponed
                        && vaadinDragEvent != null
                        && VDragAndDropManager.get().getCurrentDropHandler() == getDropHandler()) {
                    VDragAndDropManager.get().interruptDrag();
                }
                dragLeavPostponed = false;
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

        vaadinDragEvent.setCurrentGwtEvent(event);
        getDropHandler().dragOver(vaadinDragEvent);
        // needed to be set for Safari, otherwise drop will not happen
        if (BrowserInfo.get().isWebkit()) {
            String s = event.getEffectAllowed();
            if ("all".equals(s) || s.contains("opy")) {
                event.setDragEffect("copy");
            } else {
                event.setDragEffect(s);
            }
        }
        event.preventDefault();
        event.stopPropagation();
        return false;
    }

    public boolean html5DragDrop(VHtml5DragEvent event) {
        if (dropHandler == null || !currentlyValid) {
            return true;
        }

        VTransferable transferable = vaadinDragEvent.getTransferable();

        JsArrayString types = event.getTypes();
        for (int i = 0; i < types.length(); i++) {
            String type = types.get(i);
            if (isAcceptedType(type)) {
                String data = event.getDataAsText(type);
                if (data != null) {
                    transferable.setData(type, data);
                }
            }
        }

        int fileCount = event.getFileCount();
        if (fileCount > 0) {
            transferable.setData("filecount", fileCount);
            for (int i = 0; i < fileCount; i++) {
                final int fileId = filecounter++;
                final VHtml5File file = event.getFile(i);
                transferable.setData("fi" + i, "" + fileId);
                transferable.setData("fn" + i, file.getName());
                transferable.setData("ft" + i, file.getType());
                transferable.setData("fs" + i, file.getSize());
                postFile(fileId, file);
            }

        }

        VDragAndDropManager.get().endDrag();
        vaadinDragEvent = null;
        event.preventDefault();
        event.stopPropagation();

        return false;
    }

    protected String[] acceptedTypes = new String[] { "Text", "Url",
            "text/html", "text/plain", "text/rtf" };

    private boolean isAcceptedType(String type) {
        for (String t : acceptedTypes) {
            if (t.equals(type)) {
                return true;
            }
        }
        return false;
    }

    static class ExtendedXHR extends XMLHttpRequest {

        protected ExtendedXHR() {
        }

        public final native void sendBinary(JavaScriptObject data)
        /*-{
            //this.overrideMimeType('text/plain; charset=x-user-defined-binary');
            this.sendAsBinary(data);
        }-*/;

    }

    /**
     * Currently supports only FF36 as no other browser supports natively File
     * api.
     * 
     * @param fileId
     * @param data
     */
    private void postFile(final int fileId, final VHtml5File file) {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                /*
                 * File contents is sent deferred to allow quick reaction on GUI
                 * although file upload may last long.
                 */
                file.readAsBinary(new Callback() {
                    public void handleFile(final JavaScriptObject object) {

                        ExtendedXHR extendedXHR = (ExtendedXHR) ExtendedXHR
                                .create();
                        String name = "XHRFILE" + getPid() + "." + fileId;
                        extendedXHR
                                .setOnReadyStateChange(new ReadyStateChangeHandler() {
                                    public void onReadyStateChange(
                                            XMLHttpRequest xhr) {
                                        if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                                            client.sendPendingVariableChanges();
                                            xhr.clearOnReadyStateChange();
                                        }
                                    }
                                });
                        extendedXHR.open("POST", client.getAppUri());
                        multipartSend(extendedXHR, object, name);

                    }
                });

            }
        });

    }

    private String getPid() {
        return client.getPid(this);
    }

    private native void multipartSend(JavaScriptObject xhr,
            JavaScriptObject data, String name)
    /*-{
     
        var boundaryString = "------------------------------------------VAADINXHRFILEUPLOAD";
        var boundary = "--" + boundaryString;
        var CRLF = "\r\n";
        xhr.setRequestHeader("Content-type", "multipart/form-data; boundary=\"" + boundaryString + "\"");
        var requestBody = boundary
                + CRLF
                + "Content-Disposition: form-data; name=\""+name+"\"; filename=\"file\""
                + CRLF
                + "Content-Type: application/octet-stream" // hard coded, type sent separately
                + CRLF + CRLF + data.target.result + CRLF + boundary + "--" + CRLF;
        xhr.setRequestHeader("Content-Length", requestBody.length);
        
        
        xhr.sendAsBinary(requestBody);
         
     }-*/;

    public VDropHandler getDropHandler() {
        return dropHandler;
    }

    protected VerticalDropLocation verticalDropLocation;
    protected HorizontalDropLocation horizontalDropLocation;
    private VerticalDropLocation emphasizedVDrop;
    private HorizontalDropLocation emphasizedHDrop;

    /**
     * Flag used by html5 dd
     */
    private boolean currentlyValid;

    private static final String OVER_STYLE = "v-ddwrapper-over";

    public class CustomDropHandler extends VAbstractDropHandler {

        @Override
        public void dragEnter(VDragEvent drag) {
            updateDropDetails(drag);
            currentlyValid = false;
            super.dragEnter(drag);
        }

        @Override
        public void dragLeave(VDragEvent drag) {
            deEmphasis(true);
            dragLeavPostponed = false;
        }

        @Override
        public void dragOver(final VDragEvent drag) {
            boolean detailsChanged = updateDropDetails(drag);
            if (detailsChanged) {
                currentlyValid = false;
                validate(new VAcceptCallback() {
                    public void accepted(VDragEvent event) {
                        dragAccepted(drag);
                    }
                }, drag);
            }
        }

        @Override
        public boolean drop(VDragEvent drag) {
            deEmphasis(true);

            Map<String, Object> dd = drag.getDropDetails();

            // this is absolute layout based, and we may want to set
            // component
            // relatively to where the drag ended.
            // need to add current location of the drop area

            int absoluteLeft = getAbsoluteLeft();
            int absoluteTop = getAbsoluteTop();

            dd.put("absoluteLeft", absoluteLeft);
            dd.put("absoluteTop", absoluteTop);

            if (verticalDropLocation != null) {
                dd.put("verticalLocation", verticalDropLocation.toString());
                dd.put("horizontalLocation", horizontalDropLocation.toString());
            }

            return super.drop(drag);
        }

        @Override
        protected void dragAccepted(VDragEvent drag) {
            currentlyValid = true;
            emphasis(drag);
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

    public boolean updateDropDetails(VDragEvent drag) {
        VerticalDropLocation oldVL = verticalDropLocation;
        verticalDropLocation = DDUtil.getVerticalDropLocation(getElement(),
                drag.getCurrentGwtEvent().getClientY(), 0.2);
        drag.getDropDetails().put("verticalLocation",
                verticalDropLocation.toString());
        HorizontalDropLocation oldHL = horizontalDropLocation;
        horizontalDropLocation = DDUtil.getHorizontalDropLocation(getElement(),
                drag.getCurrentGwtEvent().getClientX(), 0.2);
        drag.getDropDetails().put("horizontalLocation",
                horizontalDropLocation.toString());
        if (oldHL != horizontalDropLocation || oldVL != verticalDropLocation) {
            return true;
        } else {
            return false;
        }
    }

    protected void deEmphasis(boolean doLayout) {
        Size size = null;
        if (doLayout) {
            size = new RenderInformation.Size(getOffsetWidth(),
                    getOffsetHeight());
        }
        if (emphasizedVDrop != null) {
            VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE, false);
            VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                    + emphasizedVDrop.toString().toLowerCase(), false);
            VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                    + emphasizedHDrop.toString().toLowerCase(), false);
        }
        if (doLayout) {
            handleVaadinRelatedSizeChange(size);
        }
    }

    protected void emphasis(VDragEvent drag) {
        Size size = new RenderInformation.Size(getOffsetWidth(),
                getOffsetHeight());
        deEmphasis(false);
        VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE, true);
        VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                + verticalDropLocation.toString().toLowerCase(), true);
        VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                + horizontalDropLocation.toString().toLowerCase(), true);
        emphasizedVDrop = verticalDropLocation;
        emphasizedHDrop = horizontalDropLocation;

        // TODO build (to be an example) an emphasis mode where drag image
        // is fitted before or after the content
        handleVaadinRelatedSizeChange(size);

    }

    protected void handleVaadinRelatedSizeChange(Size originalSize) {
        if (isDynamicHeight() || isDynamicWidth()) {
            if (!originalSize.equals(new RenderInformation.Size(
                    getOffsetWidth(), getOffsetHeight()))) {
                Util.notifyParentOfSizeChange(VDragAndDropWrapper.this, false);
            }
        }
        client.handleComponentRelativeSize(VDragAndDropWrapper.this);
        Util.notifyParentOfSizeChange(this, false);

    }

}
