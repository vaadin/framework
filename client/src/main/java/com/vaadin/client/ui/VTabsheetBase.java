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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.shared.ui.tabsheet.TabState;

/**
 * Base class for a multi-view widget such as TabSheet or Accordion.
 *
 * @author Vaadin Ltd.
 */
public abstract class VTabsheetBase extends ComplexPanel implements HasEnabled {

    /** For internal use only. May be removed or replaced in the future. */
    protected ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    protected final List<String> tabKeys = new ArrayList<>();
    /** For internal use only. May be removed or replaced in the future. */
    protected Set<String> disabledTabKeys = new HashSet<>();

    /** For internal use only. May be removed or replaced in the future. */
    protected int activeTabIndex = 0;
    /** For internal use only. May be removed or replaced in the future. */
    protected boolean disabled;
    /** For internal use only. May be removed or replaced in the future. */
    protected boolean readonly;

    /** For internal use only. May be removed or replaced in the future. */
    protected AbstractComponentConnector connector;

    private boolean tabCaptionsAsHtml = false;

    /**
     * Constructs a multi-view widget with the given classname.
     *
     * @param classname
     *            the style name to set
     */
    @SuppressWarnings("deprecation")
    public VTabsheetBase(String classname) {
        setElement(DOM.createDiv());
        setStyleName(classname);
    }

    /**
     * @return a list of currently shown Widgets
     */
    public abstract Iterator<Widget> getWidgetIterator();

    /**
     * Clears current tabs and contents.
     *
     * @deprecated This method is not called by the framework code anymore.
     */
    @Deprecated
    protected abstract void clearPaintables();

    /**
     * Implement in extending classes. This method should render needed elements
     * and set the visibility of the tab according to the 'visible' parameter.
     * This method should not update the selection, the connector should handle
     * that separately.
     *
     * @param tabState
     *            shared state of a single tab
     * @param index
     *            the index of that tab
     */
    public abstract void renderTab(TabState tabState, int index);

    /**
     * Implement in extending classes. This method should return the number of
     * tabs currently rendered.
     *
     * @return the number of currently rendered tabs
     */
    public abstract int getTabCount();

    /**
     * Implement in extending classes. This method should return the connector
     * corresponding to the given index.
     *
     * @param index
     *            the index of the tab whose connector to find
     * @return the connector of the queried tab, or {@code null} if not found
     */
    public abstract ComponentConnector getTab(int index);

    /**
     * Implement in extending classes. This method should remove the rendered
     * tab with the specified index.
     *
     * @param index
     *            the index of the tab to remove
     */
    public abstract void removeTab(int index);

    /**
     * Returns whether the width of the widget is undefined.
     *
     * @since 7.2
     * @return {@code true} if width of the widget is determined by its content,
     *         {@code false} otherwise
     */
    protected boolean isDynamicWidth() {
        return getConnectorForWidget(this).isUndefinedWidth();
    }

    /**
     * Returns whether the height of the widget is undefined.
     *
     * @since 7.2
     * @return {@code true} if height of the widget is determined by its
     *         content, {@code false} otherwise
     */
    protected boolean isDynamicHeight() {
        return getConnectorForWidget(this).isUndefinedHeight();
    }

    /**
     * Sets the connector that should be notified of events etc.
     *
     * For internal use only. This method may be removed or replaced in the
     * future.
     *
     * @since 7.2
     * @param connector
     *            the connector of this widget
     */
    public void setConnector(AbstractComponentConnector connector) {
        this.connector = connector;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void clearTabKeys() {
        tabKeys.clear();
        disabledTabKeys.clear();
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param key
     *            an internal key that corresponds with a tab
     * @param disabled
     *            {@code true} if the tab should be disabled, {@code false}
     *            otherwise
     */
    public void addTabKey(String key, boolean disabled) {
        tabKeys.add(key);
        if (disabled) {
            disabledTabKeys.add(key);
        }
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param client
     *            the current application connection instance
     */
    public void setClient(ApplicationConnection client) {
        this.client = client;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param activeTabIndex
     *            the index of the currently active tab
     */
    public void setActiveTabIndex(int activeTabIndex) {
        this.activeTabIndex = activeTabIndex;
    }

    /** For internal use only. May be removed or replaced in the future. */
    @Override
    public void setEnabled(boolean enabled) {
        disabled = !enabled;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param readonly
     *            {@code true} if this widget should be read-only, {@code false}
     *            otherwise
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param widget
     *            the widget whose connector to find
     * @return the connector
     */
    protected ComponentConnector getConnectorForWidget(Widget widget) {
        return ConnectorMap.get(client).getConnector(widget);
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param index
     *            the index of the tab to select
     */
    public abstract void selectTab(int index);

    @Override
    public boolean isEnabled() {
        return !disabled;
    }

    /**
     * Sets whether the caption is rendered as HTML.
     * <p>
     * The default is false, i.e. render tab captions as plain text
     * <p>
     * This value is delegated from the TabsheetState.
     *
     * @since 7.4
     * @param tabCaptionsAsHtml
     *            {@code true} if the captions are rendered as HTML,
     *            {@code false} if rendered as plain text
     */
    public void setTabCaptionsAsHtml(boolean tabCaptionsAsHtml) {
        this.tabCaptionsAsHtml = tabCaptionsAsHtml;
    }

    /**
     * Checks whether captions are rendered as HTML
     *
     * The default is false, i.e. render tab captions as plain text
     *
     * @since 7.4
     * @return true if the captions are rendered as HTML, false if rendered as
     *         plain text
     */
    public boolean isTabCaptionsAsHtml() {
        return tabCaptionsAsHtml;
    }

}
