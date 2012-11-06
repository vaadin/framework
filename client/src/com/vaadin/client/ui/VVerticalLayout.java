/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.client.ui;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.Util;
import com.vaadin.client.ui.orderedlayout.Slot;
import com.vaadin.client.ui.orderedlayout.VAbstractOrderedLayout;

/**
 * Represents a layout where the children is ordered vertically
 */
public class VVerticalLayout extends VAbstractOrderedLayout {

    public static final String CLASSNAME = "v-verticallayout";

    /**
     * Default constructor
     */
    public VVerticalLayout() {
        setStyleName(CLASSNAME);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        addStyleName(StyleConstants.UI_LAYOUT);
        addStyleName("v-vertical");
    }

    @Override
    protected void recalculateExpands() {
        double total = 0;
        for (Slot slot : widgetToSlot.values()) {
            if (slot.getExpandRatio() > -1) {
                total += slot.getExpandRatio();
            } else {

                slot.getElement().getStyle().clearHeight();

            }
        }
        for (Slot slot : widgetToSlot.values()) {
            if (slot.getExpandRatio() > -1) {
                slot.setHeight((100 * (slot.getExpandRatio() / total)) + "%");
                if (slot.isRelativeHeight()) {
                    Util.notifyParentOfSizeChange(this, true);
                }
            }
        }
    }

    @Override
    protected void clearExpand() {
        if (expandWrapper != null) {
            for (; expandWrapper.getChildCount() > 0;) {
                Element el = expandWrapper.getChild(0).cast();
                getElement().appendChild(el);
                el.getStyle().clearHeight();
                el.getStyle().clearMarginTop();
            }
            expandWrapper.removeFromParent();
            expandWrapper = null;
        }
    }

    @Override
    public void updateExpand() {
        boolean isExpanding = false;
        for (Widget slot : getChildren()) {
            if (((Slot) slot).getExpandRatio() > -1) {
                isExpanding = true;
            } else {
                slot.getElement().getStyle().clearHeight();
            }
            slot.getElement().getStyle().clearMarginLeft();
            slot.getElement().getStyle().clearMarginTop();
        }

        if (isExpanding) {
            if (expandWrapper == null) {
                expandWrapper = DOM.createDiv();
                expandWrapper.setClassName("v-expand");
                for (; getElement().getChildCount() > 0;) {
                    Node el = getElement().getChild(0);
                    expandWrapper.appendChild(el);
                }
                getElement().appendChild(expandWrapper);
            }

            int totalSize = 0;
            for (Widget w : getChildren()) {
                Slot slot = (Slot) w;
                if (slot.getExpandRatio() == -1) {

                    if (layoutManager != null) {
                        // TODO check caption position
                        int size = layoutManager.getOuterHeight(slot
                                .getWidget().getElement())
                                - layoutManager.getMarginHeight(slot
                                        .getWidget().getElement());
                        if (slot.hasCaption()) {
                            size += layoutManager.getOuterHeight(slot
                                    .getCaptionElement())
                                    - layoutManager.getMarginHeight(slot
                                            .getCaptionElement());
                        }
                        if (size > 0) {
                            totalSize += size;
                        }

                    } else {
                        totalSize += slot.getOffsetHeight();
                    }
                }
                // TODO fails in Opera, always returns 0
                int spacingSize = slot.getVerticalSpacing();
                if (spacingSize > 0) {
                    totalSize += spacingSize;
                }
            }

            // When we set the margin to the first child, we don't need
            // overflow:hidden in the layout root element, since the wrapper
            // would otherwise be placed outside of the layout root element
            // and block events on elements below it.
            expandWrapper.getStyle().setPaddingTop(totalSize, Unit.PX);
            expandWrapper.getFirstChildElement().getStyle()
                    .setMarginTop(-totalSize, Unit.PX);

            recalculateExpands();
        }
    }

    @Override
    public void recalculateLayoutHeight() {
        // Not needed
    }
}
