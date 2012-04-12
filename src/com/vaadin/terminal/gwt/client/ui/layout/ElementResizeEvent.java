/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.layout;

import com.google.gwt.dom.client.Element;
import com.vaadin.terminal.gwt.client.LayoutManager;

public class ElementResizeEvent {
    private final Element element;
    private final LayoutManager layoutManager;

    public ElementResizeEvent(LayoutManager layoutManager, Element element) {
        this.layoutManager = layoutManager;
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }
}
