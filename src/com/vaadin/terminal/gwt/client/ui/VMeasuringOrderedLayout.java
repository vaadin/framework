/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VMeasuringOrderedLayout extends ComplexPanel {

    public static final String CLASSNAME = "v-orderedlayout";

    final boolean isVertical;

    ValueMap expandRatios;

    ValueMap alignments;

    Map<VPaintableWidget, VCaption> captions = new HashMap<VPaintableWidget, VCaption>();

    final DivElement spacingMeasureElement;

    protected VMeasuringOrderedLayout(String className, boolean isVertical) {
        DivElement element = Document.get().createDivElement();
        setElement(element);
        // TODO These should actually be defined in css
        Style style = element.getStyle();
        style.setOverflow(Overflow.HIDDEN);
        style.setPosition(Position.RELATIVE);

        spacingMeasureElement = Document.get().createDivElement();
        Style spacingStyle = spacingMeasureElement.getStyle();
        spacingStyle.setPosition(Position.ABSOLUTE);
        getElement().appendChild(spacingMeasureElement);

        setStyleName(className);
        this.isVertical = isVertical;
    }

    static Element getWrapper(Widget widget) {
        return widget.getElement().getParentElement();
    }

    private void add(Widget widget, DivElement wrapper) {
        add(widget, (com.google.gwt.user.client.Element) wrapper.cast());
    }

    void addChildWidget(Widget widget) {
        DivElement wrapper = Document.get().createDivElement();
        wrapper.getStyle().setPosition(Position.ABSOLUTE);
        getElement().appendChild(wrapper);
        add(widget, wrapper);
        widget.getElement().getStyle()
                .setProperty("MozBoxSizing", "border-box");
        widget.getElement().getStyle().setProperty("boxSizing", "border-box");
    }

    void addCaption(VCaption caption, Widget widget) {
        Element wrapper = getWrapper(widget);

        // Logical attach.
        getChildren().add(caption);

        // Physical attach.
        DOM.insertBefore((com.google.gwt.user.client.Element) wrapper.cast(),
                caption.getElement(), widget.getElement());

        // Adopt.
        adopt(caption);
    }

    private void togglePrefixedStyleName(String name, boolean enabled) {
        if (enabled) {
            addStyleName(CLASSNAME + name);
        } else {
            removeStyleName(CLASSNAME + name);
        }
    }

    void updateMarginStyleNames(VMarginInfo marginInfo) {
        togglePrefixedStyleName("-margin-top", marginInfo.hasTop());
        togglePrefixedStyleName("-margin-right", marginInfo.hasRight());
        togglePrefixedStyleName("-margin-bottom", marginInfo.hasBottom());
        togglePrefixedStyleName("-margin-left", marginInfo.hasLeft());
    }

    void updateSpacingStyleName(boolean spacingEnabled) {
        if (spacingEnabled) {
            spacingMeasureElement.addClassName(CLASSNAME + "-spacing-on");
            spacingMeasureElement.removeClassName(CLASSNAME + "-spacing-off");
        } else {
            spacingMeasureElement.removeClassName(CLASSNAME + "-spacing-on");
            spacingMeasureElement.addClassName(CLASSNAME + "-spacing-off");
        }
    }
}
