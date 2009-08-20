/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Set;

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

    public void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents) {
    }

}
