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

package com.itmill.tk.terminal;

import java.util.Map;

/** Interface implemented by all the classes capable of handling external parameters.
 * 
 * <p>Some terminals can provide external parameters for application. For example
 * GET and POST parameters are passed to application as external parameters on 
 * Web Adapter. The parameters can be received at any time during the application 
 * lifecycle. All the parameter handlers implementing this interface and registered
 * to {@link com.itmill.tk.ui.Window} receive all the parameters got from
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
