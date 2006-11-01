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

package com.itmill.toolkit.service;

import java.io.File;
import java.util.Collection;

import com.itmill.toolkit.Application;

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
