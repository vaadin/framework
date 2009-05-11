/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;
import java.net.URL;

/**
 * Interface implemented by all the classes capable of handling URI:s.
 * 
 * <p>
 * <code>URIHandler</code> can provide <code>DownloadStream</code> for
 * transferring data for client.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface URIHandler extends Serializable {

    /**
     * Handles a given relative URI. If the URI handling wants to emit a
     * downloadable stream it can return download stream object. If no emitting
     * stream is necessary, null should be returned instead.
     * 
     * @param context
     *            the URl.
     * @param relativeUri
     *            the relative uri.
     * @return the download stream object.
     */
    public DownloadStream handleURI(URL context, String relativeUri);

    /**
     * URIHandler error event.
     */
    public interface ErrorEvent extends Terminal.ErrorEvent {

        /**
         * Gets the source URIHandler.
         * 
         * @return the URIHandler.
         */
        public URIHandler getURIHandler();

    }
}
