package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractOrderedLayout;

public abstract class BaseAlignment extends BaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // create two columns of components with different
        // alignment. Used to test alignment in layouts
        init();
        buildLayout();
        super.setup(request);
    }

    public BaseAlignment(Class<? extends AbstractOrderedLayout> layoutClass) {
        super(layoutClass);
    }

    /**
     * Build Layout for test
     */
    private void buildLayout() {
        for (int i = 0; i < components.length; i++) {
            AbstractOrderedLayout layout = null;
            try {
                layout = (AbstractOrderedLayout) layoutClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            layout.setMargin(false);
            layout.setSpacing(false);
            layout.setHeight("100px");
            layout.setWidth("200px");
            layout.addComponent(components[i]);
            layout.setComponentAlignment(components[i], alignments[i]);
            if (i < components.length / 2) {
                l1.addComponent(layout);
            } else {
                l2.addComponent(layout);
            }
        }
    }
}
