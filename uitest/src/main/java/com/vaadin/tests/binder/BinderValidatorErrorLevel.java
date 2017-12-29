package com.vaadin.tests.binder;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class BinderValidatorErrorLevel extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Binder<Object> binder = new Binder<>();

        for (ErrorLevel l : ErrorLevel.values()) {
            TextField field = new TextField(l.name());
            binder.forField(field)
                    .withValidator(s -> s.length() > 3,
                            "ErrorLevel: " + l.name(), l)
                    .bind(t -> "", (t, s) -> {
                    });
            addComponent(field);
        }
        binder.validate();
    }

}
