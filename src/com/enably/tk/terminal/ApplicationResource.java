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

import com.enably.tk.Application;

/** This interface must be implemented by classes wishing to provide Application resources.
 * 
 * Application resources are a set of named resources (pictures, sounds, etc) associated
 * with some specific application. Having named application resources provides a convenient 
 * method for having inter-theme common resources for an application.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface ApplicationResource extends Resource {

	/** Default cache time. */
	public static final long DEFAULT_CACHETIME = 1000*60*60*24;

	/** Get resource as stream */
	public DownloadStream getStream();
		
	/** Get the application of the resource */
	public Application getApplication();

	/** Get virtual filename for the resource */
	public String getFilename();
	
	/** Get lenght of cache expiracy time.
	 * 
	 * <p>This gives the adapter the possibility cache streams sent to the 
	 * client. The caching may be made in adapter or at the client if the 
	 * client supports caching. Default is DEFAULT_CACHETIME.</p>
	 * 
	 * @return Cache time in milliseconds
	 */
	public long getCacheTime();

	/** Get the size of the download buffer used for this resource.
	 * 
	 * <p>If the buffer size is 0, the buffer size is decided by the 
	 * terminal adapter. The default value is 0.</p>
	 * 
	 * @return int The size of the buffer in bytes.
	 */
	public int getBufferSize();

}
