package com.vaadin.tests.components.gridlayout;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class GridLayoutAlignments extends UI {

    @Override
    protected void init(VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        final GridLayout g = new GridLayout();
        g.setStyleName("border");
        getPage().getStyles().add(".border {border: 1px solid black;}");

        g.setColumns(1);
        g.setRows(1);

        NativeButton target = new NativeButton();
        target.setWidth("30px");
        target.setHeight("30px");
        g.addComponent(target);

        g.setWidth("402px"); // 400 + border
        g.setHeight("402px");

        g.setComponentAlignment(g.getComponent(0, 0), Alignment.MIDDLE_CENTER);

        layout.addComponent(g);

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.addComponent(createButton(g, Alignment.TOP_LEFT));
        buttonLayout.addComponent(createButton(g, Alignment.MIDDLE_LEFT));
        buttonLayout.addComponent(createButton(g, Alignment.BOTTOM_LEFT));
        buttonLayout.addComponent(createButton(g, Alignment.TOP_CENTER));
        buttonLayout.addComponent(createButton(g, Alignment.MIDDLE_CENTER));
        buttonLayout.addComponent(createButton(g, Alignment.BOTTOM_CENTER));
        buttonLayout.addComponent(createButton(g, Alignment.TOP_RIGHT));
        buttonLayout.addComponent(createButton(g, Alignment.MIDDLE_RIGHT));
        buttonLayout.addComponent(createButton(g, Alignment.BOTTOM_RIGHT));

        layout.addComponent(buttonLayout);
        layout.setExpandRatio(buttonLayout, 1);
        setContent(layout);
    }

    private Component createButton(final GridLayout g,
            final Alignment topLeft) {
        return new Button(
                "Align " + topLeft.getVerticalAlignment() + ", "
                        + topLeft.getHorizontalAlignment(),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        g.setComponentAlignment(g.getComponent(0, 0), topLeft);
                    }
                });
    }
}