package com.vaadin.tests.components.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Slider;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

public class HiddenSliderHandle extends AbstractReindeerTestUI {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setup(VaadinRequest request) {
        Table t = new Table();
        Slider s = new Slider();
        t.setWidth("200px");
        s.setWidth("100px");
        t.addContainerProperty("s", Slider.class, null);
        Item i = t.addItem("123");
        i.getItemProperty("s").setValue(s);
        getLayout().addComponent(t);
    }

    @Override
    protected String getTestDescription() {
        return "Slider's handler should be accessible (visible) if slider is put inside table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13681;
    }

}
