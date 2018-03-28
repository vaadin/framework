package com.vaadin.tests.components.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;

public class SliderDisable extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);

        final Slider slider = new Slider(0, 5);
        slider.setWidth(200, Unit.PIXELS);
        slider.setValue(1.0D);

        Button disableButton = new Button("Disable slider");
        disableButton.setId("disableButton");
        disableButton.addClickListener(event -> slider.setEnabled(false));

        content.addComponent(slider);
        content.addComponent(disableButton);
        setContent(content);
    }

    @Override
    protected String getTestDescription() {
        return "The apparent value of the slider should not change when the slider is disabled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12676;
    }

}
