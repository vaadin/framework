/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

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
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;

public class VAccordion extends VTabsheetBase {

    public static final String CLASSNAME = "v-accordion";

    private Set<Widget> widgets = new HashSet<Widget>();

    HashMap<StackItem, UIDL> lazyUpdateMap = new HashMap<StackItem, UIDL>();

    StackItem openTab = null;

    int selectedUIDLItemIndex = -1;

    public VAccordion() {
        super(CLASSNAME);
    }

    @Override
    protected void renderTab(UIDL tabUidl, int index, boolean selected,
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

        item.setVisible(!hidden);

        if (selected) {
            selectedUIDLItemIndex = itemIndex;
        }

        if (tabUidl.getChildCount() > 0) {
            lazyUpdateMap.put(item, tabUidl.getChildUIDL(0));
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
            if (tabContent != itemWidget) {
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

    void open(int itemIndex) {
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

        // Update the size for the open tab
        updateOpenTabSize();
    }

    void close(StackItem item) {
        if (!item.isOpen()) {
            return;
        }

        item.close();
        activeTabIndex = -1;
        openTab = null;

    }

    @Override
    protected void selectTab(final int index, final UIDL contentUidl) {
        StackItem item = getStackItem(index);
        if (index != activeTabIndex) {
            open(index);
            iLayout();
            // TODO Check if this is needed
            client.runDescendentsLayout(this);

        }
        item.setContent(contentUidl);
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
     * Sets the size of the open tab
     */
    void updateOpenTabSize() {
        if (openTab == null) {
            return;
        }

        // WIDTH
        if (!isDynamicWidth()) {
            openTab.setWidth("100%");
        } else {
            openTab.setWidth(null);
        }

        // HEIGHT
        if (!isDynamicHeight()) {
            int usedPixels = 0;
            for (Widget w : getChildren()) {
                StackItem item = (StackItem) w;
                if (item == openTab) {
                    usedPixels += item.getCaptionHeight();
                } else {
                    // This includes the captionNode borders
                    usedPixels += item.getHeight();
                }
            }

            int offsetHeight = getOffsetHeight();

            int spaceForOpenItem = offsetHeight - usedPixels;

            if (spaceForOpenItem < 0) {
                spaceForOpenItem = 0;
            }

            openTab.setHeight(spaceForOpenItem);
        } else {
            openTab.setHeightFromWidget();

        }

    }

    public void iLayout() {
        if (openTab == null) {
            return;
        }

        if (isDynamicWidth()) {
            int maxWidth = 40;
            for (Widget w : getChildren()) {
                StackItem si = (StackItem) w;
                int captionWidth = si.getCaptionWidth();
                if (captionWidth > maxWidth) {
                    maxWidth = captionWidth;
                }
            }
            int widgetWidth = openTab.getWidgetWidth();
            if (widgetWidth > maxWidth) {
                maxWidth = widgetWidth;
            }
            super.setWidth(maxWidth + "px");
            openTab.setWidth(maxWidth);
        }
    }

    /**
     * A StackItem has always two children, Child 0 is a VCaption, Child 1 is
     * the actual child widget.
     */
    protected class StackItem extends ComplexPanel implements ClickHandler {

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

        public StackItem(UIDL tabUidl) {
            setElement(DOM.createDiv());
            caption = new VCaption(client);
            caption.addClickHandler(this);
            super.add(caption, captionNode);
            DOM.appendChild(captionNode, caption.getElement());
            DOM.appendChild(getElement(), captionNode);
            DOM.appendChild(getElement(), content);
            setStyleName(CLASSNAME + "-item");
            DOM.setElementProperty(content, "className", CLASSNAME
                    + "-item-content");
            DOM.setElementProperty(captionNode, "className", CLASSNAME
                    + "-item-caption");
            close();
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
                ComponentConnector oldPaintable = ConnectorMap.get(client)
                        .getConnector(oldWidget);
                ConnectorMap.get(client).unregisterConnector(oldPaintable);
                widgets.remove(oldWidget);
                remove(1);
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
            newPntbl.updateFromUIDL(contentUidl, client);
            if (contentUidl.getBooleanAttribute("cached")) {
                /*
                 * The size of a cached, relative sized component must be
                 * updated to report correct size.
                 */
                client.handleComponentRelativeSize(newPntbl.getWidget());
            }
            if (isOpen() && isDynamicHeight()) {
                setHeightFromWidget();
            }
        }

        public void onClick(ClickEvent event) {
            onSelectTab(this);
        }

        public void updateCaption(UIDL uidl) {
            // TODO need to call this because the caption does not have an owner
            caption.updateCaptionWithoutOwner(
                    uidl.getStringAttribute(TabsheetBaseConnector.ATTRIBUTE_TAB_CAPTION),
                    uidl.hasAttribute(TabsheetBaseConnector.ATTRIBUTE_TAB_DISABLED),
                    uidl.hasAttribute(TabsheetBaseConnector.ATTRIBUTE_TAB_DESCRIPTION),
                    uidl.hasAttribute(TabsheetBaseConnector.ATTRIBUTE_TAB_ERROR_MESSAGE),
                    uidl.getStringAttribute(TabsheetBaseConnector.ATTRIBUTE_TAB_ICON));
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
    protected Iterator<Widget> getWidgetIterator() {
        return widgets.iterator();
    }

    @Override
    protected int getTabCount() {
        return getWidgetCount();
    }

    @Override
    protected void removeTab(int index) {
        StackItem item = getStackItem(index);
        remove(item);
    }

    @Override
    protected ComponentConnector getTab(int index) {
        if (index < getWidgetCount()) {
            Widget w = getStackItem(index);
            return ConnectorMap.get(client).getConnector(w);
        }

        return null;
    }

    StackItem getStackItem(int index) {
        return (StackItem) getWidget(index);
    }

}
