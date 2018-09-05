package com.vaadin.tests.components.window;

import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ShortcutInWindow extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        HorizontalLayout buttons = new HorizontalLayout();
        final TextField name = new TextField("Name");
        name.setValueChangeMode(ValueChangeMode.BLUR);
        name.setValueChangeTimeout(1000);
        name.addValueChangeListener(
                (e) -> log("Value Changed: " + e.getValue()));
        final Button toggle = new Button(name.getValueChangeMode().toString());
        toggle.addClickListener((e) -> {
            int o = name.getValueChangeMode().ordinal();
            int i = ValueChangeMode.values().length <= o + 1 ? 0 : o + 1;
            ValueChangeMode m = ValueChangeMode.values()[i];
            toggle.setCaption(m.toString());
            name.setValueChangeMode(m);
            log("New ValueChangeMode: " + m);
        });
        final Button submit = new Button("Submit",
                (e) -> log("Submitted value: " + name.getValue()));
        submit.setClickShortcut(KeyCode.ENTER);
        buttons.addComponent(toggle);
        buttons.addComponent(submit);
        content.addComponent(name);
        content.addComponent(buttons);
        Window popup = new Window();
        popup.center();
        popup.setContent(content);
        getUI().addWindow(popup);

    }

}
