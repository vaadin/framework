package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

public class GridLayoutMarginSpacing extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        t1.setSizeUndefined();
        t2.setSizeUndefined();

        final Button btn1 = new Button("Toggle margin on/off");
        btn1.addClickListener(event -> {
            boolean margin = layout.getMargin().hasLeft();
            layout.setMargin(!margin);
        });
        final Button btn2 = new Button("Toggle spacing on/off");
        btn2.addClickListener(event -> layout.setSpacing(!layout.isSpacing()));
        layout.addComponent(btn1);
        layout.addComponent(btn2);

        layout.addComponent(t1);
        layout.setMargin(false);
        layout.setSpacing(false);
        // Must add something around the hr to avoid the margins collapsing
        Label spacer = new Label(
                "<div style='height: 1px'></div><hr /><div style='height: 1px'></div>",
                ContentMode.HTML);
        spacer.setWidth("100%");
        layout.addComponent(spacer);
        layout.addComponent(t2);
    }
}
