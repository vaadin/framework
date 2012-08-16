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
package com.vaadin.ui.themes;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class Reindeer extends BaseTheme {

    public static final String THEME_NAME = "reindeer";

    /***************************************************************************
     * 
     * Label styles
     * 
     **************************************************************************/

    /**
     * Large font for main application headings
     */
    public static final String LABEL_H1 = "h1";

    /**
     * Large font for different sections in the application
     */
    public static final String LABEL_H2 = "h2";

    /**
     * Small and a little lighter font
     */
    public static final String LABEL_SMALL = "light";

    /**
     * @deprecated Use {@link #LABEL_SMALL} instead.
     */
    @Deprecated
    public static final String LABEL_LIGHT = "small";

    /***************************************************************************
     * 
     * Button styles
     * 
     **************************************************************************/

    /**
     * Default action style for buttons (the button that should get activated
     * when the user presses 'enter' in a form). Use sparingly, only one default
     * button per view should be visible.
     */
    public static final String BUTTON_DEFAULT = "primary";

    /**
     * @deprecated Use {@link #BUTTON_DEFAULT} instead
     */
    @Deprecated
    public static final String BUTTON_PRIMARY = BUTTON_DEFAULT;

    /**
     * Small sized button, use for context specific actions for example
     */
    public static final String BUTTON_SMALL = "small";

    /***************************************************************************
     * 
     * TextField styles
     * 
     **************************************************************************/

    /**
     * Small sized text field with small font
     */
    public static final String TEXTFIELD_SMALL = "small";

    /***************************************************************************
     * 
     * Panel styles
     * 
     **************************************************************************/

    /**
     * Removes borders and background color from the panel
     */
    public static final String PANEL_LIGHT = "light";

    /***************************************************************************
     * 
     * SplitPanel styles
     * 
     **************************************************************************/

    /**
     * Reduces the split handle to a minimal size (1 pixel)
     */
    public static final String SPLITPANEL_SMALL = "small";

    /***************************************************************************
     * 
     * TabSheet styles
     * 
     **************************************************************************/

    /**
     * Removes borders from the default tab sheet style.
     */
    public static final String TABSHEET_BORDERLESS = "borderless";

    /**
     * Removes borders and background color from the tab sheet, and shows the
     * tabs as a small bar.
     */
    public static final String TABSHEET_SMALL = "bar";

    /**
     * @deprecated Use {@link #TABSHEET_SMALL} instead.
     */
    @Deprecated
    public static final String TABSHEET_BAR = TABSHEET_SMALL;

    /**
     * Removes borders and background color from the tab sheet. The tabs are
     * presented with minimal lines indicating the selected tab.
     */
    public static final String TABSHEET_MINIMAL = "minimal";

    /**
     * Makes the tab close buttons visible only when the user is hovering over
     * the tab.
     */
    public static final String TABSHEET_HOVER_CLOSABLE = "hover-closable";

    /**
     * Makes the tab close buttons visible only when the tab is selected.
     */
    public static final String TABSHEET_SELECTED_CLOSABLE = "selected-closable";

    /***************************************************************************
     * 
     * Table styles
     * 
     **************************************************************************/

    /**
     * Removes borders from the table
     */
    public static final String TABLE_BORDERLESS = "borderless";

    /**
     * Makes the table headers dark and more prominent.
     */
    public static final String TABLE_STRONG = "strong";

    /***************************************************************************
     * 
     * Layout styles
     * 
     **************************************************************************/

    /**
     * Changes the background of a layout to white. Applies to
     * {@link VerticalLayout}, {@link HorizontalLayout}, {@link GridLayout},
     * {@link FormLayout}, {@link CssLayout}, {@link VerticalSplitPanel} and
     * {@link HorizontalSplitPanel}.
     * <p>
     * <em>Does not revert any contained components back to normal if some 
     * parent layout has style {@link #LAYOUT_BLACK} applied.</em>
     */
    public static final String LAYOUT_WHITE = "white";

    /**
     * Changes the background of a layout to a shade of blue. Applies to
     * {@link VerticalLayout}, {@link HorizontalLayout}, {@link GridLayout},
     * {@link FormLayout}, {@link CssLayout}, {@link VerticalSplitPanel} and
     * {@link HorizontalSplitPanel}.
     * <p>
     * <em>Does not revert any contained components back to normal if some 
     * parent layout has style {@link #LAYOUT_BLACK} applied.</em>
     */
    public static final String LAYOUT_BLUE = "blue";

    /**
     * <p>
     * Changes the background of a layout to almost black, and at the same time
     * transforms contained components to their black style correspondents when
     * available. At least texts, buttons, text fields, selects, date fields,
     * tables and a few other component styles should change.
     * </p>
     * <p>
     * Applies to {@link VerticalLayout}, {@link HorizontalLayout},
     * {@link GridLayout}, {@link FormLayout} and {@link CssLayout}.
     * </p>
     * 
     */
    public static final String LAYOUT_BLACK = "black";

    /***************************************************************************
     * 
     * Window styles
     * 
     **************************************************************************/

    /**
     * Makes the whole window white and increases the font size of the title.
     */
    public static final String WINDOW_LIGHT = "light";

    /**
     * Makes the whole window black, and changes contained components in the
     * same way as {@link #LAYOUT_BLACK} does.
     */
    public static final String WINDOW_BLACK = "black";
}
