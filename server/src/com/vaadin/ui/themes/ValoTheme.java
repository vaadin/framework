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
package com.vaadin.ui.themes;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.ColumnHeaderMode;

/**
 * <p>
 * Additional style names which can be used with the Valo theme.
 * </p>
 * 
 * <p>
 * These styles are only available if the
 * <code>$v-included-additional-styles</code> Sass list variable contains the
 * name of the component for that additional style name (e.g.
 * <code>button, textfield, table</code>).
 * </p>
 * 
 * <p>
 * Most of these additional style names can be included individually into your
 * custom theme using the component specific Sass mixins, in which case you can
 * also define the style names yourself. See the Valo theme Sass API
 * documentation for additional information.
 * </p>
 * 
 * TODO link to Sass API documentation
 * 
 * @since 7.3
 * @author Vaadin Ltd
 */
public class ValoTheme {

    public static final String THEME_NAME = "valo";

    /***************************************************************************
     * 
     * Notification styles
     * 
     **************************************************************************/

    /**
     * Styles the notification to look like {@link Type#TRAY_NOTIFICATION},
     * without setting the position and delay. Can be combined with any other
     * Notification style.
     */
    public static final String NOTIFICATION_TRAY = "tray";

    /**
     * Styles the notification to look like {@link Type#WARNING_MESSAGE},
     * without setting the position and delay. Can be combined with any other
     * Notification style.
     */
    public static final String NOTIFICATION_WARNING = "warning";

    /**
     * Styles the notification to look like {@link Type#ERROR_MESSAGE}, without
     * setting the position and delay. Can be combined with any other
     * Notification style.
     */
    public static final String NOTIFICATION_ERROR = "error";

    /**
     * Styles the notification to look like a system notification. Can be
     * combined with any other Notification style.
     */
    public static final String NOTIFICATION_SYSTEM = "system";

    /**
     * Styles the notification to span the entire width of the viewport. Can be
     * combined with any other Notification style.
     */
    public static final String NOTIFICATION_BAR = "bar";

    /**
     * Smaller padding and font size for the notification. Can be combined with
     * any other Notification style.
     */
    public static final String NOTIFICATION_SMALL = "small";

    /**
     * Adds a close button to the notification to imply that the user must click
     * on the notification to dismiss it. Use in combination with an infinite
     * delay (<code>-1</code>). Can be combined with any other Notification
     * style.
     */
    public static final String NOTIFICATION_CLOSABLE = "closable";

    /**
     * Success notification style. Adds a border around the notification and an
     * icon next to the title. Can be combined with any other Label style.
     */
    public static final String NOTIFICATION_SUCCESS = "success";

    /**
     * Failure notification style. Adds a border around the notification and an
     * icon next to the title. Can be combined with any other Label style.
     */
    public static final String NOTIFICATION_FAILURE = "failure";

    /***************************************************************************
     * 
     * Label styles
     * 
     **************************************************************************/

    /**
     * Header style for main application headings. Can be combined with any
     * other Label style.
     */
    public static final String LABEL_H1 = "h1";

    /**
     * Header style for different sections in the application. Can be combined
     * with any other Label style.
     */
    public static final String LABEL_H2 = "h2";

    /**
     * Header style for different sub-sections in the application. Can be
     * combined with any other Label style.
     */
    public static final String LABEL_H3 = "h3";

    /**
     * Header style for different sub-sections in the application. Can be
     * combined with any other Label style.
     */
    public static final String LABEL_H4 = "h4";

    /**
     * A utility style that can be combined with the {@link #LABEL_H1},
     * {@link #LABEL_H2}, {@link #LABEL_H3} and {@link #LABEL_H4} styles to
     * remove the default margins from the header.
     */
    public static final String LABEL_NO_MARGIN = "no-margin";

    /**
     * Tiny font size. Suitable for additional/supplementary UI text. Can be
     * combined with any other Label style.
     */
    public static final String LABEL_TINY = "tiny";

    /**
     * Small font size. Suitable for additional/supplementary UI text. Can be
     * combined with any other Label style.
     */
    public static final String LABEL_SMALL = "small";

    /**
     * Large font size. Suitable for important/prominent UI text. Can be
     * combined with any other Label style.
     */
    public static final String LABEL_LARGE = "large";

