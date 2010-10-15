package com.vaadin.terminal;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * Receiver is a special kind of variable which value is streamed to given
 * {@link OutputStream}. E.g. in web terminals Receivers can be used to send
 * large files from browsers to the server.
 * <p>
 * Note, writing to the {@link OutputStream} is not synchronized by the terminal
 * (not to avoid stalls in other operations when eg. streaming to a slow network
 * service). If UI is changed as a side effect of writing to given output
 * stream, developer must handle synchronization manually.
 * <p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 6.5
 * @see PaintTarget#addVariable(ReceiverOwner, String, Receiver)
 * @see ReceiverOwner
 */
public interface Receiver extends Serializable {

    /**
     * Invoked by the terminal when a new upload arrives.
     * 
     * @param filename
     *            the filename of the upload if known by the terminal, usually
     *            as specified by the client.
     * @param MIMEType
     *            the MIME type of the uploaded file.
     * @return Stream to which the uploaded file should be written.
     */
    public OutputStream receiveUpload(String filename, String MIMEType);
}
