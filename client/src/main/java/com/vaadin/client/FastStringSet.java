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

import java.util.Collection;

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

    public native void addAll(FastStringSet set)
    /*-{
        for(var string in set) {
            if (Object.hasOwnProperty.call(set, string)) {
                this[string] = true;
            }
        }
    }-*/;

    public native JsArrayString dump()
    /*-{
        var array = [];
        for(var string in this) {
            if (this.hasOwnProperty(string)) {
                array.push(string);
            }
        }
        return array;
    }-*/;

    public native void remove(String string)
    /*-{
        delete this[string];
    }-*/;

    public native boolean isEmpty()
    /*-{
        for(var string in this) {
            if (this.hasOwnProperty(string)) {
                return false;
            }
        }
        return true;
    }-*/;

    public static FastStringSet create() {
        return JavaScriptObject.createObject().cast();
    }

    public native void addAllTo(Collection<String> target)
    /*-{
        for(var string in this) {
            if (Object.hasOwnProperty.call(this, string)) {
                target.@java.util.Collection::add(Ljava/lang/Object;)(string);
            }
        }
     }-*/;

    public native void removeAll(FastStringSet valuesToRemove)
    /*-{
        for(var string in valuesToRemove) {
            if (Object.hasOwnProperty.call(valuesToRemove, string)) {
                delete this[string];
            }
        }
    }-*/;
}
