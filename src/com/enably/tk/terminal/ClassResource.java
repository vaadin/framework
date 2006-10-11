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

package com.enably.tk.terminal;

import com.enably.tk.Application;
import com.enably.tk.service.FileTypeResolver;

/** Class resource is a named resource accessed with the class loader.
 *  
 *  This can be used to access resources such as icons, files, etc.
 *  @see java.lang.Class#getResource(java.lang.String)
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ClassResource implements ApplicationResource {

	/** Default buffer size for this stream resource */
	private int bufferSize = 0;

	/** Default cache time for this stream resource */
	private long cacheTime = DEFAULT_CACHETIME;	

	/** Associated class used for indetifying the source of the resource */
	private Class associatedClass;
	
	/** Name of the resource is relative to the associated class */
	private String resourceName;
	
	/** Application used for serving the class */
	private Application application;

	/** Create new application resource instance. 
	 * The resource id is relative to the location of the application class.
	 * 
	 * @param resourceName Unique identifier of the resource within the application.
	 * @param application The application this resource will be added to.
	 * */
	public ClassResource(String resourceName, Application application) {
		this.associatedClass = application.getClass();
		this.resourceName = resourceName;
		this.application = application;
		if (resourceName == null)
			throw new NullPointerException();
		application.addResource(this);
	}

	/** Create new application resource instance. 
	 * 
	 * @param associatedClass The class of the which the resource is associated.
	 * @param resourceName Unique identifier of the resource within the application.
	 * @param application The application this resource will be added to.
	 * */
	public ClassResource(
		Class associatedClass,
		String resourceName,
		Application application) {
		this.associatedClass = associatedClass;
		this.resourceName = resourceName;
		this.application = application;
		if (resourceName == null || associatedClass == null)
			throw new NullPointerException();
		application.addResource(this);
	}

	public String getMIMEType() {
		return FileTypeResolver.getMIMEType(this.resourceName);
	}

	public Application getApplication() {
		return application;
	}

	public String getFilename() {
		int index = 0;
		int next = 0;
		while ((next = resourceName.indexOf('/', index)) > 0
			&& next + 1 < resourceName.length())
			index = next + 1;
		return resourceName.substring(index);
	}

	public DownloadStream getStream() {
		DownloadStream ds = new DownloadStream(
			associatedClass.getResourceAsStream(resourceName),
			getMIMEType(),
			getFilename());
		ds.setBufferSize(getBufferSize());
		ds.setCacheTime(cacheTime);
		return ds;
	}

	/* documented in superclass */
	public int getBufferSize() {
		return bufferSize;
	}

	/** Set the size of the download buffer used for this resource.
	 * @param bufferSize The size of the buffer in bytes.
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/* documented in superclass */
	public long getCacheTime() {
		return cacheTime;
	}

	/** Set lenght of cache expiracy time.
	 * 
	 * <p>This gives the adapter the possibility cache streams sent to the
	 * client. The caching may be made in adapter or at the client if the 
	 * client supports caching. Zero or negavive value disbales the 
	 * caching of this stream.</p>
	 * 
	 * @param cacheTime The cache time in milliseconds.
	 * 
	 */
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}
}
