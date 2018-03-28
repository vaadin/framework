package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class ChangeHierarchyBeforeResponse extends AbstractReindeerTestUI {
    private CssLayout layout = new CssLayout() {
        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            if (initial) {
                addComponent(buttonToAdd);
                removeComponent(labelToRemove);
            }
        }
    };

    private Label labelToRemove = new Label("Label to remove") {
        int count = 0;

        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            if (initial) {
                count++;
                setValue("Initial count: " + count);
            }
        }
    };

    private Button buttonToAdd = new Button("Added from beforeClientResponse",
            event -> layout.addComponent(labelToRemove)) {
        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            setCaption("Add label to layout");
        }
    };

    @Override
    protected void setup(VaadinRequest request) {
        layout.addComponent(labelToRemove);

        addComponent(layout);
    }

}
