package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

public class ComboBoxMixedUpdate extends AbstractTestUIWithLog {

    private Pojo pojo;

    @Override
    protected void setup(VaadinRequest request) {
        Binder<Pojo> binder = new Binder<>();
        ComboBox<Integer> numbers = new ComboBox<>();
        numbers.setItems(Arrays.asList(0, 1, 2, 3));
        binder.forField(numbers).bind(Pojo::getNumber, Pojo::setNumber);

        pojo = new Pojo(1);
        binder.setBean(pojo);

        Button reset = new Button("reset");
        reset.setId("reset");
        reset.addClickListener(e -> {
            pojo.setNumber(0);
            // refresh binder
            binder.readBean(pojo);
        });

        Button show = new Button("show values");
        show.setId("show");
        show.addClickListener(e -> {
            log("Bean value = " + pojo.getNumber() + " - ComboBox value = "
                    + numbers.getValue());
        });

        HorizontalLayout buttons = new HorizontalLayout(numbers, show, reset);

        getLayout().addComponents(buttons);
    }

    @Override
    protected String getTestDescription() {
        return "1: Write not null value (1-3) that differs from previous selection and TAB out -- don't select from drop down"
                + "<br>2: Click the 'show values' button to confirm both ComboBox and bean values were updated"
                + "<br>3: Click the 'reset' button (both ComboBox and bean values should go to 0)"
                + "<br>4: Re-focus ComboBox, write the previous value and TAB out -- don't select from drop down"
                + "<br>5: Both ComboBox and bean values should have the written value, not 0.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10660;
    }

    public class Pojo {
        int number;

        public Pojo(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
