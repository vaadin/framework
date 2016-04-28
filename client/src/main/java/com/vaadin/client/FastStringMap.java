/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class FastStringMap<T> extends JavaScriptObject {

    protected FastStringMap() {
        // JSO constructor
    }

    public native void put(String key, T value)
    /*-{
         this[key] = value;
    }-*/;

    public native T get(String key)
    /*-{
         return this[key];
    }-*/;

    public native boolean containsKey(String key)
    /*-{
         //Can't use this.hasOwnProperty in case that key is used
         return Object.hasOwnProperty.call(this, key);
    }-*/;

    public native void remove(String key)
    /*-{
         delete this[key];
    }-*/;

    public native JsArrayString getKeys()
    /*-{
        var keys = [];
        for(var key in this) {
            if (Object.hasOwnProperty.call(this, key)) {
                keys.push(key);
            } 
        }
        return keys;
    }-*/;

    public native int size()
    /*-{
        var size = 0;
        for(var key in this) {
            if (Object.hasOwnProperty.call(this, key)) {
                size++;
            } 
        }
        return size;
    }-*/;

    public static <T> FastStringMap<T> create() {
        return JavaScriptObject.createObject().cast();
    }
}
