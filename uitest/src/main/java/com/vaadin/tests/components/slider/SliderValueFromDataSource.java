package com.vaadin.tests.components.slider;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Slider;

public class SliderValueFromDataSource extends AbstractTestUI {

    public static class TestBean {

        private double doubleValue = 10.0;
        private float floatValue = 0.5f;

        public double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public float getFloatValue() {
            return floatValue;
        }

        public void setFloatValue(float floatValue) {
            this.floatValue = floatValue;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Item item = new BeanItem<TestBean>(new TestBean());

        Slider slider = new Slider(0, 20);
        slider.setWidth("200px");
        slider.setPropertyDataSource(item.getItemProperty("doubleValue"));
        addComponent(slider);

        ProgressIndicator pi = new ProgressIndicator();
        pi.setPollingInterval(60 * 1000);
        pi.setWidth("200px");
        pi.setPropertyDataSource(item.getItemProperty("floatValue"));
        addComponent(pi);
    }

    @Override
    protected String getTestDescription() {
        return "Slider and ProgressIndicator do not properly pass a value from data source to the client";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10921;
    }
}
