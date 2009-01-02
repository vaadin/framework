package com.itmill.toolkit.tests.tickets;

import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.validator.StringLengthValidator;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1811 extends com.itmill.toolkit.Application {

    LinkedList listOfAllFields = new LinkedList();

    @Override
    public void init() {

        final Window main = new Window("#1811");
        setMainWindow(main);

        Validator strLenValidator = new StringLengthValidator(
                "String must be at least 3 chars long and non-null", 3, -1,
                false);

        TextField tf1 = new TextField(
                "Text field with default settings (required=false)");
        listOfAllFields.add(tf1);

        TextField tf2 = new TextField("Text field with required=true");
        tf2.setRequired(true);
        listOfAllFields.add(tf2);

        TextField tf3 = new TextField(
                "Text field with required=true and strlen >= 3");
        tf3.setRequired(true);
        tf3.addValidator(strLenValidator);
        listOfAllFields.add(tf3);

        TextField tf4 = new TextField(
                "Text field with required=false (default) and strlen >= 3");
        tf4.addValidator(strLenValidator);
        listOfAllFields.add(tf4);

        for (Iterator i = listOfAllFields.iterator(); i.hasNext();) {
            TextField tf = (TextField) i.next();
            main.addComponent(tf);
            tf.setImmediate(true);
        }

        Button checkValidity = new Button("Check validity of the fields");
        main.addComponent(checkValidity);
        checkValidity.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                StringBuffer msg = new StringBuffer();
                for (java.util.Iterator i = listOfAllFields.iterator(); i
                        .hasNext();) {
                    TextField tf = (TextField) i.next();
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
                w.addComponent(new Label(msg.toString(), Label.CONTENT_XHTML));
                main.addWindow(w);
            }
        });
    }

}
