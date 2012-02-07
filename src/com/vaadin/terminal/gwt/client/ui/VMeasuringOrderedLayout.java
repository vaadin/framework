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
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VMeasuringOrderedLayout extends ComplexPanel {

    public static final String CLASSNAME = "v-orderedlayout";

    private static final int MARGIN_SIZE = 20;

    final boolean isVertical;

    ApplicationConnection client;

    String id;

    private RenderSpace space;

    ValueMap expandRatios;

    ValueMap alignments;

    Map<VPaintableWidget, VCaption> captions = new HashMap<VPaintableWidget, VCaption>();

    boolean spacing;

    VMarginInfo activeMarginsInfo;

    protected VMeasuringOrderedLayout(String className, boolean isVertical) {
        DivElement element = Document.get().createDivElement();
        setElement(element);
        // TODO These should actually be defined in css
        Style style = element.getStyle();
        style.setOverflow(Overflow.HIDDEN);
        style.setPosition(Position.RELATIVE);

        setStyleName(className);
        this.isVertical = isVertical;
    }

    static Element getWrapper(Widget widget) {
        return widget.getElement().getParentElement();
    }

    private void add(Widget widget, DivElement wrapper) {
        add(widget, (com.google.gwt.user.client.Element) wrapper.cast());
    }

    int getEndMarginInDirection(boolean isVertical) {
        if (isVertical) {
            return activeMarginsInfo.hasBottom() ? MARGIN_SIZE : 0;
        } else {
            return activeMarginsInfo.hasRight() ? MARGIN_SIZE : 0;
        }
    }

    int getStartMarginInDirection(boolean isVertical) {
        if (isVertical) {
            return activeMarginsInfo.hasTop() ? MARGIN_SIZE : 0;
        } else {
            return activeMarginsInfo.hasLeft() ? MARGIN_SIZE : 0;
        }
    }

    int getSpacingInDirection(boolean isVertical) {
        if (spacing) {
            return 20;
        } else {
            return 0;
        }
    }

    AlignmentInfo getAlignment(VPaintableWidget child) {
        String pid = VPaintableMap.get(client).getPid(child);
        if (alignments.containsKey(pid)) {
            return new AlignmentInfo(alignments.getInt(pid));
        } else {
            return AlignmentInfo.TOP_LEFT;
        }
    }

    double getExpandRatio(VPaintableWidget child) {
        String pid = VPaintableMap.get(client).getPid(child);
        if (expandRatios.containsKey(pid)) {
            return expandRatios.getRawNumber(pid);
        } else {
            return 0;
        }
    }

    void addChildWidget(Widget widget) {
        DivElement wrapper = Document.get().createDivElement();
        wrapper.getStyle().setPosition(Position.ABSOLUTE);
        getElement().appendChild(wrapper);
        add(widget, wrapper);
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

    void remove(VCaption caption) {
        remove(caption);
    }
}
