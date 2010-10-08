package com.vaadin.terminal;

import java.io.Serializable;

import com.vaadin.Application;
import com.vaadin.terminal.ReceiverOwner.ReceivingController;

/**
 * Special kind of {@link VariableOwner} that can send and receive information
 * with the terminal implementation about the progress of receiving data to its
 * Receiver. The actual communication happens via {@link ReceivingController}
 * which is fetched by the terminal when the Receiving is about to start.
 */
public interface ReceiverOwner extends VariableOwner {

    /*
     * The monitor/control is passed to separate ReceivingContorller because:
     * 
     * - possibly some component in the future may need support for streaming to
     * multiple Receivers at the same time.
     * 
     * - we don't want to bloat implementing ReceiverOwner's API. Now only one
     * method is published and they can decide what event/methods to publish as
     * their public API.
     */

    /**
     * Returns a handle for the terminal via the ReceiverOwner can monitor and
     * control the steaming of data to {@link Receiver}.
     * <p>
     * Most commonly ReceiverOwner implementation wants to implement this method
     * as final and reveal its own API for the end users.
     * 
     * @param receiver
     *            the Receiver whose streaming is to be controlled
     * @return a {@link ReceivingController} that will be used to control and
     *         monitor the progress of streaming
     */
    ReceivingController getReceivingController(Receiver receiver);

    interface ReceivingEvent extends Serializable {

        /**
         * @return the file name of the streamed file if known
         */
        String getFileName();

        /**
         * @return the mime type of the streamed file if known
         */
        String getMimeType();

        /**
         * @return the Receiver into which the content is being streamed
         */
        Receiver getReceiver();

        /**
         * @return the length of the stream (in bytes) if known, else -1
         */
        long getContentLength();

        /**
         * @return then number of bytes streamed to Receiver
         */
        long getBytesReceived();
    }

    /**
     * Event passed to
     * {@link ReceivingController#uploadStarted(ReceivingStartedEvent)} method
     * before the streaming of the content to {@link Receiver} starts.
     */
    public interface ReceivingStartedEvent extends ReceivingEvent {
    }

    /**
     * Event passed to
     * {@link ReceivingController#onProgress(ReceivingProgressedEvent)} method
     * during the streaming progresses.
     */
    public interface ReceivingProgressedEvent extends ReceivingEvent {
    }

    /**
     * Event passed to
     * {@link ReceivingController#uploadFinished(ReceivingEndedEvent)} method
     * the contents have been streamed to Receiver successfully.
     */
    public interface ReceivingEndedEvent extends ReceivingEvent {
    }

    /**
     * Event passed to
     * {@link ReceivingController#uploadFailed(ReceivingFailedEvent)} method
     * when the streaming ended before the end of the input. The streaming may
     * fail due an interruption by {@link ReceivingController} or due an other
     * unknown exception in communication. In the latter case the exception is
     * also passed to
     * {@link Application#terminalError(com.vaadin.terminal.Terminal.ErrorEvent)}
     * .
     */
    public interface ReceivingFailedEvent extends ReceivingEvent {

        /**
         * @return the exception that caused the receiving not to finish cleanly
         */
        Exception getException();

    }

    public interface ReceivingController {
        /**
         * Whether the {@link #onProgress(long, long)} method should be called
         * during the upload.
         * <p>
         * {@link #onProgress(long, long)} is called in a synchronized block
         * during the content is being received. This is potentially bit slow,
         * so we are calling this method only if requested. The value is
         * requested after the {@link #uploadStarted(ReceivingStartedEvent)}
         * event, but not after each buffer reading.
         * 
         * @return true if this ReceiverOwner wants to by notified during the
         *         upload of the progress of streaming.
         * @see ReceiverOwner#onProgress(int, int)
         */
        boolean listenProgress();

        /**
         * This method is called by the terminal if {@link #listenProgress()}
         * returns true when the streaming starts.
         */
        void onProgress(ReceivingProgressedEvent event);

        void uploadStarted(ReceivingStartedEvent event);

        void uploadFinished(ReceivingEndedEvent event);

        void uploadFailed(ReceivingFailedEvent event);

        /*
         * Not synchronized to avoid stalls (caused by UIDL requests) while
         * streaming the content. Implementations also most commonly atomic even
         * without the restriction.
         */
        /**
         * ReceiverOwner can set this flag to true if it wants the Terminal to
         * stop receiving current upload.
         * <p>
         * Note, the usage of this method is not synchronized over the
         * Application instance by the terminal like other methods. The
         * implementation should only return a boolean field and especially not
         * to modify UI or implement a synchronization by itself.
         * 
         * @return true if the streaming should be interrupted as soon as
         *         possible.
         */
        boolean isInterrupted();
    }

}
