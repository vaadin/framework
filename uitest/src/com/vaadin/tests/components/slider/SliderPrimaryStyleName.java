package com.vaadin.tests.components.slider;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Slider;

public class SliderPrimaryStyleName extends TestBase {

    @Override
    protected void setup() {
        final Slider slider = new Slider(0, 100);
        slider.setWidth("100px");
        slider.setPrimaryStyleName("my-slider");
        addComponent(slider);

        addComponent(new Button("Change primary style",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        slider.setPrimaryStyleName("my-second-slider");
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Setting the primary stylename should work both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9898;
    }

}
