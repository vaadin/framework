/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper;
import com.vaadin.terminal.gwt.client.ui.dd.HorizontalDropLocation;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Html5File.ProxyReceiver;

@SuppressWarnings("serial")
@ClientWidget(VDragAndDropWrapper.class)
public class DragAndDropWrapper extends CustomComponent implements DropTarget,
        DragSource, ReceiverOwner {

    public class WrapperTransferable extends TransferableImpl {

        /**
         * @deprecated this class is made top level in recent version. Use
         *             com.vaadin.ui.Html5File instead
         */
        @Deprecated
        private class Html5File extends com.vaadin.ui.Html5File {

            Html5File(String name, long size, String mimeType) {
                super(name, size, mimeType);
            }

        }

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
         * transferrable is not a component (most likely an html5 drag).
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

        public VerticalDropLocation verticalDropLocation() {
            return VerticalDropLocation
                    .valueOf((String) getData("verticalLocation"));
        }

        public HorizontalDropLocation horizontalDropLocation() {
            return HorizontalDropLocation
                    .valueOf((String) getData("horizontalLocation"));
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
        WRAPPER
    }

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

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("dragStartMode", dragStartMode.ordinal());
        if (getDropHandler() != null) {
            getDropHandler().getAcceptCriterion().paint(target);
        }
        if (receivers != null && receivers.size() > 0) {
            for (String id : receivers.keySet()) {
                Html5File html5File = receivers.get(id);
                if (html5File.getReceiver() != null) {
                    target.addVariable(this, "rec-" + id,
                            html5File.getProxyReceiver());
                } else {
                    // instructs the client side not to send the file
                    target.addVariable(this, "rec-" + id, (String) null);
                }
            }
        }
    }

    private DropHandler dropHandler;
    private Html5File currentlyUploadedFile;
    private boolean listenProgressOfUploadedFile;

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

    /*
     * Single controller is enough for atm as files are transferred in serial.
     * If parallel transfer is needed, this logic needs to go to Html5File
     */
    private ReceivingController controller = new ReceivingController() {
        /*
         * With XHR2 file posts we can't provide as much information from the
         * terminal as with multipart request. This helper class wraps the
         * terminal event and provides the lacking information from the
         * Html5File.
         */
        class ReceivingEventWrapper implements ReceivingFailedEvent,
                ReceivingEndedEvent, ReceivingStartedEvent,
                ReceivingProgressedEvent {
            private ReceivingEvent wrappedEvent;

            ReceivingEventWrapper(ReceivingEvent e) {
                wrappedEvent = e;
            }

            public String getMimeType() {
                return currentlyUploadedFile.getType();
            }

            public String getFileName() {
                return currentlyUploadedFile.getFileName();
            }

            public long getContentLength() {
                return currentlyUploadedFile.getFileSize();
            }

            public Receiver getReceiver() {
                return currentlyUploadedFile.getReceiver();
            }

            public Exception getException() {
                if (wrappedEvent instanceof ReceivingFailedEvent) {
                    return ((ReceivingFailedEvent) wrappedEvent).getException();
                }
                return null;
            }

            public long getBytesReceived() {
                return wrappedEvent.getBytesReceived();
            }
        }

        public boolean listenProgress() {
            return listenProgressOfUploadedFile;
        }

        public void onProgress(ReceivingProgressedEvent event) {
            currentlyUploadedFile.getUploadListener().onProgress(
                    new ReceivingEventWrapper(event));
        }

        public void uploadStarted(ReceivingStartedEvent event) {
            currentlyUploadedFile = ((ProxyReceiver) event.getReceiver())
                    .getFile();
            listenProgressOfUploadedFile = currentlyUploadedFile
                    .getUploadListener() != null;
            if (listenProgressOfUploadedFile) {
                currentlyUploadedFile.getUploadListener().uploadStarted(
                        new ReceivingEventWrapper(event));
            }
        }

        public void uploadFinished(ReceivingEndedEvent event) {
            if (listenProgressOfUploadedFile) {
                currentlyUploadedFile.getUploadListener().uploadFinished(
                        new ReceivingEventWrapper(event));
            }
        }

        public void uploadFailed(final ReceivingFailedEvent event) {
            if (listenProgressOfUploadedFile) {
                currentlyUploadedFile.getUploadListener().uploadFailed(
                        new ReceivingEventWrapper(event));
            }
        }

        public boolean isInterrupted() {
            return currentlyUploadedFile.isInterrupted();
        }

    };

    public ReceivingController getReceivingController(Receiver receiver) {
        return controller;
    }

}
