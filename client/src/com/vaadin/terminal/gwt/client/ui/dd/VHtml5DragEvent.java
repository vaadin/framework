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
package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.NativeEvent;

/**
 * Helper class to access html5 style drag events.
 * 
 * TODO Gears support ?
 */
public class VHtml5DragEvent extends NativeEvent {
    protected VHtml5DragEvent() {
    }

    public final native JsArrayString getTypes()
    /*-{
        // IE does not support types, return some basic values
        return this.dataTransfer.types ? this.dataTransfer.types : ["Text","Url","Html"];
     }-*/;

    public final native String getDataAsText(String type)
    /*-{
         var v = this.dataTransfer.getData(type);
         return v;
     }-*/;

    /**
     * Works on FF 3.6 and possibly with gears.
     * 
     * @param index
     * @return
     */
    public final native String getFileAsString(int index)
    /*-{
        if(this.dataTransfer.files.length > 0 && this.dataTransfer.files[0].getAsText) {
            return this.dataTransfer.files[index].getAsText("UTF-8");
        }
        return null;
    }-*/;

    /**
     * @deprecated As of Vaadin 6.8, replaced by {@link #setDropEffect(String)}.
     */
    @Deprecated
    public final void setDragEffect(String effect) {
        setDropEffect(effect);
    }

    public final native void setDropEffect(String effect)
    /*-{
        try {
            this.dataTransfer.dropEffect = effect;
        } catch (e){}
     }-*/;

    public final native String getEffectAllowed()
    /*-{
            return this.dataTransfer.effectAllowed;
     }-*/;

    public final native void setEffectAllowed(String effect)
    /*-{
            this.dataTransfer.effectAllowed = effect;
     }-*/;

    public final native int getFileCount()
    /*-{
            return this.dataTransfer.files ? this.dataTransfer.files.length : 0;
     }-*/;

    public final native VHtml5File getFile(int fileIndex)
    /*-{
            return this.dataTransfer.files[fileIndex];
     }-*/;

    public final native void setHtml5DataFlavor(String flavor, String data)
    /*-{
        this.dataTransfer.setData(flavor, data);
    }-*/;

}
