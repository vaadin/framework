package com.vaadin.tests.themes;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.NativeButton;

@SuppressWarnings("serial")
public class ButtonsTest extends com.vaadin.server.LegacyApplication {

    final LegacyWindow main = new LegacyWindow("Button states & themes");

    CheckBox styleToggle;
    CheckBox iconToggle;
    CheckBox nativeToggle;
    CheckBox themeToggle;
    boolean largeIcons = false;
    boolean nativeButtons = false;

    final HorizontalLayout toggles = new HorizontalLayout();

    @Override
    public void init() {
        setMainWindow(main);
        setTheme("reindeer");

        themeToggle = new CheckBox("Runo theme");
        themeToggle.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (getTheme() == "reindeer") {
                    setTheme("runo");
                } else {
                    setTheme("reindeer");
                }
            }
        });
        themeToggle.setStyleName("small");
        themeToggle.setImmediate(true);

        styleToggle = new CheckBox("Black style");
        styleToggle.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (!main.getContent().getStyleName().contains("black")) {
                    main.getContent().setStyleName("black");
                } else {
                    main.getContent().setStyleName("");
                }
            }
        });
        styleToggle.setImmediate(true);
        styleToggle.setStyleName("small");

        iconToggle = new CheckBox("64x icons");
        iconToggle.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                largeIcons = !largeIcons;
                recreateAll();
            }
        });
        iconToggle.setImmediate(true);
        iconToggle.setStyleName("small");

        nativeToggle = new CheckBox("Native buttons");
        nativeToggle.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                nativeButtons = !nativeButtons;
                recreateAll();
            }
        });
        nativeToggle.setImmediate(true);
        nativeToggle.setStyleName("small");

        toggles.setSpacing(true);
        toggles.addComponent(themeToggle);
        toggles.addComponent(styleToggle);
        toggles.addComponent(iconToggle);
        toggles.addComponent(nativeToggle);
        main.addComponent(toggles);

        recreateAll();

    }

    private void recreateAll() {
        main.removeAllComponents();
        main.addComponent(toggles);

        main.addComponent(buildButtons(false, false, false, false));
        main.addComponent(buildButtons(false, false, true, false));
        main.addComponent(buildButtons(false, true, false, false));
        main.addComponent(buildButtons(false, true, true, false));
        main.addComponent(buildButtons(true, false, false, false));
        main.addComponent(buildButtons(true, false, true, false));
        main.addComponent(buildButtons(true, true, false, false));
        main.addComponent(buildButtons(true, true, true, false));

        main.addComponent(buildButtons(false, false, false, true));
        main.addComponent(buildButtons(false, false, true, true));
        main.addComponent(buildButtons(false, true, false, true));
        main.addComponent(buildButtons(false, true, true, true));
        main.addComponent(buildButtons(true, false, false, true));
        main.addComponent(buildButtons(true, false, true, true));
        main.addComponent(buildButtons(true, true, false, true));
        main.addComponent(buildButtons(true, true, true, true));

        final Button b = new Button("Tabindex");
        b.setTabIndex(1);
        main.addComponent(b);

        Button c = new Button("toggle enabled", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                b.setEnabled(!b.isEnabled());
            }
        });
        main.addComponent(c);
    }

    private Layout buildButtons(boolean disabled, boolean icon, boolean error,
            boolean sized) {

        String[] buttonStyles = new String[] { "Normal", "Primary", "Small",
                "Link" };

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setMargin(true);

        for (int i = 0; i < buttonStyles.length; i++) {
            Button b;
            if (nativeButtons) {
                b = new NativeButton(buttonStyles[i] + " style");
            } else {
                b = new Button(buttonStyles[i] + " style");
            }
            b.setStyleName(buttonStyles[i].toLowerCase());
            if (icon) {
                b.setIcon(new ThemeResource("../runo/icons/"
                        + (largeIcons ? "64" : "16") + "/document.png"));
            }
            if (error) {
                b.setComponentError(new UserError("Error"));
            }
            if (disabled) {
                b.setEnabled(false);
            }
            if (sized) {
                b.setWidth("250px");
                b.setCaption(b.getCaption() + " (250px)");
            }
            hl.addComponent(b);
        }

        return hl;
    }

}
