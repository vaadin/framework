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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.shared.ui.tabsheet.TabState;

public abstract class VTabsheetBase extends ComplexPanel implements HasEnabled {

    /** For internal use only. May be removed or replaced in the future. */
    protected ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    protected final ArrayList<String> tabKeys = new ArrayList<String>();
    /** For internal use only. May be removed or replaced in the future. */
    protected Set<String> disabledTabKeys = new HashSet<String>();

    /** For internal use only. May be removed or replaced in the future. */
    protected int activeTabIndex = 0;
    /** For internal use only. May be removed or replaced in the future. */
    protected boolean disabled;
    /** For internal use only. May be removed or replaced in the future. */
    protected boolean readonly;

    /** For internal use only. May be removed or replaced in the future. */
    protected AbstractComponentConnector connector;

    private boolean tabCaptionsAsHtml = false;

    public VTabsheetBase(String classname) {
        setElement(DOM.createDiv());
        setStyleName(classname);
    }

    /**
     * @return a list of currently shown Widgets
     */
    public abstract Iterator<Widget> getWidgetIterator();

    /**
     * Clears current tabs and contents
     */
    protected abstract void clearPaintables();

    /**
     * Implement in extending classes. This method should render needed elements
     * and set the visibility of the tab according to the 'selected' parameter.
     */
    public abstract void renderTab(TabState tabState, int index);

    /**
     * Implement in extending classes. This method should return the number of
     * tabs currently rendered.
     */
    public abstract int getTabCount();

    /**
     * Implement in extending classes. This method should return the Paintable
     * corresponding to the given index.
     */
    public abstract ComponentConnector getTab(int index);

    /**
     * Implement in extending classes. This method should remove the rendered
     * tab with the specified index.
     */
    public abstract void removeTab(int index);

    /**
     * Returns true if the width of the widget is undefined, false otherwise.
     * 
     * @since 7.2
     * @return true if width of the widget is determined by its content
     */
    protected boolean isDynamicWidth() {
        return getConnectorForWidget(this).isUndefinedWidth();
    }

    /**
     * Returns true if the height of the widget is undefined, false otherwise.
     * 
     * @since 7.2
     * @return true if width of the height is determined by its content
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
     */
    public void setConnector(AbstractComponentConnector connector) {
        this.connector = connector;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void clearTabKeys() {
        tabKeys.clear();
        disabledTabKeys.clear();
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void addTabKey(String key, boolean disabled) {
        tabKeys.add(key);
        if (disabled) {
            disabledTabKeys.add(key);
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setClient(ApplicationConnection client) {
        this.client = client;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setActiveTabIndex(int activeTabIndex) {
        this.activeTabIndex = activeTabIndex;
    }

    /** For internal use only. May be removed or replaced in the future. */
    @Override
    public void setEnabled(boolean enabled) {
        disabled = !enabled;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /** For internal use only. May be removed or replaced in the future. */
    protected ComponentConnector getConnectorForWidget(Widget widget) {
        return ConnectorMap.get(client).getConnector(widget);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public abstract void selectTab(int index);

    @Override
    public boolean isEnabled() {
        return !disabled;
    }

    /**
     * Sets whether the caption is rendered as HTML.
     * <p>
     * The default is false, i.e. render tab captions as plain text
     * 
     * @since 7.4
     * @param captionAsHtml
     *            true if the captions are rendered as HTML, false if rendered
     *            as plain text
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
