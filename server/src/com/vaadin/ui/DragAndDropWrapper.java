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
package com.vaadin.ui;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.StreamVariable;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.dd.HorizontalDropLocation;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.shared.ui.draganddropwrapper.DragAndDropWrapperConstants;

@SuppressWarnings("serial")
public class DragAndDropWrapper extends CustomComponent implements DropTarget,
        DragSource, LegacyComponent {

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
                            ((Double) rawVariables.get("fs" + i)).longValue(), // size
                            (String) rawVariables.get("ft" + i)); // mime
                    String id = (String) rawVariables.get("fi" + i);
                    files[i] = file;
                    receivers.put(id, new ProxyReceiver(id, file));
                    markAsDirty(); // paint Receivers
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

    private Map<String, ProxyReceiver> receivers = new HashMap<String, ProxyReceiver>();

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

        /**
         * Uses the component defined in
         * {@link #setDragImageComponent(Component)} as the drag image.
         */
        COMPONENT_OTHER,
    }

    private final Map<String, Object> html5DataFlavors = new LinkedHashMap<String, Object>();
    private DragStartMode dragStartMode = DragStartMode.NONE;
    private Component dragImageComponent = null;

    private Set<String> sentIds = new HashSet<String>();

    private DragAndDropWrapper() {
        super();
    }

    /**
     * Wraps given component in a {@link DragAndDropWrapper}.
     * 
     * @param root
     *            the component to be wrapped
     */
    public DragAndDropWrapper(Component root) {
        this();
        setCompositionRoot(root);
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
        markAsDirty();
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        // TODO Remove once LegacyComponent is no longer implemented
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute(DragAndDropWrapperConstants.DRAG_START_MODE,
                dragStartMode.ordinal());

        if (dragStartMode.equals(DragStartMode.COMPONENT_OTHER)) {
            if (dragImageComponent != null) {
                target.addAttribute(
                        DragAndDropWrapperConstants.DRAG_START_COMPONENT_ATTRIBUTE,
                        dragImageComponent.getConnectorId());
            } else {
                throw new IllegalArgumentException(
                        "DragStartMode.COMPONENT_OTHER set but no component "
                                + "was defined. Please set a component using DragAnd"
                                + "DropWrapper.setDragStartComponent(Component).");
            }
        }
        if (getDropHandler() != null) {
            getDropHandler().getAcceptCriterion().paint(target);
        }
        if (receivers != null && receivers.size() > 0) {
            for (Iterator<Entry<String, ProxyReceiver>> it = receivers
                    .entrySet().iterator(); it.hasNext();) {
                Entry<String, ProxyReceiver> entry = it.next();
                String id = entry.getKey();
                ProxyReceiver proxyReceiver = entry.getValue();
                Html5File html5File = proxyReceiver.file;
                if (html5File.getStreamVariable() != null) {
                    if (!sentIds.contains(id)) {
                        target.addVariable(this, "rec-" + id,
                                new ProxyReceiver(id, html5File));

                        /*
                         * if a new batch is requested to be uploaded before the
                         * last one is done, any remaining ids will be replayed.
                         * We want to avoid a new ProxyReceiver to be made since
                         * it'll get a new URL, so we need to keep extra track
                         * on what has been sent.
                         * 
                         * See #12330.
                         */
                        sentIds.add(id);

                        // these are cleaned from receivers once the upload has
                        // started
                    }
                } else {
                    // instructs the client side not to send the file
                    target.addVariable(this, "rec-" + id, (String) null);
                    // forget the file from subsequent paints
                    it.remove();
                }
            }
        }
        target.addAttribute(DragAndDropWrapperConstants.HTML5_DATA_FLAVORS,
                html5DataFlavors);
    }

    private DropHandler dropHandler;

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    public void setDropHandler(DropHandler dropHandler) {
        this.dropHandler = dropHandler;
        markAsDirty();
    }

    @Override
    public TargetDetails translateDropTargetDetails(
            Map<String, Object> clientVariables) {
        return new WrapperTargetDetails(clientVariables);
    }

    @Override
    public Transferable getTransferable(final Map<String, Object> rawVariables) {
        return new WrapperTransferable(this, rawVariables);
    }

    public void setDragStartMode(DragStartMode dragStartMode) {
        this.dragStartMode = dragStartMode;
        markAsDirty();
    }

    public DragStartMode getDragStartMode() {
        return dragStartMode;
    }

    /**
     * Sets the component that will be used as the drag image. Only used when
     * wrapper is set to {@link DragStartMode#COMPONENT_OTHER}
     * 
     * @param dragImageComponent
     */
    public void setDragImageComponent(Component dragImageComponent) {
        this.dragImageComponent = dragImageComponent;
        markAsDirty();
    }

    /**
     * Gets the component that will be used as the drag image. Only used when
     * wrapper is set to {@link DragStartMode#COMPONENT_OTHER}
     * 
     * @return <code>null</code> if no component is set.
     */
    public Component getDragImageComponent() {
        return dragImageComponent;
    }

    final class ProxyReceiver implements StreamVariable {

        private String id;
        private Html5File file;

        public ProxyReceiver(String id, Html5File file) {
            this.id = id;
            this.file = file;
        }

        private boolean listenProgressOfUploadedFile;

        @Override
        public OutputStream getOutputStream() {
            if (file.getStreamVariable() == null) {
                return null;
            }
            return file.getStreamVariable().getOutputStream();
        }

        @Override
        public boolean listenProgress() {
            return file.getStreamVariable().listenProgress();
        }

        @Override
        public void onProgress(StreamingProgressEvent event) {
            file.getStreamVariable().onProgress(
                    new ReceivingEventWrapper(event));
        }

        @Override
        public void streamingStarted(StreamingStartEvent event) {
            listenProgressOfUploadedFile = file.getStreamVariable() != null;
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable().streamingStarted(
                        new ReceivingEventWrapper(event));
            }
            // no need tell to the client about this receiver on next paint
            receivers.remove(id);
            sentIds.remove(id);
            // let the terminal GC the streamvariable and not to accept other
            // file uploads to this variable
            event.disposeStreamVariable();
        }

        @Override
        public void streamingFinished(StreamingEndEvent event) {
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable().streamingFinished(
                        new ReceivingEventWrapper(event));
            }
        }

        @Override
        public void streamingFailed(final StreamingErrorEvent event) {
            if (listenProgressOfUploadedFile) {
                file.getStreamVariable().streamingFailed(
                        new ReceivingEventWrapper(event));
            }
        }

        @Override
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

            @Override
            public String getMimeType() {
                return file.getType();
            }

            @Override
            public String getFileName() {
                return file.getFileName();
            }

            @Override
            public long getContentLength() {
                return file.getFileSize();
            }

            public StreamVariable getReceiver() {
                return ProxyReceiver.this;
            }

            @Override
            public Exception getException() {
                if (wrappedEvent instanceof StreamingErrorEvent) {
                    return ((StreamingErrorEvent) wrappedEvent).getException();
                }
                return null;
            }

            @Override
            public long getBytesReceived() {
                return wrappedEvent.getBytesReceived();
            }

            /**
             * Calling this method has no effect. DD files are receive only once
             * anyway.
             */
            @Override
            public void disposeStreamVariable() {

            }
        }

    }

}
