package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;

public class VAccordion extends VTabsheetBase implements
        ContainerResizedListener {

    public static final String CLASSNAME = "v-accordion";

    private Set<Paintable> paintables = new HashSet<Paintable>();

    private String height;

    private String width = "";

    private HashMap<StackItem, UIDL> lazyUpdateMap = new HashMap<StackItem, UIDL>();

    private RenderSpace renderSpace = new RenderSpace(0, 0, true);

    private StackItem openTab = null;

    private boolean rendering = false;

    private int selectedUIDLItemIndex = -1;

    private RenderInformation renderInformation = new RenderInformation();

    public VAccordion() {
        super(CLASSNAME);
        // IE6 needs this to calculate offsetHeight correctly
        if (BrowserInfo.get().isIE6()) {
            DOM.setStyleAttribute(getElement(), "zoom", "1");
        }
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;
        selectedUIDLItemIndex = -1;
        super.updateFromUIDL(uidl, client);
        /*
         * Render content after all tabs have been created and we know how large
         * the content area is
         */
        if (selectedUIDLItemIndex >= 0) {
            StackItem selectedItem = getStackItem(selectedUIDLItemIndex);
            UIDL selectedTabUIDL = lazyUpdateMap.remove(selectedItem);
            open(selectedUIDLItemIndex);

            selectedItem.setContent(selectedTabUIDL);
        } else if (!uidl.getBooleanAttribute("cached") && openTab != null) {
            close(openTab);
        }

        iLayout();
        // finally render possible hidden tabs
        if (lazyUpdateMap.size() > 0) {
            for (Iterator iterator = lazyUpdateMap.keySet().iterator(); iterator
                    .hasNext();) {
                StackItem item = (StackItem) iterator.next();
                item.setContent(lazyUpdateMap.get(item));
            }
            lazyUpdateMap.clear();
        }

        renderInformation.updateSize(getElement());

        rendering = false;
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
        Paintable tabContent = null;
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
                item = new StackItem(tabUidl);
                insert(item, getElement(), newIndex, true);
            }
        }
        return item;
    }

    private void open(int itemIndex) {
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

    private void close(StackItem item) {
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
            client
                    .updateVariable(id, "selected", "" + tabKeys.get(index),
                            true);
        }
    }

    @Override
    public void setWidth(String width) {
        if (this.width.equals(width)) {
            return;
        }

        super.setWidth(width);
        this.width = width;
        if (!rendering) {
            updateOpenTabSize();

            if (isDynamicHeight()) {
                Util.updateRelativeChildrenAndSendSizeUpdateEvent(client,
                        openTab, this);
                updateOpenTabSize();
            }

            if (isDynamicHeight()) {
                openTab.setHeightFromWidget();
            }
            iLayout();
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        this.height = height;

        if (!rendering) {
            updateOpenTabSize();
        }

    }

    /**
     * Sets the size of the open tab
     */
    private void updateOpenTabSize() {
        if (openTab == null) {
            renderSpace.setHeight(0);
            renderSpace.setWidth(0);
            return;
        }

        // WIDTH
        if (!isDynamicWidth()) {
            int w = getOffsetWidth();
            openTab.setWidth(w);
            renderSpace.setWidth(w);
        } else {
            renderSpace.setWidth(0);
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

            renderSpace.setHeight(spaceForOpenItem);
            openTab.setHeight(spaceForOpenItem);
        } else {
            renderSpace.setHeight(0);
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

        Util.runWebkitOverflowAutoFix(openTab.getContainerElement());

    }

    /**
     * 
     */
    protected class StackItem extends ComplexPanel implements ClickListener {

        public void setHeight(int height) {
            if (height == -1) {
                super.setHeight("");
                DOM.setStyleAttribute(content, "height", "0px");
            } else {
                super.setHeight((height + getCaptionHeight()) + "px");
                DOM.setStyleAttribute(content, "height", height + "px");
                DOM
                        .setStyleAttribute(content, "top", getCaptionHeight()
                                + "px");

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
            Widget paintable = getPaintable();
            if (paintable == null) {
                return;
            }

            int paintableHeight = (paintable).getElement().getOffsetHeight();
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
            int padding = Util.measureHorizontalPaddingAndBorder(caption
                    .getElement(), 18);
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
            caption = new VCaption(null, client);
            caption.addClickListener(this);
            if (BrowserInfo.get().isIE6()) {
                DOM.setEventListener(captionNode, this);
                DOM.sinkEvents(captionNode, Event.BUTTON_LEFT);
            }
            super.add(caption, captionNode);
            DOM.appendChild(captionNode, caption.getElement());
            DOM.appendChild(getElement(), captionNode);
            DOM.appendChild(getElement(), content);
            setStylePrimaryName(CLASSNAME + "-item");
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

        public Widget getPaintable() {
            if (getWidgetCount() > 1) {
                return getWidget(1);
            } else {
                return null;
            }
        }

        public void replacePaintable(Paintable newPntbl) {
            if (getWidgetCount() > 1) {
                client.unregisterPaintable((Paintable) getWidget(1));
                paintables.remove(getWidget(1));
                remove(1);
            }
            add((Widget) newPntbl, content);
            paintables.add(newPntbl);
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
            open = false;
        }

        public boolean isOpen() {
            return open;
        }

        public void setContent(UIDL contentUidl) {
            final Paintable newPntbl = client.getPaintable(contentUidl);
            if (getPaintable() == null) {
                add((Widget) newPntbl, content);
                paintables.add(newPntbl);
            } else if (getPaintable() != newPntbl) {
                replacePaintable(newPntbl);
            }
            newPntbl.updateFromUIDL(contentUidl, client);
            if (contentUidl.getBooleanAttribute("cached")) {
                /*
                 * The size of a cached, relative sized component must be
                 * updated to report correct size.
                 */
                client.handleComponentRelativeSize((Widget) newPntbl);
            }
            if (isOpen() && isDynamicHeight()) {
                setHeightFromWidget();
            }
        }

        public void onClick(Widget sender) {
            onSelectTab(this);
        }

        public void updateCaption(UIDL uidl) {
            caption.updateCaption(uidl);
        }

        public int getWidgetWidth() {
            return DOM.getFirstChild(content).getOffsetWidth();
        }

        public boolean contains(Paintable p) {
            return (getPaintable() == p);
        }

        public boolean isCaptionVisible() {
            return caption.isVisible();
        }

    }

    @Override
    protected void clearPaintables() {
        clear();
    }

    public boolean isDynamicHeight() {
        return height == null || height.equals("");
    }

    public boolean isDynamicWidth() {
        return width == null || width.equals("");
    }

    @Override
    protected Iterator getPaintableIterator() {
        return paintables.iterator();
    }

    public boolean hasChildComponent(Widget component) {
        if (paintables.contains(component)) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        for (Widget w : getChildren()) {
            StackItem item = (StackItem) w;
            if (item.getPaintable() == oldComponent) {
                item.replacePaintable((Paintable) newComponent);
                return;
            }
        }
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        /* Accordion does not render its children's captions */
    }

    public boolean requestLayout(Set<Paintable> child) {
        if (!isDynamicHeight() && !isDynamicWidth()) {
            /*
             * If the height and width has been specified for this container the
             * child components cannot make the size of the layout change
             */

            return true;
        }

        updateOpenTabSize();

        if (renderInformation.updateSize(getElement())) {
            /*
             * Size has changed so we let the child components know about the
             * new size.
             */
            iLayout();
            // TODO Check if this is needed
            client.runDescendentsLayout(this);

            return false;
        } else {
            /*
             * Size has not changed so we do not need to propagate the event
             * further
             */
            return true;
        }

    }

    public RenderSpace getAllocatedSpace(Widget child) {
        return renderSpace;
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
    protected Paintable getTab(int index) {
        if (index < getWidgetCount()) {
            return (Paintable) (getStackItem(index)).getPaintable();
        }

        return null;
    }

    private StackItem getStackItem(int index) {
        return (StackItem) getWidget(index);
    }
}
