package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class ComboBoxDuplicateCaption extends TestBase {

    private Log log = new Log(5);

    @Override
    protected void setup() {
        List<Person> list = new ArrayList<>();
        Person p1 = new Person();
        p1.setFirstName("John");
        p1.setLastName("Doe");
        list.add(p1);

        Person p2 = new Person();
        p2.setFirstName("Jane");
        p2.setLastName("Doe");
        list.add(p2);

        ComboBox<Person> box = new ComboBox<>("Duplicate captions test Box");
        box.setId("ComboBox");
        box.addValueChangeListener(event -> {
            Person p = event.getValue();
            log.log("Person = " + p.getFirstName() + " " + p.getLastName());
        });
        box.setItems(list);
        box.setItemCaptionGenerator(Person::getLastName);

        addComponent(log);

        addComponent(box);
        addComponent(new Button("Focus this"));
    }

    @Override
    protected String getDescription() {
        return "ComboBoxes with duplicate item captions should not try to do a select (exact match search) for onBlur if not waitingForFilteringResponse";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10766;
    }
}