    /**
     * Huge font size. Suitable for important/prominent UI text. Can be combined
     * with any other Label style.
     */
    public static final String LABEL_HUGE = "huge";

    /**
     * Lighter font weight. Suitable for additional/supplementary UI text. Can
     * be combined with any other Label style.
     */
    public static final String LABEL_LIGHT = "light";

    /**
     * Bolder font weight. Suitable for important/prominent UI text. Can be
     * combined with any other Label style.
     */
    public static final String LABEL_BOLD = "bold";

    /**
     * Colored text. Can be combined with any other Label style.
     */
    public static final String LABEL_COLORED = "colored";

    /**
     * Success badge style. Adds a border around the label and an icon next to
     * the text. Suitable for UI notifications that need to in the direct
     * context of some component. Can be combined with any other Label style.
     */
    public static final String LABEL_SUCCESS = "success";

    /**
     * Failure badge style. Adds a border around the label and an icon next to
     * the text. Suitable for UI notifications that need to in the direct
     * context of some component. Can be combined with any other Label style.
     */
    public static final String LABEL_FAILURE = "failure";

    /**
     * Spinner style. Add this style name to an empty Label to create a spinner.
     * 
     * <h4>Example</h4>
     * 
     * <pre>
     * Label spinner = new Label();
     * spinner.addStyleName(ValoTheme.LABEL_SPINNER);
     * </pre>
     */
    public static final String LABEL_SPINNER = "spinner";

    /***************************************************************************
     * 
     * Button styles
     * 
     **************************************************************************/

    /**
     * Primary action button (e.g. the button that should get activated when the
     * user presses the <code>enter</code> key in a form). Use sparingly, only
     * one default button per view should be visible. Can be combined with any
     * other Button style.
     */
    public static final String BUTTON_PRIMARY = "primary";

    /**
     * A prominent button that can be used instead of the
     * {@link #BUTTON_PRIMARY} for primary actions when the action is considered
     * <b>safe</b> for the user (i.e. does not cause any data loss or any other
     * irreversible action). Can be combined with any other Button style.
     */
    public static final String BUTTON_FRIENDLY = "friendly";

    /**
     * A prominent button that can be used when the action is considered
     * <b>unsafe</b> for the user (i.e. it causes data loss or some other
     * irreversible action). Can be combined with any other Button style.
     */
    public static final String BUTTON_DANGER = "danger";

    /**
     * Borderless button. Can be combined with any other Button style.
     */
    public static final String BUTTON_BORDERLESS = "borderless";

    /**
     * Borderless button with a colored caption text. Can be combined with any
     * other Button style.
     */
    public static final String BUTTON_BORDERLESS_COLORED = "borderless-colored";

    /**
     * "Quiet" button, which looks like {@link #BUTTON_BORDERLESS} until you
     * hover over it with the mouse. Can be combined with any other Button
     * style.
     */
    public static final String BUTTON_QUIET = "quiet";

    /**
     * Makes the button look like the Link component. Can be combined with any
     * other Button style.
     */
    public static final String BUTTON_LINK = "link";

    /**
     * Tiny size button. Can be combined with any other Button style.
     */
    public static final String BUTTON_TINY = "tiny";

    /**
     * Small size button. Can be combined with any other Button style.
     */
    public static final String BUTTON_SMALL = "small";

    /**
     * Large size button. Can be combined with any other Button style.
     */
    public static final String BUTTON_LARGE = "large";

    /**
     * Huge size button. Can be combined with any other Button style.
     */
    public static final String BUTTON_HUGE = "huge";

    /**
     * Align the icon to the right side of the button caption. Can be combined
     * with any other Button style.
     */
    public static final String BUTTON_ICON_ALIGN_RIGHT = "icon-align-right";

    /**
     * Stack the icon on top of the button caption. Can be combined with any
     * other Button style.
     */
    public static final String BUTTON_ICON_ALIGN_TOP = "icon-align-top";

    /**
     * Only show the icon in the button, and size the button to a square shape.
     */
    public static final String BUTTON_ICON_ONLY = "icon-only";

    /***************************************************************************
     * 
     * Link styles
     * 
     **************************************************************************/

    /**
     * Small size link.
     */
    public static final String LINK_SMALL = "small";

