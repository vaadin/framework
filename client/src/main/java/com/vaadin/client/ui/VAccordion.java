/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.VCaption;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.TouchScrollDelegate.TouchScrollHandler;
import com.vaadin.client.ui.VAccordion.StackItem;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.ui.accordion.AccordionState;
import com.vaadin.shared.ui.tabsheet.TabState;
import com.vaadin.shared.ui.tabsheet.TabsheetServerRpc;
import com.vaadin.shared.util.SharedUtil;

/**
 * Widget class for the Accordion component. Displays one child item's contents
 * at a time.
 *
 * @author Vaadin Ltd
 *
 */
public class VAccordion extends VTabsheetBase {

    /** Default classname for this widget. */
    public static final String CLASSNAME = AccordionState.PRIMARY_STYLE_NAME;

    private Set<Widget> widgets = new HashSet<>();

    private StackItem openTab;

    /** For internal use only. May be removed or replaced in the future. */
    public int selectedItemIndex = -1;

    private final TouchScrollHandler touchScrollHandler;

    private int tabulatorIndex;

    /**
     * Constructs a widget for an Accordion.
     */
    public VAccordion() {
        super(CLASSNAME);

        touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderTab(TabState tabState, int index) {
        StackItem item;

        if (getWidgetCount() <= index) {
            // Create stackItem and render caption
            item = new StackItem();
            if (getWidgetCount() == 0) {
                item.addStyleDependentName("first");
            }
            add(item, getElement());
        } else {
            item = getStackItem(index);
        }
        item.updateCaption(tabState);

        item.updateTabStyleName(tabState.styleName);

        item.setVisible(tabState.visible);

        item.setId(tabState.id);
    }

    @Override
    public void selectTab(int index) {
        selectedItemIndex = index;
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        updateStyleNames(style);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        updateStyleNames(style);
    }

    /**
     * Updates the primary style name base for all stack items.
     *
     * @param primaryStyleName
     *            the new primary style name base
     */
    protected void updateStyleNames(String primaryStyleName) {
        for (Widget w : getChildren()) {
            if (w instanceof StackItem) {
                StackItem item = (StackItem) w;
                item.updateStyleNames(primaryStyleName);
            }
        }
    }

    /**
     * For internal use only. May be renamed or removed in a future release.
     * <p>
     * Sets the tabulator index for the active stack item. The active stack item
     * represents the entire accordion in the browser's focus cycle (excluding
     * any focusable elements within the content panel).
     * <p>
     * This value is delegated from the TabsheetState via AccordionState.
     *
     * @param tabIndex
     *            tabulator index for the open stack item
     * @since 8.1.7
     */
    public void setTabIndex(int tabIndex) {
        tabulatorIndex = tabIndex;
        StackItem openStackItem = getOpenStackItem();
        if (openStackItem != null) {
            openStackItem.getElement().setTabIndex(tabIndex);
        }
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param itemIndex
     *            the index of the stack item to open
     */
    public void open(int itemIndex) {
        StackItem item = (StackItem) getWidget(itemIndex);
        boolean alreadyOpen = false;
        if (openTab != null) {
            if (openTab.isOpen()) {
                if (openTab == item) {
                    alreadyOpen = true;
                } else {
                    openTab.close();
                }
            }
        }
        if (!alreadyOpen) {
            item.open();
            activeTabIndex = itemIndex;
            openTab = item;
        }

    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param item
     *            the stack item to close
     */
    public void close(StackItem item) {
        if (!item.isOpen()) {
            return;
        }

        item.close();
        activeTabIndex = -1;
        openTab = null;

    }

    /**
     * Handle stack item selection.
     *
     * @param item
     *            the selected stack item
     */
    public void onSelectTab(StackItem item) {
        final int index = getWidgetIndex(item);

        if (index != activeTabIndex && !disabled && !readonly
                && !disabledTabKeys.contains(tabKeys.get(index))) {

            addStyleDependentName("loading");

            connector.getRpcProxy(TabsheetServerRpc.class)
                    .setSelected(tabKeys.get(index));
        }
    }

    /**
     * A StackItem has always two children, Child 0 is a VCaption, Child 1 is
     * the actual child widget.
     */
    public class StackItem extends ComplexPanel implements ClickHandler {

        private Widget widget;
        private String id;

        /**
         * Sets the height for this stack item's contents.
         *
         * @param height
         *            the height to set (in pixels), or {@code -1} to remove
         *            height
         */
        public void setHeight(int height) {
            if (height == -1) {
                super.setHeight("");
                content.getStyle().setHeight(0, Unit.PX);
            } else {
                super.setHeight((height + getCaptionHeight()) + "px");
                content.getStyle().setHeight(height, Unit.PX);
                content.getStyle().setTop(getCaptionHeight(), Unit.PX);
            }
        }

        /**
         * Sets the identifier for this stack item.
         *
         * @param newId
         *            the identifier to set
         */
        public void setId(String newId) {
            if (!SharedUtil.equals(newId, id)) {
                if (id != null) {
                    getElement().removeAttribute("id");
                }
                id = newId;
                if (id != null && !id.isEmpty()) {
                    getElement().setId(id);
                }
            }
        }

        /**
         * Returns the wrapped widget of this stack item.
         *
         * @return the widget
         *
         * @deprecated This method is not called by the framework code anymore.
         *             Use {@link #getChildWidget()} instead.
         */
        @Deprecated
        public Widget getComponent() {
            return getChildWidget();
        }

        /**
         * Queries the height from the wrapped widget and uses it to set this
         * stack item's height.
         */
        public void setHeightFromWidget() {
            Widget widget = getChildWidget();
            if (widget == null) {
                return;
            }

            int paintableHeight = widget.getElement().getOffsetHeight();
            setHeight(paintableHeight);

        }

        /**
         * Returns caption width including padding.
         *
         * @return the width of the caption (in pixels), or zero if there is no
         *         caption element (not possible via the default implementation)
         */
        public int getCaptionWidth() {
            if (caption == null) {
                return 0;
            }

            int captionWidth = caption.getRequiredWidth();
            int padding = WidgetUtil.measureHorizontalPaddingAndBorder(
                    caption.getElement(), 18);
            return captionWidth + padding;
        }

        /**
         * Sets the width of the stack item, or removes it if given value is
         * {@code -1}.
         *
         * @param width
         *            the width to set (in pixels), or {@code -1} to remove
         *            width
         */
        public void setWidth(int width) {
            if (width == -1) {
                super.setWidth("");
            } else {
                super.setWidth(width + "px");
            }
        }

        /**
         * Returns the offset height of this stack item.
         *
         * @return the height in pixels
         *
         * @deprecated This method is not called by the framework code anymore.
         *             Use {@link #getOffsetHeight()} instead.
         */
        @Deprecated
        public int getHeight() {
            return getOffsetHeight();
        }

        /**
         * Returns the offset height of the caption node.
         *
         * @return the height in pixels
         */
        public int getCaptionHeight() {
            return captionNode.getOffsetHeight();
        }

        private VCaption caption;
        private boolean open = false;
        private Element content = DOM.createDiv();
        private Element captionNode = DOM.createDiv();
        private String styleName;

        /**
         * Constructs a stack item. The content widget should be set later when
         * the stack item is opened.
         */
        @SuppressWarnings("deprecation")
        public StackItem() {
            setElement(DOM.createDiv());
            caption = new VCaption(client);
            caption.addClickHandler(this);
            super.add(caption, captionNode);
            DOM.appendChild(captionNode, caption.getElement());
            DOM.appendChild(getElement(), captionNode);
            DOM.appendChild(getElement(), content);

            updateStyleNames(VAccordion.this.getStylePrimaryName());

            touchScrollHandler.addElement(getContainerElement());

            close();
        }

        private void updateStyleNames(String primaryStyleName) {
            content.removeClassName(getStylePrimaryName() + "-content");
            captionNode.removeClassName(getStylePrimaryName() + "-caption");

            setStylePrimaryName(primaryStyleName + "-item");
            updateTabStyleName(getStylePrimaryName());

            captionNode.addClassName(getStylePrimaryName() + "-caption");
            content.addClassName(getStylePrimaryName() + "-content");
        }

        @Override
        public void onBrowserEvent(Event event) {
            onSelectTab(this);
        }

        /**
         * Returns the container element for the content widget.
         *
         * @return the content container element
         */
        @SuppressWarnings("deprecation")
        public com.google.gwt.user.client.Element getContainerElement() {
            return DOM.asOld(content);
        }

        /**
         * Returns the wrapped widget of this stack item.
         *
         * @return the widget
         */
        public Widget getChildWidget() {
            return widget;
        }

        /**
         * Replaces the existing wrapped widget (if any) with a new widget.
         *
         * @param newWidget
         *            the new widget to wrap
         */
        public void replaceWidget(Widget newWidget) {
            if (widget != null) {
                widgets.remove(widget);
                if (open) {
                    remove(widget);
                }
            }
            widget = newWidget;
            widgets.add(newWidget);
            if (open) {
                add(widget, content);
            }

        }

        /**
         * Opens the stack item and clears any previous visibility settings.
         */
        public void open() {
            add(widget, content);
            open = true;
            content.getStyle().setTop(getCaptionHeight(), Unit.PX);
            content.getStyle().setLeft(0, Unit.PX);
            content.getStyle().clearVisibility();
            addStyleDependentName("open");
            getElement().setTabIndex(tabulatorIndex);
        }

        /**
         * Hides the stack item content but does not close the stack item.
         *
         * @deprecated This method is not called by the framework code anymore.
         */
        @Deprecated
        public void hide() {
            content.getStyle().setVisibility(Visibility.HIDDEN);
        }

        /**
         * Closes this stack item and removes the wrapped widget from the DOM
         * tree and this stack item.
         */
        public void close() {
            if (widget != null) {
                remove(widget);
            }
            content.getStyle().setVisibility(Visibility.HIDDEN);
            content.getStyle().setTop(-100000, Unit.PX);
            content.getStyle().setLeft(-100000, Unit.PX);
            removeStyleDependentName("open");
            setHeight(-1);
            setWidth("");
            open = false;
            getElement().setTabIndex(-1);
        }

        /**
         * Returns whether this stack item is open or not.
         *
         * @return {@code true} if open, {@code false} otherwise
         */
        public boolean isOpen() {
            return open;
        }

        /**
         * Updates the content of the open tab of the accordion.
         *
         * This method is mostly for internal use and may change in future
         * versions.
         *
         * @since 7.2
         * @param newWidget
         *            new content
         */
        public void setContent(Widget newWidget) {
            if (widget == null) {
                widget = newWidget;
                widgets.add(newWidget);
            } else if (widget != newWidget) {
                replaceWidget(newWidget);
            }
            if (isOpen() && isDynamicHeight()) {
                setHeightFromWidget();
            }
        }

        @Override
        public void onClick(ClickEvent event) {
            onSelectTab(this);
        }

        /**
         * Updates the caption to match the current tab state.
         *
         * @param tabState
         *            the state for this stack item
         */
        @SuppressWarnings("deprecation")
        public void updateCaption(TabState tabState) {
            // Need to call this because the caption does not have an owner, and
            // cannot have an owner, because only the selected stack item's
            // connector is sent to the client.
            caption.setCaptionAsHtml(isTabCaptionsAsHtml());
            caption.updateCaptionWithoutOwner(tabState.caption,
                    !tabState.enabled, hasAttribute(tabState.description),
                    hasAttribute(tabState.componentError),
                    tabState.componentErrorLevel,
                    connector.getResourceUrl(
                            ComponentConstants.ICON_RESOURCE + tabState.key),
                    tabState.iconAltText);
        }

        private boolean hasAttribute(String string) {
            return string != null && !string.trim().isEmpty();
        }

        /**
         * Updates the stack item's style name from the TabState.
         *
         * @param newStyleName
         *            the new style name
         */
        private void updateTabStyleName(String newStyleName) {
            if (newStyleName != null && !newStyleName.isEmpty()) {
                if (!newStyleName.equals(styleName)) {
                    // If we have a new style name
                    if (styleName != null && !styleName.isEmpty()) {
                        // Remove old style name if present
                        removeStyleDependentName(styleName);
                    }
                    // Set new style name
                    addStyleDependentName(newStyleName);
                    styleName = newStyleName;
                }
            } else if (styleName != null) {
                // Remove the set stylename if no stylename is present in the
                // uidl
                removeStyleDependentName(styleName);
                styleName = null;
            }
        }

        /**
         * Returns the offset width of the wrapped widget.
         *
         * @return the offset width in pixels, or zero if no widget is set
         */
        public int getWidgetWidth() {
            if (widget == null) {
                return 0;
            }
            return widget.getOffsetWidth();
        }

        /**
         * Returns whether the given container's widget is this stack item's
         * wrapped widget. Does not check whether the given container's widget
         * is a child of the wrapped widget.
         *
         * @param p
         *            the container whose widget to set
         * @return {@code true} if the container's widget matches wrapped
         *         widget, {@code false} otherwise
         *
         * @deprecated This method is not called by the framework code anymore.
         */
        @Deprecated
        public boolean contains(ComponentConnector p) {
            return (getChildWidget() == p.getWidget());
        }

        /**
         * Returns whether the caption element is visible or not.
         *
         * @return {@code true} if visible, {@code false} otherwise
         *
         * @deprecated This method is not called by the framework code anymore.
         */
        @Deprecated
        public boolean isCaptionVisible() {
            return caption.isVisible();
        }

    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This method is not called by the framework code anymore.
     */
    @Deprecated
    @Override
    protected void clearPaintables() {
        clear();
    }

    @Override
    public Iterator<Widget> getWidgetIterator() {
        return widgets.iterator();
    }

    @Override
    public int getTabCount() {
        return getWidgetCount();
    }

    @Override
    public void removeTab(int index) {
        StackItem item = getStackItem(index);
        remove(item);
        if (selectedItemIndex == index) {
            selectedItemIndex = -1;
        }
        touchScrollHandler.removeElement(item.getContainerElement());
    }

    @Override
    public ComponentConnector getTab(int index) {
        if (index < getWidgetCount()) {
            StackItem stackItem = getStackItem(index);
            if (stackItem == null) {
                return null;
            }
            Widget w = stackItem.getChildWidget();
            if (w != null) {
                return getConnectorForWidget(w);
            }
        }

        return null;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param index
     *            the index of the stack item to get
     * @return the stack item
     */
    public StackItem getStackItem(int index) {
        return (StackItem) getWidget(index);
    }

    /**
     * Returns an iterable over all the stack items.
     *
     * @return the iterable
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Iterable<StackItem> getStackItems() {
        return (Iterable) getChildren();
    }

    /**
     * Returns the currently open stack item.
     *
     * @return the open stack item, or {@code null} if one does not exist
     */
    public StackItem getOpenStackItem() {
        return openTab;
    }

}
