package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class FastStringSet extends JavaScriptObject {
    protected FastStringSet() {
        // JSO constructor
    }

    public native boolean contains(String string)
    /*-{
        return this.hasOwnProperty(string);
    }-*/;

    public native void add(String string)
    /*-{
        this[string] = true;
    }-*/;

    public native void addAll(JsArrayString array)
    /*-{
        for(var i = 0; i < array.length; i++) {
            this[array[i]] = true;
        }
    }-*/;

    public native JsArrayString dump()
    /*-{
        var array = [];
        for(var string in this) {
            array.push(string);
        }
        return array;
    }-*/;

    public native void remove(String string)
    /*-{
        delete this[string];
    }-*/;

    public static FastStringSet create() {
        return JavaScriptObject.createObject().cast();
    }
}