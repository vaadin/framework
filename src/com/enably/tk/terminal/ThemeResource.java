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

import com.enably.tk.service.FileTypeResolver;

/** Theme resource is a named theme dependant resource provided and 
 * managed by a theme. The actual resource contents are dynamically
 * resolved to comply with the used theme by the terminal adapter.
 * This is commonly used to provide  static images, flash, 
 * java-applets, etc for the terminals.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ThemeResource implements Resource {

	/** Id of the terminal managed resource. */
	private String resourceID = null; 

	/** Create a resource. */
	public ThemeResource(String resourceId) {
		if (resourceId == null) 
			throw new NullPointerException("Resource ID must not be null");
		if (resourceId.length() == 0)
			throw new IllegalArgumentException(
			"Resource ID can not be empty");
		if (resourceId.charAt(0) == '/')
			throw new IllegalArgumentException(
			"Resource ID must be relative (can not begin with /)");
			
		this.resourceID = resourceId;
	}

	/** Tests if the given object equals this Resource.
	 * 
	 * @param obj the object to be tested for equality
	 * @return <code>true</code> if the given object equals this Icon,
	 * <code>false</code> if not
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		return obj instanceof ThemeResource && 
			resourceID.equals(((ThemeResource)obj).resourceID);
	}

	/** @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return resourceID.hashCode();
	}
	
	public String toString() {
		return resourceID.toString();
	}
	
	/** Get the resource id */
	public String getResourceId() {
		return resourceID;	
	}
	
	public String getMIMEType() {
		return FileTypeResolver.getMIMEType(getResourceId());
	}
}
