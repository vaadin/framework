package com.vaadin.tests.serialization;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.LayoutDetector;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.JavaScript;

@Widgetset(TestingWidgetSet.NAME)
public class NoLayout extends AbstractReindeerTestUI {
    private final LayoutDetector layoutDetector = new LayoutDetector();

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(layoutDetector);

        CheckBox uiPolling = new CheckBox("UI polling enabled");
        uiPolling.addValueChangeListener(event -> {
            if (event.getValue()) {
                setPollInterval(100);
            } else {
                setPollInterval(-1);
            }
        });
        addComponent(uiPolling);

        addComponent(new Button("Change regular state",
                event -> event.getButton().setCaption(
                        String.valueOf(System.currentTimeMillis()))));
        addComponent(new Button("Change @NoLayout state",
                event -> event.getButton().setDescription(
                        String.valueOf(System.currentTimeMillis()))));
        addComponent(
                new Button("Do regular RPC", event -> JavaScript.eval("")));

        addComponent(new Button("Do @NoLayout RPC",
                event -> layoutDetector.doNoLayoutRpc()));

        addComponent(new Button("Update LegacyComponent", event -> {
            // Assumes UI is a LegacyComponent
            markAsDirty();
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Checks which actions trigger a layout phase";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(12936);
    }

}
