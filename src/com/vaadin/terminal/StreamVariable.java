package com.vaadin.terminal;

import java.io.OutputStream;
import java.io.Serializable;

import com.vaadin.Application;

/**
 * StreamVariable is a special kind of variable whose value is streamed to an
 * {@link OutputStream} provided by the {@link #getOutputStream()} method. E.g.
 * in web terminals {@link StreamVariable} can be used to send large files from
 * browsers to the server without consuming large amounts of memory.
 * <p>
 * Note, writing to the {@link OutputStream} is not synchronized by the terminal
 * (to avoid stalls in other operations when eg. streaming to a slow network
 * service or file system). If UI is changed as a side effect of writing to the
 * output stream, developer must handle synchronization manually.
 * <p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 6.5
 * @see PaintTarget#addVariable(VariableOwner, String, StreamVariable)
 */
public interface StreamVariable extends Serializable {

    /**
     * Invoked by the terminal when a new upload arrives, after
     * {@link #streamingStarted(StreamingStartedEvent)} method has been called.
     * The terminal implementation will write the streamed variable to the
     * returned output stream.
     * 
     * @return Stream to which the uploaded file should be written.
     */
    public OutputStream getOutputStream();

    /**
     * Whether the {@link #onProgress(long, long)} method should be called
     * during the upload.
     * <p>
     * {@link #onProgress(long, long)} is called in a synchronized block when
     * the content is being received. This is potentially bit slow, so we are
     * calling that method only if requested. The value is requested after the
     * {@link #uploadStarted(StreamingStartedEvent)} event, but not after
     * reading each buffer.
     * 
     * @return true if this ReceiverOwner wants to by notified during the upload
     *         of the progress of streaming.
     * @see ReceiverOwner#onProgress(int, int)
     */
    boolean listenProgress();

    /**
     * This method is called by the terminal if {@link #listenProgress()}
     * returns true when the streaming starts.
     */
    void onProgress(StreamingProgressedEvent event);

    void streamingStarted(StreamingStartedEvent event);

    void streamingFinished(StreamingEndedEvent event);

    void streamingFailed(StreamingFailedEvent event);

    /*
     * Not synchronized to avoid stalls (caused by UIDL requests) while
     * streaming the content. Implementations also most commonly atomic even
     * without the restriction.
     */
    /**
     * ReceiverOwner can set this flag to true if it wants the Terminal to stop
     * receiving current upload.
     * <p>
     * Note, the usage of this method is not synchronized over the Application
     * instance by the terminal like other methods. The implementation should
     * only return a boolean field and especially not modify UI or implement a
     * synchronization by itself.
     * 
     * @return true if the streaming should be interrupted as soon as possible.
     */
    boolean isInterrupted();

    interface StreamingEvent extends Serializable {

        /**
         * @return the file name of the streamed file if known
         */
        String getFileName();

        /**
         * @return the mime type of the streamed file if known
         */
        String getMimeType();

        /**
         * @return the length of the stream (in bytes) if known, else -1
         */
        long getContentLength();

        /**
         * @return then number of bytes streamed to StreamVariable
         */
        long getBytesReceived();
    }

    /**
     * Event passed to {@link #uploadStarted(StreamingStartedEvent)} method
     * before the streaming of the content to {@link StreamVariable} starts.
     */
    public interface StreamingStartedEvent extends StreamingEvent {
    }

    /**
     * Event passed to {@link #onProgress(StreamingProgressedEvent)} method
     * during the streaming progresses.
     */
    public interface StreamingProgressedEvent extends StreamingEvent {
    }

    /**
     * Event passed to {@link #uploadFinished(StreamingEndedEvent)} method the
     * contents have been streamed to StreamVariable successfully.
     */
    public interface StreamingEndedEvent extends StreamingEvent {
    }

    /**
     * Event passed to {@link #uploadFailed(StreamingFailedEvent)} method when
     * the streaming ended before the end of the input. The streaming may fail
     * due an interruption by {@link } or due an other unknown exception in
     * communication. In the latter case the exception is also passed to
     * {@link Application#terminalError(com.vaadin.terminal.Terminal.ErrorEvent)}
     * .
     */
    public interface StreamingFailedEvent extends StreamingEvent {

        /**
         * @return the exception that caused the receiving not to finish cleanly
         */
        Exception getException();

    }

}
