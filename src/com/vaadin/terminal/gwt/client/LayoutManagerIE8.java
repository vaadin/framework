/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.dom.client.Element;

public class LayoutManagerIE8 extends LayoutManager {

    @Override
    protected native void setMeasuredSize(Element element,
            MeasuredSize measuredSize)
    // IE8 cannot do delete element.vMeasuredSize, at least in the case when
    // element is not attached to the document (e.g. when a caption is removed)
    /*-{
        if (measuredSize) {
            element.vMeasuredSize = measuredSize;
        } else {
            element.vMeasuredSize = undefined;
        }
    }-*/;

}
