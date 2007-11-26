/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.UploadStream;

/**
 * Component for client file uploading.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Upload extends AbstractComponent implements Component.Focusable {

    /**
     * Upload buffer size.
     */
    private static final int BUFFER_SIZE = 64 * 1024; // 64k

    /**
     * Should the field be focused on next repaint?
     */
    private boolean focus = false;

    /**
     * The tab order number of this field.
     */
    private int tabIndex = 0;

    /**
     * The output of the upload is redirected to this receiver.
     */
    private Receiver receiver;

    private long focusableId = -1;

    private boolean isUploading;

    private long contentLength = -1;

    private int totalBytes;

    private String buttonCaption = "Upload";

    /**
     * ProgressListener to which information about progress is sent during
     * upload
     */
    private ProgressListener progressListener;

    /* TODO: Add a default constructor, receive to temp file. */

    /**
     * Creates a new instance of Upload that redirects the uploaded data to
     * given stream.
     * 
     * @param caption
     * @param uploadReceiver
     */
    public Upload(String caption, Receiver uploadReceiver) {
        focusableId = Window.getNewFocusableId(this);
        setCaption(caption);
        receiver = uploadReceiver;
    }

    /**
     * Gets the component type.
     * 
     * @return Component type as string.
     */
    public String getTag() {
        return "upload";
    }

    public void receiveUpload(UploadStream upload) {
        if (!isUploading) {
            throw new IllegalStateException("uploading not started");
        }

        // Gets file properties
        String filename = upload.getContentName();
        String type = upload.getContentType();

        fireStarted(filename, type);

        // Gets the output target stream
        OutputStream out = receiver.receiveUpload(filename, type);
        if (out == null) {
            throw new RuntimeException(
                    "Error getting outputstream from upload receiver");
        }

        InputStream in = upload.getStream();

        if (null == in) {
            // No file, for instance non-existent filename in html upload
            fireUploadInterrupted(filename, type, 0);
            endUpload();
            return;
        }

        byte buffer[] = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        totalBytes = 0;
        try {
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
                if (progressListener != null && contentLength > 0) {
                    // update progress if listener set and contentLength
                    // received
                    progressListener.updateProgress(totalBytes, contentLength);
                }
            }

            // upload successful
            out.close();
            fireUploadSuccess(filename, type, totalBytes);
            endUpload();
            requestRepaint();

        } catch (IOException e) {

            // Download interrupted
            fireUploadInterrupted(filename, type, totalBytes);
            endUpload();
        }
    }

    /**
     * Invoked when the value of a variable has changed.
     * 
     * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    public void changeVariables(Object source, Map variables) {
        // NOP

    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *                Target to paint the content on.
     * @throws PaintException
     *                 if the paint operation failed.
     */
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
     * Interface that must be implemented by the upload receivers.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface Receiver {

        /**
         * Invoked when a new upload arrives.
         * 
         * @param filename
         *                the desired filename of the upload, usually as
         *                specified by the client.
         * @param MIMEType
         *                the MIME type of the uploaded file.
         * @return Stream to which the uploaded file should be written.
         */
        public OutputStream receiveUpload(String filename, String MIMEType);
    }

    /* Upload events ************************************************ */

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
        } catch (java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException("Internal error");
        }
    }

    /**
     * Upload.Received event is sent when the upload receives a file, regardless
     * if the receival was successfull.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class FinishedEvent extends Component.Event {

        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3257288015385670969L;

        /**
         * Length of the received file.
         */
        private long length;

        /**
         * MIME type of the received file.
         */
        private String type;

        /**
         * Received file name.
         */
        private String filename;

        /**
         * 
         * @param source
         *                the source of the file.
         * @param filename
         *                the received file name.
         * @param MIMEType
         *                the MIME type of the received file.
         * @param length
         *                the length of the received file.
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

        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3833746590157386293L;

        /**
         * 
         * @param source
         * @param filename
         * @param MIMEType
         * @param length
         */
        public FailedEvent(Upload source, String filename, String MIMEType,
                long length) {
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

        /**
         * Serial generated by eclipse.
         */
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

        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = -3984393770487403525L;
        private String filename;
        private String type;

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
    public interface StartedListener {

        /**
         * Upload has started.
         * 
         * @param event
         *                the Upload started event.
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
    public interface FinishedListener {

        /**
         * Upload has finished.
         * 
         * @param event
         *                the Upload finished event.
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
    public interface FailedListener {

        /**
         * Upload has finished unsuccessfully.
         * 
         * @param event
         *                the Upload failed event.
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
    public interface SucceededListener {

        /**
         * Upload successfull..
         * 
         * @param event
         *                the Upload successfull event.
         */
        public void uploadSucceeded(SucceededEvent event);
    }

    /**
     * Adds the upload started event listener.
     * 
     * @param listener
     *                the Listener to be added.
     */
    public void addListener(StartedListener listener) {
        addListener(StartedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * Removes the upload started event listener.
     * 
     * @param listener
     *                the Listener to be removed.
     */
    public void removeListener(StartedListener listener) {
        removeListener(FinishedEvent.class, listener, UPLOAD_STARTED_METHOD);
    }

    /**
     * Adds the upload received event listener.
     * 
     * @param listener
     *                the Listener to be added.
     */
    public void addListener(FinishedListener listener) {
        addListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
    }

    /**
     * Removes the upload received event listener.
     * 
     * @param listener
     *                the Listener to be removed.
     */
    public void removeListener(FinishedListener listener) {
        removeListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
    }

    /**
     * Adds the upload interrupted event listener.
     * 
     * @param listener
     *                the Listener to be added.
     */
    public void addListener(FailedListener listener) {
        addListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * Removes the upload interrupted event listener.
     * 
     * @param listener
     *                the Listener to be removed.
     */
    public void removeListener(FailedListener listener) {
        removeListener(FinishedEvent.class, listener, UPLOAD_FAILED_METHOD);
    }

    /**
     * Adds the upload success event listener.
     * 
     * @param listener
     *                the Listener to be added.
     */
    public void addListener(SucceededListener listener) {
        addListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
    }

    /**
     * Removes the upload success event listener.
     * 
     * @param listener
     *                the Listener to be removed.
     */
    public void removeListener(SucceededListener listener) {
        removeListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
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
     * Emit upload received event.
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
     * Emits the upload interrupted event.
     * 
     * @param filename
     * @param MIMEType
     * @param length
     */
    protected void fireUploadInterrupted(String filename, String MIMEType,
            long length) {
        fireEvent(new Upload.FailedEvent(this, filename, MIMEType, length));
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
     *                the receiver to set.
     */
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * Sets the focus to this component.
     * 
     * @see com.itmill.toolkit.ui.Component.Focusable#focus()
     */
    public void focus() {
        Window w = getWindow();
        if (w != null) {
            w.setFocusedComponent(this);
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
     * Gets the unique ID of focusable.
     * 
     * @see com.itmill.toolkit.ui.Component.Focusable#getFocusableId()
     */
    public long getFocusableId() {
        return focusableId;
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
     */
    public void startUpload() {
        if (isUploading) {
            throw new IllegalStateException("uploading already started");
        }
        isUploading = true;
    }

    /**
     * Go into state where new uploading can begin.
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
     * Sets listener to track progress of upload.
     * 
     * @param progressListener
     */
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * Gets listener that tracks progress of upload.
     * 
     * @return listener
     * 
     */
    public ProgressListener getProgressListener() {
        return progressListener;
    }

    /**
     * ProgressListener receives events to track progress of upload.
     */
    public interface ProgressListener {
        /**
         * Updates progress to listener
         * 
         * @param readBytes
         *                bytes transferred
         * @param contentLength
         *                total size of file currently being uploaded, -1 if
         *                unknown
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
     *                text for uploads button.
     */
    public void setButtonCaption(String buttonCaption) {
        this.buttonCaption = buttonCaption;
    }
}
