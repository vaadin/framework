package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldFaultyInputNotValid extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DateField df = new DateField();
        Binder<Void> binder = new Binder<>();
        binder.forField(df).bind(v -> LocalDate.now(), (v, t) -> {
            /* NO-OP */ });
        df.setRangeStart(LocalDate.now().minusDays(2));
        addComponent(df);
        addComponent(new Button("Validate", event -> Notification
                .show(binder.validate().isOk() ? "OK" : "Fail")));
    }

}
