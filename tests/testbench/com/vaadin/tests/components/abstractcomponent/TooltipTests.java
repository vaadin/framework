package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class TooltipTests extends TestBase {

    private Panel panel;
    private VerticalLayout layout;
    private Label label;

    @Override
    protected String getDescription() {
        return "Generic tooltip handling tests";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8425;
    }

    @Override
    protected void setup() {
        HorizontalLayout topLayout = new HorizontalLayout();
        addComponent(topLayout);
        CheckBox panelCbox = new CheckBox("Panel");
        panelCbox.addListener(panelListener);
        topLayout.addComponent(panelCbox);
        CheckBox layoutCbox = new CheckBox("Layout");
        layoutCbox.addListener(layoutListener);
        topLayout.addComponent(layoutCbox);
        CheckBox labelCbox = new CheckBox("Label");
        topLayout.addComponent(labelCbox);
        labelCbox.addListener(labelListener);

        panel = new Panel();
        panel.setCaption("Panel caption");
        panel.setId("panel");
        addComponent(panel);

        layout = new VerticalLayout();
        layout.setId("layout");
        layout.setMargin(true);
        layout.setSpacing(true);
        panel.setContent(layout);

        label = new Label("Hover me!");
        label.setId("label");
        layout.addComponent(label);
    }

    private final Property.ValueChangeListener panelListener = new Property.ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            boolean value = (Boolean) (event.getProperty().getValue());
            if (value) {
                panel.setDescription("I'm panel!");
            } else {
                panel.setDescription("");
            }
        }

    };

    private final Property.ValueChangeListener layoutListener = new Property.ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            boolean value = (Boolean) (event.getProperty().getValue());
            if (value) {
                layout.setDescription("I'm layout!");
            } else {
                layout.setDescription("");
            }
        }

    };

    private final Property.ValueChangeListener labelListener = new Property.ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            boolean value = (Boolean) (event.getProperty().getValue());
            if (value) {
                label.setDescription("I'm label!");
            } else {
                label.setDescription("");
            }
        }

    };

}
