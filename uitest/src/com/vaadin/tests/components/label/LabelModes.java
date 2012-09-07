package com.vaadin.tests.components.label;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Label;

public class LabelModes extends ComponentTestCase<Label> {

    @Override
    protected Class<Label> getTestClass() {
        return Label.class;
    }

    @Override
    protected void initializeComponents() {

        Label l;
        l = createLabel("This is an undefined wide label with default content mode");
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nbut is in\ndefault content mode");
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nand is in\npreformatted mode");
        l.setContentMode(ContentMode.PREFORMATTED);
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nand is in\nhtml mode");
        l.setContentMode(ContentMode.HTML);
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel("This label                       contains\nnewlines and spaces\nand is in\nraw mode");
        l.setContentMode(ContentMode.RAW);
        l.setWidth(null);
        addTestComponent(l);

    }

    private Label createLabel(String text, String caption) {
        Label l = new Label(text);
        l.setCaption(caption);

        return l;
    }

    private Label createLabel(String text) {
        return createLabel(text, null);
    }

    @Override
    protected String getDescription() {
        return "A generic test for Labels in different configurations";
    }

}
