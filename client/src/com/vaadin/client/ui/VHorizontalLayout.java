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
import com.vaadin.shared.ui.AlignmentInfo;

/**
 * Represents a layout where the children is ordered vertically
 */
public class VHorizontalLayout extends VAbstractOrderedLayout {

    public static final String CLASSNAME = "v-horizontallayout";

    /**
     * Default constructor
     */
    public VHorizontalLayout() {
        setStyleName(CLASSNAME);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        addStyleName(StyleConstants.UI_LAYOUT);
        addStyleName("v-horizontal");
    }

    @Override
    protected void recalculateExpands() {
        double total = 0;
        for (Slot slot : widgetToSlot.values()) {
            if (slot.getExpandRatio() > -1) {
                total += slot.getExpandRatio();
            } else {
                slot.getElement().getStyle().clearWidth();
            }
        }
        for (Slot slot : widgetToSlot.values()) {
            if (slot.getExpandRatio() > -1) {
                slot.setWidth((100 * (slot.getExpandRatio() / total)) + "%");
                if (slot.isRelativeWidth()) {
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
                el.getStyle().clearWidth();
                el.getStyle().clearMarginLeft();
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
                slot.getElement().getStyle().clearWidth();
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
                        int max = -1;
                        max = layoutManager.getOuterWidth(slot.getWidget()
                                .getElement())
                                - layoutManager.getMarginWidth(slot.getWidget()
                                        .getElement());
                        if (slot.hasCaption()) {
                            int max2 = layoutManager.getOuterWidth(slot
                                    .getCaptionElement())
                                    - layoutManager.getMarginWidth(slot
                                            .getCaptionElement());
                            max = Math.max(max, max2);
                        }
                        if (max > 0) {
                            totalSize += max;
                        }
                    } else {
                        totalSize += slot.getOffsetWidth();
                    }
                }
                // TODO fails in Opera, always returns 0
                int spacingSize = slot.getHorizontalSpacing();
                if (spacingSize > 0) {
                    totalSize += spacingSize;
                }
            }

            // When we set the margin to the first child, we don't need
            // overflow:hidden in the layout root element, since the wrapper
            // would otherwise be placed outside of the layout root element
            // and block events on elements below it.
            expandWrapper.getStyle().setPaddingLeft(totalSize, Unit.PX);
            expandWrapper.getFirstChildElement().getStyle()
                    .setMarginLeft(-totalSize, Unit.PX);

            recalculateExpands();
        }
    }

    @Override
    public void recalculateLayoutHeight() {
        // Only needed if a horizontal layout is undefined high, and contains
        // relative height children or vertical alignments
        if (definedHeight) {
            return;
        }

        boolean hasRelativeHeightChildren = false;
        boolean hasVAlign = false;

        for (Widget slot : getChildren()) {
            Widget widget = ((Slot) slot).getWidget();
            String h = widget.getElement().getStyle().getHeight();
            if (h != null && h.indexOf("%") > -1) {
                hasRelativeHeightChildren = true;
            }
            AlignmentInfo a = ((Slot) slot).getAlignment();
            if (a != null && (a.isVerticalCenter() || a.isBottom())) {
                hasVAlign = true;
            }
        }

        if (hasRelativeHeightChildren || hasVAlign) {
            int newHeight;
            if (layoutManager != null) {
                newHeight = layoutManager.getOuterHeight(getElement())
                        - layoutManager.getMarginHeight(getElement());
            } else {
                newHeight = getElement().getOffsetHeight();
            }
            VHorizontalLayout.this.getElement().getStyle()
                    .setHeight(newHeight, Unit.PX);
        }
    }
}
