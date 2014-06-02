package com.vaadin.tests.themes.valo;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

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
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@Theme("tests-valo")
@Title("Valo Theme Test")
public class ValoThemeTest extends UI {

    HorizontalLayout root = new HorizontalLayout();
    CssLayout viewDisplay = new CssLayout();
    CssLayout menu = new CssLayout();
    private Navigator navigator;
    private LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();

    @Override
    protected void init(VaadinRequest request) {
        // Show .v-app-loading badge
        // try {
        // Thread.sleep(2000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        getPage().setTitle("Valo Theme Test");
        setContent(root);
        root.setWidth("100%");

        root.addComponent(buildMenu());

        viewDisplay.setWidth("100%");
        viewDisplay.addStyleName("view");
        root.addComponent(viewDisplay);
        root.setExpandRatio(viewDisplay, 1);

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
                for (Iterator<Component> it = menu.iterator(); it.hasNext();) {
                    it.next().removeStyleName("selected");
                }
                for (Entry<String, String> item : menuItems.entrySet()) {
                    if (event.getViewName().equals(item.getKey())) {
                        for (Iterator<Component> it = menu.iterator(); it
                                .hasNext();) {
                            Component c = it.next();
                            if (item.getValue().equals(c.getCaption())) {
                                c.addStyleName("selected");
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        });

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
        menuItems.put("panels", "Panels");
        menuItems.put("splitpanels", "Split Panels");
        menuItems.put("tabs", "Tabs");
        menuItems.put("accordions", "Accordions");
        menuItems.put("popupviews", "Popup Views");
        menuItems.put("calendar", "Calendar");
        menuItems.put("forms", "Forms");

        menu.setStyleName("valo-menu");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName("valo-menu-title");
        menu.addComponent(top);

        Label title = new Label("Vaadin<br><strong>Valo Theme Styles</strong>",
                ContentMode.HTML);
        title.setSizeUndefined();
        top.addComponent(title);
        top.setExpandRatio(title, 1);

        MenuBar settings = new MenuBar();
        MenuItem settingsItem = settings.addItem("", FontAwesome.COG, null);
        settingsItem.setStyleName("icon-only");
        settingsItem.addItem("Edit Profile", null);
        settingsItem.addItem("Preferences", null);
        settingsItem.addSeparator();
        settingsItem.addItem("Sign Out", null);
        top.addComponent(settings);

        final CheckBox enabled = new CheckBox("Enabled", true);
        enabled.setDescription("Enable or disable the components on the right side");
        menu.addComponent(enabled);
        enabled.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                viewDisplay.setEnabled(enabled.getValue());
            }
        });

        Label label = null;
        int count = -1;
        for (final Entry<String, String> item : menuItems.entrySet()) {
            if (item.getKey().equals("labels")) {
                label = new Label("Components", ContentMode.HTML);
                label.setPrimaryStyleName("valo-menu-subtitle");
                label.addStyleName("h4");
                label.setSizeUndefined();
                menu.addComponent(label);
            }
            if (item.getKey().equals("panels")) {
                label.setValue(label.getValue() + " <span class=\"badge\">"
                        + count + "</span>");
                count = 0;
                label = new Label("Containers", ContentMode.HTML);
                label.setPrimaryStyleName("valo-menu-subtitle");
                label.addStyleName("h4");
                label.setSizeUndefined();
                menu.addComponent(label);
            }
            if (item.getKey().equals("calendar")) {
                label.setValue(label.getValue() + " <span class=\"badge\">"
                        + count + "</span>");
                count = 0;
                label = new Label("Other", ContentMode.HTML);
                label.setPrimaryStyleName("valo-menu-subtitle");
                label.addStyleName("h4");
                label.setSizeUndefined();
                menu.addComponent(label);
            }
            Button b = new Button(item.getValue(), new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    navigator.navigateTo(item.getKey());
                }
            });
            b.setPrimaryStyleName("valo-menu-item");
            menu.addComponent(b);
            count++;
        }
        label.setValue(label.getValue() + " <span class=\"badge\">" + count
                + "</span>");

        return menu;
    }

    static String[] strings = new String[] { "lorem", "ipsum", "dolor", "sit",
            "amet", "consectetur" };
    static int stringCount = -1;

    static String nextString(boolean capitalize) {
        if (++stringCount >= strings.length) {
            stringCount = 0;
        }
        return capitalize ? strings[stringCount].substring(0, 1).toUpperCase()
                + strings[stringCount].substring(1) : strings[stringCount];
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
        IndexedContainer container = hierarchical ? new HierarchicalContainer()
                : new IndexedContainer();

        container.addContainerProperty(CAPTION_PROPERTY, String.class, null);
        container.addContainerProperty(ICON_PROPERTY, Resource.class, null);
        container.addContainerProperty(INDEX_PROPERTY, Integer.class, null);
        container
                .addContainerProperty(DESCRIPTION_PROPERTY, String.class, null);
        for (int i = 1; i < size + 1; i++) {
            Item item = container.addItem(i);
            item.getItemProperty(CAPTION_PROPERTY).setValue(
                    nextString(true) + " " + nextString(false));
            item.getItemProperty(INDEX_PROPERTY).setValue(i);
            item.getItemProperty(DESCRIPTION_PROPERTY).setValue(
                    nextString(true) + " " + nextString(false) + " "
                            + nextString(false));
            item.getItemProperty(ICON_PROPERTY).setValue(TestIcon.get());
        }
        container.getItem(container.getIdByIndex(0))
                .getItemProperty(ICON_PROPERTY).setValue(TestIcon.get());

        if (hierarchical) {
            for (int i = 1; i < size + 1; i++) {
                for (int j = 1; j < 5; j++) {
                    String id = i + " -> " + j;
                    Item child = container.addItem(id);
                    child.getItemProperty(CAPTION_PROPERTY).setValue(
                            nextString(true) + " " + nextString(false));
                    child.getItemProperty(ICON_PROPERTY).setValue(
                            TestIcon.get());
                    ((Hierarchical) container).setChildrenAllowed(id, false);
                    ((Hierarchical) container).setParent(id, i);
                }
            }
        }
        return container;
    }
}