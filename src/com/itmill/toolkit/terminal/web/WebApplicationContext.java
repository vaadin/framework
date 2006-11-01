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

package com.itmill.toolkit.terminal.web;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.service.ApplicationContext;
import com.itmill.toolkit.ui.Window;

/** Web application context for Millstone applications.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.1
 */
public class WebApplicationContext implements ApplicationContext {

	private List listeners;
	private HttpSession session;
	private WeakHashMap formActions = new WeakHashMap();
	
	/** Create a new Web Application Context. */
	WebApplicationContext(HttpSession session) {
		this.session = session;
	}

	/** Get the form action for given window.
	 * 
	 * By default, this action is "", which preserves the current url. Commonly
	 * this is wanted to be set to <code>application.getUrl().toString()</code>
	 * or <code>window.getUrl().toString()</code> in order to clean any
	 * local links or parameters set from the action.
	 * 
	 * @param window Window for which the action is queried
	 * @return Action to be set into Form action attribute
	 */ 
	public String getWindowFormAction(Window window) {
		String action = (String) formActions.get(window);
		return action == null ? "" : action;
	}
	
	/** Set the form action for given window.
	 * 
	 * By default, this action is "", which preserves the current url. Commonly
	 * this is wanted to be set to <code>application.getUrl().toString()</code>
	 * or <code>window.getUrl().toString()</code> in order to clean any
	 * local links or parameters set from the action.
	 * 
	 * @param window Window for which the action is set
	 * @param action New action for the window.
	 */ 
	public void setWindowFormAction(Window window, String action) {
		if (action == null || action == "") 
			formActions.remove(window);
		else
			formActions.put(window,action);
	}
	
	/* (non-Javadoc)
	 * @see com.itmill.toolkit.service.ApplicationContext#getBaseDirectory()
	 */
	public File getBaseDirectory() {
		String realPath = session.getServletContext().getRealPath("/");
		return new File(realPath);
	}

	/** Get the http-session application is running in.
	 * 
	 * @return HttpSession this application context resides in
	 */
	public HttpSession getHttpSession() {
		return session;
	}

	/* (non-Javadoc)
	 * @see com.itmill.toolkit.service.ApplicationContext#getApplications()
	 */
	public Collection getApplications() {
		LinkedList applications =
			(LinkedList) session.getAttribute(
				WebAdapterServlet.SESSION_ATTR_APPS);

		return Collections.unmodifiableCollection(
			applications == null ? (new LinkedList()) : applications);
	}
	
	/** Get application context for HttpSession.
	 * 
	 * @return application context for HttpSession.
	 */
	static public WebApplicationContext getApplicationContext(HttpSession session) {
		return new WebApplicationContext(session);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return session.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return session.hashCode();
	}

	/* (non-Javadoc)
	 * @see com.itmill.toolkit.service.ApplicationContext#addTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
	 */
	public void addTransactionListener(TransactionListener listener) {
		if (this.listeners == null)
			this.listeners = new LinkedList();
		this.listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.itmill.toolkit.service.ApplicationContext#removeTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
	 */
	public void removeTransactionListener(TransactionListener listener) {
		if (this.listeners != null)
			this.listeners.remove(listener);
		
	}

	/** Notify transaction start */
	protected void startTransaction(Application application, HttpServletRequest request) {
		if (this.listeners == null) return;
		for (Iterator i = this.listeners.iterator(); i.hasNext();) {
			((ApplicationContext.TransactionListener)i.next()).transactionStart(application,request);			
		}
	}

	/** Notify transaction end */
	protected void endTransaction(Application application, HttpServletRequest request) {
		if (this.listeners == null) return;
		for (Iterator i = this.listeners.iterator(); i.hasNext();) {
			((ApplicationContext.TransactionListener)i.next()).transactionEnd(application,request);
		}
	}
}
