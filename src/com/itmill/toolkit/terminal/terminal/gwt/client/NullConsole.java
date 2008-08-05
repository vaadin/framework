/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

/**
 * Client side console implementation for non-debug mode that discards all
 * messages.
 * 
 */
public class NullConsole implements Console {

    public void dirUIDL(UIDL u) {
    }

    public void error(String msg) {
    }

    public void log(String msg) {
    }

    public void printObject(Object msg) {
    }

}
