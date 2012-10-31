package com.vaadin.tests.components.button;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class ButtonsInHorizontalLayout extends AbstractTestCase {

    @Override
    public void init() {
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);

        content.addComponent(createButtonLayout(null));
        content.addComponent(createButtonLayout(BaseTheme.BUTTON_LINK));

        setMainWindow(new LegacyWindow("", content));
    }

    private HorizontalLayout createButtonLayout(String style) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(createButton(style));
        layout.addComponent(createButton(style));
        layout.addComponent(createButton(style));
        return layout;
    }

    private Button createButton(String style) {
        Button button = new Button(
                "Look at me in IE7 or IE8 in compatibility mode");
        if (style != null && style.length() != 0) {
            button.setStyleName(style);
        }
        return button;
    }

    @Override
    protected String getDescription() {
        return "Tests for rendering of buttons in a HorizontalLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7978;
    }

}
