/* **********************************************************************

 Millstone AJAX Adaper
 

 Millstone is a registered trademark of IT Mill Ltd
 Copyright 2000-2005 IT Mill Ltd, All rights reserved
 Use without explicit license from IT Mill Ltd is prohibited.
 
 For more information, contact:

 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:  +358 2 4802 7181
 20540, Turku                          email: info@itmill.com
 Finland                               company www: www.itmill.com

 ********************************************************************** */

package com.itmill.tk.terminal.ajax;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.itmill.tk.Application;
import com.itmill.tk.service.ApplicationContext;

/** Application context for ajax applications.
 * 
 * @author IT Mill Ltd, Joonas Lehtinen
 * @version @VERSION@
 * @since 3.1
 */
public class AjaxApplicationContext implements ApplicationContext {

    private LinkedList applications = new LinkedList();

    private ServletContext servletContext;

    private LinkedList transactionListeners = new LinkedList();

    private WeakHashMap applicationToManagerMap = new WeakHashMap();

    AjaxApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    ApplicationManager getApplicationManager(Application application) {
        ApplicationManager vm = (ApplicationManager) applicationToManagerMap
                .get(application);
        if (vm == null) {
            vm = new ApplicationManager(application);
            applicationToManagerMap.put(application, vm);
        }
        return vm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.tk.service.ApplicationContext#getBaseDirectory()
     */
    public File getBaseDirectory() {

        String path = servletContext.getRealPath("/");
        if (path == null)
            return null;
        return new File(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.tk.service.ApplicationContext#getApplications()
     */
    public Collection getApplications() {
        return Collections.unmodifiableCollection(applications);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.tk.service.ApplicationContext#addTransactionListener(com.itmill.tk.service.ApplicationContext.TransactionListener)
     */
    public void addTransactionListener(TransactionListener listener) {
        transactionListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.tk.service.ApplicationContext#removeTransactionListener(com.itmill.tk.service.ApplicationContext.TransactionListener)
     */
    public void removeTransactionListener(TransactionListener listener) {
        transactionListeners.remove(listener);
    }

    /**
     * Create a new application.
     * 
     * @return New application instance
     */
    Application createApplication(Class applicationClass, URL applicationUrl,
            Locale locale, Properties applicationStartProperties)
            throws InstantiationException, IllegalAccessException {

        Application application = null;

        // Create new application and start it
        try {
            application = (Application) applicationClass.newInstance();
            applications.add(application);
            application.setLocale(locale);

            getApplicationManager(application).takeControl();

            application.start(applicationUrl, applicationStartProperties, this);

        } catch (IllegalAccessException e) {
            Log.error("Illegal access to application class "
                    + applicationClass.getName());
            throw e;
        } catch (InstantiationException e) {
            Log.error("Failed to instantiate application class: "
                    + applicationClass.getName());
            throw e;
        }

        return application;
    }

    void removeApplication(Application application) {
        applications.remove(application);
    }

    Application getApplication(URL applicationUrl, String servletPath) {
        // Search for the application (using the application URI) from the list
        Application application = null;
        for (Iterator i = applications.iterator(); i.hasNext()
                && application == null;) {
            Application a = (Application) i.next();
            String aPath = a.getURL().getPath();
            if (servletPath.length() < aPath.length())
                servletPath += "/";
            if (servletPath.equals(aPath))
                application = a;
        }

        // Remove stopped application from the list
        if (application != null && !application.isRunning()) {
            applications.remove(application);
            application = null;
        }

        return application;
    }
    
	/** Notify transaction start */
	protected void startTransaction(Application application, HttpServletRequest request) {
		if (this.transactionListeners == null) return;
		for (Iterator i = this.transactionListeners.iterator(); i.hasNext();) {
			((ApplicationContext.TransactionListener)i.next()).transactionStart(application,request);			
		}
	}

	/** Notify transaction end */
	protected void endTransaction(Application application, HttpServletRequest request) {
		if (this.transactionListeners == null) return;
		for (Iterator i = this.transactionListeners.iterator(); i.hasNext();) {
			((ApplicationContext.TransactionListener)i.next()).transactionEnd(application,request);
		}
	}

	/** Closes this application context and all applications bound to it.
	 * 
	 */
	public void close() {
		for (Iterator i = this.applications.iterator(); i.hasNext();) {
			Application app = (Application) i.next();
			app.close();
		}
		
	}
    
}
