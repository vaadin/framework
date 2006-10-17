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

package com.enably.tk.terminal;

import java.util.Map;

/** Interface implemented by all the classes capable of handling external parameters.
 * 
 * <p>Some terminals can provide external parameters for application. For example
 * GET and POST parameters are passed to application as external parameters on 
 * Web Adapter. The parameters can be received at any time during the application 
 * lifecycle. All the parameter handlers implementing this interface and registered
 * to {@link com.enably.tk.ui.Window} receive all the parameters got from
 * the terminal in the given window.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface ParameterHandler {
    
    /** Handle parameters. 
     * 
     * <p>Handle the given parameters. The parameters are given as inmodifieable 
     * name to value map. All parameters names are of type: {@link java.lang.String}.
     * All the parameter values are arrays of strings.</p>
     * 
     * @param parameters Inmodifiable name to value[] mapping.
     * 
     */
    public void handleParameters(Map parameters);

	/** ParameterHandler error event */
	public interface ErrorEvent extends Terminal.ErrorEvent {

		/** Get the source ParameterHandler. */
		public ParameterHandler getParameterHandler();

	}

}
