package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class GridAddReplaceMove extends GridBaseLayoutTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {

        final HorizontalLayout source = new HorizontalLayout();
        source.addComponent(new Label("OTHER LABEL 1"));
        source.addComponent(new Label("OTHER LABEL 2"));

        final AbstractComponent c1 = new Label("<b>LABEL</b>",
                ContentMode.HTML);
        final AbstractComponent c2 = new Label("<b>LABEL</b>",
                ContentMode.HTML);
        final AbstractComponent c3 = new Table("TABLE");
        c3.setHeight("100px");
        c3.setWidth("100%");

        final Button btnAdd = new Button("Test add");
        final Button btnReplace = new Button("Test replace");
        final Button btnMove = new Button("Test move");
        final Button btnRemove = new Button("Test remove");

        layout.addComponent(btnAdd);
        layout.addComponent(btnReplace);
        layout.addComponent(btnMove);
        layout.addComponent(btnRemove);

        btnAdd.addClickListener(event -> layout.addComponent(new TextField()));
        btnReplace.addClickListener(event -> layout.replaceComponent(c1, c3));
        btnMove.addClickListener(event -> layout.moveComponentsFrom(source));
        btnRemove.addClickListener(event -> {
            layout.removeComponent(c1);
            layout.removeComponent(c2);
        });

        layout.addComponent(c1);
        layout.addComponent(c2);
        layout.addComponent(c3);
    }
}
