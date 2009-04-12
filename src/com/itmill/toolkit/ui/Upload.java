/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.UploadStream;

/**
 * Component for uploading files from client to server.
 * 
 * The visible component consists of a file name input box and a browse button
 * and an upload submit button to start uploading.
 * 
 * The Upload component needs a java.io.OutputStream to write the uploaded data.
 * You need to implement the Upload.Receiver interface and return the output
 * stream in the receiveUpload() method.
 * 
 * You can get an event regarding starting (StartedEvent), progress
 * (ProgressEvent), and finishing (FinishedEvent) of upload by implementing
 * StartedListener, ProgressListener, and FinishedListener, respectively. The
 * FinishedListener is called for both failed and succeeded uploads. If you wish
 * to separate between these two cases, you can use SucceededListener
 * (SucceededEvenet) and FailedListener (FailedEvent).
 * 
 * The upload component does not itself show upload progress, but you can use
 * the ProgressIndicator for providing progress feedback by implementing
 * ProgressListener and updating the indicator in updateProgress().
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Upload extends AbstractComponent implements Component.Focusable {

    private boolean delayedFocus;

    /**
     * Upload buffer size.
     */
    private static final int BUFFER_SIZE = 64 * 1024; // 64k

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
     * ProgressListener to which information about progress is sent during
     * upload
     */
    private ProgressListener progressListener;
    private LinkedHashSet progressListeners;

    /* TODO: Add a default constructor, receive to temp file. */

    /**
     * Creates a new instance of Upload that redirects the uploaded data to
     * stream given by the Receiver.
     * 
     * @param caption
     *            Normal component caption. You can set the caption of the
     *            upload submit button with setButtonCaption().
     * @param uploadReceiver
     *            Receiver to call to retrieve output stream when upload starts.
     */
    public Upload(String caption, Receiver uploadReceiver) {
        setCaption(caption);
        receiver = uploadReceiver;
    }

    /**
     * Gets the component type.
     * 
     * @return Component type as string.
     */
    @Override
    public String getTag() {
        return "upload";
    }

    /**
     * This method is called by terminal when upload is received.
     * 
     * Note, this method is called outside synchronized (Application) block, so
     * overriding this may be dangerous.
     * 
     * @param upload
     */
    public void receiveUpload(UploadStream upload) {
        if (!isUploading) {
            throw new IllegalStateException("uploading not started");
        }

        // Gets file properties
        final String filename = upload.getContentName();
        final String type = upload.getContentType();

        final Application application = getApplication();

        synchronized (application) {
            fireStarted(filename, type);
        }

        // Gets the output target stream
        final OutputStream out = receiver.receiveUpload(filename, type);
        if (out == null) {
            synchronized (application) {
                fireNoOutputStream(filename, type, 0);
                endUpload();
            }
            return;
        }

        final InputStream in = upload.getStream();

        if (null == in) {
            // No file, for instance non-existent filename in html upload
            synchronized (application) {
                fireNoInputStream(filename, type, 0);
                endUpload();
            }
            return;
        }

        final byte buffer[] = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        totalBytes = 0;
        try {
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
                if (progressListener != null && contentLength > 0) {
                    // update progress if listener set and contentLength
                    // received
                    synchronized (application) {
                        fireUpdateProgress(totalBytes, contentLength);
                    }
                }
            }

            // upload successful
            out.close();
            synchronized (application) {
                fireUploadSuccess(filename, type, totalBytes);
                endUpload();
                requestRepaint();
            }

        } catch (final Exception e) {
            synchronized (application) {
                // Download interrupted
                fireUploadInterrupted(filename, type, totalBytes, e);
                endUpload();
            }
        }
    }

    /**
     * Invoked when the value of a variable has changed.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {
        // NOP

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
        // The field should be focused
        if (focus) {
            target.addAttribute("focus", true);
        }

        // The tab ordering number
        if (tabIndex >= 0) {
            target.addAttribute("tabindex", tabIndex);
        }

        target.addAttribute("state", isUploading);

        target.addAttribute("buttoncaption", buttonCaption);

        target.addVariable(this, "fake", true);

        target.addUploadStreamVariable(this, "stream");
    }

    /**
     * Interface that must be implemented by the upload receivers to provide the
     * Upload component an output stream to write the uploaded data.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface Receiver extends Serializable {

        /**
         * Invoked when a new upload arrives.
         * 
         * @param filename
         *            the desired filename of the upload, usually as specified
         *            by the client.
         * @param MIMEType
         *            the MIME type of the uploaded file.
         * @return Stream to which the uploaded file should be written.
         */
        public OutputStream receiveUpload(String filename, String MIMEType);
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
     * Upload.Received event is sent when the upload receives a file, regardless
     * of whether the reception was successful or failed. If you wish to
     * distinguish between the two cases, use either SucceededEvent or
     * FailedEvent, which are both subclasses of the FinishedEvent.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class FinishedEvent extends Component.Event {

        private static final long serialVersionUID = 3257288015385670969L;

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
     * Upload.Interrupted event is sent when the upload is received, but the
     * reception is interrupted for some reason.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class FailedEvent extends FinishedEvent {

        private static final long serialVersionUID = 3833746590157386293L;

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
    public class NoOutputStreamEvent extends FailedEvent {

        private static final long serialVersionUID = 4745219890852396500L;

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
    public class NoInputStreamEvent extends FailedEvent {

        private static final long serialVersionUID = -529960205445737170L;

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
     * Upload.Success event is sent when the upload is received successfully.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class SucceededEvent extends FinishedEvent {

        private static final long serialVersionUID = 3256445798169524023L;

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
     * Upload.Started event is sent when the upload is started to received.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 5.0
     */
    public class StartedEvent extends Component.Event {

        private static final long serialVersionUID = -3984393770487403525L;
        private final String filename;
        private final String type;

        /**
         * 
         * @param source
         * @param filename
         * @param MIMEType
         * @param length
         */
        public StartedEvent(Upload source, String filename, String MIMEType) {
            super(source);
            this.filename = filename;
            type = MIMEType;
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

    }

    /**
     * Receives the events when the upload starts.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
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
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
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
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
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
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
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
     * Adds the upload started event listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(StartedListener listener) {
        addListener(StartedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * Removes the upload started event listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(StartedListener listener) {
        removeListener(StartedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * Adds the upload received event listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(FinishedListener listener) {
        addListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
    }

    /**
     * Removes the upload received event listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(FinishedListener listener) {
        removeListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
    }

    /**
     * Adds the upload interrupted event listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(FailedListener listener) {
        addListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * Removes the upload interrupted event listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(FailedListener listener) {
        removeListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * Adds the upload success event listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(SucceededListener listener) {
        addListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
    }

    /**
     * Removes the upload success event listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(SucceededListener listener) {
        removeListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
    }

    /**
     * Adds the upload success event listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(ProgressListener listener) {
        if (progressListeners == null) {
            progressListeners = new LinkedHashSet();
        }
        progressListeners.add(listener);
    }

    /**
     * Removes the upload success event listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(ProgressListener listener) {
        if (progressListeners != null) {
            progressListeners.remove(listener);
        }
    }

    /**
     * Emit upload received event.
     * 
     * @param filename
     * @param MIMEType
     * @param length
     */
    protected void fireStarted(String filename, String MIMEType) {
        fireEvent(new Upload.StartedEvent(this, filename, MIMEType));
    }

    /**
     * Emit upload finished event.
     * 
     * @param filename
     * @param MIMEType
     * @param length
     */
    protected void fireUploadReceived(String filename, String MIMEType,
            long length) {
        fireEvent(new Upload.FinishedEvent(this, filename, MIMEType, length));
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
            for (Iterator it = progressListeners.iterator(); it.hasNext();) {
                ProgressListener l = (ProgressListener) it.next();
                l.updateProgress(totalBytes, contentLength);
            }
        }
        // deprecated:
        if (progressListener != null) {
            progressListener.updateProgress(totalBytes, contentLength);
        }
    }

    /**
     * Returns the current receiver.
     * 
     * @return the Receiver.
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

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.ui.Component.Focusable#focus()
     */
    public void focus() {
        final Application app = getApplication();
        if (app != null) {
            getWindow().setFocusedComponent(this);
            delayedFocus = false;
        } else {
            delayedFocus = true;
        }
    }

    /**
     * Gets the Tabulator index of this Focusable component.
     * 
     * @see com.itmill.toolkit.ui.Component.Focusable#getTabIndex()
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Sets the Tabulator index of this Focusable component.
     * 
     * @see com.itmill.toolkit.ui.Component.Focusable#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * Sets the size of the file currently being uploaded.
     * 
     * @param contentLength
     */
    public void setUploadSize(long contentLength) {
        this.contentLength = contentLength;
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
    }

    /**
     * Go into state where new uploading can begin.
     * 
     * Warning: this is an internal method used by the framework and should not
     * be used by user of the Upload component.
     */
    public void endUpload() {
        isUploading = false;
        contentLength = -1;
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
     * This method is deprecated, use addListener(ProgressListener) instead.
     * 
     * @deprecated Use addListener(ProgressListener) instead.
     * @param progressListener
     */
    @Deprecated
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * This method is deprecated.
     * 
     * @deprecated Replaced with addListener/removeListener
     * @return listener
     * 
     */
    @Deprecated
    public ProgressListener getProgressListener() {
        return progressListener;
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
     * File uploads usually have button that starts actual upload progress. This
     * method is used to set text in that button.
     * 
     * @param buttonCaption
     *            text for uploads button.
     */
    public void setButtonCaption(String buttonCaption) {
        this.buttonCaption = buttonCaption;
    }

    /**
     * Notifies the component that it is connected to an application.
     * 
     * @see com.itmill.toolkit.ui.Component#attach()
     */
    @Override
    public void attach() {
        super.attach();
        if (delayedFocus) {
            focus();
        }
    }

}
