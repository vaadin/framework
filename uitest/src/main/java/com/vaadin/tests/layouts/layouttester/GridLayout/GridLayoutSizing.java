package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class GridLayoutSizing extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayoutForLayoutSizing("layout");
        super.setup(request);
        layout.setSizeFull();
    }

    @Override
    protected void getLayoutForLayoutSizing(final String compType) {

        layout.setSpacing(false);
        layout.setMargin(false);

        final AbstractComponent c1 = getTestTable();
        final AbstractComponent c2 = getTestTable();

        class SetSizeButton extends Button {
            SetSizeButton(final String size) {
                super();
                setCaption("Set size " + size);
                addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (compType == "layout") {
                            layout.setHeight(size);
                            layout.setWidth(size);
                        } else if (compType == "component") {
                            c2.setHeight(size);
                            c2.setWidth(size);
                        } else {
                        }

                    }
                });
            }

        }
        Button btn1 = new SetSizeButton("600px");
        Button btn2 = new SetSizeButton("-1px");
        Button btn3 = new SetSizeButton("75%");
        Button btn4 = new SetSizeButton("100%");

        layout.addComponent(btn1);
        layout.addComponent(btn2);
        layout.addComponent(btn3);
        layout.addComponent(btn4);
        layout.addComponent(c1);
        layout.addComponent(new Label(
                "<div style='height: 1px'></div><hr /><div style='height: 1px'></div>",
                ContentMode.HTML));
        layout.addComponent(c2);
        btn2.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Label newLabel = new Label("--- NEW LABEL ---");
                newLabel.setSizeUndefined();
                layout.addComponent(newLabel);
            }
        });
        btn2.setCaption(btn2.getCaption() + " + add Label");
    }
}
