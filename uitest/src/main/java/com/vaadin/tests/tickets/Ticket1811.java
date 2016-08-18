package com.vaadin.tests.tickets;

import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.LegacyStringLengthValidator;
import com.vaadin.v7.ui.LegacyTextField;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Window;

public class Ticket1811 extends com.vaadin.server.LegacyApplication {

    LinkedList<LegacyTextField> listOfAllFields = new LinkedList<LegacyTextField>();

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow("#1811");
        setMainWindow(main);

        Validator strLenValidator = new LegacyStringLengthValidator(
                "String must be at least 3 chars long and non-null", 3, -1,
                false);

        LegacyTextField tf1 = new LegacyTextField(
                "Text field with default settings (required=false)");
        listOfAllFields.add(tf1);

        LegacyTextField tf2 = new LegacyTextField(
                "Text field with required=true");
        tf2.setRequired(true);
        listOfAllFields.add(tf2);

        LegacyTextField tf3 = new LegacyTextField(
                "Text field with required=true and strlen >= 3");
        tf3.setRequired(true);
        tf3.addValidator(strLenValidator);
        listOfAllFields.add(tf3);

        LegacyTextField tf4 = new LegacyTextField(
                "Text field with required=false (default) and strlen >= 3");
        tf4.addValidator(strLenValidator);
        listOfAllFields.add(tf4);

        for (Iterator<LegacyTextField> i = listOfAllFields.iterator(); i
                .hasNext();) {
            LegacyTextField tf = i.next();
            main.addComponent(tf);
            tf.setImmediate(true);
        }

        Button checkValidity = new Button("Check validity of the fields");
        main.addComponent(checkValidity);
        checkValidity.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                StringBuffer msg = new StringBuffer();
                for (Iterator<LegacyTextField> i = listOfAllFields.iterator(); i
                        .hasNext();) {
                    LegacyTextField tf = i.next();
                    msg.append("<h1>" + tf.getCaption() + "</h1>\n");
                    if (tf.isValid()) {
                        msg.append("VALID\n<hr/>");
                    } else {
                        msg.append("INVALID<br/><i>" + tf.getErrorMessage()
                                + "</i><hr/>");
                    }
                }
                Window w = new Window("Status of the fields");
                w.setModal(true);
                w.setContent(new Label(msg.toString(), ContentMode.HTML));
                main.addWindow(w);
            }
        });
    }

}
