/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.ui.orderedlayout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Profiler;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.ui.MarginInfo;

/**
 * Base class for ordered layouts
 */
public class VAbstractOrderedLayout extends FlowPanel {

    protected boolean spacing = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean vertical = true;

    protected boolean definedHeight = false;

    private Map<Widget, Slot> widgetToSlot = new HashMap<Widget, Slot>();

    private Element expandWrapper;

    private LayoutManager layoutManager;

    /**
     * Keep track of the last allocated expand size to help detecting when it
     * changes.
     */
    private int lastExpandSize = -1;

    public VAbstractOrderedLayout(boolean vertical) {
        this.vertical = vertical;
    }

    /**
     * See the method {@link #addOrMoveSlot(Slot, int, boolean)}.
     * 
     * <p>
     * This method always adjusts spacings for the whole layout.
     * 
     * @param slot
     *            The slot to move or add
     * @param index
     *            The index where the slot should be placed.
     * @deprecated since 7.1.4, use {@link #addOrMoveSlot(Slot, int, boolean)}
     */
    @Deprecated
    public void addOrMoveSlot(Slot slot, int index) {
        addOrMoveSlot(slot, index, true);
    }

    /**
     * Add or move a slot to another index.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * <p>
     * You should note that the index does not refer to the DOM index if
     * spacings are used. If spacings are used then the index will be adjusted
     * to include the spacings when inserted.
     * <p>
     * For instance when using spacing the index converts to DOM index in the
     * following way:
     * 
     * <pre>
     * index : 0 -> DOM index: 0
     * index : 1 -> DOM index: 1
     * index : 2 -> DOM index: 3
     * index : 3 -> DOM index: 5
     * index : 4 -> DOM index: 7
     * </pre>
     * 
     * When using this method never account for spacings.
     * <p>
     * The caller should remove all spacings before calling this method and
     * re-add them (if necessary) after this method. This can be done before and
     * after all slots have been added/moved.
     * </p>
     * 
     * @since 7.1.4
     * 
     * @param slot
     *            The slot to move or add
     * @param index
     *            The index where the slot should be placed.
     * @param adjustSpacing
     *            true to recalculate spacings for the whole layout after the
     *            operation
     */
    public void addOrMoveSlot(Slot slot, int index, boolean adjustSpacing) {
        Profiler.enter("VAOL.onConnectorHierarchyChange addOrMoveSlot find index");
        if (slot.getParent() == this) {
            int currentIndex = getWidgetIndex(slot);
            if (index == currentIndex) {
                Profiler.leave("VAOL.onConnectorHierarchyChange addOrMoveSlot find index");
                return;
            }
        }
        Profiler.leave("VAOL.onConnectorHierarchyChange addOrMoveSlot find index");

        Profiler.enter("VAOL.onConnectorHierarchyChange addOrMoveSlot insert");
        insert(slot, index);
        Profiler.leave("VAOL.onConnectorHierarchyChange addOrMoveSlot insert");

        if (adjustSpacing) {
            Profiler.enter("VAOL.onConnectorHierarchyChange addOrMoveSlot setSpacing");
            setSpacing(spacing);
            Profiler.leave("VAOL.onConnectorHierarchyChange addOrMoveSlot setSpacing");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated As of 7.2, use or override
     *             {@link #insert(Widget, Element, int, boolean)} instead.
     */
    @Override
    @Deprecated
    protected void insert(Widget child,
            com.google.gwt.user.client.Element container, int beforeIndex,
            boolean domInsert) {
        // Validate index; adjust if the widget is already a child of this
        // panel.
        beforeIndex = adjustIndex(child, beforeIndex);

        // Detach new child.
        child.removeFromParent();

        // Logical attach.
        getChildren().insert(child, beforeIndex);

        // Physical attach.
        container = expandWrapper != null ? DOM.asOld(expandWrapper)
                : getElement();
        if (domInsert) {
            if (spacing) {
                if (beforeIndex != 0) {
                    /*
                     * Since the spacing elements are located at the same DOM
                     * level as the slots we need to take them into account when
                     * calculating the slot position.
                     * 
                     * The spacing elements are always located before the actual
                     * slot except for the first slot which do not have a
                     * spacing element like this
                     * 
                     * |<slot1><spacing2><slot2><spacing3><slot3>...|
                     */
                    beforeIndex = beforeIndex * 2 - 1;
                }
            }
            DOM.insertChild(container, child.getElement(), beforeIndex);
        } else {
            DOM.appendChild(container, child.getElement());
        }

        // Adopt.
        adopt(child);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 7.2
     */
    @Override
    protected void insert(Widget child, Element container, int beforeIndex,
            boolean domInsert) {
        insert(child, DOM.asOld(container), beforeIndex, domInsert);
    }

    /**
     * Remove a slot from the layout
     * 
     * @param widget
     * @return
     */
    public void removeWidget(Widget widget) {
        Slot slot = widgetToSlot.get(widget);
        remove(slot);
        widgetToSlot.remove(widget);
    }

    /**
     * Get the containing slot for a widget. If no slot is found a new slot is
     * created and returned.
     * 
     * @param widget
     *            The widget whose slot you want to get
     * 
     * @return
     */
    public Slot getSlot(Widget widget) {
        Slot slot = widgetToSlot.get(widget);
        if (slot == null) {
            slot = new Slot(this, widget);
            widgetToSlot.put(widget, slot);
        }
        return slot;
    }

    /**
     * Gets a slot based on the widget element. If no slot is found then null is
     * returned.
     * 
     * @param widgetElement
     *            The element of the widget ( Same as getWidget().getElement() )
     * @return
     * @deprecated As of 7.2, call or override {@link #getSlot(Element)} instead
     */
    @Deprecated
    public Slot getSlot(com.google.gwt.user.client.Element widgetElement) {
        for (Map.Entry<Widget, Slot> entry : widgetToSlot.entrySet()) {
            if (entry.getKey().getElement() == widgetElement) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Gets a slot based on the widget element. If no slot is found then null is
     * returned.
     * 
     * @param widgetElement
     *            The element of the widget ( Same as getWidget().getElement() )
     * @return
     * 
     * @since 7.2
     */
    public Slot getSlot(Element widgetElement) {
        return getSlot(DOM.asOld(widgetElement));
    }

    /**
     * Set the layout manager for the layout
     * 
     * @param manager
     *            The layout manager to use
     */
    public void setLayoutManager(LayoutManager manager) {
        layoutManager = manager;
    }

    /**
     * Get the layout manager used by this layout
     * 
     */
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Deducts the caption position by examining the wrapping element.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param captionWrap
     *            The wrapping element
     * 
     * @return The caption position
     * @deprecated As of 7.2, call or override
     *             {@link #getCaptionPositionFromElement(Element)} instead
     */
    @Deprecated
    public CaptionPosition getCaptionPositionFromElement(
            com.google.gwt.user.client.Element captionWrap) {
        RegExp captionPositionRegexp = RegExp.compile("v-caption-on-(\\S+)");

        // Get caption position from the classname
        MatchResult matcher = captionPositionRegexp.exec(captionWrap
                .getClassName());
        if (matcher == null || matcher.getGroupCount() < 2) {
            return CaptionPosition.TOP;
        }
        String captionClass = matcher.getGroup(1);
        CaptionPosition captionPosition = CaptionPosition.valueOf(
                CaptionPosition.class, captionClass.toUpperCase());
        return captionPosition;
    }

    /**
     * Deducts the caption position by examining the wrapping element.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param captionWrap
     *            The wrapping element
     * 
     * @return The caption position
     * @since 7.2
     */
    public CaptionPosition getCaptionPositionFromElement(Element captionWrap) {
        return getCaptionPositionFromElement(DOM.asOld(captionWrap));
    }

    /**
     * Update the offset off the caption relative to the slot
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param caption
     *            The caption element
     * @deprecated As of 7.2, call or override
     *             {@link #updateCaptionOffset(Element)} instead
     */
    @Deprecated
    public void updateCaptionOffset(com.google.gwt.user.client.Element caption) {

        Element captionWrap = caption.getParentElement();

        Style captionWrapStyle = captionWrap.getStyle();
        captionWrapStyle.clearPaddingTop();
        captionWrapStyle.clearPaddingRight();
        captionWrapStyle.clearPaddingBottom();
        captionWrapStyle.clearPaddingLeft();

        Style captionStyle = caption.getStyle();
        captionStyle.clearMarginTop();
        captionStyle.clearMarginRight();
        captionStyle.clearMarginBottom();
        captionStyle.clearMarginLeft();

        // Get caption position from the classname
        CaptionPosition captionPosition = getCaptionPositionFromElement(captionWrap);

        if (captionPosition == CaptionPosition.LEFT
                || captionPosition == CaptionPosition.RIGHT) {
            int captionWidth;
            if (layoutManager != null) {
                captionWidth = layoutManager.getOuterWidth(caption)
                        - layoutManager.getMarginWidth(caption);
            } else {
                captionWidth = caption.getOffsetWidth();
            }
            if (captionWidth > 0) {
                if (captionPosition == CaptionPosition.LEFT) {
                    captionWrapStyle.setPaddingLeft(captionWidth, Unit.PX);
                    captionStyle.setMarginLeft(-captionWidth, Unit.PX);
                } else {
                    captionWrapStyle.setPaddingRight(captionWidth, Unit.PX);
                    captionStyle.setMarginRight(-captionWidth, Unit.PX);
                }
            }
        }
        if (captionPosition == CaptionPosition.TOP
                || captionPosition == CaptionPosition.BOTTOM) {
            int captionHeight;
            if (layoutManager != null) {
                captionHeight = layoutManager.getOuterHeight(caption)
                        - layoutManager.getMarginHeight(caption);
            } else {
                captionHeight = caption.getOffsetHeight();
            }
            if (captionHeight > 0) {
                if (captionPosition == CaptionPosition.TOP) {
                    captionWrapStyle.setPaddingTop(captionHeight, Unit.PX);
                    captionStyle.setMarginTop(-captionHeight, Unit.PX);
                } else {
                    captionWrapStyle.setPaddingBottom(captionHeight, Unit.PX);
                    captionStyle.setMarginBottom(-captionHeight, Unit.PX);
                }
            }
        }
    }

    /**
     * Update the offset off the caption relative to the slot
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param caption
     *            The caption element
     * @since 7.2
     */
    public void updateCaptionOffset(Element caption) {
        updateCaptionOffset(DOM.asOld(caption));
    }

    /**
     * Set the margin of the layout
     * 
     * @param marginInfo
     *            The margin information
     */
    public void setMargin(MarginInfo marginInfo) {
        if (marginInfo != null) {
            setStyleName("v-margin-top", marginInfo.hasTop());
            setStyleName("v-margin-right", marginInfo.hasRight());
            setStyleName("v-margin-bottom", marginInfo.hasBottom());
            setStyleName("v-margin-left", marginInfo.hasLeft());
        }
    }

    /**
     * Turn on or off spacing in the layout
     * 
     * @param spacing
     *            True if spacing should be used, false if not
     */
    public void setSpacing(boolean spacing) {
        Profiler.enter("VAOL.onConnectorHierarchyChange setSpacing");
        this.spacing = spacing;
        // first widget does not have spacing on
        // optimization to avoid looking up widget indices on every iteration
        Widget firstSlot = null;
        if (getWidgetCount() > 0) {
            firstSlot = getWidget(0);
        }
        for (Slot slot : widgetToSlot.values()) {
            slot.setSpacing(spacing && firstSlot != slot);
        }
        Profiler.leave("VAOL.onConnectorHierarchyChange setSpacing");
    }

    /**
     * Assigns relative sizes to the children that should expand based on their
     * expand ratios.
     */
    public void updateExpandedSizes() {
        // Ensure the expand wrapper is in place
        if (expandWrapper == null) {
            expandWrapper = DOM.createDiv();
            expandWrapper.setClassName("v-expand");

            // Detach all widgets before modifying DOM
            for (Widget widget : getChildren()) {
                orphan(widget);
            }

            while (getElement().getChildCount() > 0) {
                Node el = getElement().getChild(0);
                expandWrapper.appendChild(el);
            }
            getElement().appendChild(expandWrapper);

            // Attach all widgets again
            for (Widget widget : getChildren()) {
                adopt(widget);
            }
        }

        // Sum up expand ratios to get the denominator
        double total = 0;
        for (Slot slot : widgetToSlot.values()) {
            // FIXME expandRatio might be <0
            total += slot.getExpandRatio();
        }

        // Give each expanded child its own share
        for (Slot slot : widgetToSlot.values()) {

            Element slotElement = slot.getElement();
            slotElement.removeAttribute("aria-hidden");

            Style slotStyle = slotElement.getStyle();
            slotStyle.clearVisibility();
            slotStyle.clearMarginLeft();
            slotStyle.clearMarginTop();

            if (slot.getExpandRatio() != 0) {
                // FIXME expandRatio might be <0
                double size = 100 * (slot.getExpandRatio() / total);

                if (vertical) {
                    slot.setHeight(size + "%");
                    if (slot.hasRelativeHeight()) {
                        Util.notifyParentOfSizeChange(this, true);
                    }
                } else {
                    slot.setWidth(size + "%");
                    if (slot.hasRelativeWidth()) {
                        Util.notifyParentOfSizeChange(this, true);
                    }
                }

            } else if (slot.isRelativeInDirection(vertical)) {
                // Relative child without expansion gets no space at all
                if (vertical) {
                    slot.setHeight("0");
                } else {
                    slot.setWidth("0");
                }
                slotStyle.setVisibility(Visibility.HIDDEN);
                slotElement.setAttribute("aria-hidden", "true");

            } else {
                // Non-relative child without expansion should be unconstrained
                if (BrowserInfo.get().isIE8()) {
                    // unconstrained in IE8 is auto
                    if (vertical) {
                        slot.setHeight("auto");
                    } else {
                        slot.setWidth("auto");
                    }
                } else {
                    if (vertical) {
                        slotStyle.clearHeight();
                    } else {
                        slotStyle.clearWidth();
                    }
                }
            }
        }
    }

    /**
     * Removes elements used to expand a slot.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void clearExpand() {
        if (expandWrapper != null) {
            // Detach all widgets before modifying DOM
            for (Widget widget : getChildren()) {
                orphan(widget);
            }

            lastExpandSize = -1;
            while (expandWrapper.getChildCount() > 0) {
                Element el = expandWrapper.getChild(0).cast();
                getElement().appendChild(el);
                if (vertical) {
                    el.getStyle().clearHeight();
                    el.getStyle().clearMarginTop();
                } else {
                    el.getStyle().clearWidth();
                    el.getStyle().clearMarginLeft();
                }
            }
            expandWrapper.removeFromParent();
            expandWrapper = null;

            // Attach children again
            for (Widget widget : getChildren()) {
                adopt(widget);
            }
        }
    }

    /**
     * Updates the expand compensation based on the measured sizes of children
     * without expand.
     */
    public void updateExpandCompensation() {
        boolean isExpanding = false;
        for (Widget slot : getChildren()) {
            // FIXME expandRatio might be <0
            if (((Slot) slot).getExpandRatio() != 0) {
                isExpanding = true;
                break;
            }
        }

        if (isExpanding) {
            /*
             * Expanded slots have relative sizes that together add up to 100%.
             * To make room for slots without expand, we will add padding that
             * is not considered for relative sizes and a corresponding negative
             * margin for the unexpanded slots. We calculate the size by summing
             * the size of all non-expanded non-relative slots.
             * 
             * Relatively sized slots without expansion are considered to get
             * 0px, but we still keep them visible (causing overflows) to help
             * the developer see what's happening. Forcing them to only get 0px
             * would make them disappear which would avoid overflows but would
             * instead cause confusion as they would then just disappear without
             * any obvious reason.
             */
            int totalSize = 0;
            for (Widget w : getChildren()) {
                Slot slot = (Slot) w;
                if (slot.getExpandRatio() == 0
                        && !slot.isRelativeInDirection(vertical)) {

                    if (layoutManager != null) {
                        // TODO check caption position
                        if (vertical) {
                            int size = layoutManager.getOuterHeight(slot
                                    .getWidget().getElement());
                            if (slot.hasCaption()) {
                                size += layoutManager.getOuterHeight(slot
                                        .getCaptionElement());
                            }
                            if (size > 0) {
                                totalSize += size;
                            }
                        } else {
                            int max = -1;
                            max = layoutManager.getOuterWidth(slot.getWidget()
                                    .getElement());
                            if (slot.hasCaption()) {
                                int max2 = layoutManager.getOuterWidth(slot
                                        .getCaptionElement());
                                max = Math.max(max, max2);
                            }
                            if (max > 0) {
                                totalSize += max;
                            }
                        }
                    } else {
                        // FIXME expandRatio might be <0
                        totalSize += vertical ? slot.getOffsetHeight() : slot
                                .getOffsetWidth();
                    }
                }
                // TODO fails in Opera, always returns 0
                int spacingSize = vertical ? slot.getVerticalSpacing() : slot
                        .getHorizontalSpacing();
                if (spacingSize > 0) {
                    totalSize += spacingSize;
                }
            }

            // When we set the margin to the first child, we don't need
            // overflow:hidden in the layout root element, since the wrapper
            // would otherwise be placed outside of the layout root element
            // and block events on elements below it.
            if (vertical) {
                expandWrapper.getStyle().setPaddingTop(totalSize, Unit.PX);
                expandWrapper.getFirstChildElement().getStyle()
                        .setMarginTop(-totalSize, Unit.PX);
            } else {
                expandWrapper.getStyle().setPaddingLeft(totalSize, Unit.PX);
                expandWrapper.getFirstChildElement().getStyle()
                        .setMarginLeft(-totalSize, Unit.PX);
            }

            // Measure expanded children again if their size might have changed
            if (totalSize != lastExpandSize) {
                lastExpandSize = totalSize;
                for (Widget w : getChildren()) {
                    Slot slot = (Slot) w;
                    // FIXME expandRatio might be <0
                    if (slot.getExpandRatio() != 0) {
                        if (layoutManager != null) {
                            layoutManager.setNeedsMeasure(Util
                                    .findConnectorFor(slot.getWidget()));
                        } else if (slot.getWidget() instanceof RequiresResize) {
                            ((RequiresResize) slot.getWidget()).onResize();
                        }
                    }
                }
            }
        }
        WidgetUtil.forceIE8Redraw(getElement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        definedHeight = (height != null && !"".equals(height));
    }

    /**
     * Sets the slots style names. The style names will be prefixed with the
     * v-slot prefix.
     * 
     * @param stylenames
     *            The style names of the slot.
     */
    public void setSlotStyleNames(Widget widget, String... stylenames) {
        Slot slot = getSlot(widget);
        if (slot == null) {
            throw new IllegalArgumentException(
                    "A slot for the widget could not be found. Has the widget been added to the layout?");
        }
        slot.setStyleNames(stylenames);
    }

}
