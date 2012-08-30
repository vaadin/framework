/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.service;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;

import com.vaadin.Application;

/**
 * <code>ApplicationContext</code> provides information about the running
 * context of the application. Each context is shared by all applications that
 * are open for one user. In a web-environment this corresponds to a
 * HttpSession.
 * 
 * @author Vaadin Ltd.
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
     * Adds a transaction listener to this context. The transaction listener is
     * called before and after each each request related to this session except
     * when serving static resources.
     * 
     * The transaction listener must not be null.
     * 
     * @see com.vaadin.service.ApplicationContext#addTransactionListener(com.vaadin.service.ApplicationContext.TransactionListener)
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
     * Returns the time between requests, in seconds, before this context is
     * invalidated. A negative time indicates the context should never timeout.
     */
    public int getMaxInactiveInterval();

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
