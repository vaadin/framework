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

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

/**
 * A helper class used to make it easier for {@link Widget}s to implement
 * {@link Focusable}.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.3
 * 
 */
public class FocusUtil {

    /**
     * Sets the access key property
     * 
     * @param focusable
     *            The widget for which we want to set the access key.
     * @param key
     *            The access key to set
     */
    public static void setAccessKey(Widget focusable, char key) {
        assert (focusable != null && focusable.getElement() != null) : "Can't setAccessKey for a widget without an element";
        focusable.getElement().setPropertyString("accessKey", "" + key);
    }

    /**
     * Explicitly focus/unfocus the given widget. Only one widget can have focus
     * at a time, and the widget that does will receive all keyboard events.
     * 
     * @param focusable
     *            the widget to focus/unfocus
     * @param focused
     *            whether this widget should take focus or release it
     */
    public static void setFocus(Widget focusable, boolean focus) {
        assert (focusable != null && focusable.getElement() != null) : "Can't setFocus for a widget without an element";

        if (focus) {
            focusable.getElement().focus();
        } else {
            focusable.getElement().blur();
        }
    }

    /**
     * Sets the widget's position in the tab index. If more than one widget has
     * the same tab index, each such widget will receive focus in an arbitrary
     * order. Setting the tab index to <code>-1</code> will cause the widget to
     * be removed from the tab order.
     * 
     * @param focusable
     *            The widget
     * @param tabIndex
     *            the widget's tab index
     */
    public static void setTabIndex(Widget focusable, int tabIndex) {
        assert (focusable != null && focusable.getElement() != null) : "Can't setTabIndex for a widget without an element";

        focusable.getElement().setTabIndex(tabIndex);
    }

    /**
     * Gets the widget's position in the tab index.
     * 
     * @param focusable
     *            The widget
     * 
     * @return the widget's tab index
     */
    public static int getTabIndex(Widget focusable) {
        assert (focusable != null && focusable.getElement() != null) : "Can't getTabIndex for a widget without an element";

        return focusable.getElement().getTabIndex();
    }
}
