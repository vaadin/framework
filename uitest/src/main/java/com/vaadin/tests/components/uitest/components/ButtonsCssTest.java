package com.vaadin.tests.components.uitest.components;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

public class ButtonsCssTest extends GridLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public ButtonsCssTest(TestSampler parent) {
        this.parent = parent;
        setSpacing(true);
        setWidth("100%");
        setColumns(6);

        Button b = new Button("Default button");
        b.setId("button" + debugIdCounter++);
        addComponent(b);

        b = new Button("Button with icon");
        b.setIcon(new ThemeResource(parent.ICON_URL));
        b.setId("button" + debugIdCounter++);
        addComponent(b);

        b = new Button("Button with tooltip");
        b.setDescription("The tooltip");
        b.setId("button" + debugIdCounter++);
        addComponent(b);

        b = new Button("Link button");
        b.setStyleName(BaseTheme.BUTTON_LINK);
        b.setId("button" + debugIdCounter++);
        addComponent(b);

        b = new Button("Disabled on click button");
        b.setDisableOnClick(true);
        b.setId("button" + debugIdCounter++);
        addComponent(b);

        CheckBox cb = new CheckBox("Checkbox");
        cb.setId("button" + debugIdCounter++);
        addComponent(cb);

        cb = new CheckBox("Checkbox with icon");
        cb.setIcon(new ThemeResource(parent.ICON_URL));
        cb.setId("button" + debugIdCounter++);
        addComponent(cb);

        Link l = new Link("A link", new ExternalResource(""));
        l.setId("button" + debugIdCounter++);
        addComponent(l);

        createButtonWith("Primary", Reindeer.BUTTON_DEFAULT, null);
        createButtonWith("Small", Reindeer.BUTTON_SMALL, null);
        createButtonWith("Default", Runo.BUTTON_DEFAULT, null);
        createButtonWith("Big", Runo.BUTTON_BIG, null);
        createButtonWith("Wide", ChameleonTheme.BUTTON_WIDE, null);
        createButtonWith("Tall", ChameleonTheme.BUTTON_TALL, null);
        createButtonWith("Borderless", ChameleonTheme.BUTTON_BORDERLESS, null);
        createButtonWith("Icn top", ChameleonTheme.BUTTON_ICON_ON_TOP,
                parent.ICON_URL);
        createButtonWith("Icn right", ChameleonTheme.BUTTON_ICON_ON_RIGHT,
                parent.ICON_URL);
        createButtonWith("Icon only", ChameleonTheme.BUTTON_ICON_ONLY,
                parent.ICON_URL);
        createButtonWith("Down", ChameleonTheme.BUTTON_DOWN, null);

    }

    private void createButtonWith(String caption, String primaryStyleName,
            String iconUrl) {
        Button b = new Button();
        b.setId("button" + debugIdCounter++);

        if (caption != null) {
            b.setCaption(caption);
        }

        if (primaryStyleName != null) {
            b.addStyleName(primaryStyleName);
        }

        if (iconUrl != null) {
            b.setIcon(new ThemeResource(iconUrl));
        }

        addComponent(b);

    }

    @Override
    public void addComponent(Component component) {
        parent.registerComponent(component);
        super.addComponent(component);
    }

}
