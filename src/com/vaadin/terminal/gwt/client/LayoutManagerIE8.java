/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;

import com.google.gwt.dom.client.Element;

public class LayoutManagerIE8 extends LayoutManager {

    protected HashMap<Element, MeasuredSize> sizes = new HashMap<Element, MeasuredSize>();

    @Override
    protected void setMeasuredSize(Element element, MeasuredSize measuredSize) {
        if (measuredSize != null) {
            sizes.put(element, measuredSize);
        } else {
            sizes.remove(element);
        }
    }

    // @Override
    // protected native void setMeasuredSize(Element element,
    // MeasuredSize measuredSize)
    // IE8 cannot do delete element.vMeasuredSize, at least in the case when
    // element is not attached to the document (e.g. when a caption is removed)
    /*-{
        if (measuredSize) {
            element.vMeasuredSize = measuredSize;
        } else {
            element.vMeasuredSize = undefined;
        }
    //    }-*/;

    @Override
    protected MeasuredSize getMeasuredSize(Element element,
            MeasuredSize defaultSize) {
        MeasuredSize size = sizes.get(element);
        if (size != null) {
            return size;
        }
        return defaultSize;
    }

}