    /**
     * Large size link.
     */
    public static final String LINK_LARGE = "large";

    /***************************************************************************
     * 
     * TextField styles
     * 
     **************************************************************************/

    /**
     * Tiny size text field. Can be combined with any other TextField style.
     */
    public static final String TEXTFIELD_TINY = "tiny";

    /**
     * Small size text field. Can be combined with any other TextField style.
     */
    public static final String TEXTFIELD_SMALL = "small";

    /**
     * Large size text field. Can be combined with any other TextField style.
     */
    public static final String TEXTFIELD_LARGE = "large";

    /**
     * Huge size text field. Can be combined with any other TextField style.
     */
    public static final String TEXTFIELD_HUGE = "huge";

    /**
     * Removes the border and background from the text field. Can be combined
     * with any other TextField style.
     */
    public static final String TEXTFIELD_BORDERLESS = "borderless";

    /**
     * Align the text inside the field to the right. Can be combined with any
     * other TextField style.
     */
    public static final String TEXTFIELD_ALIGN_RIGHT = "align-right";

    /**
     * Align the text inside the field to center. Can be combined with any other
     * TextField style.
     */
    public static final String TEXTFIELD_ALIGN_CENTER = "align-center";

    /**
     * Move the default caption icon inside the text field. Can be combined with
     * any other TextField style.
     */
    public static final String TEXTFIELD_INLINE_ICON = "inline-icon";

    /***************************************************************************
     * 
     * TextArea styles
     * 
     **************************************************************************/

    /**
     * Tiny size text area. Can be combined with any other TextArea style.
     */
    public static final String TEXTAREA_TINY = "tiny";

    /**
     * Small size text area. Can be combined with any other TextArea style.
     */
    public static final String TEXTAREA_SMALL = "small";

    /**
     * Large size text area. Can be combined with any other TextArea style.
     */
    public static final String TEXTAREA_LARGE = "large";

    /**
     * Huge size text area. Can be combined with any other TextArea style.
     */
    public static final String TEXTAREA_HUGE = "huge";

    /**
     * Removes the border and background from the text area. Can be combined
     * with any other TextArea style.
     */
    public static final String TEXTAREA_BORDERLESS = "borderless";

    /**
     * Align the text inside the area to the right. Can be combined with any
     * other TextArea style.
     */
    public static final String TEXTAREA_ALIGN_RIGHT = "align-right";

    /**
     * Align the text inside the area to center. Can be combined with any other
     * TextArea style.
     */
    public static final String TEXTAREA_ALIGN_CENTER = "align-center";

    /***************************************************************************
     * 
     * DateField styles
     * 
     **************************************************************************/

    /**
     * Tiny size date field. Can be combined with any other DateField style.
     */
    public static final String DATEFIELD_TINY = "tiny";

    /**
     * Small size date field. Can be combined with any other DateField style.
     */
    public static final String DATEFIELD_SMALL = "small";

    /**
     * Large size date field. Can be combined with any other DateField style.
     */
    public static final String DATEFIELD_LARGE = "large";

    /**
     * Huge size date field. Can be combined with any other DateField style.
     */
    public static final String DATEFIELD_HUGE = "huge";

    /**
     * Removes the border and background from the date field. Can be combined
     * with any other DateField style.
     */
    public static final String DATEFIELD_BORDERLESS = "borderless";

    /**
     * Align the text inside the field to the right. Can be combined with any
     * other DateField style.
     */
    public static final String DATEFIELD_ALIGN_RIGHT = "align-right";

    /**
     * Align the text inside the field to center. Can be combined with any other
     * DateField style.
     */
    public static final String DATEFIELD_ALIGN_CENTER = "align-center";

    /***************************************************************************
     * 
     * ComboBox styles
     * 
     **************************************************************************/

    /**
     * Tiny size combo box. Can be combined with any other ComboBox style.
     */
    public static final String COMBOBOX_TINY = "tiny";

    /**
     * Small size combo box. Can be combined with any other ComboBox style.
     */
    public static final String COMBOBOX_SMALL = "small";

    /**
     * Large size combo box. Can be combined with any other ComboBox style.
     */
    public static final String COMBOBOX_LARGE = "large";

    /**
     * Huge size combo box. Can be combined with any other ComboBox style.
     */
    public static final String COMBOBOX_HUGE = "huge";

