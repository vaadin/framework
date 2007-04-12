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

import com.itmill.toolkit.service.FileTypeResolver;

/** 
 * <code>ThemeResource</code> is a named theme dependant resource provided and 
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

	/** 
	 * Id of the terminal managed resource. 
	 */
	private String resourceID = null; 

	/** 
	 * Creates a resource. 
	 * @param resourceId the Id of the resource.
	 */
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

	/** 
	 * Tests if the given object equals this Resource.
	 * 
	 * @param obj the object to be tested for equality.
	 * @return <code>true</code> if the given object equals this Icon,
	 * <code>false</code> if not.
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		return obj instanceof ThemeResource && 
			resourceID.equals(((ThemeResource)obj).resourceID);
	}

	/** 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return resourceID.hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return resourceID.toString();
	}
	
	/** 
	 * Gets the resource id.
	 * @return the resource id. 
	 */
	public String getResourceId() {
		return resourceID;	
	}
	
	/**
	 * @see com.itmill.toolkit.terminal.Resource#getMIMEType()
	 */
	public String getMIMEType() {
		return FileTypeResolver.getMIMEType(getResourceId());
	}
}
