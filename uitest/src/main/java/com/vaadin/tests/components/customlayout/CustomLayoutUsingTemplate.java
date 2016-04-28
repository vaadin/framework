package com.vaadin.tests.components.customlayout;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class CustomLayoutUsingTemplate extends TestBase implements
        ClickListener {

    CustomLayout layout;
    Button button1 = new Button("Add Button to first location", this);
    Button button2 = new Button("Add TextField to second location", this);

    @Override
    protected void setup() {
        String thisPackage = CustomLayoutUsingTemplate.class.getName().replace(
                '.', '/');
        thisPackage = thisPackage.replaceAll(
                CustomLayoutUsingTemplate.class.getSimpleName() + "$", "");
        String template = thisPackage + "template.htm";
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                template);

        addComponent(button1);

        try {
            layout = new CustomLayout(is);
            addComponent(layout);
        } catch (IOException e) {
            addComponent(new Label(e.getMessage()));
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String getDescription() {
        return "Test for using a CustomLayout with a template read from an input stream and passed through the state";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == button1) {
            layout.addComponent(button2, "location1");
        } else {
            layout.addComponent(new TextField("A text field!"), "location2");
        }
    }
}
