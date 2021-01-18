package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;

public class DateTimeFieldResolutionChange extends AbstractTestUIWithLog {

    protected DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    @Override
    protected void setup(VaadinRequest request) {
        Binder<Pojo> binder = new Binder<>(Pojo.class);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        final DateTimeField monthField = new DateTimeField() {
            @Override
            public void setValue(LocalDateTime value) {
                if (value != null) {
                    log("MonthField set value " + DATE_FORMATTER.format(value));
                }
                super.setValue(value);
            }
        };
        monthField.setResolution(DateTimeResolution.MONTH);
        monthField.setId("MonthField");
        monthField.addValueChangeListener(
                event -> log("MonthField value change event: "
                        + DATE_FORMATTER.format(event.getValue())));
        binder.bind(monthField, "value1");

        final DateTimeField dayField = new DateTimeField() {
            @Override
            public void setValue(LocalDateTime value) {
                if (value != null) {
                    log("DayField set value " + DATE_FORMATTER.format(value));
                }
                super.setValue(value);
            }
        };
        dayField.setResolution(DateTimeResolution.DAY);
        dayField.setId("DayField");
        dayField.addValueChangeListener(
                event -> log("DayField value change event: "
                        + DATE_FORMATTER.format(event.getValue())));
        binder.bind(dayField, "value2");

        Pojo pojo = new Pojo();
        binder.setBean(pojo);

        Button monthButton = new Button("month", e -> {
            monthField.setResolution(DateTimeResolution.MONTH);
            dayField.setResolution(DateTimeResolution.MONTH);
        });

        Button dayButton = new Button("day", e -> {
            monthField.setResolution(DateTimeResolution.DAY);
            dayField.setResolution(DateTimeResolution.DAY);
        });

        Button logButton = new Button("log", e -> {
            log("MonthField current value: "
                    + DATE_FORMATTER.format(pojo.getValue1()));
            log("DayField current value: "
                    + DATE_FORMATTER.format(pojo.getValue2()));
        });

        Button setButton = new Button("set", e -> {
            LocalDateTime newDate = LocalDateTime.of(2021, 2, 14, 16, 17);
            pojo.setValue1(newDate);
            pojo.setValue2(newDate);
            binder.setBean(pojo);
        });

        horizontalLayout.addComponents(monthField, dayField, monthButton,
                dayButton, logButton, setButton);
        addComponent(horizontalLayout);
    }

    public class Pojo {
        private LocalDateTime value1, value2 = null;

        public LocalDateTime getValue1() {
            return value1;
        }

        public void setValue1(LocalDateTime value1) {
            this.value1 = value1;
        }

        public LocalDateTime getValue2() {
            return value2;
        }

        public void setValue2(LocalDateTime value2) {
            this.value2 = value2;
        }
    }

    @Override
    protected String getTestDescription() {
        return "Date field value should immediately update to match resolution.";
    }
}
