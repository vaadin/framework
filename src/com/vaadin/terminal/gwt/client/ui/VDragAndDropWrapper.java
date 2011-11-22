/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ValueMap;
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
    public static final String DRAG_START_MODE = "dragStartMode";
    public static final String HTML5_DATA_FLAVORS = "html5-data-flavors";

    private static final String CLASSNAME = "v-ddwrapper";
    protected static final String DRAGGABLE = "draggable";

    public VDragAndDropWrapper() {
        super();
        sinkEvents(VTooltip.TOOLTIP_EVENTS);

        hookHtml5Events(getElement());
        setStyleName(CLASSNAME);
        addDomHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                if (startDrag(event.getNativeEvent())) {
                    event.preventDefault(); // prevent text selection
                }
            }
        }, MouseDownEvent.getType());

        addDomHandler(new TouchStartHandler() {
            public void onTouchStart(TouchStartEvent event) {
                if (startDrag(event.getNativeEvent())) {
                    /*
                     * Dont let eg. panel start scrolling.
                     */
                    event.stopPropagation();
                }
            }
        }, TouchStartEvent.getType());

        sinkEvents(Event.TOUCHEVENTS);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

    /**
     * Starts a drag and drop operation from mousedown or touchstart event if
     * required conditions are met.
     * 
     * @param event
     * @return true if the event was handled as a drag start event
     */
    private boolean startDrag(NativeEvent event) {
        if (dragStartMode == WRAPPER || dragStartMode == COMPONENT) {
            VTransferable transferable = new VTransferable();
            transferable.setDragSource(VDragAndDropWrapper.this);

            Paintable paintable;
            Widget w = Util.findWidget((Element) event.getEventTarget().cast(),
                    null);
            while (w != null && !(w instanceof Paintable)) {
                w = w.getParent();
            }
            paintable = (Paintable) w;

            transferable.setData("component", paintable);
            VDragEvent dragEvent = VDragAndDropManager.get().startDrag(
                    transferable, event, true);

            transferable.setData("mouseDown",
                    new MouseEventDetails(event).serialize());

            if (dragStartMode == WRAPPER) {
                dragEvent.createDragImage(getElement(), true);
            } else {
                dragEvent.createDragImage(((Widget) paintable).getElement(),
                        true);
            }
            return true;
        }
        return false;
    }

    protected final static int NONE = 0;
    protected final static int COMPONENT = 1;
    protected final static int WRAPPER = 2;
    protected final static int HTML5 = 3;

    protected int dragStartMode;

    private ApplicationConnection client;
    private VAbstractDropHandler dropHandler;
    private VDragEvent vaadinDragEvent;

    private int filecounter = 0;
    private Map<String, String> fileIdToReceiver;
    private ValueMap html5DataFlavors;
    private Element dragStartElement;

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        super.updateFromUIDL(uidl, client);
        if (!uidl.hasAttribute("cached") && !uidl.hasAttribute("hidden")) {
            UIDL acceptCrit = uidl.getChildByTagName("-ac");
            if (acceptCrit == null) {
                dropHandler = null;
            } else {
                if (dropHandler == null) {
                    dropHandler = new CustomDropHandler();
                }
                dropHandler.updateAcceptRules(acceptCrit);
            }

            Set<String> variableNames = uidl.getVariableNames();
            for (String fileId : variableNames) {
                if (fileId.startsWith("rec-")) {
                    String receiverUrl = uidl.getStringVariable(fileId);
                    fileId = fileId.substring(4);
                    if (fileIdToReceiver == null) {
                        fileIdToReceiver = new HashMap<String, String>();
                    }
                    if ("".equals(receiverUrl)) {
                        Integer id = Integer.parseInt(fileId);
                        int indexOf = fileIds.indexOf(id);
                        if (indexOf != -1) {
                            files.remove(indexOf);
                            fileIds.remove(indexOf);
                        }
                    } else {
                        fileIdToReceiver.put(fileId, receiverUrl);
                    }
                }
            }
            startNextUpload();

            dragStartMode = uidl.getIntAttribute(DRAG_START_MODE);
            initDragStartMode();
            html5DataFlavors = uidl.getMapAttribute(HTML5_DATA_FLAVORS);
        }
    }

    protected void initDragStartMode() {
        Element div = getElement();
        if (dragStartMode == HTML5) {
            if (dragStartElement == null) {
                dragStartElement = getDragStartElement();
                dragStartElement.setPropertyBoolean(DRAGGABLE, true);
                VConsole.log("draggable = "
                        + dragStartElement.getPropertyBoolean(DRAGGABLE));
                hookHtml5DragStart(dragStartElement);
                VConsole.log("drag start listeners hooked.");
            }
        } else {
            dragStartElement = null;
            if (div.hasAttribute(DRAGGABLE)) {
                div.removeAttribute(DRAGGABLE);
            }
        }
    }

    protected Element getDragStartElement() {
        return getElement();
    }

    private boolean uploading;

    private ReadyStateChangeHandler readyStateChangeHandler = new ReadyStateChangeHandler() {
        public void onReadyStateChange(XMLHttpRequest xhr) {
            if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                // visit server for possible
                // variable changes
                client.sendPendingVariableChanges();
                uploading = false;
                startNextUpload();
                xhr.clearOnReadyStateChange();
            }
        }
    };
    private Timer dragleavetimer;

    private void startNextUpload() {
        Scheduler.get().scheduleDeferred(new Command() {

            public void execute() {
                if (!uploading) {
                    if (fileIds.size() > 0) {

                        uploading = true;
                        final Integer fileId = fileIds.remove(0);
                        VHtml5File file = files.remove(0);
                        final String receiverUrl = client
                                .translateVaadinUri(fileIdToReceiver
                                        .remove(fileId.toString()));
                        ExtendedXHR extendedXHR = (ExtendedXHR) ExtendedXHR
                                .create();
                        extendedXHR
                                .setOnReadyStateChange(readyStateChangeHandler);
                        extendedXHR.open("POST", receiverUrl);
                        extendedXHR.postFile(file);
                    }
                }

            }
        });

    }

    public boolean html5DragStart(VHtml5DragEvent event) {
        if (dragStartMode == HTML5) {
            /*
             * Populate html5 payload with dataflavors from the serverside
             */
            JsArrayString flavors = html5DataFlavors.getKeyArray();
            for (int i = 0; i < flavors.length(); i++) {
                String flavor = flavors.get(i);
                event.setHtml5DataFlavor(flavor,
                        html5DataFlavors.getString(flavor));
            }
            event.setEffectAllowed("copy");
            return true;
        }
        return false;
    }

    public boolean html5DragEnter(VHtml5DragEvent event) {
        if (dropHandler == null) {
            return true;
        }
        try {
            if (dragleavetimer != null) {
                // returned quickly back to wrapper
                dragleavetimer.cancel();
                dragleavetimer = null;
            }
            if (VDragAndDropManager.get().getCurrentDropHandler() != getDropHandler()) {
                VTransferable transferable = new VTransferable();
                transferable.setDragSource(this);

                vaadinDragEvent = VDragAndDropManager.get().startDrag(
                        transferable, event, false);
                VDragAndDropManager.get().setCurrentDropHandler(
                        getDropHandler());
            }
            try {
                event.preventDefault();
                event.stopPropagation();
            } catch (Exception e) {
                // VConsole.log("IE9 fails");
            }
            return false;
        } catch (Exception e) {
            GWT.getUncaughtExceptionHandler().onUncaughtException(e);
            return true;
        }
    }

    public boolean html5DragLeave(VHtml5DragEvent event) {
        if (dropHandler == null) {
            return true;
        }

        try {
            dragleavetimer = new Timer() {
                @Override
                public void run() {
                    // Yes, dragleave happens before drop. Makes no sense to me.
                    // IMO shouldn't fire leave at all if drop happens (I guess
                    // this
                    // is what IE does).
                    // In Vaadin we fire it only if drop did not happen.
                    if (vaadinDragEvent != null
                            && VDragAndDropManager.get()
                                    .getCurrentDropHandler() == getDropHandler()) {
                        VDragAndDropManager.get().interruptDrag();
                    }
                }
            };
            dragleavetimer.schedule(350);
            try {
                event.preventDefault();
                event.stopPropagation();
            } catch (Exception e) {
                // VConsole.log("IE9 fails");
            }
            return false;
        } catch (Exception e) {
            GWT.getUncaughtExceptionHandler().onUncaughtException(e);
            return true;
        }
    }

    public boolean html5DragOver(VHtml5DragEvent event) {
        if (dropHandler == null) {
            return true;
        }

        if (dragleavetimer != null) {
            // returned quickly back to wrapper
            dragleavetimer.cancel();
            dragleavetimer = null;
        }

        vaadinDragEvent.setCurrentGwtEvent(event);
        getDropHandler().dragOver(vaadinDragEvent);

        String s = event.getEffectAllowed();
        if ("all".equals(s) || s.contains("opy")) {
            event.setDropEffect("copy");
        } else {
            event.setDropEffect(s);
        }

        try {
            event.preventDefault();
            event.stopPropagation();
        } catch (Exception e) {
            // VConsole.log("IE9 fails");
        }
        return false;
    }

    public boolean html5DragDrop(VHtml5DragEvent event) {
        if (dropHandler == null || !currentlyValid) {
            return true;
        }
        try {

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
                    queueFilePost(fileId, file);
                }

            }

            VDragAndDropManager.get().endDrag();
            vaadinDragEvent = null;
            try {
                event.preventDefault();
                event.stopPropagation();
            } catch (Exception e) {
                // VConsole.log("IE9 fails");
            }
            return false;
        } catch (Exception e) {
            GWT.getUncaughtExceptionHandler().onUncaughtException(e);
            return true;
        }

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

        public final native void postFile(VHtml5File file)
        /*-{

            this.setRequestHeader('Content-Type', 'multipart/form-data');
            this.send(file);
        }-*/;

    }

    /**
     * Currently supports only FF36 as no other browser supports natively File
     * api.
     * 
     * @param fileId
     * @param data
     */
    private List<Integer> fileIds = new ArrayList<Integer>();
    private List<VHtml5File> files = new ArrayList<VHtml5File>();

    private void queueFilePost(final int fileId, final VHtml5File file) {
        fileIds.add(fileId);
        files.add(file);
    }

    private String getPid() {
        return client.getPid(this);
    }

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
            dragleavetimer = null;
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

    protected native void hookHtml5DragStart(Element el)
    /*-{
        var me = this;
        el.addEventListener("dragstart",  function(ev) {
            return me.@com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper::html5DragStart(Lcom/vaadin/terminal/gwt/client/ui/dd/VHtml5DragEvent;)(ev);
        }, false);
    }-*/;

    /**
     * Prototype code, memory leak risk.
     * 
     * @param el
     */
    protected native void hookHtml5Events(Element el)
    /*-{
            var me = this;

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
    }-*/;

    public boolean updateDropDetails(VDragEvent drag) {
        VerticalDropLocation oldVL = verticalDropLocation;
        verticalDropLocation = DDUtil.getVerticalDropLocation(getElement(),
                drag.getCurrentGwtEvent(), 0.2);
        drag.getDropDetails().put("verticalLocation",
                verticalDropLocation.toString());
        HorizontalDropLocation oldHL = horizontalDropLocation;
        horizontalDropLocation = DDUtil.getHorizontalDropLocation(getElement(),
                drag.getCurrentGwtEvent(), 0.2);
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