    /**
     * Removes the border and background from the combo box. Can be combined
     * with any other ComboBox style.
     */
    public static final String COMBOBOX_BORDERLESS = "borderless";

    /**
     * Align the text inside the combo box to the right. Can be combined with
     * any other TextField style.
     */
    public static final String COMBOBOX_ALIGN_RIGHT = "align-right";

    /**
     * Align the text inside the combo box to center. Can be combined with any
     * other TextField style.
     */
    public static final String COMBOBOX_ALIGN_CENTER = "align-center";

    /***************************************************************************
     * 
     * CheckBox styles
     * 
     **************************************************************************/

    /**
     * Small size check box. Can be combined with any other CheckBox style.
     */
    public static final String CHECKBOX_SMALL = "small";

    /**
     * Large size check box. Can be combined with any other CheckBox style.
     */
    public static final String CHECKBOX_LARGE = "large";

    /***************************************************************************
     * 
     * OptionGroup styles
     * 
     **************************************************************************/

    /**
     * Small size option group. Can be combined with any other OptionGroup
     * style.
     */
    public static final String OPTIONGROUP_SMALL = "small";

    /**
     * Large size option group. Can be combined with any other OptionGroup
     * style.
     */
    public static final String OPTIONGROUP_LARGE = "large";

    /**
     * Display the options horizontally in a row (by default the items are
     * stacked vertically).
     */
    public static final String OPTIONGROUP_HORIZONTAL = "horizontal";

    /***************************************************************************
     * 
     * Slider styles
     * 
     **************************************************************************/

    /**
     * Hide the indicator bar from the slider. Can be combined with any other
     * Slider style.
     */
    public static final String SLIDER_NO_INDICATOR = "no-indicator";

    /***************************************************************************
     * 
     * ProgressBar styles
     * 
     **************************************************************************/

    /**
     * Make the progress bar indicator appear as a dot which progresses over the
     * progress bar track (instead of a growing bar).
     */
    public static final String PROBRESSBAR_POINT = "point";

    /***************************************************************************
     * 
     * MenuBar styles
     * 
     **************************************************************************/

    /**
     * Small size menu bar. Can be combined with any other MenuBar style.
     */
    public static final String MENUBAR_SMALL = "small";

    /**
     * Borderless menu bar. Can be combined with any other MenuBar style.
     */
    public static final String MENUBAR_BORDERLESS = "borderless";

    /***************************************************************************
     * 
     * Table and TreeTable styles
     * 
     **************************************************************************/

    /**
     * Remove the alternating row colors. Can be combined with any other
     * Table/TreeTable style.
     */
    public static final String TABLE_NO_STRIPES = "no-stripes";

    /**
     * See {@link #TABLE_NO_STRIPES}
     */
    public static final String TREETABLE_NO_STRIPES = TABLE_NO_STRIPES;

    /**
     * Remove the vertical divider lines between the table columns. Can be
     * combined with any other Table/TreeTable style.
     */
    public static final String TABLE_NO_VERTICAL_LINES = "no-vertical-lines";

    /**
     * See {@link #TABLE_NO_VERTICAL_LINES}
     */
    public static final String TREETABLE_NO_VERTICAL_LINES = TABLE_NO_VERTICAL_LINES;

    /**
     * Remove the horizontal divider lines between the table rows. Can be
     * combined with any other Table/TreeTable style.
     */
    public static final String TABLE_NO_HORIZONTAL_LINES = "no-horizontal-lines";

    /**
     * See {@link #TABLE_NO_HORIZONTAL_LINES}
     */
    public static final String TREETABLE_NO_HORIZONTAL_LINES = TABLE_NO_HORIZONTAL_LINES;

    /**
     * Hide the table column headers (effectively the same as
     * {@link ColumnHeaderMode#HIDDEN}). Can be combined with any other
     * Table/TreeTable style.
     */
    public static final String TABLE_NO_HEADER = "no-header";

    /**
     * See {@link #TABLE_NO_HEADER}
     */
    public static final String TREETABLE_NO_HEADER = TABLE_NO_HEADER;

    /**
     * Remove the outer border of the table. Can be combined with any other
     * Table/TreeTable style.
     */
    public static final String TABLE_BORDERLESS = "borderless";

