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

    public native final void readAsBinary(final Callback callback)
    /*-{
        var r = new FileReader();
        r.onloadend = function(content) {
            callback.@com.vaadin.terminal.gwt.client.ui.dd.VHtml5File.Callback::handleFile(Lcom/google/gwt/core/client/JavaScriptObject;)(content);
        };
        r.readAsBinaryString(this);
        var j = 0;
        
    }-*/;

    public native final void readAsDataUrl(final Callback callback)
    /*-{
        var r = new FileReader();
        r.onloadend = function(content) {
            callback.@com.vaadin.terminal.gwt.client.ui.dd.VHtml5File.Callback::handleFile(Lcom/google/gwt/core/client/JavaScriptObject;)(content);
        };
        r.readAsDataURL(this);
    }-*/;

    public interface Callback {
        public void handleFile(JavaScriptObject object);
    }
}
