package com.vaadin.tests.components.form;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;

public class FormWithEnterShortCut extends TestBase {
    private Log log = new Log(2);

    @Override
    protected void setup() {

        final Form form = new Form();
        final TextField tf = new TextField("Search");
        form.addField("searchfield", tf);

        Button button = new Button("Go");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log.log("search: " + tf.getValue());
            }
        });
        button.setClickShortcut(KeyCode.ENTER);
        button.setStyleName("primary");

        form.getFooter().addComponent(button);

        addComponent(log);
        addComponent(form);

    }

    @Override
    protected String getDescription() {
        return "Focusing a button and pressing enter (which is a shortcut for button click) should only produce one click event";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5433;
    }
}
