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

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.LegacyPaint;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.tabsheet.TabsheetBaseConstants;
import com.vaadin.shared.ui.tabsheet.TabsheetConstants;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

/**
 * TabSheet component.
 * 
 * Tabs are typically identified by the component contained on the tab (see
 * {@link ComponentContainer}), and tab metadata (including caption, icon,
 * visibility, enabledness, closability etc.) is kept in separate {@link Tab}
 * instances.
 * 
 * Tabs added with {@link #addComponent(Component)} get the caption and the icon
 * of the component at the time when the component is created, and these are not
 * automatically updated after tab creation.
 * 
 * A tab sheet can have multiple tab selection listeners and one tab close
 * handler ({@link CloseHandler}), which by default removes the tab from the
 * TabSheet.
 * 
 * The {@link TabSheet} can be styled with the .v-tabsheet, .v-tabsheet-tabs and
 * .v-tabsheet-content styles. Themes may also have pre-defined variations of
 * the tab sheet presentation, such as {@link Reindeer#TABSHEET_BORDERLESS},
 * {@link Runo#TABSHEET_SMALL} and several other styles in {@link Reindeer}.
 * 
 * The current implementation does not load the tabs to the UI before the first
 * time they are shown, but this may change in future releases.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public class TabSheet extends AbstractComponentContainer implements Focusable,
        FocusNotifier, BlurNotifier, LegacyComponent, SelectiveRenderer {

    /**
     * List of component tabs (tab contents). In addition to being on this list,
     * there is a {@link Tab} object in tabs for each tab with meta-data about
     * the tab.
     */
    private final ArrayList<Component> components = new ArrayList<Component>();

    /**
     * Map containing information related to the tabs (caption, icon etc).
     */
    private final HashMap<Component, Tab> tabs = new HashMap<Component, Tab>();

    /**
     * Selected tab content component.
     */
    private Component selected = null;

    /**
     * Mapper between server-side component instances (tab contents) and keys
     * given to the client that identify tabs.
     */
    private final KeyMapper<Component> keyMapper = new KeyMapper<Component>();

    /**
     * When true, the tab selection area is not displayed to the user.
     */
    private boolean tabsHidden;

    /**
     * Handler to be called when a tab is closed.
     */
    private CloseHandler closeHandler;

    private int tabIndex;

    /**
     * Constructs a new TabSheet. A TabSheet is immediate by default, and the
     * default close handler removes the tab being closed.
     */
    public TabSheet() {
        super();
        // expand horizontally by default
        setWidth(100, UNITS_PERCENTAGE);
        setImmediate(true);
        setCloseHandler(new CloseHandler() {

            @Override
            public void onTabClose(TabSheet tabsheet, Component c) {
                tabsheet.removeComponent(c);
            }
        });
    }

    /**
     * Constructs a new TabSheet containing the given components.
     * 
     * @param components
     *            The components to add to the tab sheet. Each component will be
     *            added to a separate tab.
     */
    public TabSheet(Component... components) {
        this();
        addComponents(components);
    }

    /**
     * Gets the component container iterator for going through all the
     * components (tab contents).
     * 
     * @return the unmodifiable Iterator of the tab content components
     */

    @Override
    public Iterator<Component> iterator() {
        return Collections.unmodifiableList(components).iterator();
    }

    /**
     * Gets the number of contained components (tabs). Consistent with the
     * iterator returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components
     */

    @Override
    public int getComponentCount() {
        return components.size();
    }

    /**
     * Removes a component and its corresponding tab.
     * 
     * If the tab was selected, the first eligible (visible and enabled)
     * remaining tab is selected.
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
                    setSelected(null);
                } else {
                    // select the first enabled and visible tab, if any
                    updateSelection();
                    fireSelectedTabChange();
                }
            }
            markAsDirty();
        }
    }

    /**
     * Removes a {@link Tab} and the component associated with it, as previously
     * added with {@link #addTab(Component)},
     * {@link #addTab(Component, String, Resource)} or
     * {@link #addComponent(Component)}.
     * <p>
     * If the tab was selected, the first eligible (visible and enabled)
     * remaining tab is selected.
     * </p>
     * 
     * @see #addTab(Component)
     * @see #addTab(Component, String, Resource)
     * @see #addComponent(Component)
     * @see #removeComponent(Component)
     * @param tab
     *            the Tab to remove
     */
    public void removeTab(Tab tab) {
        removeComponent(tab.getComponent());
    }

    /**
     * Adds a new tab into TabSheet. Component caption and icon are copied to
     * the tab metadata at creation time.
     * 
     * @see #addTab(Component)
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
     * The first tab added to a tab sheet is automatically selected and a tab
     * selection event is fired.
     * 
     * If the component is already present in the tab sheet, changes its caption
     * and returns the corresponding (old) tab, preserving other tab metadata.
     * 
     * @param c
     *            the component to be added onto tab - should not be null.
     * @param caption
     *            the caption to be set for the component and used rendered in
     *            tab bar
     * @return the created {@link Tab}
     */
    public Tab addTab(Component c, String caption) {
        return addTab(c, caption, null);
    }

    /**
     * Adds a new tab into TabSheet.
     * 
     * The first tab added to a tab sheet is automatically selected and a tab
     * selection event is fired.
     * 
     * If the component is already present in the tab sheet, changes its caption
     * and icon and returns the corresponding (old) tab, preserving other tab
     * metadata.
     * 
     * @param c
     *            the component to be added onto tab - should not be null.
     * @param caption
     *            the caption to be set for the component and used rendered in
     *            tab bar
     * @param icon
     *            the icon to be set for the component and used rendered in tab
     *            bar
     * @return the created {@link Tab}
     */
    public Tab addTab(Component c, String caption, Resource icon) {
        return addTab(c, caption, icon, components.size());
    }

    /**
     * Adds a new tab into TabSheet.
     * 
     * The first tab added to a tab sheet is automatically selected and a tab
     * selection event is fired.
     * 
     * If the component is already present in the tab sheet, changes its caption
     * and icon and returns the corresponding (old) tab, preserving other tab
     * metadata like the position.
     * 
     * @param c
     *            the component to be added onto tab - should not be null.
     * @param caption
     *            the caption to be set for the component and used rendered in
     *            tab bar
     * @param icon
     *            the icon to be set for the component and used rendered in tab
     *            bar
     * @param position
     *            the position at where the the tab should be added.
     * @return the created {@link Tab}
     */
    public Tab addTab(Component c, String caption, Resource icon, int position) {
        if (c == null) {
            return null;
        } else if (tabs.containsKey(c)) {
            Tab tab = tabs.get(c);
            tab.setCaption(caption);
            tab.setIcon(icon);
            return tab;
        } else {
            components.add(position, c);

            Tab tab = new TabSheetTabImpl(caption, icon);

            tabs.put(c, tab);
            if (selected == null) {
                setSelected(c);
                fireSelectedTabChange();
            }
            super.addComponent(c);
            markAsDirty();
            return tab;
        }
    }

    /**
     * Adds a new tab into TabSheet. Component caption and icon are copied to
     * the tab metadata at creation time.
     * 
     * If the tab sheet already contains the component, its tab is returned.
     * 
     * @param c
     *            the component to be added onto tab - should not be null.
     * @return the created {@link Tab}
     */
    public Tab addTab(Component c) {
        return addTab(c, components.size());
    }

    /**
     * Adds a new tab into TabSheet. Component caption and icon are copied to
     * the tab metadata at creation time.
     * 
     * If the tab sheet already contains the component, its tab is returned.
     * 
     * @param c
     *            the component to be added onto tab - should not be null.
     * @param position
     *            The position where the tab should be added
     * @return the created {@link Tab}
     */
    public Tab addTab(Component c, int position) {
        if (c == null) {
            return null;
        } else if (tabs.containsKey(c)) {
            return tabs.get(c);
        } else {
            return addTab(c, c.getCaption(), c.getIcon(), position);
        }
    }

    /**
     * Moves all components from another container to this container. The
     * components are removed from the other container.
     * 
     * If the source container is a {@link TabSheet}, component captions and
     * icons are copied from it.
     * 
     * @param source
     *            the container components are removed from.
     */

    @Override
    public void moveComponentsFrom(ComponentContainer source) {
        for (final Iterator<Component> i = source.getComponentIterator(); i
                .hasNext();) {
            final Component c = i.next();
            String caption = null;
            Resource icon = null;
            if (TabSheet.class.isAssignableFrom(source.getClass())) {
                Tab tab = ((TabSheet) source).getTab(c);
                caption = tab.getCaption();
                icon = tab.getIcon();
            }
            source.removeComponent(c);
            addTab(c, caption, icon);

        }
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the paint target
     * @throws PaintException
     *             if the paint operation failed.
     */

    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        if (areTabsHidden()) {
            target.addAttribute("hidetabs", true);
        }

        if (tabIndex != 0) {
            target.addAttribute("tabindex", tabIndex);
        }

        target.startTag("tabs");

        for (final Iterator<Component> i = getComponentIterator(); i.hasNext();) {
            final Component component = i.next();

            Tab tab = tabs.get(component);

            target.startTag("tab");
            if (!tab.isEnabled() && tab.isVisible()) {
                target.addAttribute(
                        TabsheetBaseConstants.ATTRIBUTE_TAB_DISABLED, true);
            }

            if (!tab.isVisible()) {
                target.addAttribute("hidden", true);
            }

            if (tab.isClosable()) {
                target.addAttribute("closable", true);
            }

            // tab icon, caption and description, but used via
            // VCaption.updateCaption(uidl)
            final Resource icon = tab.getIcon();
            if (icon != null) {
                target.addAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_ICON,
                        icon);
            }
            final String caption = tab.getCaption();
            if (caption != null && caption.length() > 0) {
                target.addAttribute(
                        TabsheetBaseConstants.ATTRIBUTE_TAB_CAPTION, caption);
            }
            ErrorMessage tabError = tab.getComponentError();
            if (tabError != null) {
                target.addAttribute(
                        TabsheetBaseConstants.ATTRIBUTE_TAB_ERROR_MESSAGE,
                        tabError.getFormattedHtmlMessage());
            }
            final String description = tab.getDescription();
            if (description != null) {
                target.addAttribute(
                        TabsheetBaseConstants.ATTRIBUTE_TAB_DESCRIPTION,
                        description);
            }

            final String styleName = tab.getStyleName();
            if (styleName != null && styleName.length() != 0) {
                target.addAttribute(TabsheetConstants.TAB_STYLE_NAME, styleName);
            }

            target.addAttribute("key", keyMapper.key(component));
            if (component.equals(selected)) {
                target.addAttribute("selected", true);
                LegacyPaint.paint(component, target);
            }
            target.endTag("tab");
        }

        target.endTag("tabs");

        if (selected != null) {
            target.addVariable(this, "selected", keyMapper.key(selected));
        }

    }

    /**
     * Are the tab selection parts ("tabs") hidden.
     * 
     * @return true if the tabs are hidden in the UI
     */
    public boolean areTabsHidden() {
        return tabsHidden;
    }

    /**
     * Hides or shows the tab selection parts ("tabs").
     * 
     * @param tabsHidden
     *            true if the tabs should be hidden
     */
    public void hideTabs(boolean tabsHidden) {
        this.tabsHidden = tabsHidden;
        markAsDirty();
    }

    /**
     * Returns the {@link Tab} (metadata) for a component. The {@link Tab}
     * object can be used for setting caption,icon, etc for the tab.
     * 
     * @param c
     *            the component
     * @return The tab instance associated with the given component, or null if
     *         the tabsheet does not contain the component.
     */
    public Tab getTab(Component c) {
        return tabs.get(c);
    }

    /**
     * Returns the {@link Tab} (metadata) for a component. The {@link Tab}
     * object can be used for setting caption,icon, etc for the tab.
     * 
     * @param position
     *            the position of the tab
     * @return The tab in the given position, or null if the position is out of
     *         bounds.
     */
    public Tab getTab(int position) {
        if (position >= 0 && position < getComponentCount()) {
            return getTab(components.get(position));
        } else {
            return null;
        }
    }

    /**
     * Sets the selected tab. The tab is identified by the tab content
     * component. Does nothing if the tabsheet doesn't contain the component.
     * 
     * @param c
     */
    public void setSelectedTab(Component c) {
        if (c != null && components.contains(c) && !c.equals(selected)) {
            setSelected(c);
            updateSelection();
            fireSelectedTabChange();
            markAsDirty();
        }
    }

    /**
     * Sets the selected tab in the TabSheet. Ensures that the selected tab is
     * repainted if needed.
     * 
     * @param c
     *            The new selection or null for no selection
     */
    private void setSelected(Component c) {
        selected = c;
        // Repaint of the selected component is needed as only the selected
        // component is communicated to the client. Otherwise this will be a
        // "cached" update even though the client knows nothing about the
        // connector
        if (selected != null) {
            selected.markAsDirtyRecursive();
        }
    }

    /**
     * Sets the selected tab. The tab is identified by the corresponding
     * {@link Tab Tab} instance. Does nothing if the tabsheet doesn't contain
     * the given tab.
     * 
     * @param tab
     */
    public void setSelectedTab(Tab tab) {
        if (tab != null) {
            setSelectedTab(tab.getComponent());
        }
    }

    /**
     * Sets the selected tab, identified by its position. Does nothing if the
     * position is out of bounds.
     * 
     * @param position
     */
    public void setSelectedTab(int position) {
        setSelectedTab(getTab(position));
    }

    /**
     * Checks if the current selection is valid, and updates the selection if
     * the previously selected component is not visible and enabled. The first
     * visible and enabled tab is selected if the current selection is empty or
     * invalid.
     * 
     * This method does not fire tab change events, but the caller should do so
     * if appropriate.
     * 
     * @return true if selection was changed, false otherwise
     */
    private boolean updateSelection() {
        Component originalSelection = selected;
        for (final Iterator<Component> i = getComponentIterator(); i.hasNext();) {
            final Component component = i.next();

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

                // The current selection is not valid so we need to change
                // it
                if (tab.isEnabled() && tab.isVisible()) {
                    setSelected(component);
                    break;
                } else {
                    /*
                     * The current selection is not valid but this tab cannot be
                     * selected either.
                     */
                    setSelected(null);
                }
            }
        }
        return originalSelection != selected;
    }

    /**
     * Gets the selected tab content component.
     * 
     * @return the selected tab contents
     */
    public Component getSelectedTab() {
        return selected;
    }

    // inherits javadoc

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey("selected")) {
            setSelectedTab(keyMapper.get((String) variables.get("selected")));
        }
        if (variables.containsKey("close")) {
            final Component tab = keyMapper
                    .get((String) variables.get("close"));
            if (tab != null) {
                closeHandler.onTabClose(this, tab);
            }
        }
        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
    }

    /**
     * Replaces a component (tab content) with another. This can be used to
     * change tab contents or to rearrange tabs. The tab position and some
     * metadata are preserved when moving components within the same
     * {@link TabSheet}.
     * 
     * If the oldComponent is not present in the tab sheet, the new one is added
     * at the end.
     * 
     * If the oldComponent is already in the tab sheet but the newComponent
     * isn't, the old tab is replaced with a new one, and the caption and icon
     * of the old one are copied to the new tab.
     * 
     * If both old and new components are present, their positions are swapped.
     * 
     * {@inheritDoc}
     */

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {

        if (selected == oldComponent) {
            // keep selection w/o selectedTabChange event
            setSelected(newComponent);
        }

        Tab newTab = tabs.get(newComponent);
        Tab oldTab = tabs.get(oldComponent);

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator<Component> i = components.iterator(); i.hasNext();) {
            final Component component = i.next();

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
            newTab = addTab(newComponent, oldLocation);
            // Copy all relevant metadata to the new tab (#8793)
            // TODO Should reuse the old tab instance instead?
            copyTabMetadata(oldTab, newTab);
        } else {
            components.set(oldLocation, newComponent);
            components.set(newLocation, oldComponent);

            // Tab associations are not changed, but metadata is swapped between
            // the instances
            // TODO Should reassociate the instances instead?
            Tab tmp = new TabSheetTabImpl(null, null);
            copyTabMetadata(newTab, tmp);
            copyTabMetadata(oldTab, newTab);
            copyTabMetadata(tmp, oldTab);

            markAsDirty();
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
     * Selected tab change event. This event is sent when the selected (shown)
     * tab in the tab sheet is changed.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public static class SelectedTabChangeEvent extends Component.Event {

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
     * Selected tab change event listener. The listener is called whenever
     * another tab is selected, including when adding the first tab to a
     * tabsheet.
     * 
     * @author Vaadin Ltd.
     * 
     * @since 3.0
     */
    public interface SelectedTabChangeListener extends Serializable {

        /**
         * Selected (shown) tab in tab sheet has has been changed.
         * 
         * @param event
         *            the selected tab change event.
         */
        public void selectedTabChange(SelectedTabChangeEvent event);
    }

    /**
     * Adds a tab selection listener
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addSelectedTabChangeListener(SelectedTabChangeListener listener) {
        addListener(SelectedTabChangeEvent.class, listener,
                SELECTED_TAB_CHANGE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addSelectedTabChangeListener(SelectedTabChangeListener)}
     **/
    @Deprecated
    public void addListener(SelectedTabChangeListener listener) {
        addSelectedTabChangeListener(listener);
    }

    /**
     * Removes a tab selection listener
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeSelectedTabChangeListener(
            SelectedTabChangeListener listener) {
        removeListener(SelectedTabChangeEvent.class, listener,
                SELECTED_TAB_CHANGE_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeSelectedTabChangeListener(SelectedTabChangeListener)}
     **/
    @Deprecated
    public void removeListener(SelectedTabChangeListener listener) {
        removeSelectedTabChangeListener(listener);
    }

    /**
     * Sends an event that the currently selected tab has changed.
     */
    protected void fireSelectedTabChange() {
        fireEvent(new SelectedTabChangeEvent(this));
    }

    /**
     * Tab meta-data for a component in a {@link TabSheet}.
     * 
     * The meta-data includes the tab caption, icon, visibility and enabledness,
     * closability, description (tooltip) and an optional component error shown
     * in the tab.
     * 
     * Tabs are identified by the component contained on them in most cases, and
     * the meta-data can be obtained with {@link TabSheet#getTab(Component)}.
     */
    public interface Tab extends Serializable {
        /**
         * Returns the visible status for the tab. An invisible tab is not shown
         * in the tab bar and cannot be selected.
         * 
         * @return true for visible, false for hidden
         */
        public boolean isVisible();

        /**
         * Sets the visible status for the tab. An invisible tab is not shown in
         * the tab bar and cannot be selected, selection is changed
         * automatically when there is an attempt to select an invisible tab.
         * 
         * @param visible
         *            true for visible, false for hidden
         */
        public void setVisible(boolean visible);

        /**
         * Returns the closability status for the tab.
         * 
         * @return true if the tab is allowed to be closed by the end user,
         *         false for not allowing closing
         */
        public boolean isClosable();

        /**
         * Sets the closability status for the tab. A closable tab can be closed
         * by the user through the user interface. This also controls if a close
         * button is shown to the user or not.
         * <p>
         * Note! Currently only supported by TabSheet, not Accordion.
         * </p>
         * 
         * @param visible
         *            true if the end user is allowed to close the tab, false
         *            for not allowing to close. Should default to false.
         */
        public void setClosable(boolean closable);

        /**
         * Returns the enabled status for the tab. A disabled tab is shown as
         * such in the tab bar and cannot be selected.
         * 
         * @return true for enabled, false for disabled
         */
        public boolean isEnabled();

        /**
         * Sets the enabled status for the tab. A disabled tab is shown as such
         * in the tab bar and cannot be selected.
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
         */
        public String getCaption();

        /**
         * Gets the icon for the tab.
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
         * briefly describe the state of the tab to the user, and is typically
         * shown as a tooltip when hovering over the tab.
         * 
         * @return the description for the tab
         */
        public String getDescription();

        /**
         * Sets the description for the tab. The description can be used to
         * briefly describe the state of the tab to the user, and is typically
         * shown as a tooltip when hovering over the tab.
         * 
         * @param description
         *            the new description string for the tab.
         */
        public void setDescription(String description);

        /**
         * Sets an error indicator to be shown in the tab. This can be used e.g.
         * to communicate to the user that there is a problem in the contents of
         * the tab.
         * 
         * @see AbstractComponent#setComponentError(ErrorMessage)
         * 
         * @param componentError
         *            error message or null for none
         */
        public void setComponentError(ErrorMessage componentError);

        /**
         * Gets the current error message shown for the tab.
         * 
         * TODO currently not sent to the client
         * 
         * @see AbstractComponent#setComponentError(ErrorMessage)
         */
        public ErrorMessage getComponentError();

        /**
         * Get the component related to the Tab
         */
        public Component getComponent();

        /**
         * Sets a style name for the tab. The style name will be rendered as a
         * HTML class name, which can be used in a CSS definition.
         * 
         * <pre>
         * Tab tab = tabsheet.addTab(tabContent, &quot;Tab text&quot;);
         * tab.setStyleName(&quot;mystyle&quot;);
         * </pre>
         * <p>
         * The used style name will be prefixed with "
         * {@code v-tabsheet-tabitemcell-}". For example, if you give a tab the
         * style "{@code mystyle}", the tab will get a "
         * {@code v-tabsheet-tabitemcell-mystyle}" style. You could then style
         * the component with:
         * </p>
         * 
         * <pre>
         * .v-tabsheet-tabitemcell-mystyle {font-style: italic;}
         * </pre>
         * 
         * <p>
         * This method will trigger a {@link RepaintRequestEvent} on the
         * TabSheet to which the Tab belongs.
         * </p>
         * 
         * @param styleName
         *            the new style to be set for tab
         * @see #getStyleName()
         */
        public void setStyleName(String styleName);

        /**
         * Gets the user-defined CSS style name of the tab. Built-in style names
         * defined in Vaadin or GWT are not returned.
         * 
         * @return the style name or of the tab
         * @see #setStyleName(String)
         */
        public String getStyleName();
    }

    /**
     * TabSheet's implementation of {@link Tab} - tab metadata.
     */
    public class TabSheetTabImpl implements Tab {

        private String caption = "";
        private Resource icon = null;
        private boolean enabled = true;
        private boolean visible = true;
        private boolean closable = false;
        private String description = null;
        private ErrorMessage componentError = null;
        private String styleName;

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

        @Override
        public String getCaption() {
            return caption;
        }

        @Override
        public void setCaption(String caption) {
            this.caption = caption;
            markAsDirty();
        }

        @Override
        public Resource getIcon() {
            return icon;
        }

        @Override
        public void setIcon(Resource icon) {
            this.icon = icon;
            markAsDirty();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (updateSelection()) {
                fireSelectedTabChange();
            }
            markAsDirty();
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
            if (updateSelection()) {
                fireSelectedTabChange();
            }
            markAsDirty();
        }

        @Override
        public boolean isClosable() {
            return closable;
        }

        @Override
        public void setClosable(boolean closable) {
            this.closable = closable;
            markAsDirty();
        }

        public void close() {

        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
            markAsDirty();
        }

        @Override
        public ErrorMessage getComponentError() {
            return componentError;
        }

        @Override
        public void setComponentError(ErrorMessage componentError) {
            this.componentError = componentError;
            markAsDirty();
        }

        @Override
        public Component getComponent() {
            for (Map.Entry<Component, Tab> entry : tabs.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }
            return null;
        }

        @Override
        public void setStyleName(String styleName) {
            this.styleName = styleName;
            markAsDirty();
        }

        @Override
        public String getStyleName() {
            return styleName;
        }
    }

    /**
     * CloseHandler is used to process tab closing events. Default behavior is
     * to remove the tab from the TabSheet.
     * 
     * @author Jouni Koivuviita / Vaadin Ltd.
     * @since 6.2.0
     * 
     */
    public interface CloseHandler extends Serializable {

        /**
         * Called when a user has pressed the close icon of a tab in the client
         * side widget.
         * 
         * @param tabsheet
         *            the TabSheet to which the tab belongs to
         * @param tabContent
         *            the component that corresponds to the tab whose close
         *            button was clicked
         */
        void onTabClose(final TabSheet tabsheet, final Component tabContent);
    }

    /**
     * Provide a custom {@link CloseHandler} for this TabSheet if you wish to
     * perform some additional tasks when a user clicks on a tabs close button,
     * e.g. show a confirmation dialogue before removing the tab.
     * 
     * To remove the tab, if you provide your own close handler, you must call
     * {@link #removeComponent(Component)} yourself.
     * 
     * The default CloseHandler for TabSheet will only remove the tab.
     * 
     * @param handler
     */
    public void setCloseHandler(CloseHandler handler) {
        closeHandler = handler;
    }

    /**
     * Sets the position of the tab.
     * 
     * @param tab
     *            The tab
     * @param position
     *            The new position of the tab
     */
    public void setTabPosition(Tab tab, int position) {
        int oldPosition = getTabPosition(tab);
        components.remove(oldPosition);
        components.add(position, tab.getComponent());
        markAsDirty();
    }

    /**
     * Gets the position of the tab
     * 
     * @param tab
     *            The tab
     * @return
     */
    public int getTabPosition(Tab tab) {
        return components.indexOf(tab.getComponent());
    }

    @Override
    public void focus() {
        super.focus();
    }

    @Override
    public int getTabIndex() {
        return tabIndex;
    }

    @Override
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        markAsDirty();
    }

    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by {@link #addBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void addListener(BlurListener listener) {
        addBlurListener(listener);
    }

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(BlurListener listener) {
        removeBlurListener(listener);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void addListener(FocusListener listener) {
        addFocusListener(listener);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(FocusListener listener) {
        removeFocusListener(listener);
    }

    @Override
    public boolean isRendered(Component childComponent) {
        return childComponent == getSelectedTab();
    }

    /**
     * Copies properties from one Tab to another.
     * 
     * @param from
     *            The tab whose data to copy.
     * @param to
     *            The tab to which copy the data.
     */
    private static void copyTabMetadata(Tab from, Tab to) {
        to.setCaption(from.getCaption());
        to.setIcon(from.getIcon());
        to.setDescription(from.getDescription());
        to.setVisible(from.isVisible());
        to.setEnabled(from.isEnabled());
        to.setClosable(from.isClosable());
        to.setStyleName(from.getStyleName());
        to.setComponentError(from.getComponentError());
    }
}
