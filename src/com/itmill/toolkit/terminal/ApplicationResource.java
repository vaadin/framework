/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.terminal;

import com.itmill.toolkit.Application;

/**
 * This interface must be implemented by classes wishing to provide Application
 * resources.
 * <p>
 * <code>ApplicationResource</code> are a set of named resources (pictures,
 * sounds, etc) associated with some specific application. Having named
 * application resources provides a convenient method for having inter-theme
 * common resources for an application.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface ApplicationResource extends Resource {

	/**
	 * Default cache time.
	 */
	public static final long DEFAULT_CACHETIME = 1000 * 60 * 60 * 24;

	/**
	 * Gets resource as stream.
	 */
	public DownloadStream getStream();

	/**
	 * Gets the application of the resource.
	 */
	public Application getApplication();

	/**
	 * Gets the virtual filename for this resource.
	 * 
	 * @return the file name associated to this resource.
	 */
	public String getFilename();

	/**
	 * Gets the length of cache expiration time.
	 * 
	 * <p>
	 * This gives the adapter the possibility cache streams sent to the client.
	 * The caching may be made in adapter or at the client if the client
	 * supports caching. Default is <code>DEFAULT_CACHETIME</code>.
	 * </p>
	 * 
	 * @return Cache time in milliseconds
	 */
	public long getCacheTime();

	/**
	 * Gets the size of the download buffer used for this resource.
	 * 
	 * <p>
	 * If the buffer size is 0, the buffer size is decided by the terminal
	 * adapter. The default value is 0.
	 * </p>
	 * 
	 * @return int the size of the buffer in bytes.
	 */
	public int getBufferSize();

}
