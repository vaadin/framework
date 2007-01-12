/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

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
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.terminal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.service.FileTypeResolver;

/** File resources are files or directories on local filesystem. The files and directories
 * are served trough URI:s to the client terminal and thus must be registered to an 
 * URI context before they can be used. The resource is automatically registered
 * to the application when it is created.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class FileResource implements ApplicationResource {

	/** Default buffer size for this stream resource */
	private int bufferSize = 0;

	/** File where the downloaded content is fetched from. */
	private File sourceFile;

	/** Application */
	private Application application;

	/** Default cache time for this stream resource */
	private long cacheTime = DownloadStream.DEFAULT_CACHETIME;		

	/** Create new file resource for providing given file for
	 * client terminals.
	 */
	public FileResource(File sourceFile, Application application) {
		this.application = application;
		setSourceFile(sourceFile);
		application.addResource(this);
	}

	public DownloadStream getStream() {
		try {
			DownloadStream ds = new DownloadStream(
				new FileInputStream(this.sourceFile),
				getMIMEType(),
				getFilename());
			ds.setCacheTime(cacheTime);
			return ds;
		} catch (FileNotFoundException e) {
			// No logging for non-existing files at this level. 
			return null;
		}
	}

	/** Returns the source file.
	 * @return File
	 */
	public File getSourceFile() {
		return sourceFile;
	}

	/** Sets the source file.
	 * @param sourceFile The source file to set
	 */
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @see com.itmill.toolkit.terminal.ApplicationResource#getApplication()
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * @see com.itmill.toolkit.terminal.ApplicationResource#getFilename()
	 */
	public String getFilename() {
		return sourceFile.getName();
	}

	/**
	 * @see com.itmill.toolkit.terminal.Resource#getMIMEType()
	 */
	public String getMIMEType() {
		return FileTypeResolver.getMIMEType(sourceFile);
	}
	
	/** Get lenght of cache expiracy time.
	 *  This gives the adapter the possibility cache streams sent to the client.
	 *  The caching may be made in adapter or at the client if the client supports
	 *  caching. Default is DownloadStream.DEFAULT_CACHETIME.
	 * @return Cache time in milliseconds
	 */
	public long getCacheTime() {
		return cacheTime;
	}

	/** Set lenght of cache expiracy time.
	 *  This gives the adapter the possibility cache streams sent to the client.
	 *  The caching may be made in adapter or at the client if the client supports
	 *  caching. Zero or negavive value disbales the caching of this stream.
	 * @param cacheTime The cache time in milliseconds.
	 */
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
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

}
