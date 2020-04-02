package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

public class BaseLayoutForSpacingMargin extends BaseLayoutTestUI {
    /**
     * @param layoutClass
     */
    public BaseLayoutForSpacingMargin(
            Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        l2.addComponent(t1);
        l2.setMargin(false);
        l2.setSpacing(false);
        // Must add something around the hr to avoid the margins collapsing
        Label spacer = new Label(
                "<div style='height: 1px'></div><hr /><div style='height: 1px'></div>",
                ContentMode.HTML);
        spacer.setWidth("100%");
        l2.addComponent(spacer);
        l2.addComponent(t2);
        final Button btn1 = new Button("Toggle margin on/off");
        btn1.addClickListener(event -> {
            boolean margin = l2.getMargin().hasLeft();
            l2.setMargin(!margin);
        });
        final Button btn2 = new Button("Toggle spacing on/off");
        btn2.addClickListener(event -> l2.setSpacing(!l2.isSpacing()));
        l1.addComponent(btn1);
        l1.addComponent(btn2);
    }
}
