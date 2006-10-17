/* *************************************************************************
 
                               Enably Toolkit 

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
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.demo.features;

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
