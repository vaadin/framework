/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.terminal.gwt.server;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
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

/**
 * Web application context for the IT Mill Toolkit applications.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public class WebApplicationContext implements ApplicationContext {

	private List listeners;

	private HttpSession session;

	private WeakHashMap formActions = new WeakHashMap();

	/**
	 * Creates a new Web Application Context.
	 * 
	 * @param session
	 *            the HTTP session.
	 */
	WebApplicationContext(HttpSession session) {
		this.session = session;
	}

	/**
	 * Gets the form action for given window.
	 * <p>
	 * By default, this action is "", which preserves the current url. Commonly
	 * this is wanted to be set to <code>application.getUrl.toString</code> or
	 * <code>window.getUrl.toString</code> in order to clean any local links
	 * or parameters set from the action.
	 * </p>
	 * 
	 * @param window
	 *            the Window for which the action is queried.
	 * @return the Action to be set into Form action attribute.
	 */
	public String getWindowFormAction(Window window) {
		String action = (String) formActions.get(window);
		return action == null ? "" : action;
	}

	/**
	 * Sets the form action for given window.
	 * <p>
	 * By default, this action is "", which preserves the current url. Commonly
	 * this is wanted to be set to <code>application.getUrl.toString</code> or
	 * <code>window.getUrl.toString</code> in order to clean any local links
	 * or parameters set from the action.
	 * </p>
	 * 
	 * @param window
	 *            the Window for which the action is set.
	 * @param action
	 *            the New action for the window.
	 */
	public void setWindowFormAction(Window window, String action) {
		if (action == null || action == "")
			formActions.remove(window);
		else
			formActions.put(window, action);
	}

	/**
	 * Gets the application context base directory.
	 * 
	 * @see com.itmill.toolkit.service.ApplicationContext#getBaseDirectory()
	 */
	public File getBaseDirectory() {
		String realPath = ApplicationServlet.getResourcePath(session
				.getServletContext(), "/");
		if (realPath == null)
			return null;
		return new File(realPath);
	}

	/**
	 * Gets the http-session application is running in.
	 * 
	 * @return HttpSession this application context resides in.
	 */
	public HttpSession getHttpSession() {
		return session;
	}

	/**
	 * Gets the applications in this context.
	 * 
	 * @see com.itmill.toolkit.service.ApplicationContext#getApplications()
	 */
	public Collection getApplications() {
		LinkedList applications = (LinkedList) session
				.getAttribute(ApplicationServlet.SESSION_ATTR_APPS);

		return Collections
				.unmodifiableCollection(applications == null ? (new LinkedList())
						: applications);
	}

	/**
	 * Gets the application context for HttpSession.
	 * 
	 * @param session
	 *            the HTTP session.
	 * @return the application context for HttpSession.
	 */
	static public WebApplicationContext getApplicationContext(
			HttpSession session) {
		return new WebApplicationContext(session);
	}

	/**
	 * Returns <code>true</code> if and only if the argument is not
	 * <code>null</code> and is a Boolean object that represents the same
	 * boolean value as this object.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return session.equals(obj);
	}

	/**
	 * Returns the hash code value .
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return session.hashCode();
	}

	/**
	 * Adds the transaction listener to this context.
	 * 
	 * @see com.itmill.toolkit.service.ApplicationContext#addTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
	 */
	public void addTransactionListener(TransactionListener listener) {
		if (this.listeners == null)
			this.listeners = new LinkedList();
		this.listeners.add(listener);
	}

	/**
	 * Removes the transaction listener from this context.
	 * 
	 * @see com.itmill.toolkit.service.ApplicationContext#removeTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
	 */
	public void removeTransactionListener(TransactionListener listener) {
		if (this.listeners != null)
			this.listeners.remove(listener);

	}

	/**
	 * Notifies the transaction start.
	 * 
	 * @param application
	 * @param request
	 *            the HTTP request.
	 */
	protected void startTransaction(Application application,
			HttpServletRequest request) {
		if (this.listeners == null)
			return;
		for (Iterator i = this.listeners.iterator(); i.hasNext();) {
			((ApplicationContext.TransactionListener) i.next())
					.transactionStart(application, request);
		}
	}

	/**
	 * Notifies the transaction end.
	 * 
	 * @param application
	 * @param request
	 *            the HTTP request.
	 */
	protected void endTransaction(Application application,
			HttpServletRequest request) {
		if (this.listeners == null)
			return;

		LinkedList exceptions = null;
		for (Iterator i = this.listeners.iterator(); i.hasNext();)
			try {
				((ApplicationContext.TransactionListener) i.next())
						.transactionEnd(application, request);
			} catch (RuntimeException t) {
				if (exceptions == null)
					exceptions = new LinkedList();
				exceptions.add(t);
			}

		// If any runtime exceptions occurred, throw a combined exception
		if (exceptions != null) {
			StringBuffer msg = new StringBuffer();
			for (Iterator i = listeners.iterator(); i.hasNext();) {
				RuntimeException e = (RuntimeException) i.next();
				if (msg.length() == 0)
					msg.append("\n\n--------------------------\n\n");
				msg.append(e.getMessage() + "\n");
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace, true));
				msg.append(trace.toString());
			}
			throw new RuntimeException(msg.toString());
		}
	}

}
