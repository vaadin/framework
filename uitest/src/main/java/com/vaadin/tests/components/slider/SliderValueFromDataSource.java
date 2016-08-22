package com.vaadin.tests.components.slider;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.ProgressBar;

public class SliderValueFromDataSource extends AbstractTestUI {

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
        binder.bind(bean);
        addComponent(slider);

        ProgressBar pi = new ProgressBar();
        pi.setWidth("200px");
        pi.setPropertyDataSource(item.getItemProperty("floatValue"));
        addComponent(pi);
    }

    @Override
    protected String getTestDescription() {
        return "Slider and ProgressBar do not properly pass a value from data source to the client";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10921;
    }
}
