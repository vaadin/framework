/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;

final class ComponentDetailMap extends JavaScriptObject {

    protected ComponentDetailMap() {
    }

    static ComponentDetailMap create() {
        return (ComponentDetailMap) JavaScriptObject.createObject();
    }

    boolean isEmpty() {
        return size() == 0;
    }

    final native boolean containsKey(String key)
    /*-{
        return this.hasOwnProperty(key);
    }-*/;

    final native ComponentDetail get(String key)
    /*-{
        return this[key];
    }-*/;

    final native void put(String id, ComponentDetail value)
    /*-{
        this[id] = value;
    }-*/;

    final native void remove(String id)
    /*-{
        delete this[id];
    }-*/;

    final native int size()
    /*-{
        var count = 0;
        for(var key in this) {
            count++;
        }
        return count;
    }-*/;

    final native void clear()
    /*-{
        for(var key in this) {
            if(this.hasOwnProperty(key)) {
                delete this[key];
            }
        }
    }-*/;

    private final native void fillWithValues(Collection<ComponentDetail> list)
    /*-{
        for(var key in this) {
            // $entry not needed as function is not exported
            list.@java.util.Collection::add(Ljava/lang/Object;)(this[key]);
        }
    }-*/;

    final Collection<ComponentDetail> values() {
        ArrayList<ComponentDetail> list = new ArrayList<ComponentDetail>();
        fillWithValues(list);
        return list;
    }

}
