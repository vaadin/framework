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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.ui.TouchScrollDelegate.TouchScrollHandler;
import com.vaadin.shared.ui.tabsheet.TabsheetBaseConstants;
import com.vaadin.shared.ui.tabsheet.TabsheetConstants;

public class VAccordion extends VTabsheetBase {

    public static final String CLASSNAME = "v-accordion";

    private Set<Widget> widgets = new HashSet<Widget>();

    /** For internal use only. May be removed or replaced in the future. */
    public HashMap<StackItem, UIDL> lazyUpdateMap = new HashMap<StackItem, UIDL>();

    private StackItem openTab = null;

    /** For internal use only. May be removed or replaced in the future. */
    public int selectedUIDLItemIndex = -1;

    private final TouchScrollHandler touchScrollHandler;

    public VAccordion() {
        super(CLASSNAME);
        touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
    }

    @Override
    public void renderTab(UIDL tabUidl, int index, boolean selected,
            boolean hidden) {
        StackItem item;
        int itemIndex;
        if (getWidgetCount() <= index) {
            // Create stackItem and render caption
            item = new StackItem(tabUidl);
            if (getWidgetCount() == 0) {
                item.addStyleDependentName("first");
            }
            itemIndex = getWidgetCount();
            add(item, getElement());
        } else {
            item = getStackItem(index);
            item = moveStackItemIfNeeded(item, index, tabUidl);
            itemIndex = index;
        }
        item.updateCaption(tabUidl);

        item.updateTabStyleName(tabUidl
                .getStringAttribute(TabsheetConstants.TAB_STYLE_NAME));

        item.setVisible(!hidden);

        if (selected) {
            selectedUIDLItemIndex = itemIndex;
        }

        if (tabUidl.getChildCount() > 0) {
            lazyUpdateMap.put(item, tabUidl.getChildUIDL(0));
        }
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

    /**
     * This method tries to find out if a tab has been rendered with a different
     * index previously. If this is the case it re-orders the children so the
     * same StackItem is used for rendering this time. E.g. if the first tab has
     * been removed all tabs which contain cached content must be moved 1 step
     * up to preserve the cached content.
     * 
     * @param item
     * @param newIndex
     * @param tabUidl
     * @return
     */
    private StackItem moveStackItemIfNeeded(StackItem item, int newIndex,
            UIDL tabUidl) {
        UIDL tabContentUIDL = null;
        ComponentConnector tabContent = null;
        if (tabUidl.getChildCount() > 0) {
            tabContentUIDL = tabUidl.getChildUIDL(0);
            tabContent = client.getPaintable(tabContentUIDL);
        }

        Widget itemWidget = item.getComponent();
        if (tabContent != null) {
            if (tabContent.getWidget() != itemWidget) {
                /*
                 * This is not the same widget as before, find out if it has
                 * been moved
                 */
                int oldIndex = -1;
                StackItem oldItem = null;
                for (int i = 0; i < getWidgetCount(); i++) {
                    Widget w = getWidget(i);
                    oldItem = (StackItem) w;
                    if (tabContent == oldItem.getComponent()) {
                        oldIndex = i;
                        break;
                    }
                }

                if (oldIndex != -1 && oldIndex > newIndex) {
                    /*
                     * The tab has previously been rendered in another position
                     * so we must move the cached content to correct position.
                     * We move only items with oldIndex > newIndex to prevent
                     * moving items already rendered in this update. If for
                     * instance tabs 1,2,3 are removed and added as 3,2,1 we
                     * cannot re-use "1" when we get to the third tab.
                     */
                    insert(oldItem, getElement(), newIndex, true);
                    return oldItem;
                }
            }
        } else {
            // Tab which has never been loaded. Must assure we use an empty
            // StackItem
            Widget oldWidget = item.getComponent();
            if (oldWidget != null) {
                oldWidget.removeFromParent();
            }
        }
        return item;
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
            client.updateVariable(id, "selected", "" + tabKeys.get(index), true);
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
                DOM.setStyleAttribute(content, "height", "0px");
            } else {
                super.setHeight((height + getCaptionHeight()) + "px");
                DOM.setStyleAttribute(content, "height", height + "px");
                DOM.setStyleAttribute(content, "top", getCaptionHeight() + "px");

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

        public StackItem(UIDL tabUidl) {
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

        public Element getContainerElement() {
            return content;
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
                widgets.remove(oldWidget);
            }
            add(newWidget, content);
            widgets.add(newWidget);
        }

        public void open() {
            open = true;
            DOM.setStyleAttribute(content, "top", getCaptionHeight() + "px");
            DOM.setStyleAttribute(content, "left", "0px");
            DOM.setStyleAttribute(content, "visibility", "");
            addStyleDependentName("open");
        }

        public void hide() {
            DOM.setStyleAttribute(content, "visibility", "hidden");
        }

        public void close() {
            DOM.setStyleAttribute(content, "visibility", "hidden");
            DOM.setStyleAttribute(content, "top", "-100000px");
            DOM.setStyleAttribute(content, "left", "-100000px");
            removeStyleDependentName("open");
            setHeight(-1);
            setWidth("");
            open = false;
        }

        public boolean isOpen() {
            return open;
        }

        public void setContent(UIDL contentUidl) {
            final ComponentConnector newPntbl = client
                    .getPaintable(contentUidl);
            Widget newWidget = newPntbl.getWidget();
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

        public void updateCaption(UIDL uidl) {
            // TODO need to call this because the caption does not have an owner
            caption.updateCaptionWithoutOwner(
                    uidl.getStringAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_CAPTION),
                    uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_DISABLED),
                    uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_DESCRIPTION),
                    uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_ERROR_MESSAGE),
                    uidl.getStringAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_ICON));
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

    boolean isDynamicWidth() {
        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);
        return paintable.isUndefinedWidth();
    }

    boolean isDynamicHeight() {
        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);
        return paintable.isUndefinedHeight();
    }

    @Override
    @SuppressWarnings("unchecked")
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
                return ConnectorMap.get(client).getConnector(w);
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
