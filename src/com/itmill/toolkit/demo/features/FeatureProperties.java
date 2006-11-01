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

public class FeatureProperties extends Feature {

	protected String getExampleSrc() {
		return super.getExampleSrc();
	}

	protected String getTitle() {
		return "Property Data Model";
	}

	protected String getDescriptionXHTML() {
		return "<p>Millstone data model is one of the core concepts "
			+ "in the library and Property-interface is the base of that "
			+ "model. Property provides standardized API for a singe data object "
			+ "that can be getted and setted. A property is always typed, but can optionally "
			+ "support data type conversions. Optionally properties can provide "
			+ "value change events for following the state changes.</p>"
			+ "<p>The most important function of the Property as well as other "
			+ "data models is to connect classes implementing the interface directly to "
			+ "editor and viewer classes. Typically this is used to connect different "
			+ "data sources to UI components for editing and viewing their contents.</p>"
			+ "<p>Properties can be utilized either by implementing the interface "
			+ "or by using some of the existing property implementations. Millstone "
			+ "includes Property interface implementations for "
			+ "arbitrary function pairs or Bean-properties as well as simple object "
			+ "properties.</p>"
			+ "<p>Many of the UI components also imlement Property interface and allow "
			+ "setting of other components as their data-source. These UI-components "
			+ "include TextField, DateField, Select, Table, Button, "
			+ "Label and Tree.</p>";
	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getImage()
	 */
	protected String getImage() {
		return "properties.jpg";
	}

}
