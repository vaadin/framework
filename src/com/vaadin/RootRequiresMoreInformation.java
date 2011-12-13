/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;

/**
 * Exception that is thrown to indicate that creating or initializing the root
 * requires information detailed from the web browser ({@link BrowserDetails})
 * to be present.
 * 
 * This exception may not be thrown if that information is already present in
 * the current WrappedRequest.
 * 
 * @see Application#getRoot(WrappedRequest)
 * @see WrappedRequest#getBrowserDetails()
 * 
 * @since 7.0
 */
public class RootRequiresMoreInformation extends Exception {
    // Nothing of interest here
}
