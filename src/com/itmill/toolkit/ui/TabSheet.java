/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.itmill.toolkit.terminal.KeyMapper;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.Paintable.RepaintRequestListener;

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
    private final LinkedList tabs = new LinkedList();

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
        return java.util.Collections.unmodifiableList(tabs).iterator();
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component c) {
        if (c != null && tabs.contains(c)) {
            super.removeComponent(c);
            keyMapper.remove(c);
            tabs.remove(c);
            if (c.equals(selected)) {
                if (tabs.isEmpty()) {
                    selected = null;
                } else {
                    selected = (Component) tabs.getFirst();
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
     */
    public void addTab(Component c, String caption, Resource icon) {
        if (c != null) {
            if (caption != null) {
                c.setCaption(caption);
            }
            if (icon != null) {
                c.setIcon(icon);
            }
            addTab(c);
        }
    }

    /**
     * Adds a new tab into TabSheet. Components caption and icon are rendered
     * into tab.
     * 
     * @param c
     *            the component to be added onto tab.
     */
    public void addTab(Component c) {
        if (c != null) {
            tabs.addLast(c);
            if (selected == null) {
                selected = c;
                fireSelectedTabChange();
            }
            super.addComponent(c);
            requestRepaint();
        }
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
            final Component c = (Component) i.next();

            /*
             * If we have no selection, if the current selection is invisible or
             * if the current selection is disabled (but the whole component is
             * not) we select this tab instead
             */
            if (selected == null || !selected.isVisible()
                    || (!selected.isEnabled() && isEnabled())) {
                selected = c;
            }
            target.startTag("tab");
            if (!c.isEnabled() && c.isVisible()) {
                target.addAttribute("disabled", true);
            }

            if (!c.isVisible()) {
                target.addAttribute("hidden", true);
            }

            final Resource icon = getTabIcon(c);
            if (icon != null) {
                target.addAttribute("icon", icon);
            }
            final String caption = getTabCaption(c);
            if (caption != null && caption.length() > 0) {
                target.addAttribute("caption", caption);
            }

            if (c instanceof AbstractComponent) {
                AbstractComponent ac = (AbstractComponent) c;
                if (ac.getDescription() != null) {
                    target.addAttribute("description", ac.getDescription());
                }
                if (ac.getComponentError() != null) {
                    ac.getComponentError().paint(target);
                }
            }

            target.addAttribute("key", keyMapper.key(c));
            if (c.equals(selected)) {
                target.addAttribute("selected", true);
                c.paint(target);
                paintedTabs.add(c);
            } else if (paintedTabs.contains(c)) {
                c.paint(target);
            } else {
                c.requestRepaintRequests();
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
     */
    public String getTabCaption(Component c) {
        if (c.getCaption() == null) {
            return "";
        } else {
            return c.getCaption();
        }
    }

    /**
     * Sets tabs captions.
     * 
     * @param c
     *            the component.
     * @param caption
     *            the caption to set.
     */
    public void setTabCaption(Component c, String caption) {
        if (tabs.contains(c)) {
            c.setCaption(caption);
        }
    }

    /**
     * Gets the icon for a component.
     * 
     * @param c
     *            the component.
     */
    public Resource getTabIcon(Component c) {
        return c.getIcon();
    }

    /**
     * Sets overridden icon for given component.
     * 
     * Normally TabSheet uses icon from component
     * 
     * @param c
     * @param icon
     */
    public void setTabIcon(Component c, Resource icon) {
        if (tabs.contains(c)) {
            c.setIcon(icon);
        }
    }

    /**
     * Sets the selected tab.
     * 
     * @param c
     */
    public void setSelectedTab(Component c) {
        if (c != null && tabs.contains(c) && !selected.equals(c)) {
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
     * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
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

        // Gets the captions
        final String oldCaption = getTabCaption(oldComponent);
        final Resource oldIcon = getTabIcon(oldComponent);
        final String newCaption = getTabCaption(newComponent);
        final Resource newIcon = getTabIcon(newComponent);

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator i = tabs.iterator(); i.hasNext();) {
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
            addComponent(newComponent);
            tabs.remove(newComponent);
            tabs.add(oldLocation, newComponent);
            setTabCaption(newComponent, oldCaption);
            setTabIcon(newComponent, oldIcon);
        } else {
            if (oldLocation > newLocation) {
                tabs.remove(oldComponent);
                tabs.add(newLocation, oldComponent);
                tabs.remove(newComponent);
                tabs.add(oldLocation, newComponent);
            } else {
                tabs.remove(newComponent);
                tabs.add(oldLocation, newComponent);
                tabs.remove(oldComponent);
                tabs.add(newLocation, oldComponent);
            }
            setTabCaption(newComponent, oldCaption);
            setTabIcon(newComponent, oldIcon);
            setTabCaption(oldComponent, newCaption);
            setTabIcon(oldComponent, newIcon);

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

}
