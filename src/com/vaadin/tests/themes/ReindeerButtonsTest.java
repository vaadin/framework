package com.vaadin.tests.themes;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ReindeerButtonsTest extends com.vaadin.Application {

    final Window main = new Window("Reindeer buttons");

    @Override
    public void init() {
        setMainWindow(main);
        setTheme("reindeer");

        Button toggle = new Button("Toggle black style",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (!main.getContent().getStyleName().contains("black")) {
                            main.getContent().setStyleName("black");
                        } else {
                            main.getContent().setStyleName("");
                        }
                    }
                });
        main.addComponent(toggle);

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
    }

    private Layout buildButtons(boolean disabled, boolean icon, boolean error,
            boolean sized) {

        String[] buttonStyles = new String[] { "", "primary", "small", "link" };

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setMargin(true);

        for (int i = 0; i < buttonStyles.length; i++) {
            Button b = new Button(buttonStyles[i] + " style");
            b.setStyleName(buttonStyles[i]);
            if (icon) {
                b.setIcon(new ThemeResource("../runo/icons/16/document.png"));
            }
            if (error) {
                b.setComponentError(new UserError("Error"));
            }
            if (disabled) {
                b.setEnabled(false);
            }
            if (sized) {
                b.setWidth("150px");
            }
            hl.addComponent(b);
        }

        return hl;
    }

}