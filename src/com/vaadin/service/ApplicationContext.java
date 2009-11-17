/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.service;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;

/**
 * <code>ApplicationContext</code> provides information about the running
 * context of the application. Each context is shared by all applications that
 * are open for one user. In a web-environment this corresponds to a
 * HttpSession.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public interface ApplicationContext extends Serializable {

    /**
     * Returns application context base directory.
     * 
     * Typically an application is deployed in a such way that is has an
     * application directory. For web applications this directory is the root
     * directory of the web applications. In some cases applications might not
     * have an application directory (for example web applications running
     * inside a war).
     * 
     * @return The application base directory or null if the application has no
     *         base directory.
     */
    public File getBaseDirectory();

    /**
     * Returns a collection of all the applications in this context.
     * 
     * Each application context contains all active applications for one user.
     * 
     * @return A collection containing all the applications in this context.
     */
    public Collection<Application> getApplications();
    
    /**
     * Adds a transaction listener to this context.
     * 
     * @param listener
     *            the listener to be added.
     * @see TransactionListener
     */
    public void addTransactionListener(TransactionListener listener);

    /**
     * Removes a transaction listener from this context.
     * 
     * @param listener
     *            the listener to be removed.
     * @see TransactionListener
     */
    public void removeTransactionListener(TransactionListener listener);

    /**
     * Interface for listening to transaction events. Implement this interface
     * to listen to all transactions between the client and the application.
     * 
     */
    public interface TransactionListener extends Serializable {

        /**
         * Invoked at the beginning of every transaction.
         * 
         * The transaction is linked to the context, not the application so if
         * you have multiple applications running in the same context you need
         * to check that the request is associated with the application you are
         * interested in. This can be done looking at the application parameter.
         * 
         * @param application
         *            the Application object.
         * @param transactionData
         *            the Data identifying the transaction.
         */
        public void transactionStart(Application application,
                Object transactionData);

        /**
         * Invoked at the end of every transaction.
         * 
         * The transaction is linked to the context, not the application so if
         * you have multiple applications running in the same context you need
         * to check that the request is associated with the application you are
         * interested in. This can be done looking at the application parameter.
         * 
         * @param applcation
         *            the Application object.
         * @param transactionData
         *            the Data identifying the transaction.
         */
        public void transactionEnd(Application application,
                Object transactionData);

    }
}
