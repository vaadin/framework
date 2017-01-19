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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ValueMap;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.dd.DDUtil;
import com.vaadin.client.ui.dd.VAbstractDropHandler;
import com.vaadin.client.ui.dd.VAcceptCallback;
import com.vaadin.client.ui.dd.VDragAndDropManager;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.client.ui.dd.VDropHandler;
import com.vaadin.client.ui.dd.VHasDropHandler;
import com.vaadin.client.ui.dd.VHtml5DragEvent;
import com.vaadin.client.ui.dd.VHtml5File;
import com.vaadin.client.ui.dd.VTransferable;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.shared.ui.dd.VerticalDropLocation;

/**
 *
 * Must have features pending:
 *
 * drop details: locations + sizes in document hierarchy up to wrapper
 *
 */
public class VDragAndDropWrapper extends VCustomComponent
        implements VHasDropHandler {

    /**
     * Minimum pixel delta is used to detect click from drag. #12838
     */
    private static final int MIN_PX_DELTA = 4;
    private static final String CLASSNAME = "v-ddwrapper";
    protected static final String DRAGGABLE = "draggable";

    /** For internal use only. May be removed or replaced in the future. */
    public boolean hasTooltip = false;
    private int startX = 0;
    private int startY = 0;

    public VDragAndDropWrapper() {
        super();
        hookHtml5Events(getElement());
        setStyleName(CLASSNAME);

        addDomHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(final MouseDownEvent event) {
                if (getConnector().isEnabled()
                        && event.getNativeEvent()
                                .getButton() == Event.BUTTON_LEFT
                        && startDrag(event.getNativeEvent())) {
                    event.preventDefault(); // prevent text selection
                    startX = event.getClientX();
                    startY = event.getClientY();
                }
            }
        }, MouseDownEvent.getType());

        addDomHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(final MouseUpEvent event) {
                final int deltaX = Math.abs(event.getClientX() - startX);
                final int deltaY = Math.abs(event.getClientY() - startY);
                if ((deltaX + deltaY) < MIN_PX_DELTA) {
                    Element clickedElement = WidgetUtil.getElementFromPoint(
                            event.getClientX(), event.getClientY());
                    clickedElement.focus();
                }
            }

        }, MouseUpEvent.getType());

        addDomHandler(new TouchStartHandler() {

            @Override
            public void onTouchStart(TouchStartEvent event) {
                if (getConnector().isEnabled()
                        && startDrag(event.getNativeEvent())) {
                    /*
                     * Dont let eg. panel start scrolling.
                     */
                    event.stopPropagation();
                }
            }
        }, TouchStartEvent.getType());

        sinkEvents(Event.TOUCHEVENTS);
    }

    /**
     * Starts a drag and drop operation from mousedown or touchstart event if
     * required conditions are met.
     *
     * @param event
     * @return true if the event was handled as a drag start event
     */
    private boolean startDrag(NativeEvent event) {
        if (dragStartMode == WRAPPER || dragStartMode == COMPONENT
                || dragStartMode == COMPONENT_OTHER) {
            VTransferable transferable = new VTransferable();
            transferable.setDragSource(getConnector());

            ComponentConnector paintable = Util.findPaintable(client,
                    Element.as(event.getEventTarget()));
            Widget widget = paintable.getWidget();
            transferable.setData("component", paintable);
            VDragEvent dragEvent = VDragAndDropManager.get()
                    .startDrag(transferable, event, true);

            transferable.setData("mouseDown", MouseEventDetailsBuilder
                    .buildMouseEventDetails(event).serialize());

            if (dragStartMode == WRAPPER) {
                dragEvent.createDragImage(getElement(), true);
            } else if (dragStartMode == COMPONENT_OTHER
                    && getDragImageWidget() != null) {
                dragEvent.createDragImage(getDragImageWidget().getElement(),
                        true);
            } else {
                dragEvent.createDragImage(widget.getElement(), true);
            }
            return true;
        }
        return false;
    }

    protected final static int NONE = 0;
    protected final static int COMPONENT = 1;
    protected final static int WRAPPER = 2;
    protected final static int HTML5 = 3;
    protected final static int COMPONENT_OTHER = 4;

    /** For internal use only. May be removed or replaced in the future. */
    public int dragStartMode;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public VAbstractDropHandler dropHandler;

    /** For internal use only. May be removed or replaced in the future. */
    public UploadHandler uploadHandler;

    private VDragEvent vaadinDragEvent;

    int filecounter = 0;

    /** For internal use only. May be removed or replaced in the future. */
    public Map<String, String> fileIdToReceiver;

    /** For internal use only. May be removed or replaced in the future. */
    public ValueMap html5DataFlavors;

    private Element dragStartElement;

    /** For internal use only. May be removed or replaced in the future. */
    public void initDragStartMode() {
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

    protected com.google.gwt.user.client.Element getDragStartElement() {
        return getElement();
    }

    private boolean uploading;

    private final ReadyStateChangeHandler readyStateChangeHandler = new ReadyStateChangeHandler() {

        @Override
        public void onReadyStateChange(XMLHttpRequest xhr) {
            if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                // #19616 Notify the upload handler that the request is complete
                // and let it poll the server for changes.
                uploadHandler.uploadDone();
                uploading = false;
                startNextUpload();
                xhr.clearOnReadyStateChange();
            }
        }
    };
    private Timer dragleavetimer;

    /** For internal use only. May be removed or replaced in the future. */
    public void startNextUpload() {
        Scheduler.get().scheduleDeferred(new Command() {

            @Override
            public void execute() {
                if (!uploading) {
                    if (fileIds.size() > 0) {

                        uploading = true;
                        final Integer fileId = fileIds.remove(0);
                        VHtml5File file = files.remove(0);
                        final String receiverUrl = client.translateVaadinUri(
                                fileIdToReceiver.remove(fileId.toString()));
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
            if (VDragAndDropManager.get()
                    .getCurrentDropHandler() != getDropHandler()) {
                VTransferable transferable = new VTransferable();
                transferable.setDragSource(getConnector());

                vaadinDragEvent = VDragAndDropManager.get()
                        .startDrag(transferable, event, false);
                VDragAndDropManager.get()
                        .setCurrentDropHandler(getDropHandler());
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
                    if (vaadinDragEvent != null && VDragAndDropManager.get()
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

        try {
            String s = event.getEffectAllowed();
            if ("all".equals(s) || s.contains("opy")) {
                event.setDropEffect("copy");
            } else {
                event.setDropEffect(s);
            }
        } catch (Exception e) {
            // IE10 throws exception here in getEffectAllowed, ignore it, let
            // drop effect be whatever it is
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
            VDragAndDropManager.get().interruptDrag();
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

            final int eventFileCount = event.getFileCount();
            int fileIndex = 0;
            for (int i = 0; i < eventFileCount; i++) {
                // Transfer only files and not folders
                if (event.isFile(i)) {
                    final int fileId = filecounter++;
                    final VHtml5File file = event.getFile(i);
                    VConsole.log("Preparing to upload file " + file.getName()
                            + " with id " + fileId + ", size="
                            + file.getSize());
                    transferable.setData("fi" + fileIndex, "" + fileId);
                    transferable.setData("fn" + fileIndex, file.getName());
                    transferable.setData("ft" + fileIndex, file.getType());
                    transferable.setData("fs" + fileIndex, file.getSize());
                    queueFilePost(fileId, file);
                    fileIndex++;
                }
            }
            if (fileIndex > 0) {
                transferable.setData("filecount", fileIndex);
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
            // Seems like IE10 will loose the file if we don't keep a reference to it...
            this.fileBeingUploaded = file;

            this.send(file);
        }-*/;

    }

    /** For internal use only. May be removed or replaced in the future. */
    public List<Integer> fileIds = new ArrayList<Integer>();

    /** For internal use only. May be removed or replaced in the future. */
    public List<VHtml5File> files = new ArrayList<VHtml5File>();

    private void queueFilePost(final int fileId, final VHtml5File file) {
        fileIds.add(fileId);
        files.add(file);
    }

    @Override
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
    private Widget dragImageWidget;

    private static final String OVER_STYLE = "v-ddwrapper-over";

    public class CustomDropHandler extends VAbstractDropHandler {

        @Override
        public void dragEnter(VDragEvent drag) {
            if (!getConnector().isEnabled()) {
                return;
            }
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
            if (!getConnector().isEnabled()) {
                return;
            }
            boolean detailsChanged = updateDropDetails(drag);
            if (detailsChanged) {
                currentlyValid = false;
                validate(new VAcceptCallback() {

                    @Override
                    public void accepted(VDragEvent event) {
                        dragAccepted(drag);
                    }
                }, drag);
            }
        }

        @Override
        public boolean drop(VDragEvent drag) {
            if (!getConnector().isEnabled()) {
                return false;
            }
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
            if (!getConnector().isEnabled()) {
                return;
            }
            currentlyValid = true;
            emphasis(drag);
        }

        @Override
        public ComponentConnector getConnector() {
            return VDragAndDropWrapper.this.getConnector();
        }

        @Override
        public ApplicationConnection getApplicationConnection() {
            return client;
        }

    }

    public ComponentConnector getConnector() {
        return ConnectorMap.get(client).getConnector(this);
    }

    /**
     * @deprecated As of 7.2, call or override
     *             {@link #hookHtml5DragStart(Element)} instead
     */
    @Deprecated
    protected native void hookHtml5DragStart(
            com.google.gwt.user.client.Element el)
    /*-{
        var me = this;
        el.addEventListener("dragstart",  $entry(function(ev) {
            return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragStart(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
        }), false);
    }-*/;

    /**
     * @since 7.2
     */
    protected void hookHtml5DragStart(Element el) {
        hookHtml5DragStart(DOM.asOld(el));
    }

    /**
     * Prototype code, memory leak risk.
     *
     * @param el
     * @deprecated As of 7.2, call or override {@link #hookHtml5Events(Element)}
     *             instead
     */
    @Deprecated
    protected native void hookHtml5Events(com.google.gwt.user.client.Element el)
    /*-{
            var me = this;

            el.addEventListener("dragenter",  $entry(function(ev) {
                return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragEnter(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
            }), false);

            el.addEventListener("dragleave",  $entry(function(ev) {
                return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragLeave(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
            }), false);

            el.addEventListener("dragover",  $entry(function(ev) {
                return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragOver(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
            }), false);

            el.addEventListener("drop",  $entry(function(ev) {
                return me.@com.vaadin.client.ui.VDragAndDropWrapper::html5DragDrop(Lcom/vaadin/client/ui/dd/VHtml5DragEvent;)(ev);
            }), false);
    }-*/;

    /**
     * Prototype code, memory leak risk.
     *
     * @param el
     *
     * @since 7.2
     */
    protected void hookHtml5Events(Element el) {
        hookHtml5Events(DOM.asOld(el));
    }

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
        if (emphasizedVDrop != null) {
            VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE, false);
            VDragAndDropWrapper.setStyleName(getElement(),
                    OVER_STYLE + "-" + emphasizedVDrop.toString().toLowerCase(),
                    false);
            VDragAndDropWrapper.setStyleName(getElement(),
                    OVER_STYLE + "-" + emphasizedHDrop.toString().toLowerCase(),
                    false);
        }
        if (doLayout) {
            notifySizePotentiallyChanged();
        }
    }

    private void notifySizePotentiallyChanged() {
        LayoutManager.get(client).setNeedsMeasure(getConnector());
    }

    protected void emphasis(VDragEvent drag) {
        deEmphasis(false);
        VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE, true);
        VDragAndDropWrapper.setStyleName(getElement(), OVER_STYLE + "-"
                + verticalDropLocation.toString().toLowerCase(), true);
        VDragAndDropWrapper.setStyleName(getElement(),
                OVER_STYLE + "-"
                        + horizontalDropLocation.toString().toLowerCase(),
                true);
        emphasizedVDrop = verticalDropLocation;
        emphasizedHDrop = horizontalDropLocation;

        // TODO build (to be an example) an emphasis mode where drag image
        // is fitted before or after the content
        notifySizePotentiallyChanged();
    }

    /**
     * Set the widget that will be used as the drag image when using
     * DragStartMode {@link COMPONENT_OTHER} .
     *
     * @param widget
     */
    public void setDragAndDropWidget(Widget widget) {
        dragImageWidget = widget;
    }

    /**
     * @return the widget used as drag image. Returns <code>null</code> if no
     *         widget is set.
     */
    public Widget getDragImageWidget() {
        return dragImageWidget;
    }

    /**
     * Internal client side interface used by the connector and the widget for
     * the drag and drop wrapper to signal the completion of an HTML5 file
     * upload.
     *
     * @since 7.6.4
     */
    public interface UploadHandler {

        public void uploadDone();

    }

}
