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

public class FeatureItems extends Feature {

	protected String getTitle() {
		return "Item Data Model";
	}

	protected String getDescriptionXHTML() {
		return "<p>Item is an object, which contains a set of named "
			+ "properties. Each property is identified by an "
			+ "id and a reference to the property can be queried from the Item. "
			+ "Item defines inner-interfaces for maintaining the item property "
			+ "set and listening the item property set changes.</p>"
			+ "<p>Items generally represent objects in the object-oriented "
			+ "model, but with the exception that they are configurable "
			+ "and provide an event mechanism. The simplest way of utilizing "
			+ "Item interface is to use existing Item implementations. "
			+ "Provided utility classes include configurable property set,"
			+ " bean to item adapter and Form UI component.</p>";
	}

	protected String getImage() {
		return "items.jpg";
	}

}
