/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.KeyMapper;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Paintable.RepaintRequestListener;

/**
 * Tabsheet component.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class TabSheet extends AbstractComponentContainer implements
        RepaintRequestListener {

    /**
     * Linked list of component tabs.
     */
    private final LinkedList components = new LinkedList();

    /**
     * Map containing information related to the tabs (caption, icon etc).
     */
    private final HashMap<Component, Tab> tabs = new HashMap<Component, Tab>();

    /**
     * Selected tab.
     */
    private Component selected = null;

    private final KeyMapper keyMapper = new KeyMapper();

    /**
     * Holds the value of property tabsHIdden.
     */
    private boolean tabsHidden;

    private LinkedList paintedTabs = new LinkedList();

    /**
     * Constructs a new Tabsheet. Tabsheet is immediate by default.
     */
    public TabSheet() {
        super();
        // expand horizontally by default
        setWidth(100, UNITS_PERCENTAGE);
        setImmediate(true);
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    public Iterator getComponentIterator() {
        return java.util.Collections.unmodifiableList(components).iterator();
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component c) {
        if (c != null && components.contains(c)) {
            super.removeComponent(c);
            keyMapper.remove(c);
            components.remove(c);
            tabs.remove(c);
            if (c.equals(selected)) {
                if (components.isEmpty()) {
                    selected = null;
                } else {
                    selected = (Component) components.getFirst();
                    fireSelectedTabChange();
                }
            }
            requestRepaint();
        }
    }

    /**
     * Adds a new tab into TabSheet. Components caption and icon are rendered
     * into tab.
     * 
     * @param c
     *            the component to be added.
     */
    @Override
    public void addComponent(Component c) {
        addTab(c);
    }

    /**
     * Adds a new tab into TabSheet.
     * 
     * @param c
     *            the component to be added onto tab.
     * @param caption
     *            the caption to be set for the component and used rendered in
     *            tab bar
     * @param icon
     *            the icon to be set for the component and used rendered in tab
     *            bar
     * @return the created tab
     */
    public Tab addTab(Component c, String caption, Resource icon) {
        if (c != null) {
            components.addLast(c);
            Tab tab = new TabSheetTabImpl(caption, icon);

            tabs.put(c, tab);
            if (selected == null) {
                selected = c;
                fireSelectedTabChange();
            }
            super.addComponent(c);
            requestRepaint();
            return tab;
        } else {
            return null;
        }
    }

    /**
     * Adds a new tab into TabSheet. Components caption and icon are rendered
     * into tab.
     * 
     * @param c
     *            the component to be added onto tab.
     * @return the created tab
     */
    public Tab addTab(Component c) {
        if (c != null) {
            return addTab(c, c.getCaption(), c.getIcon());
        }
        return null;
    }

    /**
     * Gets the component UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     */
    @Override
    public String getTag() {
        return "tabsheet";
    }

    /**
     * Moves all components from another container to this container. The
     * components are removed from the other container.
     * 
     * @param source
     *            the container components are removed from.
     */
    @Override
    public void moveComponentsFrom(ComponentContainer source) {
        for (final Iterator i = source.getComponentIterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            String caption = null;
            Resource icon = null;
            if (TabSheet.class.isAssignableFrom(source.getClass())) {
                caption = ((TabSheet) source).getTabCaption(c);
                icon = ((TabSheet) source).getTabIcon(c);
            }
            source.removeComponent(c);
            addTab(c, caption, icon);

        }
    }

    /**
     * Paints the content of this component.
     * 
     * @param event
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        if (areTabsHidden()) {
            target.addAttribute("hidetabs", true);
        }

        target.startTag("tabs");

        for (final Iterator i = getComponentIterator(); i.hasNext();) {
            final Component component = (Component) i.next();
            Tab tab = tabs.get(component);

            /*
             * If we have no selection, if the current selection is invisible or
             * if the current selection is disabled (but the whole component is
             * not) we select this tab instead
             */
            Tab selectedTabInfo = null;
            if (selected != null) {
                selectedTabInfo = tabs.get(selected);
            }
            if (selected == null || selectedTabInfo == null
                    || !selectedTabInfo.isVisible()
                    || !selectedTabInfo.isEnabled()) {

                // The current selection is not valid so we need to change it
                if (tab.isEnabled() && tab.isVisible()) {
                    selected = component;
                } else {
                    /*
                     * The current selection is not valid but this tab cannot be
                     * selected either.
                     */
                    selected = null;
                }
            }
            target.startTag("tab");
            if (!tab.isEnabled() && tab.isVisible()) {
                target.addAttribute("disabled", true);
            }

            if (!tab.isVisible()) {
                target.addAttribute("hidden", true);
            }

            final Resource icon = tab.getIcon();
            if (icon != null) {
                target.addAttribute("icon", icon);
            }
            final String caption = tab.getCaption();
            if (caption != null && caption.length() > 0) {
                target.addAttribute("caption", caption);
            }

            final String description = tab.getDescription();
            if (description != null) {
                target.addAttribute("description", description);
            }

            final ErrorMessage componentError = tab.getComponentError();
            if (componentError != null) {
                componentError.paint(target);
            }

            target.addAttribute("key", keyMapper.key(component));
            if (component.equals(selected)) {
                target.addAttribute("selected", true);
                component.paint(target);
                paintedTabs.add(component);
            } else if (paintedTabs.contains(component)) {
                component.paint(target);
            } else {
                component.requestRepaintRequests();
            }
            target.endTag("tab");
        }

        target.endTag("tabs");

        if (selected != null) {
            target.addVariable(this, "selected", keyMapper.key(selected));
        }
    }

    /**
     * Are tabs hidden.
     * 
     * @return the Property visibility.
     */
    public boolean areTabsHidden() {
        return tabsHidden;
    }

    /**
     * Setter for property tabsHidden.
     * 
     * @param tabsHidden
     *            True if the tabs should be hidden.
     */
    public void hideTabs(boolean tabsHidden) {
        this.tabsHidden = tabsHidden;
        requestRepaint();
    }

    /**
     * Gets the caption for a component.
     * 
     * @param c
     *            the component.
     * @deprecated Use {@link #getTab(Component)} and {@link Tab#getCaption()}
     *             instead.
     */
    @Deprecated
    public String getTabCaption(Component c) {
        Tab info = tabs.get(c);
        if (info == null) {
            return "";
        } else {
            return info.getCaption();
        }
    }

    /**
     * Sets tabs captions.
     * 
     * @param c
     *            the component.
     * @param caption
     *            the caption to set.
     * @deprecated Use {@link #getTab(Component)} and
     *             {@link Tab#setCaption(String)} instead.
     */
    @Deprecated
    public void setTabCaption(Component c, String caption) {
        Tab info = tabs.get(c);
        if (info != null) {
            info.setCaption(caption);
            requestRepaint();
        }
    }

    /**
     * Gets the icon for a component.
     * 
     * @param c
     *            the component.
     * @deprecated Use {@link #getTab(Component)} and {@link Tab#getIcon()}
     *             instead.
     */
    @Deprecated
    public Resource getTabIcon(Component c) {
        Tab info = tabs.get(c);
        if (info == null) {
            return null;
        } else {
            return info.getIcon();
        }
    }

    /**
     * Sets icon for the given component.
     * 
     * Normally TabSheet uses icon from component
     * 
     * @param c
     *            the component
     * @param icon
     *            the icon to set
     * @deprecated Use {@link #getTab(Component)} and
     *             {@link Tab#setIcon(Resource)} instead.
     */
    @Deprecated
    public void setTabIcon(Component c, Resource icon) {
        Tab info = tabs.get(c);
        if (info != null) {
            info.setIcon(icon);
            requestRepaint();
        }
    }

    /**
     * Returns the Tab for the component. The Tab object can be used for setting
     * caption,icon, etc for the tab.
     * 
     * @param c
     *            the component
     * @return
     */
    public Tab getTab(Component c) {
        return tabs.get(c);
    }

    /**
     * Sets the selected tab.
     * 
     * @param c
     */
    public void setSelectedTab(Component c) {
        if (c != null && components.contains(c) && !c.equals(selected)) {
            selected = c;
            fireSelectedTabChange();
            requestRepaint();
        }
    }

    /**
     * Gets the selected tab.
     * 
     * @return the selected tab.
     */
    public Component getSelectedTab() {
        return selected;
    }

    /**
     * Invoked when the value of a variable has changed.
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {
        if (variables.containsKey("selected")) {
            setSelectedTab((Component) keyMapper.get((String) variables
                    .get("selected")));
        }
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        if (selected == oldComponent) {
            // keep selection w/o selectedTabChange event
            selected = newComponent;
        }

        Tab newTab = tabs.get(newComponent);
        Tab oldTab = tabs.get(oldComponent);

        // Gets the captions
        String oldCaption = null;
        Resource oldIcon = null;
        String newCaption = null;
        Resource newIcon = null;

        if (oldTab != null) {
            oldCaption = oldTab.getCaption();
            oldIcon = oldTab.getIcon();
        }

        if (newTab != null) {
            newCaption = newTab.getCaption();
            newIcon = newTab.getIcon();
        } else {
            newCaption = newComponent.getCaption();
            newIcon = newComponent.getIcon();
        }

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator i = components.iterator(); i.hasNext();) {
            final Component component = (Component) i.next();

            if (component == oldComponent) {
                oldLocation = location;
            }
            if (component == newComponent) {
                newLocation = location;
            }

            location++;
        }

        if (oldLocation == -1) {
            addComponent(newComponent);
        } else if (newLocation == -1) {
            removeComponent(oldComponent);
            keyMapper.remove(oldComponent);
            newTab = addTab(newComponent);
            components.remove(newComponent);
            components.add(oldLocation, newComponent);
            newTab.setCaption(oldCaption);
            newTab.setIcon(oldIcon);
        } else {
            if (oldLocation > newLocation) {
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
            } else {
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
            }

            if (newTab != null) {
                // This should always be true
                newTab.setCaption(oldCaption);
                newTab.setIcon(oldIcon);
            }
            if (oldTab != null) {
                // This should always be true
                oldTab.setCaption(newCaption);
                oldTab.setIcon(newIcon);
            }

            requestRepaint();
        }

    }

    /* Click event */

    private static final Method SELECTED_TAB_CHANGE_METHOD;
    static {
        try {
            SELECTED_TAB_CHANGE_METHOD = SelectedTabChangeListener.class
                    .getDeclaredMethod("selectedTabChange",
                            new Class[] { SelectedTabChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in TabSheet");
        }
    }

    /**
     * Selected Tab Change event. This event is thrown, when the selected tab in
     * the tab sheet is changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class SelectedTabChangeEvent extends Component.Event {

        /**
         * New instance of selected tab change event
         * 
         * @param source
         *            the Source of the event.
         */
        public SelectedTabChangeEvent(Component source) {
            super(source);
        }

        /**
         * TabSheet where the event occurred.
         * 
         * @return the Source of the event.
         */
        public TabSheet getTabSheet() {
            return (TabSheet) getSource();
        }
    }

    /**
     * Selected Tab Change Event listener
     * 
     * @author IT Mill Ltd.
     * 
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface SelectedTabChangeListener extends Serializable {

        /**
         * Visible tab in tab sheet has has been changed.
         * 
         * @param event
         *            the Selected tab change event.
         */
        public void selectedTabChange(SelectedTabChangeEvent event);
    }

    /**
     * Adds the selected tab change listener
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(SelectedTabChangeListener listener) {
        addListener(SelectedTabChangeEvent.class, listener,
                SELECTED_TAB_CHANGE_METHOD);
    }

    /**
     * Removes the selected tab change listener
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(SelectedTabChangeListener listener) {
        removeListener(SelectedTabChangeEvent.class, listener,
                SELECTED_TAB_CHANGE_METHOD);
    }

    /**
     * Emits the options change event.
     */
    protected void fireSelectedTabChange() {
        fireEvent(new SelectedTabChangeEvent(this));
    }

    /*
     * If child is not rendered on the client we need to repaint on child
     * repaint due the way captions and icons are handled.
     */
    public void repaintRequested(RepaintRequestEvent event) {
        if (!paintedTabs.contains(event.getPaintable())) {
            requestRepaint();
        }
    }

    @Override
    public void detach() {
        super.detach();
        paintedTabs.clear();
    }

    /**
     *
     */
    public interface Tab extends Serializable {
        /**
         * Returns the visible status for the tab.
         * 
         * @return true for visible, false for hidden
         */
        public boolean isVisible();

        /**
         * Sets the visible status for the tab.
         * 
         * @param visible
         *            true for visible, false for hidden
         */
        public void setVisible(boolean visible);

        /**
         * Returns the enabled status for the tab.
         * 
         * @return true for enabled, false for disabled
         */
        public boolean isEnabled();

        /**
         * Sets the enabled status for the tab.
         * 
         * @param enabled
         *            true for enabled, false for disabled
         */
        public void setEnabled(boolean enabled);

        /**
         * Sets the caption for the tab.
         * 
         * @param caption
         *            the caption to set
         */
        public void setCaption(String caption);

        /**
         * Gets the caption for the tab.
         * 
         */
        public String getCaption();

        /**
         * Gets the icon for the tab.
         * 
         */
        public Resource getIcon();

        /**
         * Sets the icon for the tab.
         * 
         * @param icon
         *            the icon to set
         */
        public void setIcon(Resource icon);

        /**
         * Gets the description for the tab. The description can be used to
         * briefly describe the state of the tab to the user.
         * 
         * @return the description for the tab
         */
        public String getDescription();

        /**
         * Sets the description for the tab.
         * 
         * @param description
         *            the new description string for the tab.
         */
        public void setDescription(String description);

        public void setComponentError(ErrorMessage componentError);

        public ErrorMessage getComponentError();

    }

    /**
     * TabSheet's implementation of Tab
     * 
     */
    public class TabSheetTabImpl implements Tab {

        private String caption = "";
        private Resource icon = null;
        private boolean enabled = true;
        private boolean visible = true;
        private String description = null;
        private ErrorMessage componentError = null;

        public TabSheetTabImpl(String caption, Resource icon) {
            if (caption == null) {
                caption = "";
            }
            this.caption = caption;
            this.icon = icon;
        }

        /**
         * Returns the tab caption. Can never be null.
         */
        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
            requestRepaint();
        }

        public Resource getIcon() {
            return icon;
        }

        public void setIcon(Resource icon) {
            this.icon = icon;
            requestRepaint();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            requestRepaint();
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
            requestRepaint();
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
            requestRepaint();
        }

        public ErrorMessage getComponentError() {
            return componentError;
        }

        public void setComponentError(ErrorMessage componentError) {
            this.componentError = componentError;
            requestRepaint();
        }

    }
}
