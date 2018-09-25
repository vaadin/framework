/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

import com.vaadin.server.NoInputStreamException;
import com.vaadin.server.NoOutputStreamException;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.StreamVariable.StreamingProgressEvent;
import com.vaadin.shared.EventId;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.upload.UploadClientRpc;
import com.vaadin.shared.ui.upload.UploadServerRpc;
import com.vaadin.shared.ui.upload.UploadState;
import com.vaadin.util.ReflectTools;

/**
 * Component for uploading files from client to server.
 *
 * <p>
 * The visible component consists of a file name input box and a browse button
 * and an upload submit button to start uploading.
 *
 * <p>
 * The Upload component needs a java.io.OutputStream to write the uploaded data.
 * You need to implement the Upload.Receiver interface and return the output
 * stream in the receiveUpload() method.
 *
 * <p>
 * You can get an event regarding starting (StartedEvent), progress
 * (ProgressEvent), and finishing (FinishedEvent) of upload by implementing
 * StartedListener, ProgressListener, and FinishedListener, respectively. The
 * FinishedListener is called for both failed and succeeded uploads. If you wish
 * to separate between these two cases, you can use SucceededListener
 * (SucceededEvenet) and FailedListener (FailedEvent).
 *
 * <p>
 * The upload component does not itself show upload progress, but you can use
 * the ProgressIndicator for providing progress feedback by implementing
 * ProgressListener and updating the indicator in updateProgress().
 *
 * <p>
 * Setting upload component immediate with {@link #setImmediateMode(boolean)}
 * initiates the upload as soon as a file is selected, instead of the common
 * pattern of file selection field and upload button.
 *
 * <p>
 * Note! Because of browser dependent implementations of <input type="file">
 * element, setting size for Upload component is not supported. For some
 * browsers setting size may work to some extend.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Upload extends AbstractComponent
        implements Component.Focusable, LegacyComponent {

    /**
     * Should the field be focused on next repaint?
     */
    private final boolean focus = false;

    /**
     * The tab order number of this field.
     */
    private int tabIndex = 0;

    /**
     * The output of the upload is redirected to this receiver.
     */
    private Receiver receiver;

    private boolean isUploading;

    private long contentLength = -1;

    private int totalBytes;

    private String buttonCaption = "Upload";

    private String buttonStyleName;

    /**
     * ProgressListeners to which information about progress is sent during
     * upload
     */
    private LinkedHashSet<ProgressListener> progressListeners;

    private boolean interrupted = false;

    private boolean notStarted;

    private int nextid;

    /**
     * Creates a new instance of Upload.
     *
     * The receiver must be set before performing an upload.
     */
    public Upload() {
        registerRpc(new UploadServerRpc() {
            @Override
            public void change(String filename) {
                fireEvent(new ChangeEvent(Upload.this, filename));
            }

            @Override
            public void poll() {
                // Nothing to do, called only to visit the server
            }
        });
    }

    public Upload(String caption, Receiver uploadReceiver) {
        this();
        setCaption(caption);
        receiver = uploadReceiver;
    }

    /**
     * Invoked when the value of a variable has changed.
     *
     * @see com.vaadin.ui.LegacyComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey("pollForStart")) {
            int id = (Integer) variables.get("pollForStart");
            if (!isUploading && id == nextid) {
                notStarted = true;
                markAsDirty();
            } else {
            }
        }
    }

    /**
     * Paints the content of this component.
     *
     * @param target
     *            Target to paint the content on.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (notStarted) {
            target.addAttribute("notStarted", true);
            notStarted = false;
            return;
        }
        // The field should be focused
        if (focus) {
            target.addAttribute("focus", true);
        }

        // The tab ordering number
        if (tabIndex >= 0) {
            target.addAttribute("tabindex", tabIndex);
        }

        target.addAttribute("state", isUploading);

        if (buttonCaption != null) {
            target.addAttribute("buttoncaption", buttonCaption);
            if (buttonStyleName != null) {
                target.addAttribute("buttonstylename", buttonStyleName);
            }
        }

        target.addAttribute("nextid", nextid);

        // Post file to this stream variable
        target.addVariable(this, "action", getStreamVariable());

    }

    /**
     * Interface that must be implemented by the upload receivers to provide the
     * Upload component an output stream to write the uploaded data.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @FunctionalInterface
    public interface Receiver extends Serializable {

        /**
         * Invoked when a new upload arrives.
         *
         * @param filename
         *            the desired filename of the upload, usually as specified
         *            by the client.
         * @param mimeType
         *            the MIME type of the uploaded file.
         * @return Stream to which the uploaded file should be written.
         */
        public OutputStream receiveUpload(String filename, String mimeType);
    }

    /* Upload events */

    private static final Method UPLOAD_FINISHED_METHOD;

    private static final Method UPLOAD_FAILED_METHOD;

    private static final Method UPLOAD_SUCCEEDED_METHOD;

    private static final Method UPLOAD_STARTED_METHOD;

    static {
        try {
            UPLOAD_FINISHED_METHOD = FinishedListener.class
                    .getDeclaredMethod("uploadFinished", FinishedEvent.class);
            UPLOAD_FAILED_METHOD = FailedListener.class
                    .getDeclaredMethod("uploadFailed", FailedEvent.class);
            UPLOAD_STARTED_METHOD = StartedListener.class
                    .getDeclaredMethod("uploadStarted", StartedEvent.class);
            UPLOAD_SUCCEEDED_METHOD = SucceededListener.class
                    .getDeclaredMethod("uploadSucceeded", SucceededEvent.class);
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(
                    "Internal error finding methods in Upload");
        }
    }

    /**
     * Upload.FinishedEvent is sent when the upload receives a file, regardless
     * of whether the reception was successful or failed. If you wish to
     * distinguish between the two cases, use either SucceededEvent or
     * FailedEvent, which are both subclasses of the FinishedEvent.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public static class FinishedEvent extends Component.Event {

        /**
         * Length of the received file.
         */
        private final long length;

        /**
         * MIME type of the received file.
         */
        private final String type;

        /**
         * Received file name.
         */
        private final String filename;

        /**
         *
         * @param source
         *            the source of the file.
         * @param filename
         *            the received file name.
         * @param mimeType
         *            the MIME type of the received file.
         * @param length
         *            the length of the received file.
         */
        public FinishedEvent(Upload source, String filename, String mimeType,
                long length) {
            super(source);
            type = mimeType;
            this.filename = filename;
            this.length = length;
        }

        /**
         * Uploads where the event occurred.
         *
         * @return the Source of the event.
         */
        public Upload getUpload() {
            return (Upload) getSource();
        }

        /**
         * Gets the file name.
         *
         * @return the filename.
         */
        public String getFilename() {
            return filename;
        }

        /**
         * Gets the MIME Type of the file.
         *
         * @return the MIME type.
         */
        public String getMIMEType() {
            return type;
        }

        /**
         * Gets the length of the file.
         *
         * @return the length.
         */
        public long getLength() {
            return length;
        }

    }

    /**
     * Upload.FailedEvent event is sent when the upload is received, but the
     * reception is interrupted for some reason.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public static class FailedEvent extends FinishedEvent {

        private Exception reason = null;

        /**
         *
         * @param source
         * @param filename
         * @param mimeType
         * @param length
         * @param reason
         */
        public FailedEvent(Upload source, String filename, String mimeType,
                long length, Exception reason) {
            this(source, filename, mimeType, length);
            this.reason = reason;
        }

        /**
         *
         * @param source
         * @param filename
         * @param mimeType
         * @param length
         */
        public FailedEvent(Upload source, String filename, String mimeType,
                long length) {
            super(source, filename, mimeType, length);
        }

        /**
         * Gets the exception that caused the failure.
         *
         * @return the exception that caused the failure, null if n/a
         */
        public Exception getReason() {
            return reason;
        }

    }

    /**
     * FailedEvent that indicates that an output stream could not be obtained.
     */
    public static class NoOutputStreamEvent extends FailedEvent {

        /**
         *
         * @param source
         * @param filename
         * @param mimeType
         * @param length
         */
        public NoOutputStreamEvent(Upload source, String filename,
                String mimeType, long length) {
            super(source, filename, mimeType, length);
        }
    }

    /**
     * FailedEvent that indicates that an input stream could not be obtained.
     */
    public static class NoInputStreamEvent extends FailedEvent {

        /**
         *
         * @param source
         * @param filename
         * @param mimeType
         * @param length
         */
        public NoInputStreamEvent(Upload source, String filename,
                String mimeType, long length) {
            super(source, filename, mimeType, length);
        }
    }

    /**
     * Upload.SucceededEvent event is sent when the upload is received
     * successfully.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public static class SucceededEvent extends FinishedEvent {

        /**
         *
         * @param source
         * @param filename
         * @param mimeType
         * @param length
         */
        public SucceededEvent(Upload source, String filename, String mimeType,
                long length) {
            super(source, filename, mimeType, length);
        }
    }

    /**
     * Upload.StartedEvent event is sent when the upload is started to received.
     *
     * @author Vaadin Ltd.
     * @since 5.0
     */
    public static class StartedEvent extends Component.Event {

        private final String filename;
        private final String type;
        /**
         * Length of the received file.
         */
        private final long length;

        /**
         *
         * @param source
         * @param filename
         * @param mimeType
         * @param contentLength
         */
        public StartedEvent(Upload source, String filename, String mimeType,
                long contentLength) {
            super(source);
            this.filename = filename;
            type = mimeType;
            length = contentLength;
        }

        /**
         * Uploads where the event occurred.
         *
         * @return the Source of the event.
         */
        public Upload getUpload() {
            return (Upload) getSource();
        }

        /**
         * Gets the file name.
         *
         * @return the filename.
         */
        public String getFilename() {
            return filename;
        }

        /**
         * Gets the MIME Type of the file.
         *
         * @return the MIME type.
         */
        public String getMIMEType() {
            return type;
        }

        /**
         * @return the length of the file that is being uploaded
         */
        public long getContentLength() {
            return length;
        }

    }

    /**
     * Upload.ChangeEvent event is sent when the value (filename) of the upload
     * changes.
     *
     * @since 7.2
     */
    public static class ChangeEvent extends Component.Event {

        private final String filename;

        public ChangeEvent(Upload source, String filename) {
            super(source);
            this.filename = filename;
        }

        /**
         * Uploads where the event occurred.
         *
         * @return the Source of the event.
         */
        @Override
        public Upload getSource() {
            return (Upload) super.getSource();
        }

        /**
         * Gets the file name.
         *
         * @return the filename.
         */
        public String getFilename() {
            return filename;
        }

    }

    /**
     * Receives the events when the upload starts.
     *
     * @author Vaadin Ltd.
     * @since 5.0
     */
    @FunctionalInterface
    public interface StartedListener extends Serializable {

        /**
         * Upload has started.
         *
         * @param event
         *            the Upload started event.
         */
        public void uploadStarted(StartedEvent event);
    }

    /**
     * Receives the events when the uploads are ready.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @FunctionalInterface
    public interface FinishedListener extends Serializable {

        /**
         * Upload has finished.
         *
         * @param event
         *            the Upload finished event.
         */
        public void uploadFinished(FinishedEvent event);
    }

    /**
     * Receives events when the uploads are finished, but unsuccessful.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @FunctionalInterface
    public interface FailedListener extends Serializable {

        /**
         * Upload has finished unsuccessfully.
         *
         * @param event
         *            the Upload failed event.
         */
        public void uploadFailed(FailedEvent event);
    }

    /**
     * Receives events when the uploads are successfully finished.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @FunctionalInterface
    public interface SucceededListener extends Serializable {

        /**
         * Upload successful.
         *
         * @param event
         *            the Upload successful event.
         */
        public void uploadSucceeded(SucceededEvent event);
    }

    /**
     * Listener for {@link ChangeEvent}.
     *
     * @since 7.2
     */
    @FunctionalInterface
    public interface ChangeListener extends Serializable {

        Method FILENAME_CHANGED = ReflectTools.findMethod(ChangeListener.class,
                "filenameChanged", ChangeEvent.class);

        /**
         * A file has been selected but upload has not yet started.
         *
         * @param event
         *            the change event
         */
        public void filenameChanged(ChangeEvent event);
    }

    /**
     * Adds the upload started event listener.
     *
     * @param listener
     *            the Listener to be added, not null
     * @since 8.0
     */
    public Registration addStartedListener(StartedListener listener) {
        return addListener(StartedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * Removes the upload started event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    @Deprecated
    public void removeStartedListener(StartedListener listener) {
        removeListener(StartedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * Adds the upload received event listener.
     *
     * @param listener
     *            the Listener to be added, not null
     * @since 8.0
     */
    public Registration addFinishedListener(FinishedListener listener) {
        return addListener(FinishedEvent.class, listener,
                UPLOAD_FINISHED_METHOD);
    }

    /**
     * Removes the upload received event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    @Deprecated
    public void removeFinishedListener(FinishedListener listener) {
        removeListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
    }

    /**
     * Adds the upload interrupted event listener.
     *
     * @param listener
     *            the Listener to be added, not null
     * @since 8.0
     */
    public Registration addFailedListener(FailedListener listener) {
        return addListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * Removes the upload interrupted event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    @Deprecated
    public void removeFailedListener(FailedListener listener) {
        removeListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * Adds the upload success event listener.
     *
     * @param listener
     *            the Listener to be added, not null
     * @since 8.0
     */
    public Registration addSucceededListener(SucceededListener listener) {
        return addListener(SucceededEvent.class, listener,
                UPLOAD_SUCCEEDED_METHOD);
    }

    /**
     * Removes the upload success event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    @Deprecated
    public void removeSucceededListener(SucceededListener listener) {
        removeListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
    }

    /**
     * Adds the upload progress event listener.
     *
     * @param listener
     *            the progress listener to be added
     * @since 8.0
     */
    public Registration addProgressListener(ProgressListener listener) {
        Objects.requireNonNull(listener, "Listener must not be null.");
        if (progressListeners == null) {
            progressListeners = new LinkedHashSet<>();
        }
        progressListeners.add(listener);
        return () -> {
            if (progressListeners != null) {
                progressListeners.remove(listener);
            }
        };
    }

    /**
     * Removes the upload progress event listener.
     *
     * @param listener
     *            the progress listener to be removed
     */
    @Deprecated
    public void removeProgressListener(ProgressListener listener) {
        if (progressListeners != null) {
            progressListeners.remove(listener);
        }
    }

    /**
     * Adds a filename change event listener.
     *
     * @param listener
     *            the Listener to add, not null
     * @since 8.0
     */
    public Registration addChangeListener(ChangeListener listener) {
        return addListener(EventId.CHANGE, ChangeEvent.class, listener,
                ChangeListener.FILENAME_CHANGED);
    }

    /**
     * Removes a filename change event listener.
     *
     * @param listener
     *            the listener to be removed
     */
    @Deprecated
    public void removeChangeListener(ChangeListener listener) {
        super.removeListener(EventId.CHANGE, ChangeEvent.class, listener);
    }

    /**
     * Emit upload received event.
     *
     * @param filename
     * @param mimeType
     */
    protected void fireStarted(String filename, String mimeType) {
        fireEvent(new Upload.StartedEvent(this, filename, mimeType,
                contentLength));
    }

    /**
     * Emits the upload failed event.
     *
     * @param filename
     * @param mimeType
     * @param length
     */
    protected void fireUploadInterrupted(String filename, String mimeType,
            long length) {
        fireEvent(new Upload.FailedEvent(this, filename, mimeType, length));
    }

    protected void fireNoInputStream(String filename, String mimeType,
            long length) {
        fireEvent(new Upload.NoInputStreamEvent(this, filename, mimeType,
                length));
    }

    protected void fireNoOutputStream(String filename, String mimeType,
            long length) {
        fireEvent(new Upload.NoOutputStreamEvent(this, filename, mimeType,
                length));
    }

    protected void fireUploadInterrupted(String filename, String mimeType,
            long length, Exception e) {
        fireEvent(new Upload.FailedEvent(this, filename, mimeType, length, e));
    }

    /**
     * Emits the upload success event.
     *
     * @param filename
     * @param MIMEType
     * @param length
     *
     */
    protected void fireUploadSuccess(String filename, String MIMEType,
            long length) {
        fireEvent(new Upload.SucceededEvent(this, filename, MIMEType, length));
    }

    /**
     * Emits the progress event.
     *
     * @param totalBytes
     *            bytes received so far
     * @param contentLength
     *            actual size of the file being uploaded, if known
     *
     */
    protected void fireUpdateProgress(long totalBytes, long contentLength) {
        // this is implemented differently than other listeners to maintain
        // backwards compatibility
        if (progressListeners != null) {
            for (ProgressListener l : progressListeners) {
                l.updateProgress(totalBytes, contentLength);
            }
        }
    }

    /**
     * Returns the current receiver.
     *
     * @return the StreamVariable.
     */
    public Receiver getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver.
     *
     * @param receiver
     *            the receiver to set.
     */
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focus() {
        super.focus();
    }

    /**
     * Gets the Tabulator index of this Focusable component.
     *
     * @see com.vaadin.ui.Component.Focusable#getTabIndex()
     */
    @Override
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Sets the Tabulator index of this Focusable component.
     *
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * Go into upload state. This is to prevent double uploading on same
     * component.
     *
     * Warning: this is an internal method used by the framework and should not
     * be used by user of the Upload component. Using it results in the Upload
     * component going in wrong state and not working. It is currently public
     * because it is used by another class.
     */
    public void startUpload() {
        if (isUploading) {
            throw new IllegalStateException("uploading already started");
        }
        isUploading = true;
        nextid++;
    }

    /**
     * Interrupts the upload currently being received. The interruption will be
     * done by the receiving thread so this method will return immediately and
     * the actual interrupt will happen a bit later.
     */
    public void interruptUpload() {
        if (isUploading) {
            interrupted = true;
        }
    }

    /**
     * Go into state where new uploading can begin.
     *
     * Warning: this is an internal method used by the framework and should not
     * be used by user of the Upload component.
     */
    private void endUpload() {
        isUploading = false;
        contentLength = -1;
        interrupted = false;
        markAsDirty();
    }

    public boolean isUploading() {
        return isUploading;
    }

    /**
     * Gets read bytes of the file currently being uploaded.
     *
     * @return bytes
     */
    public long getBytesRead() {
        return totalBytes;
    }

    /**
     * Returns size of file currently being uploaded. Value sane only during
     * upload.
     *
     * @return size in bytes
     */
    public long getUploadSize() {
        return contentLength;
    }

    /**
     * ProgressListener receives events to track progress of upload.
     */
    @FunctionalInterface
    public interface ProgressListener extends Serializable {
        /**
         * Updates progress to listener.
         *
         * @param readBytes
         *            bytes transferred
         * @param contentLength
         *            total size of file currently being uploaded, -1 if unknown
         */
        public void updateProgress(long readBytes, long contentLength);
    }

    /**
     * Returns the string rendered into button that fires uploading.
     *
     * @return String to be rendered into button that fires uploading
     */
    public String getButtonCaption() {
        return buttonCaption;
    }

    /**
     * Returns the stylename rendered into button that fires uploading.
     *
     * @return Stylename to be rendered into button that fires uploading
     * @since 8.2
     */
    public String getButtonStyleName() {
        return buttonStyleName;
    }

    /**
     * In addition to the actual file chooser, upload components have button
     * that starts actual upload progress. This method is used to set text in
     * that button.
     * <p>
     * In case the button text is set to null, the button is hidden. In this
     * case developer must explicitly initiate the upload process with
     * {@link #submitUpload()}.
     * <p>
     * In case the Upload is used in immediate mode using
     * {@link #setImmediateMode(boolean)}, the file choose (html input with type
     * "file") is hidden and only the button with this text is shown.
     * <p>
     *
     * <p>
     * <strong>Note</strong> the string given is set as is to the button. HTML
     * formatting is not stripped. Be sure to properly validate your value
     * according to your needs.
     *
     * @param buttonCaption
     *            text for upload components button.
     */
    public void setButtonCaption(String buttonCaption) {
        this.buttonCaption = buttonCaption;
        markAsDirty();
    }

    /**
     * In addition to the actual file chooser, upload components have button
     * that starts actual upload progress. This method is used to set a
     * stylename to that button.
     *
     * @param buttonStyleName
     *            styleName for upload components button.
     * @see #setButtonCaption(String) about when the button is shown / hidden.
     * @since 8.2
     */
    public void setButtonStyleName(String buttonStyleName) {
        this.buttonStyleName = buttonStyleName;
        markAsDirty();
    }

    /**
     * Forces the upload the send selected file to the server.
     * <p>
     * In case developer wants to use this feature, he/she will most probably
     * want to hide the uploads internal submit button by setting its caption to
     * null with {@link #setButtonCaption(String)} method.
     * <p>
     * Note, that the upload runs asynchronous. Developer should use normal
     * upload listeners to trac the process of upload. If the field is empty
     * uploaded the file name will be empty string and file length 0 in the
     * upload finished event.
     * <p>
     * Also note, that the developer should not remove or modify the upload in
     * the same user transaction where the upload submit is requested. The
     * upload may safely be hidden or removed once the upload started event is
     * fired.
     */
    public void submitUpload() {
        markAsDirty();
        getRpcProxy(UploadClientRpc.class).submitUpload();
    }

    @Override
    public void markAsDirty() {
        super.markAsDirty();
    }

    /*
     * Handle to terminal via Upload monitors and controls the upload during it
     * is being streamed.
     */
    private com.vaadin.server.StreamVariable streamVariable;

    protected com.vaadin.server.StreamVariable getStreamVariable() {
        if (streamVariable == null) {
            streamVariable = new com.vaadin.server.StreamVariable() {
                private StreamingStartEvent lastStartedEvent;

                @Override
                public boolean listenProgress() {
                    return progressListeners != null
                            && !progressListeners.isEmpty();
                }

                @Override
                public void onProgress(StreamingProgressEvent event) {
                    fireUpdateProgress(event.getBytesReceived(),
                            event.getContentLength());
                }

                @Override
                public boolean isInterrupted() {
                    return interrupted;
                }

                @Override
                public OutputStream getOutputStream() {
                    if (getReceiver() == null) {
                        throw new IllegalStateException(
                                "Upload cannot be performed without a receiver set");
                    }
                    OutputStream receiveUpload = getReceiver().receiveUpload(
                            lastStartedEvent.getFileName(),
                            lastStartedEvent.getMimeType());
                    lastStartedEvent = null;
                    return receiveUpload;
                }

                @Override
                public void streamingStarted(StreamingStartEvent event) {
                    startUpload();
                    contentLength = event.getContentLength();
                    fireStarted(event.getFileName(), event.getMimeType());
                    lastStartedEvent = event;
                }

                @Override
                public void streamingFinished(StreamingEndEvent event) {
                    fireUploadSuccess(event.getFileName(), event.getMimeType(),
                            event.getContentLength());
                    endUpload();
                    if (lastStartedEvent != null)
                        lastStartedEvent.disposeStreamVariable();
                }

                @Override
                public void streamingFailed(StreamingErrorEvent event) {
                    try {
                        Exception exception = event.getException();
                        if (exception instanceof NoInputStreamException) {
                            fireNoInputStream(event.getFileName(),
                                    event.getMimeType(), 0);
                        } else if (exception instanceof NoOutputStreamException) {
                            fireNoOutputStream(event.getFileName(),
                                    event.getMimeType(), 0);
                        } else {
                            fireUploadInterrupted(event.getFileName(),
                                    event.getMimeType(), 0, exception);
                        }
                    } finally {
                        endUpload();
                        if (lastStartedEvent != null)
                            lastStartedEvent.disposeStreamVariable();
                    }
                }
            };
        }
        return streamVariable;
    }

    @Override
    public java.util.Collection<?> getListeners(java.lang.Class<?> eventType) {
        if (StreamingProgressEvent.class.isAssignableFrom(eventType)) {
            if (progressListeners == null) {
                return Collections.emptyList();
            } else {
                return Collections.unmodifiableCollection(progressListeners);
            }

        }
        return super.getListeners(eventType);
    }

    /**
     * Sets the immediate mode of the upload.
     * <p>
     * If the upload is in immediate mode, the file upload is started
     * immediately after the user has selected the file.
     * <p>
     * If the upload is not in immediate mode, after selecting the file the user
     * must click another button to start the upload.
     * <p>
     * The default mode of an Upload component is immediate.
     *
     * @param immediateMode
     *            {@code true} for immediate mode, {@code false} for not
     * @since 8.0
     */
    public void setImmediateMode(boolean immediateMode) {
        getState().immediateMode = immediateMode;
    }

    /**
     * Returns the immediate mode of the upload.
     * <p>
     * The default mode of an Upload component is immediate.
     *
     * @return {@code true} if the upload is in immediate mode, {@code false} if
     *         the upload is not in immediate mode
     * @see #setImmediateMode(boolean)
     * @since 8.0
     */
    public boolean isImmediateMode() {
        return getState(false).immediateMode;
    }

    @Override
    protected UploadState getState() {
        return (UploadState) super.getState();
    }

    @Override
    protected UploadState getState(boolean markAsDirty) {
        return (UploadState) super.getState(markAsDirty);
    }

    /**
     * Returns the component's list of accepted content-types. According to RFC
     * 1867, if the attribute is present, the browser might constrain the file
     * patterns prompted for to match those with the corresponding appropriate
     * file extensions for the platform.
     *
     * @return comma-separated list of desired mime types to be uploaded
     * @see #setAcceptMimeTypes
     * @since 8.5
     */
    public String getAcceptMimeTypes() {
        return getState(false).acceptMimeTypes;
    }

    /**
     * Sets the component's list of accepted content-types. According to RFC
     * 1867, if the attribute is present, the browser might constrain the file
     * patterns prompted for to match those with the corresponding appropriate
     * file extensions for the platform. Good examples are: {@code image/*} or
     * {@code image/png,text/plain}
     *
     * @param acceptMimeTypes
     *            comma-separated list of desired mime types to be uploaded
     * @see #getAcceptMimeTypes
     * @since 8.5
     */
    public void setAcceptMimeTypes(String acceptMimeTypes) {
        getState().acceptMimeTypes = acceptMimeTypes;
    }

}
