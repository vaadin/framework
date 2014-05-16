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
package com.vaadin.server;

import java.io.OutputStream;
import java.io.Serializable;

import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.StreamVariable.StreamingStartEvent;

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
 * @author Vaadin Ltd.
 * @since 6.5
 * @see PaintTarget#addVariable(VariableOwner, String, StreamVariable)
 */
public interface StreamVariable extends Serializable {

    /**
     * Invoked by the terminal when a new upload arrives, after
     * {@link #streamingStarted(StreamingStartEvent)} method has been called.
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
     * {@link #uploadStarted(StreamingStartEvent)} event, but not after reading
     * each buffer.
     * 
     * @return true if this {@link StreamVariable} wants to by notified during
     *         the upload of the progress of streaming.
     * @see #onProgress(StreamingProgressEvent)
     */
    public boolean listenProgress();

    /**
     * This method is called by the terminal if {@link #listenProgress()}
     * returns true when the streaming starts.
     */
    public void onProgress(StreamingProgressEvent event);

    public void streamingStarted(StreamingStartEvent event);

    public void streamingFinished(StreamingEndEvent event);

    public void streamingFailed(StreamingErrorEvent event);

    /*
     * Not synchronized to avoid stalls (caused by UIDL requests) while
     * streaming the content. Implementations also most commonly atomic even
     * without the restriction.
     */
    /**
     * If this method returns true while the content is being streamed the
     * Terminal to stop receiving current upload.
     * <p>
     * Note, the usage of this method is not synchronized over the Application
     * instance by the terminal like other methods. The implementation should
     * only return a boolean field and especially not modify UI or implement a
     * synchronization by itself.
     * 
     * @return true if the streaming should be interrupted as soon as possible.
     */
    public boolean isInterrupted();

    public interface StreamingEvent extends Serializable {

        /**
         * @return the file name of the streamed file if known
         */
        public String getFileName();

        /**
         * @return the mime type of the streamed file if known
         */
        public String getMimeType();

        /**
         * @return the length of the stream (in bytes) if known, else -1
         */
        public long getContentLength();

        /**
         * @return then number of bytes streamed to StreamVariable
         */
        public long getBytesReceived();
    }

    /**
     * Event passed to {@link #uploadStarted(StreamingStartEvent)} method before
     * the streaming of the content to {@link StreamVariable} starts.
     */
    public interface StreamingStartEvent extends StreamingEvent {
        /**
         * The owner of the StreamVariable can call this method to inform the
         * terminal implementation that this StreamVariable will not be used to
         * accept more post.
         */
        public void disposeStreamVariable();
    }

    /**
     * Event passed to {@link #onProgress(StreamingProgressEvent)} method during
     * the streaming progresses.
     */
    public interface StreamingProgressEvent extends StreamingEvent {
    }

    /**
     * Event passed to {@link #uploadFinished(StreamingEndEvent)} method the
     * contents have been streamed to StreamVariable successfully.
     */
    public interface StreamingEndEvent extends StreamingEvent {
    }

    /**
     * Event passed to {@link #uploadFailed(StreamingErrorEvent)} method when
     * the streaming ended before the end of the input. The streaming may fail
     * due an interruption by {@link } or due an other unknown exception in
     * communication. In the latter case the exception is also passed to
     * {@link VaadinSession#error(com.vaadin.server.Terminal.ErrorEvent)} .
     */
    public interface StreamingErrorEvent extends StreamingEvent {

        /**
         * @return the exception that caused the receiving not to finish cleanly
         */
        public Exception getException();

    }

}
