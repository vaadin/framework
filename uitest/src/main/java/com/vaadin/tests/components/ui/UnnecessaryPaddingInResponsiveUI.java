package com.vaadin.tests.components.ui;

import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class UnnecessaryPaddingInResponsiveUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Responsive.makeResponsive(this);

        HorizontalLayout root = new HorizontalLayout();
        root.setSpacing(true);

        MenuLayout menu = new MenuLayout();

        root.addComponent(menu);

        setContent(root);

        setWidth(799, Unit.PIXELS);
        setId("UI");

        // Uncomment this to enable responsive features in Valo Menu and
        // introduce a padding-top to the UI. When this is commented the
        // padding-top should be 0 and the related test should pass.

        // addStyleName(ValoTheme.UI_WITH_MENU);
    }

    class MenuLayout extends VerticalLayout {
        public MenuLayout() {
            setSizeFull();
            setPrimaryStyleName(ValoTheme.MENU_ROOT);

            CssLayout titleLo = new CssLayout();
            titleLo.addStyleName(ValoTheme.MENU_TITLE);
            titleLo.addComponent(new Label("menu-title"));

            addComponent(titleLo);

            for (int i = 1; i <= 5; i++) {
                Button button = new Button("Menu Item " + i);
                button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
                addComponent(button);
            }
        }

    }
}
