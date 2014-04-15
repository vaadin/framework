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
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.UIDL;

public abstract class VTabsheetBase extends ComplexPanel {

    /** For internal use only. May be removed or replaced in the future. */
    public String id;
    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public final ArrayList<String> tabKeys = new ArrayList<String>();
    /** For internal use only. May be removed or replaced in the future. */
    public Set<String> disabledTabKeys = new HashSet<String>();

    /** For internal use only. May be removed or replaced in the future. */
    public int activeTabIndex = 0;
    /** For internal use only. May be removed or replaced in the future. */
    public boolean disabled;
    /** For internal use only. May be removed or replaced in the future. */
    public boolean readonly;

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
    abstract protected void clearPaintables();

    /**
     * Implement in extending classes. This method should render needed elements
     * and set the visibility of the tab according to the 'selected' parameter.
     */
    public abstract void renderTab(final UIDL tabUidl, int index,
            boolean selected, boolean hidden);

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
}
