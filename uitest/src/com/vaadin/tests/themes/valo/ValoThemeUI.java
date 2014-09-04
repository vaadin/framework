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
package com.vaadin.tests.themes.valo;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Theme("tests-valo")
@Title("Valo Theme Test")
@PreserveOnRefresh
public class ValoThemeUI extends UI {

    private boolean testMode = false;

    private static LinkedHashMap<String, String> themeVariants = new LinkedHashMap<String, String>();
    static {
        themeVariants.put("tests-valo", "Default");
        themeVariants.put("tests-valo-blueprint", "Blueprint");
        themeVariants.put("tests-valo-dark", "Dark");
        themeVariants.put("tests-valo-facebook", "Facebook");
        themeVariants.put("tests-valo-flatdark", "Flat dark");
        themeVariants.put("tests-valo-flat", "Flat");
        themeVariants.put("tests-valo-light", "Light");
        themeVariants.put("tests-valo-metro", "Metro");
    }
    private TestIcon testIcon = new TestIcon(100);

    ValoMenuLayout root = new ValoMenuLayout();
    ComponentContainer viewDisplay = root.getContentContainer();
    CssLayout menu = new CssLayout();
    CssLayout menuItemsLayout = new CssLayout();
    {
        menu.setId("testMenu");
    }
    private Navigator navigator;
    private LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();