    /**
     * See {@link #TABLE_BORDERLESS}
     */
    public static final String TREETABLE_BORDERLESS = TABLE_BORDERLESS;

    /**
     * Reduce the white space inside the table cells. Can be combined with any
     * other Table/TreeTable style.
     */
    public static final String TABLE_COMPACT = "compact";

    /**
     * See {@link #TABLE_COMPACT}
     */
    public static final String TREETABLE_COMPACT = TABLE_COMPACT;

    /**
     * Small font size and reduced the white space inside the table cells. Can
     * be combined with any other Table/TreeTable style.
     */
    public static final String TABLE_SMALL = "small";

    /**
     * See {@link #TABLE_SMALL}
     */
    public static final String TREETABLE_SMALL = TABLE_SMALL;

    /***************************************************************************
     * 
     * DragAndDropWrapper styles
     * 
     **************************************************************************/

    /**
     * Hide the "box drag hints" (i.e. the style which gets applied when the
     * drag is in the middle/center area of the drag target).
     */
    public static final String DRAG_AND_DROP_WRAPPER_NO_BOX_DRAG_HINTS = "no-box-drag-hints";

    /**
     * Hide the "vertical drag hints" (i.e. the style which gets applied when
     * the drag is in the top/bottom part of the drag target).
     */
    public static final String DRAG_AND_DROP_WRAPPER_NO_VERTICAL_DRAG_HINTS = "no-vertical-drag-hints";

    /**
     * Hide the "horizontal drag hints" (i.e. the style which gets applied when
     * the drag is in the left/right part of the drag target).
     */
    public static final String DRAG_AND_DROP_WRAPPER_NO_HORIZONTAL_DRAG_HINTS = "no-horizontal-drag-hints";

    /***************************************************************************
     * 
     * Panel styles
     * 
     **************************************************************************/

    /**
     * Remove borders and the background color of the panel. Can be combined
     * with any other Panel style.
     */
    public static final String PANEL_BORDERLESS = "borderless";

    /**
     * Show a divider between the panel caption and content when the content
     * area is scrolled. Suitable with the {@link #PANEL_BORDERLESS} style. Can
     * be combined with any other Panel style.
     */
    public static final String PANEL_SCROLL_INDICATOR = "scroll-indicator";

    /**
     * Inset panel style. Can be combined with any other Panel style.
     */
    public static final String PANEL_WELL = "well";

    /***************************************************************************
     * 
     * SplitPanel styles
     * 
     **************************************************************************/

    /**
     * Make the split handle wider.
     */
    public static final String SPLITPANEL_LARGE = "large";

    /***************************************************************************
     * 
     * TabSheet styles
     * 
     **************************************************************************/

    /**
     * Adds a border around the whole component as well as around individual
     * tabs in the tab bar. Can be combined with any other TabSheet style.
     */
    public static final String TABSHEET_FRAMED = "framed";

    /**
     * Center the tabs inside the tab bar. Works best if all the tabs fit
     * completely in the tab bar (i.e. no tab bar scrolling). Can be combined
     * with any other TabSheet style.
     */
    public static final String TABSHEET_CENTERED_TABS = "centered-tabs";

    /**
     * Give equal amount of space to all tabs in the tab bar (.i.e expand ratio
     * == 1 for all tabs). The tab captions will be truncated if they do not fit
     * in to the tab. Tab scrolling will be disabled when this style is applied
     * (all tabs will be visible at the same time). Can be combined with any
     * other TabSheet style.
     */
    public static final String TABSHEET_EQUAL_WIDTH_TABS = "equal-width-tabs";

    /**
     * Add a small amount of padding around the tabs in the tab bar, so that
     * they don't touch the outer edges of the component. Can be combined with
     * any other TabSheet style.
     */
    public static final String TABSHEET_PADDED_TABBAR = "padded-tabbar";

    /**
     * Reduce the whitespace around the tabs in the tab bar. Can be combined
     * with any other TabSheet style.
     */
    public static final String TABSHEET_COMPACT_TABBAR = "compact-tabbar";

    /**
     * Display tab icons on top of the tab captions (by default the icons are
     * place on the left side of the caption). Can be combined with any other
     * TabSheet style.
     */
    public static final String TABSHEET_ICONS_ON_TOP = "icons-on-top";

