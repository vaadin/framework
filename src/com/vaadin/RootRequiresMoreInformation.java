/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin;

/**
 * Exception that is thrown to indicate that creating or initializing the root
 * requires information from the web browser (e.g. screen size or URI fragment)
 * to be present.
 * 
 * This exception may not be thrown if that information is already present in
 * the current WrappedRequest.
 */
public class RootRequiresMoreInformation extends Exception {
    // Nothing of interest here
}
