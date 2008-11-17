package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class IAccordion extends ITabsheetBase implements
        ContainerResizedListener {

    public static final String CLASSNAME = "i-accordion";

    private ArrayList<StackItem> stack = new ArrayList<StackItem>();

    private Set<Paintable> paintables = new HashSet<Paintable>();

    private String height;

    private String width;

    private HashMap<StackItem, UIDL> lazyUpdateMap = new HashMap<StackItem, UIDL>();

    private RenderSpace renderSpace = new RenderSpace(0, 0, true);

    private StackItem openTab = null;

    private boolean rendering = false;

    private int selectedUIDLItemIndex = -1;

    private RenderInformation renderInformation = new RenderInformation();

    public IAccordion() {
        super(CLASSNAME);
        // IE6 needs this to calculate offsetHeight correctly
        if (BrowserInfo.get().isIE6()) {
            DOM.setStyleAttribute(getElement(), "zoom", "1");
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;
        selectedUIDLItemIndex = -1;
        super.updateFromUIDL(uidl, client);
        /*
         * Render content after all tabs have been created and we know how large
         * the content area is
         */
        if (selectedUIDLItemIndex >= 0) {
            StackItem selectedItem = stack.get(selectedUIDLItemIndex);
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

    protected void renderTab(UIDL tabUidl, int index, boolean selected,
            boolean hidden) {
        StackItem item;
        int itemIndex;
        if (stack.size() <= index) {
            // Create stackItem and render caption
            item = new StackItem(tabUidl);
            if (stack.size() == 0) {
                item.addStyleDependentName("first");
            }
            stack.add(item);
            itemIndex = stack.size() - 1;
            add(item, getElement());
        } else {
            item = stack.get(index);
            itemIndex = index;
            item.updateCaption(tabUidl);
        }

        item.setVisible(!hidden);

        if (selected) {
            selectedUIDLItemIndex = itemIndex;
        }
        
        if (tabUidl.getChildCount() > 0) {
            lazyUpdateMap.put(item, tabUidl.getChildUIDL(0));
        }
    }

    private void open(int itemIndex) {
        StackItem item = stack.get(itemIndex);
        if (openTab != null) {
            if (openTab.isOpen()) {
                if (openTab == item) {
                    return;
                } else {
                    openTab.close();
                }
            }
        }

        item.open();
        activeTabIndex = itemIndex;
        openTab = item;

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

    protected void selectTab(final int index, final UIDL contentUidl) {
        StackItem item = stack.get(index);
        if (index != activeTabIndex) {
            open(index);
            iLayout();
            // TODO Check if this is needed
            client.runDescendentsLayout(this);

        }
        item.setContent(contentUidl);
    }

    public void onSelectTab(StackItem item) {
        final int index = stack.indexOf(item);
        if (index != activeTabIndex && !disabled && !readonly
                && !disabledTabKeys.contains(tabKeys.get(index))) {
            addStyleDependentName("loading");
            client
                    .updateVariable(id, "selected", "" + tabKeys.get(index),
                            true);
        }
    }

    public void setWidth(String width) {
        super.setWidth(width);
        this.width = width;
        if (!rendering) {
            updateOpenTabSize();
        }
    }

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
            for (Iterator iterator = stack.iterator(); iterator.hasNext();) {
                StackItem item = (StackItem) iterator.next();
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
            for (StackItem si : stack) {
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
            int padding = Util.measureHorizontalPadding(caption.getElement(),
                    18);
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

        private ICaption caption;
        private boolean open = false;
        private Element content = DOM.createDiv();
        private Element captionNode = DOM.createDiv();

        public StackItem(UIDL tabUidl) {
            setElement(DOM.createDiv());
            caption = new ICaption(null, client);
            caption.addClickListener(this);
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

            updateCaption(tabUidl);
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

    protected void clearPaintables() {
        stack.clear();
        clear();
    }

    public boolean isDynamicHeight() {
        return height == null || height.equals("");
    }

    public boolean isDynamicWidth() {
        return width == null || width.equals("");
    }

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
        for (StackItem item : stack) {
            if (item.getPaintable() == oldComponent) {
                item.replacePaintable((Paintable) newComponent);
                return;
            }
        }
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        for (Iterator iterator = stack.iterator(); iterator.hasNext();) {
            StackItem si = (StackItem) iterator.next();
            if (si.getPaintable() == component) {
                boolean visible = si.isVisible();
                si.updateCaption(uidl);
                if (si.isCaptionVisible() != visible) {
                    si.setVisible(si.isCaptionVisible());
                }

                return;
            }
        }
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

}
