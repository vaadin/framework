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
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.ui.TouchScrollDelegate.TouchScrollHandler;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.ui.accordion.AccordionState;
import com.vaadin.shared.ui.tabsheet.TabState;
import com.vaadin.shared.ui.tabsheet.TabsheetServerRpc;

public class VAccordion extends VTabsheetBase {

    public static final String CLASSNAME = AccordionState.PRIMARY_STYLE_NAME;

    private Set<Widget> widgets = new HashSet<Widget>();

    private StackItem openTab;

    /** For internal use only. May be removed or replaced in the future. */
    public int selectedItemIndex = -1;

    private final TouchScrollHandler touchScrollHandler;

    public VAccordion() {
        super(CLASSNAME);

        touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
    }

    @Override
    public void renderTab(TabState tabState, int index) {
        StackItem item;
        int itemIndex;

        if (getWidgetCount() <= index) {
            // Create stackItem and render caption
            item = new StackItem();
            if (getWidgetCount() == 0) {
                item.addStyleDependentName("first");
            }
            itemIndex = getWidgetCount();
            add(item, getElement());
        } else {
            item = getStackItem(index);

            itemIndex = index;
        }
        item.updateCaption(tabState);

        item.updateTabStyleName(tabState.styleName);

        item.setVisible(tabState.visible);
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

    protected void updateStyleNames(String primaryStyleName) {
        for (Widget w : getChildren()) {
            if (w instanceof StackItem) {
                StackItem item = (StackItem) w;
                item.updateStyleNames(primaryStyleName);
            }
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
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

    /** For internal use only. May be removed or replaced in the future. */
    public void close(StackItem item) {
        if (!item.isOpen()) {
            return;
        }

        item.close();
        activeTabIndex = -1;
        openTab = null;

    }

    public void onSelectTab(StackItem item) {
        final int index = getWidgetIndex(item);

        if (index != activeTabIndex && !disabled && !readonly
                && !disabledTabKeys.contains(tabKeys.get(index))) {

            addStyleDependentName("loading");

            connector.getRpcProxy(TabsheetServerRpc.class).setSelected(
                    tabKeys.get(index).toString());
        }
    }

    /**
     * A StackItem has always two children, Child 0 is a VCaption, Child 1 is
     * the actual child widget.
     */
    public class StackItem extends ComplexPanel implements ClickHandler {

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

        public Widget getComponent() {
            if (getWidgetCount() < 2) {
                return null;
            }
            return getWidget(1);
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
        }

        public void setHeightFromWidget() {
            Widget widget = getChildWidget();
            if (widget == null) {
                return;
            }

            int paintableHeight = widget.getElement().getOffsetHeight();
            setHeight(paintableHeight);

        }

        /**
         * Returns caption width including padding
         * 
         * @return
         */
        public int getCaptionWidth() {
            if (caption == null) {
                return 0;
            }

            int captionWidth = caption.getRequiredWidth();
            int padding = Util.measureHorizontalPaddingAndBorder(
                    caption.getElement(), 18);
            return captionWidth + padding;
        }

        public void setWidth(int width) {
            if (width == -1) {
                super.setWidth("");
            } else {
                super.setWidth(width + "px");
            }
        }

        public int getHeight() {
            return getOffsetHeight();
        }

        public int getCaptionHeight() {
            return captionNode.getOffsetHeight();
        }

        private VCaption caption;
        private boolean open = false;
        private Element content = DOM.createDiv();
        private Element captionNode = DOM.createDiv();
        private String styleName;

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

        public com.google.gwt.user.client.Element getContainerElement() {
            return DOM.asOld(content);
        }

        public Widget getChildWidget() {
            if (getWidgetCount() > 1) {
                return getWidget(1);
            } else {
                return null;
            }
        }

        public void replaceWidget(Widget newWidget) {
            if (getWidgetCount() > 1) {
                Widget oldWidget = getWidget(1);
                remove(oldWidget);
                widgets.remove(oldWidget);
            }
            add(newWidget, content);
            widgets.add(newWidget);
        }

        public void open() {
            open = true;
            content.getStyle().setTop(getCaptionHeight(), Unit.PX);
            content.getStyle().setLeft(0, Unit.PX);
            content.getStyle().clearVisibility();
            addStyleDependentName("open");
        }

        public void hide() {
            content.getStyle().setVisibility(Visibility.HIDDEN);
        }

        public void close() {
            content.getStyle().setVisibility(Visibility.HIDDEN);
            content.getStyle().setTop(-100000, Unit.PX);
            content.getStyle().setLeft(-100000, Unit.PX);
            removeStyleDependentName("open");
            setHeight(-1);
            setWidth("");
            open = false;
        }

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
            if (getChildWidget() == null) {
                add(newWidget, content);
                widgets.add(newWidget);
            } else if (getChildWidget() != newWidget) {
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

        public void updateCaption(TabState tabState) {
            // TODO need to call this because the caption does not have an owner
            caption.updateCaptionWithoutOwner(
                    tabState.caption,
                    !tabState.enabled,
                    hasAttribute(tabState.description),
                    hasAttribute(tabState.componentError),
                    connector.getResourceUrl(ComponentConstants.ICON_RESOURCE
                            + tabState.key));
        }

        private boolean hasAttribute(String string) {
            return string != null && !string.trim().isEmpty();
        }

        /**
         * Updates a tabs stylename from the child UIDL
         * 
         * @param uidl
         *            The child uidl of the tab
         */
        private void updateTabStyleName(String newStyleName) {
            if (newStyleName != null && newStyleName.length() != 0) {
                if (!newStyleName.equals(styleName)) {
                    // If we have a new style name
                    if (styleName != null && styleName.length() != 0) {
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

        public int getWidgetWidth() {
            return DOM.getFirstChild(content).getOffsetWidth();
        }

        public boolean contains(ComponentConnector p) {
            return (getChildWidget() == p.getWidget());
        }

        public boolean isCaptionVisible() {
            return caption.isVisible();
        }

    }

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

    /** For internal use only. May be removed or replaced in the future. */
    public StackItem getStackItem(int index) {
        return (StackItem) getWidget(index);
    }

    public Iterable<StackItem> getStackItems() {
        return (Iterable) getChildren();
    }

    public StackItem getOpenStackItem() {
        return openTab;
    }

}
