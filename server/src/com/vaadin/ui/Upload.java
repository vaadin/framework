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
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import com.vaadin.server.NoInputStreamException;
import com.vaadin.server.NoOutputStreamException;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.StreamVariable.StreamingProgressEvent;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.upload.UploadClientRpc;
import com.vaadin.shared.ui.upload.UploadServerRpc;
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
 * Setting upload component immediate initiates the upload as soon as a file is
 * selected, instead of the common pattern of file selection field and upload
 * button.
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
public class Upload extends AbstractComponent implements Component.Focusable,
        LegacyComponent {

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
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
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
        }

        target.addAttribute("nextid", nextid);

        // Post file to this strean variable
        target.addVariable(this, "action", getStreamVariable());

    }

    /**
     * Interface that must be implemented by the upload receivers to provide the
     * Upload component an output stream to write the uploaded data.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
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
            UPLOAD_FINISHED_METHOD = FinishedListener.class.getDeclaredMethod(
                    "uploadFinished", new Class[] { FinishedEvent.class });
            UPLOAD_FAILED_METHOD = FailedListener.class.getDeclaredMethod(
                    "uploadFailed", new Class[] { FailedEvent.class });
            UPLOAD_STARTED_METHOD = StartedListener.class.getDeclaredMethod(
                    "uploadStarted", new Class[] { StartedEvent.class });
            UPLOAD_SUCCEEDED_METHOD = SucceededListener.class
                    .getDeclaredMethod("uploadSucceeded",
                            new Class[] { SucceededEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
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
         * @param MIMEType
         *            the MIME type of the received file.
         * @param length
         *            the length of the received file.
         */
        public FinishedEvent(Upload source, String filename, String MIMEType,
                long length) {
            super(source);
            type = MIMEType;
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
         * @param MIMEType
         * @param length
         * @param exception
         */
        public FailedEvent(Upload source, String filename, String MIMEType,
                long length, Exception reason) {
            this(source, filename, MIMEType, length);
            this.reason = reason;
        }

        /**
         *
         * @param source
         * @param filename
         * @param MIMEType
         * @param length
         * @param exception
         */
        public FailedEvent(Upload source, String filename, String MIMEType,
                long length) {
            super(source, filename, MIMEType, length);
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
         * @param MIMEType
         * @param length
         */
        public NoOutputStreamEvent(Upload source, String filename,
                String MIMEType, long length) {
            super(source, filename, MIMEType, length);
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
         * @param MIMEType
         * @param length
         */
        public NoInputStreamEvent(Upload source, String filename,
                String MIMEType, long length) {
            super(source, filename, MIMEType, length);
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
         * @param MIMEType
         * @param length
         */
        public SucceededEvent(Upload source, String filename, String MIMEType,
                long length) {
            super(source, filename, MIMEType, length);
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
         * @param MIMEType
         * @param length
         */
        public StartedEvent(Upload source, String filename, String MIMEType,
                long contentLength) {
            super(source);
            this.filename = filename;
            type = MIMEType;
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
    public interface SucceededListener extends Serializable {

        /**
         * Upload successfull..
         *
         * @param event
         *            the Upload successfull event.
         */
        public void uploadSucceeded(SucceededEvent event);
    }

    /**
     * Listener for {@link ChangeEvent}
     *
     * @since 7.2
     */
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
     *            the Listener to be added.
     */
    public void addStartedListener(StartedListener listener) {
        addListener(StartedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addStartedListener(StartedListener)}
     **/
    @Deprecated
    public void addListener(StartedListener listener) {
        addStartedListener(listener);
    }

    /**
     * Removes the upload started event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    public void removeStartedListener(StartedListener listener) {
        removeListener(StartedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeStartedListener(StartedListener)}
     **/
    @Deprecated
    public void removeListener(StartedListener listener) {
        removeStartedListener(listener);
    }

    /**
     * Adds the upload received event listener.
     *
     * @param listener
     *            the Listener to be added.
     */
    public void addFinishedListener(FinishedListener listener) {
        addListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addFinishedListener(FinishedListener)}
     **/
    @Deprecated
    public void addListener(FinishedListener listener) {
        addFinishedListener(listener);
    }

    /**
     * Removes the upload received event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    public void removeFinishedListener(FinishedListener listener) {
        removeListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeFinishedListener(FinishedListener)}
     **/
    @Deprecated
    public void removeListener(FinishedListener listener) {
        removeFinishedListener(listener);
    }

    /**
     * Adds the upload interrupted event listener.
     *
     * @param listener
     *            the Listener to be added.
     */
    public void addFailedListener(FailedListener listener) {
        addListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addFailedListener(FailedListener)}
     **/
    @Deprecated
    public void addListener(FailedListener listener) {
        addFailedListener(listener);
    }

    /**
     * Removes the upload interrupted event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    public void removeFailedListener(FailedListener listener) {
        removeListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeFailedListener(FailedListener)}
     **/
    @Deprecated
    public void removeListener(FailedListener listener) {
        removeFailedListener(listener);
    }

    /**
     * Adds the upload success event listener.
     *
     * @param listener
     *            the Listener to be added.
     */
    public void addSucceededListener(SucceededListener listener) {
        addListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addSucceededListener(SucceededListener)}
     **/
    @Deprecated
    public void addListener(SucceededListener listener) {
        addSucceededListener(listener);
    }

    /**
     * Removes the upload success event listener.
     *
     * @param listener
     *            the Listener to be removed.
     */
    public void removeSucceededListener(SucceededListener listener) {
        removeListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeSucceededListener(SucceededListener)}
     **/
    @Deprecated
    public void removeListener(SucceededListener listener) {
        removeSucceededListener(listener);
    }

    /**
     * Adds the upload progress event listener.
     *
     * @param listener
     *            the progress listener to be added
     */
    public void addProgressListener(ProgressListener listener) {
        if (progressListeners == null) {
            progressListeners = new LinkedHashSet<ProgressListener>();
        }
        progressListeners.add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addProgressListener(ProgressListener)}
     **/
    @Deprecated
    public void addListener(ProgressListener listener) {
        addProgressListener(listener);
    }

    /**
     * Removes the upload progress event listener.
     *
     * @param listener
     *            the progress listener to be removed
     */
    public void removeProgressListener(ProgressListener listener) {
        if (progressListeners != null) {
            progressListeners.remove(listener);
        }
    }

    /**
     * Adds a filename change event listener
     *
     * @param listener
     *            the Listener to add
     */
    public void addChangeListener(ChangeListener listener) {
        super.addListener(EventId.CHANGE, ChangeEvent.class, listener,
                ChangeListener.FILENAME_CHANGED);
    }

    /**
     * Removes a filename change event listener
     *
     * @param listener
     *            the listener to be removed
     */
    public void removeChangeListener(ChangeListener listener) {
        super.removeListener(EventId.CHANGE, ChangeEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeProgressListener(ProgressListener)}
     **/
    @Deprecated
    public void removeListener(ProgressListener listener) {
        removeProgressListener(listener);
    }

    /**
     * Emit upload received event.
     *
     * @param filename
     * @param MIMEType
     * @param length
     */
    protected void fireStarted(String filename, String MIMEType) {
        fireEvent(new Upload.StartedEvent(this, filename, MIMEType,
                contentLength));
    }

    /**
     * Emits the upload failed event.
     *
     * @param filename
     * @param MIMEType
     * @param length
     */
    protected void fireUploadInterrupted(String filename, String MIMEType,
            long length) {
        fireEvent(new Upload.FailedEvent(this, filename, MIMEType, length));
    }

    protected void fireNoInputStream(String filename, String MIMEType,
            long length) {
        fireEvent(new Upload.NoInputStreamEvent(this, filename, MIMEType,
                length));
    }

    protected void fireNoOutputStream(String filename, String MIMEType,
            long length) {
        fireEvent(new Upload.NoOutputStreamEvent(this, filename, MIMEType,
                length));
    }

    protected void fireUploadInterrupted(String filename, String MIMEType,
            long length, Exception e) {
        fireEvent(new Upload.FailedEvent(this, filename, MIMEType, length, e));
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
            for (Iterator<ProgressListener> it = progressListeners.iterator(); it
                    .hasNext();) {
                ProgressListener l = it.next();
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
     * done by the receiving tread so this method will return immediately and
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
    public interface ProgressListener extends Serializable {
        /**
         * Updates progress to listener
         *
         * @param readBytes
         *            bytes transferred
         * @param contentLength
         *            total size of file currently being uploaded, -1 if unknown
         */
        public void updateProgress(long readBytes, long contentLength);
    }

    /**
     * @return String to be rendered into button that fires uploading
     */
    public String getButtonCaption() {
        return buttonCaption;
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
     * {@link #setImmediate(boolean)}, the file choose (html input with type
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
                    return (progressListeners != null && !progressListeners
                            .isEmpty());
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
                return Collections.EMPTY_LIST;
            } else {
                return Collections.unmodifiableCollection(progressListeners);
            }

        }
        return super.getListeners(eventType);
    }
}
