/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Set;

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
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VErrorMessage;

public class VForm extends ComplexPanel implements Container, KeyDownHandler {

    protected String id;

    private String height = "";

    private String width = "";

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

    private RenderInformation renderInformation = new RenderInformation();

    private int borderPaddingHorizontal = -1;

    boolean rendering = false;

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
        fieldSet.appendChild(fieldContainer);
        errorMessage.setVisible(false);
        errorMessage.setStyleName(CLASSNAME + "-errormessage");
        fieldSet.appendChild(errorMessage.getElement());
        fieldSet.appendChild(footerContainer);
    }

    public void updateSize() {

        renderInformation.updateSize(getElement());

        renderInformation.setContentAreaHeight(renderInformation
                .getRenderedSize().getHeight() - getSpaceConsumedVertically());
        renderInformation.setContentAreaWidth(renderInformation
                .getRenderedSize().getWidth() - borderPaddingHorizontal);
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        if (child == lo) {
            return renderInformation.getContentAreaSize();
        } else if (child == footer) {
            return new RenderSpace(renderInformation.getContentAreaSize()
                    .getWidth(), 0);
        } else {
            VConsole.error("Invalid child requested RenderSpace information");
            return null;
        }
    }

    public boolean hasChildComponent(Widget component) {
        return component != null && (component == lo || component == footer);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        if (!hasChildComponent(oldComponent)) {
            throw new IllegalArgumentException(
                    "Old component is not inside this Container");
        }
        remove(oldComponent);
        if (oldComponent == lo) {
            lo = newComponent;
            add(newComponent, fieldContainer);
        } else {
            footer = newComponent;
            add(newComponent, footerContainer);
        }

    }

    public boolean requestLayout(Set<Widget> child) {

        if (height != null && !"".equals(height) && width != null
                && !"".equals(width)) {
            /*
             * If the height and width has been specified the child components
             * cannot make the size of the layout change
             */

            return true;
        }

        if (renderInformation.updateSize(getElement())) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void setHeight(String height) {
        if (this.height.equals(height)) {
            return;
        }

        this.height = height;
        super.setHeight(height);

        updateSize();
    }

    /**
     * @return pixels consumed by decoration, captions, descrioptiosn etc.. In
     *         other words space, not used by the actual layout in form.
     */
    private int getSpaceConsumedVertically() {
        int offsetHeight2 = fieldSet.getOffsetHeight();
        int offsetHeight3 = fieldContainer.getOffsetHeight();
        int borderPadding = offsetHeight2 - offsetHeight3;
        return borderPadding;
    }

    @Override
    public void setWidth(String width) {
        if (borderPaddingHorizontal < 0) {
            // measure excess size lazily after stylename setting, but before
            // setting width
            int ow = getOffsetWidth();
            int dow = desc.getOffsetWidth();
            borderPaddingHorizontal = ow - dow;
        }
        if (Util.equals(this.width, width)) {
            return;
        }

        this.width = width;
        super.setWidth(width);

        updateSize();

        if (!rendering && height.equals("")) {
            // Width might affect height
            Util.updateRelativeChildrenAndSendSizeUpdateEvent(client, this,
                    this);
        }
    }

    public void onKeyDown(KeyDownEvent event) {
        shortcutHandler.handleKeyboardEvent(Event.as(event.getNativeEvent()));
    }

    public Widget getWidgetForPaintable() {
        return this;
    }

    @Override
    protected void add(Widget child, Element container) {
        // Overridden to allow VFormPaintable to call this. Should be removed
        // once functionality from VFormPaintable is moved to VForm.
        super.add(child, container);
    }
}
