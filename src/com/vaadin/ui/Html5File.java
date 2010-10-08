package com.vaadin.ui;

import java.io.OutputStream;
import java.io.Serializable;

import com.vaadin.event.dd.DropHandler;
import com.vaadin.terminal.Receiver;
import com.vaadin.terminal.ReceiverOwner.ReceivingEndedEvent;
import com.vaadin.terminal.ReceiverOwner.ReceivingFailedEvent;
import com.vaadin.terminal.ReceiverOwner.ReceivingProgressedEvent;
import com.vaadin.terminal.ReceiverOwner.ReceivingStartedEvent;

/**
 * {@link DragAndDropWrapper} can receive also files from client computer if
 * appropriate HTML 5 features are supported on client side. This class wraps
 * information about dragged file on server side.
 */
public class Html5File implements Serializable {

    final class ProxyReceiver implements Receiver {
        public OutputStream receiveUpload(String filename, String MIMEType) {
            if (receiver == null) {
                return null;
            }
            return receiver.receiveUpload(filename, MIMEType);
        }

        Html5File getFile() {
            return Html5File.this;
        }
    }

    private String name;
    private long size;
    private Receiver receiver;
    private String type;

    Html5File(String name, long size, String mimeType) {
        this.name = name;
        this.size = size;
        type = mimeType;
    }

    /**
     * The receiver that is registered to the terminal. Wraps the actual
     * Receiver set later by Html5File user.
     */
    private ProxyReceiver proxyReceiver = new ProxyReceiver();
    private boolean interrupted = false;
    private Html5FileUploadListener listener;;

    public String getFileName() {
        return name;
    }

    public long getFileSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    /**
     * Sets the {@link Receiver} that into which the file contents will be
     * written. Usage of Reveiver is similar to {@link Upload} component.
     * <p>
     * If the {@link Receiver} is not set in the {@link DropHandler} the file
     * contents will not be sent to server.
     * <p>
     * <em>Note!</em> receiving file contents is experimental feature depending
     * on HTML 5 API's. It is supported only by modern web brosers like Firefox
     * 3.6 and above and recent webkit based browsers (Safari 5, Chrome 6) at
     * this time.
     * 
     * @param receiver
     *            the callback that returns stream where the implementation
     *            writes the file contents as it arrives.
     */
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    ProxyReceiver getProxyReceiver() {
        return proxyReceiver;
    }

    /**
     * Gets the {@link Html5FileUploadListener} that is used to track the progress of
     * streaming the file contents to given {@link Receiver}.
     * 
     * @return
     */
    public Html5FileUploadListener getUploadListener() {
        return listener;
    }

    /**
     * Sets the {@link Html5FileUploadListener} that can be used to track the progress of
     * streaming the file contents to given {@link Receiver}.
     * 
     * @param listener
     * @see #setReceiver(Receiver)
     */
    public void setUploadListener(Html5FileUploadListener listener) {
        this.listener = listener;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    /**
     * Interrupts uploading this file.
     * 
     * @param interrupted
     */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public interface Html5FileUploadListener {

        void onProgress(ReceivingProgressedEvent event);

        void uploadStarted(ReceivingStartedEvent event);

        void uploadFinished(ReceivingEndedEvent event);

        void uploadFailed(ReceivingFailedEvent event);
    }

}