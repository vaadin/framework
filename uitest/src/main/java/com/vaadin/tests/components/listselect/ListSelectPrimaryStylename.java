package com.vaadin.tests.components.listselect;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ListSelect;

public class ListSelectPrimaryStylename extends TestBase {

    @Override
    protected void setup() {
        final ListSelect list = new ListSelect("Caption", Arrays.asList(
                "Option 1", "Option 2", "Option 3"));
        list.setPrimaryStyleName("my-list");
        addComponent(list);

        addComponent(new Button("Change primary stylename",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        list.setPrimaryStyleName("my-second-list");
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "List select should should support primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9907;
    }

}
