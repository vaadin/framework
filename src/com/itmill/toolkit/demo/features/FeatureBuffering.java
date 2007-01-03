/* *************************************************************************
 
                               IT Mill Toolkit 

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
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.demo.features;

public class FeatureBuffering extends Feature {
	protected String getExampleSrc() {
		return super.getExampleSrc();
	}

	protected String getTitle() {
		return "Buffering";
	}
	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "<p>IT Mill Toolkit data model provides interface for implementing "
			+ "buffering in data components. The basic idea is that a component "
			+ "reading their state from data source can implement "
			+ "Buffered-interface, for storing the value internally. "
			+ "Buffering provides transactional access "
			+ "for setting data: data can be put to a component's buffer and "
			+ "afterwards committed to or discarded by re-reding it from the data source. "
			+ "The buffering can be used for creating interactive interfaces "
			+ "as well as caching the data for performance reasons.</p>"
			+ "<p>Buffered interface contains methods for committing and discarding "
			+ "changes to an object and support for controlling buffering mode "
			+ "with read-through and write-through modes. "
			+ "Read-through mode means that the value read from the buffered "
			+ "object is constantly up to date with the data source. "
			+ "Respectively the write-through mode means that all changes to the object are "
			+ "immediately updated to the data source.</p>";
	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getImage()
	 */
	protected String getImage() {
		return "buffering.jpg";
	}

}
