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

package com.itmill.tk.demo.features;

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
	 * @see com.itmill.tk.demo.features.Feature#getImage()
	 */
	protected String getImage() {
		return "properties.jpg";
	}

}
