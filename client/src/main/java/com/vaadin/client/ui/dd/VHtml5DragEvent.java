/*
 * Copyright 2000-2021 Vaadin Ltd.
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
    /** Singleton. */
    protected VHtml5DragEvent() {
    }

    /**
     * Returns type values, or {@code ["Text","Url","Html"]} if types are not
     * supported.
     *
     * @return types
     */
    public final native JsArrayString getTypes()
    /*-{
        // IE does not support types, return some basic values
        return this.dataTransfer.types ? this.dataTransfer.types : ["Text","Url","Html"];
     }-*/;

    /**
     * Returns the data for the given type as text.
     *
     * @param type
     *            the type whose data to retrieve
     * @return the data as text
     */
    public final native String getDataAsText(String type)
    /*-{
         var v = this.dataTransfer.getData(type);
         return v;
     }-*/;

    /**
     * Works on FF 3.6 and possibly with gears.
     *
     * @param index
     *            the index of the file to get
     * @return the file as text
     * @deprecated this method is no longer used internally
     */
    @Deprecated
    public final native String getFileAsString(int index)
    /*-{
        if (this.dataTransfer.files.length > 0 && this.dataTransfer.files[0].getAsText) {
            return this.dataTransfer.files[index].getAsText("UTF-8");
        }
        return null;
    }-*/;

    /**
     * Sets the drop effect value.
     *
     * @param effect
     *            the drop effect
     */
    public final native void setDropEffect(String effect)
    /*-{
        try {
            this.dataTransfer.dropEffect = effect;
        } catch (e) {}
     }-*/;

    /**
     * Returns whether drop effect is allowed or not.
     *
     * @return {@code true} id drop effect is allowed, {@code false} otherwise
     */
    public final native String getEffectAllowed()
    /*-{
            return this.dataTransfer.effectAllowed;
     }-*/;

    /**
     * Sets whether drop effect is allowed or not.
     *
     * @param effect
     *            {@code true} id drop effect should be allowed, {@code false}
     *            otherwise
     */
    public final native void setEffectAllowed(String effect)
    /*-{
            this.dataTransfer.effectAllowed = effect;
     }-*/;

    /**
     * Returns the transfer file count.
     *
     * @return the file count
     */
    public final native int getFileCount()
    /*-{
            return this.dataTransfer.files ? this.dataTransfer.files.length : 0;
     }-*/;

    /**
     * Returns the file indicated by the given index.
     *
     * @param fileIndex
     *            the index of the file
     * @return the file
     */
    public final native VHtml5File getFile(int fileIndex)
    /*-{
            return this.dataTransfer.files[fileIndex];
     }-*/;

    /**
     * Detects if dropped element is a file. <br>
     * Always returns <code>true</code> on Safari even if the dropped element is
     * a folder.
     *
     * @param fileIndex
     *            the index of the element to check
     * @return {@code true} if the dropped element is a file, {@code false}
     *         otherwise
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

    /**
     * Adds a data String with the given flavor identifier.
     *
     * @param flavor
     *            the identifier
     * @param data
     *            the data
     */
    public final native void setHtml5DataFlavor(String flavor, String data)
    /*-{
        this.dataTransfer.setData(flavor, data);
    }-*/;

}
