package com.vaadin.terminal;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * 
 * Interface that must be implemented by the upload receivers to provide the
 * Upload component an output stream to write the uploaded data.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 6.5
 */
public interface Receiver extends Serializable {

    /**
     * Invoked when a new upload arrives.
     * 
     * @param filename
     *            the desired filename of the upload, usually as specified by
     *            the client.
     * @param MIMEType
     *            the MIME type of the uploaded file.
     * @return Stream to which the uploaded file should be written.
     */
    public OutputStream receiveUpload(String filename, String MIMEType);
}
