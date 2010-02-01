package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.NativeEvent;

public class VHtml5DragEvent extends NativeEvent {
    protected VHtml5DragEvent() {
    }

    public final native JsArrayString getTypes()
    /*-{
        return this.dataTransfer.types;
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

    public final native void setDragEffect(String effect)
    /*-{
        try {
            this.dataTransfer.dropEffect = effect;
        } catch (e){}
     }-*/;

    public final native String getEffectAllowed()
    /*-{
            return this.dataTransfer.effectAllowed;
     }-*/;
}
