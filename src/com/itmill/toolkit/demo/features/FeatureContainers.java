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

package com.itmill.toolkit.demo.features;

public class FeatureContainers extends Feature {

	protected String getTitle() {
		return "Container Data Model";
	}

	protected String getDescriptionXHTML() {
		return "<p>Container is the most advanced of the data "
				+ "model supported by IT Mill Toolkit. It provides a very flexible "
				+ "way of managing set of items that share common properties. Each "
				+ "item is identified by an item id. "
				+ "Properties can be requested from container with item "
				+ "and property ids. Other way of accessing properties is to first "
				+ "request an item from container and then request its properties "
				+ "from it. </p>"
				+ "<p>Container interface was designed with flexibility and "
				+ "efficiency in mind. It contains inner interfaces for ordering "
				+ "the items sequentially, indexing the items and accessing them "
				+ "hierarchically. Those ordering models provide basis for "
				+ "Table, Tree and Select UI components. As with other data "
				+ "models, the containers support events for notifying about the "
				+ "changes.</p>"
				+ "<p>Set of utilities for converting between container models by "
				+ "adding external indexing or hierarchy into existing containers. "
				+ "In memory containers implementing indexed and hierarchical "
				+ "models provide easy to use tools for setting up in memory data "
				+ "storages. There is even a hierarchical container for direct "
				+ "file system access.</p>";
	}

	protected String getImage() {
		return "containers.jpg";
	}
}
