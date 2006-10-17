/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.terminal;

import java.net.URL;

/** Interface implemented by all the classes capable of handling URI:s.
 * 
 * <p>URI handlers can provide <code>DownloadStream</code>
 * for transferring data for client.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface URIHandler {
    
    /** Handle uri. 
     * 
     * Handle the given relative URI. If the URI handling wants to emit
     * a downloadable stream it can return download stream object. If no
     * emitting stream is necessary, null should be returned instead.
     * 
     */
    public DownloadStream handleURI(URL context, String relativeUri);

	/** URIHandler error event */
	public interface ErrorEvent extends Terminal.ErrorEvent {

		/** Get the source URIHandler. */
		public URIHandler getURIHandler();

	}
}