    /**
     * Only the selected tab has the close button visible. Does not prevent
     * closing the tab programmatically, it only hides the button from the end
     * user. Can be combined with any other TabSheet style.
     */
    public static final String TABSHEET_ONLY_SELECTED_TAB_IS_CLOSABLE = "only-selected-closable";

    /***************************************************************************
     * 
     * Accordion styles
     * 
     **************************************************************************/

    /**
     * Remove the outer border from the accordion. Can be combined with any
     * other Accordion style.
     */
    public static final String ACCORDION_BORDERLESS = "borderless";

    /***************************************************************************
     * 
     * Window and related styles
     * 
     **************************************************************************/

    /**
     * Add this style to any layout component (e.g. CssLayout, VerticalLayout or
     * HorizontalLayout) and place it inside the root layout of the window to
     * create a toolbar area for the window. You can then place any other
     * components inside the toolbar layout, e.g. a MenuBar.
     */
    public static final String WINDOW_TOP_TOOLBAR = "v-window-top-toolbar";

    /**
     * Add this style to any layout component (e.g. CssLayout, VerticalLayout or
     * HorizontalLayout) and place it inside the root layout of the window to
     * create a toolbar area for the window. You can then place any other
     * components inside the toolbar layout, e.g. a MenuBar.
     */
    public static final String WINDOW_BOTTOM_TOOLBAR = "v-window-bottom-toolbar";

    /***************************************************************************
     * 
     * FormLayout styles
     * 
     **************************************************************************/

    /**
     * Removes the borders and background from any direct child field components
     * (TextField, TextArea, DateField, ComboBox) in the layout. Reduces the
     * spacing between the form rows and adds separator lines between them.
     */
    public static final String FORMLAYOUT_LIGHT = "light";

    /***************************************************************************
     * 
     * Layout styles
     * 
     **************************************************************************/

    /**
     * Make a layout look like the Panel component (resembles visually a card).
     * Add an additional <code>v-panel-caption</code> style name to any layout
     * inside the card layout to make it look like a Panel's caption.
     */
    public static final String LAYOUT_CARD = "card";

    /**
     * Make a layout look like the {@link #PANEL_WELL} style. Add an additional
     * <code>v-panel-caption</code> style name to any layout inside the card
     * layout to make it look like a Panel's caption.
     */
    public static final String LAYOUT_WELL = "well";

    /**
     * Make a HorizontalLayout wrap contained components to a new line when the
     * isn't enough space.
     */
    public static final String LAYOUT_HORIZONTAL_WRAPPING = "wrapping";

    /**
     * Add this style name to a CssLayout to create a grouped set of components,
     * i.e. a row of components which are joined seamlessly together.
     * 
     * <h4>Example</h4>
     * 
     * <pre>
     * CssLayout group = new CssLayout();
     * group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
     * 
     * TextField field = new TextField();
     * group.addComponent(field);
     * 
     * Button button = new Button(&quot;Action&quot;);
     * group.addComponent(button);
     * </pre>
     */
    public static final String LAYOUT_COMPONENT_GROUP = "v-component-group";

    /***************************************************************************
     * 
     * Valo menu styles
     * 
     **************************************************************************/

    /**
     * <p>
     * Set the <em><b>primary</b></em> style name of a CssLayout to this, and
     * add any number of layouts with the {@link #MENU_PART} style inside it.
     * </p>
     * 
     * <p>
     * The menu style is used to create a sidebar navigation menu for the
     * application, usually action as the main navigation for the different
     * sections of the application. It usually consists of at least a number of
     * {@link #MENU_ITEM}s, and possibly some {@link #MENU_SUBTITLE}s and a
     * {@link #MENU_TITLE}.
     * </p>
     * 
     * <h4>Example</h4>
     * 
     * <pre>
     * CssLayout menuArea = new CssLayout();
     * menuArea.setPrimaryStyleName(ValoTheme.MENU_ROOT);
     * </pre>
     */
    public static final String MENU_ROOT = "valo-menu";

