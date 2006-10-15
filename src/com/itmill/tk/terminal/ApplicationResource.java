/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */
   
package com.itmill.tk.terminal;

import com.itmill.tk.Application;

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
