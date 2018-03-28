package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class BaseAddReplaceMove extends BaseLayoutTestUI {

    /**
     * @param layoutClass
     */
    public BaseAddReplaceMove(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        // Set undefined height to avoid expanding
        l2.setHeight(null);
        // extra layout from which components will be moved
        final HorizontalLayout source = new HorizontalLayout();
        Label label1 = new Label("OTHER LABEL 1");
        label1.setWidth("100%"); // Only to make test backwards compatible
        source.addComponent(label1);
        Label label2 = new Label("OTHER LABEL 2");
        label2.setWidth("100%"); // Only to make test backwards compatible
        source.addComponent(label2);

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

        l1.addComponent(btnAdd);
        l1.addComponent(btnReplace);
        l1.addComponent(btnMove);
        l1.addComponent(btnRemove);

        btnAdd.addClickListener(event -> l2.addComponent(new TextField()));
        btnReplace.addClickListener(event -> l2.replaceComponent(c1, c3));
        btnMove.addClickListener(event -> l2.moveComponentsFrom(source));
        btnRemove.addClickListener(event -> {
            l2.removeComponent(c1);
            l2.removeComponent(c2);
        });

        l2.addComponent(c1);
        l2.addComponent(c2);
        l2.addComponent(c3);
    }
}