    /**
     * Add this style name to any layout and place it inside a layout with the
     * {@link #MENU_ROOT} style to build a menu component. Use the additional
     * MENU styles for individual components inside the layout.
     * 
     * <h4>Example</h4>
     * 
     * <pre>
     * CssLayout menu = new CssLayout();
     * menu.addStyleName(ValoTheme.MENU_PART);
     * </pre>
     */
    public static final String MENU_PART = "valo-menu-part";

    /**
     * Add this style name to any layout with the {@link #MENU_PART} style name
     * to make any menu items inside the menu emphasize the icons more than the
     * captions. Useful on narrower viewport widths, since the menu width is
     * decreased quite dramatically, making more space for the content of the
     * application.
     * 
     * <h4>Example</h4>
     * 
     * <pre>
     * CssLayout menu = new CssLayout();
     * menu.addStyleName(ValoTheme.MENU_PART);
     * menu.addStyleName(ValoTheme.MENU_PART_LARGE_ICONS);
     * </pre>
     */
    public static final String MENU_PART_LARGE_ICONS = "large-icons";

    /**
     * <p>
     * Add this style name to any layout to make a header area for a menu
     * (intended to be placed in side a {@link #MENU_PART} layout). You can add
     * any components inside it, but usually you would place a Label inside.
     * </p>
     * 
     * <p>
     * Any MenuBar component that you place inside this layout will match the
     * style of the title, allowing an easy way to add a toolbar to the title
     * layout.
     * </p>
     */
    public static final String MENU_TITLE = "valo-menu-title";

    /**
     * Set the <em><b>primary</b></em> style name of a Label or a Button to this
     * style name to create a section divider in a menu.
     */
    public static final String MENU_SUBTITLE = "valo-menu-subtitle";

    /**
     * <p>
     * Set the <em><b>primary</b></em> style name of a Button to this style name
     * to create a clickable menu item in the menu.
     * </p>
     * 
     * <h4>Selected item</h4>
     * <p>
     * Add an additional style name <b><code>selected</code></b> to it to make
     * it the selected item in the menu.
     * </p>
     * 
     * <h4>Example</h4>
     * 
     * <pre>
     * Button item = new Button();
     * item.setPrimaryStyleName(ValoTheme.MENU_ITEM);
     * item.addStyleName(&quot;selected&quot;);
     * </pre>
     */
    public static final String MENU_ITEM = "valo-menu-item";

    /**
     * Add a SPAN element with this style name inside a {@link #MENU_SUBTITLE}
     * or {@link #MENU_ITEM} to add an additional badge indicator to the
     * subtitle/item. The Label/Button needs to allow HTML content in order to
     * use this style name.
     * 
     * <h4>Examples</h4>
     * 
     * <pre>
     * Button item = new Button();
     * item.setPrimaryStyleName(ValoTheme.MENU_ITEM);
     * item.setHtmlContentAllowed(true);
     * item.setCaption(&quot;Item Caption &lt;span class=\&quot;&quot; + ValoTheme.MENU_BADGE
     *         + &quot;\&quot;&gt;Badge text&lt;/span&gt;&quot;);
     * </pre>
     * 
     * <pre>
     * Label item = new Label();
     * item.setPrimaryStyleName(ValoTheme.MENU_ITEM);
     * item.setContentMode(ContentMode.HTML);
     * item.setCaption(&quot;Item Caption &lt;span class=\&quot;&quot; + ValoTheme.MENU_BADGE
     *         + &quot;\&quot;&gt;Badge text&lt;/span&gt;&quot;);
     * </pre>
     */
    public static final String MENU_BADGE = "valo-menu-badge";

    /**
     * <p>
     * Set the <em><b>primary</b></em> style name of a Label or a Button to this
     * style name to create an application logo. The logo is designed to be
     * placed inside a {@link #MENU_PART} layout.
     * </p>
     * 
     * <p>
     * The text content of the logo should be very short, since the logo area
     * only shows approximately three letters. Using one of the
     * {@link FontAwesome} icons is a good way to quickly create a logo for your
     * application.
     * </p>
     * </p>
     * 
     * <h4>Example</h4>
     * 
     * <pre>
     * Label logo = new Label(FontAwesome.ROCKET.getHtml(), ContentMode.HTML);
     * logo.setSizeUndefined();
     * logo.setPrimaryStyleName(ValoTheme.MENU_LOGO);
     * </pre>
     */
    public static final String MENU_LOGO = "valo-menu-logo";

}
