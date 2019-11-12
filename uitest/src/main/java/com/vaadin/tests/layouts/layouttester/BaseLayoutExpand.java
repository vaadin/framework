package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Table;

public class BaseLayoutExpand extends BaseLayoutTestUI {

    public BaseLayoutExpand(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        class ExpandButton extends Button {
            final private AbstractComponent c1;
            private AbstractComponent c2;
            private float expandComp1;
            private float expandComp2;

            public ExpandButton(final AbstractComponent c1,
                    final AbstractComponent c2, float e1, float e2) {
                super();
                this.c1 = c1;
                this.c2 = c2;
                expandComp1 = e1;
                expandComp2 = e2;
                setCaption("Expand ratio: " + e1 * 100 + " /" + e2 * 100);
                addClickListener(event -> {
                    l2.setExpandRatio(c1, expandComp1);
                    l2.setExpandRatio(c2, expandComp2);
                });
            }
        }
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        l2.addComponent(t1);
        l2.addComponent(t2);

        l1.addComponent(new ExpandButton(t1, t2, 1.0f, 0.0f));
        l1.addComponent(new ExpandButton(t1, t2, 0.5f, 0.50f));
        l1.addComponent(new ExpandButton(t1, t2, .25f, 0.75f));
    }
}
