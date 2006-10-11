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

package com.enably.tk.service;

import java.io.File;
import java.util.Collection;

import com.enably.tk.Application;

/** Application context provides information about the running context of
 * the application. Each context is shared by all applications that are open
 * for one user. In web-environment this corresponds to HttpSession.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.1
 */
public interface ApplicationContext {
	
	/** Returns application context base directory.
	 * 
	 * Typically millstone application is deployed in a such way that is 
	 * has application directory. For web applications this directory is the
	 * root directory of the web applications. In some cases application 
	 * might not have application directory (for example web applications 
	 * running inside of war).
	 * 
	 * @return The application base directory
	 */
	public File getBaseDirectory();
	
	/** Get the applications in this context.
	 * 
	 * Get all applications in this context. Each application context contains
	 * all applications that are open for one user.
	 * 
	 * @return Collection containing all applications in this context
	 */
	public Collection getApplications();
	
	
	/** Add transaction listener to this context.
	 * @param listener The listener to be added.
	 * @see TransactionListener
	 */
	public void addTransactionListener(TransactionListener listener);

	/** Remove transaction listener from this context.
	 * @param listener The listener to be removed.
	 * @see TransactionListener
	 */
	public void removeTransactionListener(TransactionListener listener);
	
	/** Interface for listening the application transaction events. 
	 *  Implementations of this interface can be used to listen all 
	 *  transactions between the client and the application.
	 *  
	 */
	public interface TransactionListener {
	
		/** Invoked at the beginning of every transaction.
		 * @param transactionData Data identifying the transaction.
		 */
		public void transactionStart(Application application, Object transactionData);
		

		/** Invoked at the end of every transaction.
		 * @param transactionData Data identifying the transaction.
		 */
		public void transactionEnd(Application application, Object transactionData);

	}
}
