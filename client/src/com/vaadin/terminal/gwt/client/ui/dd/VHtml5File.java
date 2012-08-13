/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper for html5 File object.
 */
public class VHtml5File extends JavaScriptObject {

    protected VHtml5File() {
    };

    public native final String getName()
    /*-{
        return this.name;
     }-*/;

    public native final String getType()
    /*-{
        return this.type;
     }-*/;

    public native final int getSize()
    /*-{
        return this.size ? this.size : 0;
    }-*/;

}
