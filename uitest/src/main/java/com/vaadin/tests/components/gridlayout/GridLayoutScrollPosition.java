package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class GridLayoutScrollPosition extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        Panel panel = new Panel();
        setContent(panel);

        GridLayout gridLayout = new GridLayout();
        gridLayout.setWidth("500px");
        panel.setContent(gridLayout);
        gridLayout.setColumns(1);
        gridLayout.setRows(1);

        Label dummyLabel = new Label("Dummy");
        dummyLabel.setHeight("500px");
        gridLayout.addComponent(dummyLabel);

        final CheckBox visibilityToggleCheckBox = new CheckBox(
                "Hide / Show toggleable components");
        visibilityToggleCheckBox.setId("visibility-toggle");
        visibilityToggleCheckBox.setHeight("2000px");
        visibilityToggleCheckBox.setValue(false); // Initially unchecked
        gridLayout.addComponent(visibilityToggleCheckBox);

        final Label toggleableLabel = new Label("Toggleable Label");
        toggleableLabel.setHeight("2000px");
        toggleableLabel.setVisible(false); // Initially hidden
        gridLayout.addComponent(toggleableLabel);

        visibilityToggleCheckBox.addValueChangeListener(event -> toggleableLabel
                .setVisible(visibilityToggleCheckBox.getValue()));

    }

    @Override
    protected String getTestDescription() {
        return "The UI scroll position should not be reset when visibility of GridLayout children is toggled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13386;
    }
}