    @Override
    protected void init(VaadinRequest request) {
        if (request.getParameter("test") != null) {
            testMode = true;

            if (browserCantRenderFontsConsistently()) {
                getPage().getStyles().add(
                        ".v-app.v-app.v-app {font-family: Sans-Serif;}");
            }
        }

        if (getPage().getWebBrowser().isIE()
                && getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }

        if (!testMode) {
            Responsive.makeResponsive(this);
        }

        getPage().setTitle("Valo Theme Test");
        setContent(root);
        root.setWidth("100%");

        root.addMenu(buildMenu());

        navigator = new Navigator(this, viewDisplay);

        navigator.addView("common", CommonParts.class);
        navigator.addView("labels", Labels.class);
        navigator.addView("buttons-and-links", ButtonsAndLinks.class);
        navigator.addView("textfields", TextFields.class);
        navigator.addView("datefields", DateFields.class);
        navigator.addView("comboboxes", ComboBoxes.class);
        navigator.addView("checkboxes", CheckBoxes.class);
        navigator.addView("sliders", Sliders.class);
        navigator.addView("menubars", MenuBars.class);
        navigator.addView("panels", Panels.class);
        navigator.addView("trees", Trees.class);
        navigator.addView("tables", Tables.class);
        navigator.addView("splitpanels", SplitPanels.class);
        navigator.addView("tabs", Tabsheets.class);
        navigator.addView("accordions", Accordions.class);
        navigator.addView("colorpickers", ColorPickers.class);
        navigator.addView("selects", NativeSelects.class);
        navigator.addView("calendar", CalendarTest.class);
        navigator.addView("forms", Forms.class);
        navigator.addView("popupviews", PopupViews.class);
        navigator.addView("dragging", Dragging.class);

        String f = Page.getCurrent().getUriFragment();
        if (f == null || f.equals("")) {
            navigator.navigateTo("common");
        }

        navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                for (Iterator<Component> it = menuItemsLayout.iterator(); it
                        .hasNext();) {
                    it.next().removeStyleName("selected");
                }
                for (Entry<String, String> item : menuItems.entrySet()) {
                    if (event.getViewName().equals(item.getKey())) {
                        for (Iterator<Component> it = menuItemsLayout
                                .iterator(); it.hasNext();) {
                            Component c = it.next();
                            if (c.getCaption() != null
                                    && c.getCaption().startsWith(
                                            item.getValue())) {
                                c.addStyleName("selected");
                                break;
                            }
                        }
                        break;
                    }
                }
                menu.removeStyleName("valo-menu-visible");
            }
        });

    }

    private boolean browserCantRenderFontsConsistently() {
        // PhantomJS renders font correctly about 50% of the time, so
        // disable it to have consistent screenshots
        // https://github.com/ariya/phantomjs/issues/10592

        // IE8 also has randomness in its font rendering...

        return getPage().getWebBrowser().getBrowserApplication()
                .contains("PhantomJS")
                || (getPage().getWebBrowser().isIE() && getPage()
                        .getWebBrowser().getBrowserMajorVersion() <= 9);
    }

    static boolean isTestMode() {
        return ((ValoThemeUI) getCurrent()).testMode;
    }

    Component buildTestMenu() {
        CssLayout menu = new CssLayout();
        menu.addStyleName("large-icons");

        Label logo = new Label("Va");
        logo.setSizeUndefined();
        logo.setPrimaryStyleName("valo-menu-logo");
        menu.addComponent(logo);

        Button b = new Button(
                "Reference <span class=\"valo-menu-badge\">3</span>");
        b.setIcon(FontAwesome.TH_LIST);
        b.setPrimaryStyleName("valo-menu-item");
        b.addStyleName("selected");
        b.setHtmlContentAllowed(true);
        menu.addComponent(b);

        b = new Button("API");
        b.setIcon(FontAwesome.BOOK);
        b.setPrimaryStyleName("valo-menu-item");
        menu.addComponent(b);

        b = new Button("Examples <span class=\"valo-menu-badge\">12</span>");
        b.setIcon(FontAwesome.TABLE);
        b.setPrimaryStyleName("valo-menu-item");
        b.setHtmlContentAllowed(true);
        menu.addComponent(b);

        return menu;
    }

    CssLayout buildMenu() {
        // Add items
        menuItems.put("common", "Common UI Elements");
        menuItems.put("labels", "Labels");
        menuItems.put("buttons-and-links", "Buttons & Links");
        menuItems.put("textfields", "Text Fields");
        menuItems.put("datefields", "Date Fields");
        menuItems.put("comboboxes", "Combo Boxes");
        menuItems.put("selects", "Selects");
        menuItems.put("checkboxes", "Check Boxes & Option Groups");
        menuItems.put("sliders", "Sliders & Progress Bars");
        menuItems.put("colorpickers", "Color Pickers");
        menuItems.put("menubars", "Menu Bars");
        menuItems.put("trees", "Trees");
        menuItems.put("tables", "Tables");
        menuItems.put("dragging", "Drag and Drop");
        menuItems.put("panels", "Panels");
        menuItems.put("splitpanels", "Split Panels");
        menuItems.put("tabs", "Tabs");
        menuItems.put("accordions", "Accordions");
        menuItems.put("popupviews", "Popup Views");
        menuItems.put("calendar", "Calendar");
        menuItems.put("forms", "Forms");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName("valo-menu-title");
        menu.addComponent(top);
        menu.addComponent(createThemeSelect());

        Button showMenu = new Button("Menu", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (menu.getStyleName().contains("valo-menu-visible")) {
                    menu.removeStyleName("valo-menu-visible");
                } else {
                    menu.addStyleName("valo-menu-visible");
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName("valo-menu-toggle");
        showMenu.setIcon(FontAwesome.LIST);
        menu.addComponent(showMenu);

        Label title = new Label("<h3>Vaadin <strong>Valo Theme</strong></h3>",
                ContentMode.HTML);
        title.setSizeUndefined();
        top.addComponent(title);
        top.setExpandRatio(title, 1);

        MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        StringGenerator sg = new StringGenerator();
        MenuItem settingsItem = settings.addItem(
                sg.nextString(true) + " " + sg.nextString(true)
                        + sg.nextString(false), new ThemeResource(
                        "../tests-valo/img/profile-pic-300px.jpg"), null);
        settingsItem.addItem("Edit Profile", null);
        settingsItem.addItem("Preferences", null);
        settingsItem.addSeparator();
        settingsItem.addItem("Sign Out", null);
        menu.addComponent(settings);

        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);

        Label label = null;
        int count = -1;
        for (final Entry<String, String> item : menuItems.entrySet()) {
            if (item.getKey().equals("labels")) {
                label = new Label("Components", ContentMode.HTML);
                label.setPrimaryStyleName("valo-menu-subtitle");
                label.addStyleName("h4");
                label.setSizeUndefined();
                menuItemsLayout.addComponent(label);
            }
            if (item.getKey().equals("panels")) {
                label.setValue(label.getValue()
                        + " <span class=\"valo-menu-badge\">" + count
                        + "</span>");
                count = 0;
                label = new Label("Containers", ContentMode.HTML);
                label.setPrimaryStyleName("valo-menu-subtitle");
                label.addStyleName("h4");
                label.setSizeUndefined();
                menuItemsLayout.addComponent(label);
            }
            if (item.getKey().equals("calendar")) {
                label.setValue(label.getValue()
                        + " <span class=\"valo-menu-badge\">" + count
                        + "</span>");
                count = 0;
                label = new Label("Other", ContentMode.HTML);
                label.setPrimaryStyleName("valo-menu-subtitle");
                label.addStyleName("h4");
                label.setSizeUndefined();
                menuItemsLayout.addComponent(label);
            }
            Button b = new Button(item.getValue(), new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    navigator.navigateTo(item.getKey());
                }
            });
            if (count == 2) {
                b.setCaption(b.getCaption()
                        + " <span class=\"valo-menu-badge\">123</span>");
            }
            b.setHtmlContentAllowed(true);
            b.setPrimaryStyleName("valo-menu-item");
            b.setIcon(testIcon.get());
            menuItemsLayout.addComponent(b);
            count++;
        }
        label.setValue(label.getValue() + " <span class=\"valo-menu-badge\">"
                + count + "</span>");

        return menu;
    }

    private Component createThemeSelect() {
        final NativeSelect ns = new NativeSelect();
        ns.setNullSelectionAllowed(false);
        ns.setId("themeSelect");
        ns.addContainerProperty("caption", String.class, "");
        ns.setItemCaptionPropertyId("caption");
        for (String identifier : themeVariants.keySet()) {
            ns.addItem(identifier).getItemProperty("caption")
                    .setValue(themeVariants.get(identifier));
        }

        ns.setValue("tests-valo");
        ns.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setTheme((String) ns.getValue());
            }
        });
        return ns;
    }

    static Handler actionHandler = new Handler() {
        private final Action ACTION_ONE = new Action("Action One");
        private final Action ACTION_TWO = new Action("Action Two");
        private final Action ACTION_THREE = new Action("Action Three");
        private final Action[] ACTIONS = new Action[] { ACTION_ONE, ACTION_TWO,
                ACTION_THREE };

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            Notification.show(action.getCaption());
        }

        @Override
        public Action[] getActions(Object target, Object sender) {
            return ACTIONS;
        }
    };

    static Handler getActionHandler() {
        return actionHandler;
    }

    static final String CAPTION_PROPERTY = "caption";
    static final String DESCRIPTION_PROPERTY = "description";
    static final String ICON_PROPERTY = "icon";
    static final String INDEX_PROPERTY = "index";

    @SuppressWarnings("unchecked")
    static Container generateContainer(final int size,
            final boolean hierarchical) {
        TestIcon testIcon = new TestIcon(90);
        IndexedContainer container = hierarchical ? new HierarchicalContainer()
                : new IndexedContainer();
        StringGenerator sg = new StringGenerator();
        container.addContainerProperty(CAPTION_PROPERTY, String.class, null);
        container.addContainerProperty(ICON_PROPERTY, Resource.class, null);
        container.addContainerProperty(INDEX_PROPERTY, Integer.class, null);
        container
                .addContainerProperty(DESCRIPTION_PROPERTY, String.class, null);
        for (int i = 1; i < size + 1; i++) {
            Item item = container.addItem(i);
            item.getItemProperty(CAPTION_PROPERTY).setValue(
                    sg.nextString(true) + " " + sg.nextString(false));
            item.getItemProperty(INDEX_PROPERTY).setValue(i);
            item.getItemProperty(DESCRIPTION_PROPERTY).setValue(
                    sg.nextString(true) + " " + sg.nextString(false) + " "
                            + sg.nextString(false));
            item.getItemProperty(ICON_PROPERTY).setValue(testIcon.get());
        }
        container.getItem(container.getIdByIndex(0))
                .getItemProperty(ICON_PROPERTY).setValue(testIcon.get());

        if (hierarchical) {
            for (int i = 1; i < size + 1; i++) {
                for (int j = 1; j < 5; j++) {
                    String id = i + " -> " + j;
                    Item child = container.addItem(id);
                    child.getItemProperty(CAPTION_PROPERTY).setValue(
                            sg.nextString(true) + " " + sg.nextString(false));
                    child.getItemProperty(ICON_PROPERTY).setValue(
                            testIcon.get());
                    ((Hierarchical) container).setParent(id, i);

                    for (int k = 1; k < 6; k++) {
                        String id2 = id + " -> " + k;
                        child = container.addItem(id2);
                        child.getItemProperty(CAPTION_PROPERTY).setValue(
                                sg.nextString(true) + " "
                                        + sg.nextString(false));
                        child.getItemProperty(ICON_PROPERTY).setValue(
                                testIcon.get());
                        ((Hierarchical) container).setParent(id2, id);

                        for (int l = 1; l < 5; l++) {
                            String id3 = id2 + " -> " + l;
                            child = container.addItem(id3);
                            child.getItemProperty(CAPTION_PROPERTY).setValue(
                                    sg.nextString(true) + " "
                                            + sg.nextString(false));
                            child.getItemProperty(ICON_PROPERTY).setValue(
                                    testIcon.get());
                            ((Hierarchical) container).setParent(id3, id2);
                        }
                    }
                }
            }
        }
        return container;
    }
}
