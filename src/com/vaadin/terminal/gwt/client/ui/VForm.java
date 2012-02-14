/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.VErrorMessage;

public class VForm extends ComplexPanel implements KeyDownHandler {

    protected String id;

    public static final String CLASSNAME = "v-form";

    Widget lo;
    Element legend = DOM.createLegend();
    Element caption = DOM.createSpan();
    private Element errorIndicatorElement = DOM.createDiv();
    Element desc = DOM.createDiv();
    Icon icon;
    VErrorMessage errorMessage = new VErrorMessage();

    Element fieldContainer = DOM.createDiv();

    Element footerContainer = DOM.createDiv();

    Element fieldSet = DOM.createFieldSet();

    Widget footer;

    ApplicationConnection client;

    ShortcutActionHandler shortcutHandler;

    HandlerRegistration keyDownRegistration;

    public VForm() {
        setElement(DOM.createDiv());
        getElement().appendChild(fieldSet);
        setStyleName(CLASSNAME);
        fieldSet.appendChild(legend);
        legend.appendChild(caption);
        errorIndicatorElement.setClassName("v-errorindicator");
        errorIndicatorElement.getStyle().setDisplay(Display.NONE);
        errorIndicatorElement.setInnerText(" "); // needed for IE
        desc.setClassName("v-form-description");
        fieldSet.appendChild(desc); // Adding description for initial padding
                                    // measurements, removed later if no
                                    // description is set
        fieldContainer.setClassName(CLASSNAME + "-content");
        fieldSet.appendChild(fieldContainer);
        errorMessage.setVisible(false);
        errorMessage.setStyleName(CLASSNAME + "-errormessage");
        fieldSet.appendChild(errorMessage.getElement());
        fieldSet.appendChild(footerContainer);
    }

    public void onKeyDown(KeyDownEvent event) {
        shortcutHandler.handleKeyboardEvent(Event.as(event.getNativeEvent()));
    }

    @Override
    protected void add(Widget child, Element container) {
        // Overridden to allow VFormPaintable to call this. Should be removed
        // once functionality from VFormPaintable is moved to VForm.
        super.add(child, container);
    }
}
