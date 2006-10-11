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

import java.net.URL;

import com.enably.tk.service.FileTypeResolver;

/** External resource implements source for resources fetched from 
 * location specified by URL:s. The resources are fetched directly by the
 * client terminal and are not fetched trough the terminal adapter.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ExternalResource implements Resource {

	/** Url of the download */
	private String sourceURL = null;


	/** Create new download component for downloading directly from given URL. 
	 * */
	public ExternalResource(URL sourceURL) {
		if (sourceURL == null) 
			throw new RuntimeException("Source must be non-null");
			
		this.sourceURL = sourceURL.toString();
	}

	/** Create new download component for downloading directly from given URL. 
	 * */
	public ExternalResource(String sourceURL) {
		if (sourceURL == null) 
			throw new RuntimeException("Source must be non-null");
			
		this.sourceURL = sourceURL.toString();
	}

	/** Get the URL of the external resource */
	public String getURL() {
		return sourceURL;
	}
	
	public String getMIMEType() {
		return FileTypeResolver.getMIMEType(getURL().toString());
	}

}
