/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Set;

public interface Console {

    public abstract void log(String msg);

    public abstract void error(String msg);

    public abstract void printObject(Object msg);

    public abstract void dirUIDL(UIDL u);

    public abstract void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents);

}