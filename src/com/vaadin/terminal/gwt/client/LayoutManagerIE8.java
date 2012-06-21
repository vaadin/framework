/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class LayoutManagerIE8 extends LayoutManager {

    private Map<Element, MeasuredSize> measuredSizes = new HashMap<Element, MeasuredSize>();

    @Override
    protected void setMeasuredSize(Element element, MeasuredSize measuredSize) {
        if (measuredSize != null) {
            measuredSizes.put(element, measuredSize);
        } else {
            measuredSizes.remove(element);
        }
    }

    @Override
    protected MeasuredSize getMeasuredSize(Element element,
            MeasuredSize defaultSize) {
        MeasuredSize measured = measuredSizes.get(element);
        if (measured != null) {
            return measured;
        } else {
            return defaultSize;
        }
    }

    @Override
    protected void cleanMeasuredSizes() {
        Iterator<Element> i = measuredSizes.keySet().iterator();
        while (i.hasNext()) {
            Element e = i.next();
            if (e.getOwnerDocument() != RootPanel.get().getElement()
                    .getOwnerDocument()) {
                i.remove();
            }
        }
    }
}
