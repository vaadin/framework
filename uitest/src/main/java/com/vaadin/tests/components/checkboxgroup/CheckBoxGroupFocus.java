package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class CheckBoxGroupFocus extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBoxGroup<String> cbg = new CheckBoxGroup<>("CheckBoxes");
        cbg.setItems("Test1", "Test2", "Test3");
        cbg.select("Test2");
        cbg.setItemCaptionGenerator(item -> "Option " + item);
        cbg.focus();
        CheckBoxGroup<String> cbg2 = new CheckBoxGroup<>("No selection");
        cbg2.setItems("Foo1", "Foo2", "Foo3");
        Button button = new Button("focus second group", e -> cbg2.focus());
        addComponents(cbg, cbg2, button);
    }

}
