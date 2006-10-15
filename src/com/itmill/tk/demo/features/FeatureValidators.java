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

public class FeatureValidators extends Feature {

	protected String getExampleSrc() {
		return super.getExampleSrc();
	}

	protected String getTitle() {
		return "Validators";
	}

	protected String getDescriptionXHTML() {
	return 
		"<p>Millstone contains simple, yet powerful validation interface, "+
		"that consists of two parts: Validator and Validatable. Validator is "+
		"any class that can check validity of an Object. Validatable is "+
		"a class with configurable validation. "+
		"Validation errors are passed as special exceptions that implement "+
		"ErrorMessage interface. This way the validation errors can be "+
		"automatically added to components.</p>"+
		"<p>Utilities for simple string and null validation are provided, as "+
		"well as combinative validators. The validation interface can also "+
		"be easily implemented by the applications for more complex "+
		"validation needs.</p>";
	}

	protected String getImage() {
		return "validators.gif";
	}

}
