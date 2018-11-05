package com.vaadin.tests.components.listselect;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ListSelectStyleNames extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        ListSelect<String> testselect = new ListSelect<>();
        testselect.setItems("abc", "def", "ghi");
        testselect.addStyleName("custominitial");
        addComponent(testselect);

        NativeSelect<String> nativeSelect = new NativeSelect<>();
        nativeSelect.setItems("abc", "def", "ghi");
        nativeSelect.addStyleName("custominitial");
        addComponent(nativeSelect);

        Button button = new Button("Add style 'new'", event -> {
            testselect.addStyleName("new");
            nativeSelect.addStyleName("new");
        });
        button.setId("add");
        addComponent(button);

        button = new Button("Change primary style to 'newprimary'", event -> {
            testselect.setPrimaryStyleName("newprimary");
            nativeSelect.setPrimaryStyleName("newprimary");
        });
        button.setId("changeprimary");
        addComponent(button);
    }

}
