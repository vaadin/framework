/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.ui.tabsheet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class VTabsheetBase extends ComplexPanel {

    protected String id;
    protected ApplicationConnection client;

    protected final ArrayList<String> tabKeys = new ArrayList<String>();
    protected int activeTabIndex = 0;
    protected boolean disabled;
    protected boolean readonly;
    protected Set<String> disabledTabKeys = new HashSet<String>();

    public VTabsheetBase(String classname) {
        setElement(DOM.createDiv());
        setStyleName(classname);
    }

    /**
     * @return a list of currently shown Widgets
     */
    abstract protected Iterator<Widget> getWidgetIterator();

    /**
     * Clears current tabs and contents
     */
    abstract protected void clearPaintables();

    /**
     * Implement in extending classes. This method should render needed elements
     * and set the visibility of the tab according to the 'selected' parameter.
     */
    protected abstract void renderTab(final UIDL tabUidl, int index,
            boolean selected, boolean hidden);

    /**
     * Implement in extending classes. This method should render any previously
     * non-cached content and set the activeTabIndex property to the specified
     * index.
     */
    protected abstract void selectTab(int index, final UIDL contentUidl);

    /**
     * Implement in extending classes. This method should return the number of
     * tabs currently rendered.
     */
    protected abstract int getTabCount();

    /**
     * Implement in extending classes. This method should return the Paintable
     * corresponding to the given index.
     */
    protected abstract ComponentConnector getTab(int index);

    /**
     * Implement in extending classes. This method should remove the rendered
     * tab with the specified index.
     */
    protected abstract void removeTab(int index);
}
