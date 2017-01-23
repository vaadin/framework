package com.vaadin.tests.components.absolutelayout;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * Test UI with different cases for component size changes
 */
public class AbsoluteLayoutResizeComponents extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbsoluteLayout layout = new AbsoluteLayout();

        addStartWithFullWidth(layout);
        addStartWithDefinedWidth(layout);
        addStartWithDefinedWidthAbsoluteLayout(layout);

        setContent(layout);
    }

    /**
     * Build test layout for #8255
     */
    private void addStartWithFullWidth(AbsoluteLayout layout) {
        final Panel full = new Panel(new CssLayout(new Label("Start Width 100%")));
        full.setWidth("100%");
        full.setId("expanding-panel");

        layout.addComponent(full, "right:0;top:10px;");
        layout.addComponent(expandButton(full), "left: 10x; top: 50px;");
    }

    /**
     * Build test layout for #8256
     */
    private void addStartWithDefinedWidth(AbsoluteLayout layout) {
        final Panel small = new Panel(new CssLayout(new Label("Start Width 250px")));
        small.setWidth("250px");
        small.setId("small-panel");

        layout.addComponent(small, "right:0;top:100px;");
        layout.addComponent(expandButton(small), "left: 10x; top: 150px;");
    }


    /**
     * Build test layout for #8257
     */
    private void addStartWithDefinedWidthAbsoluteLayout(AbsoluteLayout layout) {
        AbsoluteLayout layoutExpading = new AbsoluteLayout();
        layoutExpading.setWidth("250px");
        layoutExpading.addComponent(new Panel(new CssLayout(new Label("Start Width 250px"))));
        layoutExpading.setId("absolute-expanding");

        layout.addComponent(layoutExpading, "right:0;top:200px;");
        layout.addComponent(expandButton(layoutExpading), "left: 10x; top: 250px;");
    }

    /**
     * Create size change button for component
     *
     * @param component Component to controll with button
     * @return Created Expand Button
     */
    private Button expandButton(final Component component) {
        Button button = new Button("Change Size", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (component.getWidthUnits().equals(Sizeable.Unit.PERCENTAGE)) {
                    component.setWidth("250px");
                } else {
                    component.setWidth("100%");
                }
            }
        });
        button.setId(component.getId() + "-button");
        return button;
    }
}
