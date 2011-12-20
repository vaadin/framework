/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Set;

public interface Console {

    public abstract void log(String msg);

    public abstract void log(Throwable e);

    public abstract void error(Throwable e);

    public abstract void error(String msg);

    public abstract void printObject(Object msg);

    public abstract void dirUIDL(ValueMap u, ApplicationConfiguration cnf);

    public abstract void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents);

    public abstract void setQuietMode(boolean quietDebugMode);

    public abstract void init();

}