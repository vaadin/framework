/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.client.ui.dd;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.client.extensions.DropTargetExtensionConnector;

/**
 * Helper class to access html5 style drag events.
 *
 * @author Vaadin Ltd
 * @deprecated Since 8.1, no direct replacement currently, see
 *             {@link DropTargetExtensionConnector}
 */
@Deprecated
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
        if (this.dataTransfer.files.length > 0 && this.dataTransfer.files[0].getAsText) {
            return this.dataTransfer.files[index].getAsText("UTF-8");
        }
        return null;
    }-*/;

    public final native void setDropEffect(String effect)
    /*-{
        try {
            this.dataTransfer.dropEffect = effect;
        } catch (e) {}
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

    /**
     * Detects if dropped element is a file. <br>
     * Always returns <code>true</code> on Safari even if the dropped element is
     * a folder.
     */
    public final native boolean isFile(int fileIndex)
    /*-{
        // Chrome >= v21 and Opera >= v?
        if (this.dataTransfer.items) {
            var item = this.dataTransfer.items[fileIndex];
            if (typeof item.webkitGetAsEntry == "function") {
                var entry = item.webkitGetAsEntry();
                if (typeof entry !== "undefined" && entry !== null) {
                    return entry.isFile;
                }
            }
        }

        // Zero sized files without a type are also likely to be folders
        var file = this.dataTransfer.files[fileIndex];
        if (file.size == 0 && !file.type) {
            return false;
        }

        // TODO Make it detect folders on all browsers

        return true;
    }-*/;

    public final native void setHtml5DataFlavor(String flavor, String data)
    /*-{
        this.dataTransfer.setData(flavor, data);
    }-*/;

}
