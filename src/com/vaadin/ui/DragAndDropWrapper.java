/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
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
import com.vaadin.terminal.UploadStream;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VDragAndDropWrapper;
import com.vaadin.terminal.gwt.client.ui.dd.HorizontalDropLocation;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable.Html5File;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.UploadException;

@SuppressWarnings("serial")
@ClientWidget(VDragAndDropWrapper.class)
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
                    Html5File file = new Html5File();
                    String id = (String) rawVariables.get("fi" + i);
                    file.name = (String) rawVariables.get("fn" + i);
                    file.size = (Integer) rawVariables.get("fs" + i);
                    file.type = (String) rawVariables.get("ft" + i);
                    files[i] = file;
                    receivers.put(id, file);
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

        /**
         * {@link DragAndDropWrapper} can receive also files from client
         * computer if appropriate HTML 5 features are supported on client side.
         * This class wraps information about dragged file on server side.
         */
        public class Html5File implements Serializable {

            public String name;
            private int size;
            private Receiver receiver;
            private String type;

            public String getFileName() {
                return name;
            }

            public int getFileSize() {
                return size;
            }

            public String getType() {
                return type;
            }

            /**
             * Sets the {@link Receiver} that into which the file contents will
             * be written. Usage of Reveiver is similar to {@link Upload}
             * component.
             * 
             * <p>
             * <em>Note!</em> receiving file contents is experimental feature
             * depending on HTML 5 API's. It is supported only by Firefox 3.6 at
             * this time.
             * 
             * @param receiver
             *            the callback that returns stream where the
             *            implementation writes the file contents as it arrives.
             */
            public void setReceiver(Receiver receiver) {
                this.receiver = receiver;
            }

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

    /**
     * This method should only be used by Vaadin terminal implementation. This
     * is not end user api.
     * 
     * TODO should fire progress events + end/succes events like upload. Not
     * critical until we have a wider browser support for HTML5 File API
     * 
     * @param upstream
     * @param fileId
     * @throws UploadException
     */
    public void receiveFile(UploadStream upstream, String fileId)
            throws UploadException {
        Html5File file = receivers.get(fileId);
        if (file != null && file.receiver != null) {
            OutputStream receiveUpload = file.receiver.receiveUpload(
                    file.getFileName(), "TODO");

            InputStream stream = upstream.getStream();
            byte[] buf = new byte[AbstractApplicationServlet.MAX_BUFFER_SIZE];
            int bytesRead;
            try {
                while ((bytesRead = stream.read(buf)) != -1) {
                    receiveUpload.write(buf, 0, bytesRead);
                }
                receiveUpload.close();
            } catch (IOException e) {
                throw new UploadException(e);
            }
            // clean up the reference when file is downloaded
            receivers.remove(fileId);
        }

    }
}
