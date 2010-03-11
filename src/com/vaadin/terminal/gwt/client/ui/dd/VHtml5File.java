package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper for html5 File object.
 * 
 * TODO gear support?
 */
public class VHtml5File extends JavaScriptObject {

    protected VHtml5File() {
    };

    public native final String getName()
    /*-{
        return name;
     }-*/;

    public native final String getType()
    /*-{
        return type;
     }-*/;

    public native final int getSize()
    /*-{
        return size;
    }-*/;

    public native final void readAsBinary(final Callback callback)
    /*-{
        var r = new FileReader();
        r.onloadend = function(v) {
            callback@com.vaadin.terminal.gwt.client.ui.dd.VHtml5File.Callback::handleFile(Lcom/google/gwt/core/client/JavaScriptObject)(v);
        };
        r.readAsBinary(this);
    }-*/;

    public native final void readAsDataUrl(final Callback callback)
    /*-{
        var r = new FileReader();
        r.onloadend = function(v) {
            callback@com.vaadin.terminal.gwt.client.ui.dd.VHtml5File.Callback::handleFile(Lcom/google/gwt/core/client/JavaScriptObject)(v);
        };
        r.readAsDataURL(this);
    }-*/;

    public interface Callback {
        public void handleFile(JavaScriptObject object);
    }
}
