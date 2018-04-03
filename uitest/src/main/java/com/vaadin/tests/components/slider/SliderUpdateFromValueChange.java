/**
 *
 */
package com.vaadin.tests.components.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Slider;

/**
 * Testcase for #12133
 *
 * @author Vaadin Ltd
 */
public class SliderUpdateFromValueChange extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Slider slider = new Slider(0, 100, 1);
        slider.addValueChangeListener(event -> {
            Double value = event.getValue();
            if (value < 100.0) {
                slider.setValue(100.0);
            }
            slider.markAsDirty();

        });
        slider.setWidth(200, Unit.PIXELS);

        addComponent(slider);
    }

    @Override
    protected String getTestDescription() {
        return "Slider.setValue() does not update graphical representation of Slider component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12133;
    }
}
