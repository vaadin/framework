/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.DragAndDropWrapperConnector;
import com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper;
import com.vaadin.terminal.gwt.client.ui.dd.HorizontalDropLocation;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;

@SuppressWarnings("serial")
@ClientWidget(DragAndDropWrapperConnector.class)
public class DragAndDropWrapper extends CustomComponent implements DropTarget,
        DragSource {

    public class WrapperTransferable extends TransferableImpl {

        private Html5File[] files;

        public WrapperTransferable(Component sourceComponent,
                Map<String, Object> rawVariables) {
            super(sourceComponent, rawVariables);
            Integer fc = (Integer) rawVariables.get("filecount");
            if (fc != null) {
                files = new Html5File[fc];
                for (int i = 0; i < fc; i++) {
                    Html5File file = new Html5File(
                            (String) rawVariables.get("fn" + i), // name
                            (Integer) rawVariables.get("fs" + i), // size
                            (String) rawVariables.get("ft" + i)); // mime
                    String id = (String) rawVariables.get("fi" + i);
                    files[i] = file;
                    receivers.put(id, file);
                    requestRepaint(); // paint Receivers
                }
            }
        }

        /**
         * The component in wrapper that is being dragged or null if the
         * transferable is not a component (most likely an html5 drag).
         * 
         * @return
         */
        public Component getDraggedComponent() {
            Component object = (Component) getData("component");
            return object;
        }

        /**
         * @return the mouse down event that started the drag and drop operation
         */
        public MouseEventDetails getMouseDownEvent() {
            return MouseEventDetails.deSerialize((String) getData("mouseDown"));
        }

        public Html5File[] getFiles() {
            return files;
        }

        public String getText() {
            String data = (String) getData("Text"); // IE, html5
            if (data == null) {
                // check for "text/plain" (webkit)
                data = (String) getData("text/plain");
            }
            return data;
        }

        public String getHtml() {
            String data = (String) getData("Html"); // IE, html5
            if (data == null) {
                // check for "text/plain" (webkit)
                data = (String) getData("text/html");
            }
            return data;
        }

    }

    private Map<String, Html5File> receivers = new HashMap<String, Html5File>();

    public class WrapperTargetDetails extends TargetDetailsImpl {

        public WrapperTargetDetails(Map<String, Object> rawDropData) {
            super(rawDropData, DragAndDropWrapper.this);
        }

        /**
         * @return the absolute position of wrapper on the page
         */
        public Integer getAbsoluteLeft() {
            return (Integer) getData("absoluteLeft");
        }

        /**
         * 
         * @return the absolute position of wrapper on the page
         */
        public Integer getAbsoluteTop() {
            return (Integer) getData("absoluteTop");
        }

        /**
         * @return details about the actual event that caused the event details.
         *         Practically mouse move or mouse up.
         */
        public MouseEventDetails getMouseEvent() {
            return MouseEventDetails
                    .deSerialize((String) getData("mouseEvent"));
        }

        /**
         * @return a detail about the drags vertical position over the wrapper.
         */
        public VerticalDropLocation getVerticalDropLocation() {
            return VerticalDropLocation
                    .valueOf((String) getData("verticalLocation"));
        }

        /**
         * @return a detail about the drags horizontal position over the
         *         wrapper.
         */
        public HorizontalDropLocation getHorizontalDropLocation() {
            return HorizontalDropLocation
                    .valueOf((String) getData("horizontalLocation"));
        }

        /**
         * @deprecated use {@link #getVerticalDropLocation()} instead
         */
        @Deprecated
        public VerticalDropLocation verticalDropLocation() {
            return getVerticalDropLocation();
        }

        /**
         * @deprecated use {@link #getHorizontalDropLocation()} instead
         */
        @Deprecated
        public HorizontalDropLocation horizontalDropLocation() {
            return getHorizontalDropLocation();
        }

    }

    public enum DragStartMode {
        /**
         * {@link DragAndDropWrapper} does not start drag events at all
         */
        NONE,
        /**
         * The component on which the drag started will be shown as drag image.
         */
        COMPONENT,
        /**
         * The whole wrapper is used as a drag image when dragging.
         */
        WRAPPER,
        /**
         * The whole wrapper is used to start an HTML5 drag.
         * 
         * NOTE: In Internet Explorer 6 to 8, this prevents user interactions
         * with the wrapper's contents. For example, clicking a button inside
         * the wrapper will no longer work.
         */
        HTML5,
    }

    private final Map<String, Object> html5DataFlavors = new LinkedHashMap<String, Object>();
    private DragStartMode dragStartMode = DragStartMode.NONE;

    /**
     * Wraps given component in a {@link DragAndDropWrapper}.
     * 
     * @param root
     *            the component to be wrapped
     */
    public DragAndDropWrapper(Component root) {
        super(root);
    }

    /**
     * Sets data flavors available in the DragAndDropWrapper is used to start an
     * HTML5 style drags. Most commonly the "Text" flavor should be set.
     * Multiple data types can be set.
     * 
     * @param type
     *            the string identifier of the drag "payload". E.g. "Text" or
     *            "text/html"
     * @param value
     *            the value
     */
    public void setHTML5DataFlavor(String type, Object value) {
        html5DataFlavors.put(type, value);
        requestRepaint();
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute(VDragAndDropWrapper.DRAG_START_MODE,
                dragStartMode.ordinal());
        if (getDropHandler() != null) {
            getDropHandler().getAcceptCriterion().paint(target);
        }
        if (receivers != null && receivers.size() > 0) {
            for (Iterator<Entry<String, Html5File>> it = receivers.entrySet()
                    .iterator(); it.hasNext();) {
                Entry<String, com.vaadin.ui.Html5File> entry = it.next();
                String id = entry.getKey();
                Html5File html5File = entry.getValue();
                if (html5File.getStreamVariable() != null) {
                    target.addVariable(this, "rec-" + id, new ProxyReceiver(
                            html5File));
                    // these are cleaned from receivers once the upload has
                    // started
                } else {
                    // instructs the client side not to send the file
                    target.addVariable(this, "rec-" + id, (String) null);
                    // forget the file from subsequent paints
                    it.remove();
                }
            }
        }
        target.addAttribute(VDragAndDropWrapper.HTML5_DATA_FLAVORS,
                html5DataFlavors);
    }

    private DropHandler dropHandler;

    public DropHandler getDropHandler() {
        return dropHandler;
    }

    public void setDropHandler(DropHandler dropHandler) {
        this.dropHandler = dropHandler;
        requestRepaint();
    }

    public TargetDetails translateDropTargetDetails(
            Map<String, Object> clientVariables) {
        return new WrapperTargetDetails(clientVariables);
    }

    public Transferable getTransferable(final Map<String, Object> rawVariables) {
        return new WrapperTransferable(this, rawVariables);
    }

    public void setDragStartMode(DragStartMode dragStartMode) {
        this.dragStartMode = dragStartMode;
        requestRepaint();
    }

    public DragStartMode getDragStartMode() {
        return dragStartMode;
    }

    final class ProxyReceiver implements StreamVariable {

        private Html5File file;

        public ProxyReceiver(Html5File file) {
            this.file = file;
        }

        private boolean listenProgressOfUploadedFile;

        public OutputStream getOutputStream() {
            if (file.getStreamVariable() == null) {
                return null;
            }
            return file.getStreamVariable().getOutputStream();
        }

        public boolean listenProgress() {
            return file.getStreamVariable().listenProgress();
        }

        public void onProgress(StreamingProgressEvent event) {
            file.getStreamVariable().onProgress(
                    new ReceivingEventWrapper(event));
        }

        public void streamingStarted(StreamingStartEvent event) {
            listenProgressOfUploadedFile = file.getStreamVariable() != null;
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable().streamingStarted(
                        new ReceivingEventWrapper(event));
            }
            // no need tell to the client about this receiver on next paint
            receivers.remove(file);
            // let the terminal GC the streamvariable and not to accept other
            // file uploads to this variable
            event.disposeStreamVariable();
        }

        public void streamingFinished(StreamingEndEvent event) {
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable().streamingFinished(
                        new ReceivingEventWrapper(event));
            }
        }

        public void streamingFailed(final StreamingErrorEvent event) {
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable().streamingFailed(
                        new ReceivingEventWrapper(event));
            }
        }

        public boolean isInterrupted() {
            return file.getStreamVariable().isInterrupted();
        }

        /*
         * With XHR2 file posts we can't provide as much information from the
         * terminal as with multipart request. This helper class wraps the
         * terminal event and provides the lacking information from the
         * Html5File.
         */
        class ReceivingEventWrapper implements StreamingErrorEvent,
                StreamingEndEvent, StreamingStartEvent, StreamingProgressEvent {

            private StreamingEvent wrappedEvent;

            ReceivingEventWrapper(StreamingEvent e) {
                wrappedEvent = e;
            }

            public String getMimeType() {
                return file.getType();
            }

            public String getFileName() {
                return file.getFileName();
            }

            public long getContentLength() {
                return file.getFileSize();
            }

            public StreamVariable getReceiver() {
                return ProxyReceiver.this;
            }

            public Exception getException() {
                if (wrappedEvent instanceof StreamingErrorEvent) {
                    return ((StreamingErrorEvent) wrappedEvent).getException();
                }
                return null;
            }

            public long getBytesReceived() {
                return wrappedEvent.getBytesReceived();
            }

            /**
             * Calling this method has no effect. DD files are receive only once
             * anyway.
             */
            public void disposeStreamVariable() {

            }
        }

    }

}
