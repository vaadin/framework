package com.vaadin.tests.components.slider;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Slider;
import com.vaadin.v7.data.util.BeanItem;

public class SliderValueFromDataSource extends AbstractReindeerTestUI {

    public static class TestBean {

        private float floatValue = 0.5f;

        public float getFloatValue() {
            return floatValue;
        }

        public void setFloatValue(float doubleValue) {
            floatValue = doubleValue;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        TestBean bean = new TestBean();
        BeanItem<TestBean> item = new BeanItem<>(bean);

        Slider slider = new Slider(0, 10);
        slider.setWidth("200px");
        Binder<TestBean> binder = new Binder<>();
        binder.forField(slider).bind(
                b -> Double.valueOf(b.getFloatValue() * 10.0),
                (b, doubleValue) -> item.getItemProperty("floatValue")
                        .setValue((float) (doubleValue / 10.0)));
        binder.setBean(bean);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidth("200px");

        slider.addValueChangeListener(
                event -> progressBar.setValue(event.getValue().floatValue()));

        addComponents(slider, progressBar);
    }

    @Override
    protected String getTestDescription() {
        return "Slider and ProgressBar do not properly pass a value from data provider to the client";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10921;
    }
}
