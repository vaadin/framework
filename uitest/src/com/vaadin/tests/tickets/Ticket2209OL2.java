package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket2209OL2 extends LegacyApplication {

    private VerticalLayout gl;
    private ComboBox combo;
    private Label labelLong;

    @Override
    public void init() {
        setMainWindow(new LegacyWindow());
        getMainWindow().getContent().setWidth("250px");
        gl = new VerticalLayout();
        gl.setSizeUndefined();
        gl.setStyleName("borders");
        getMainWindow().addComponent(gl);
        setTheme("tests-tickets");
        combo = new ComboBox("Combo caption");
        labelLong = new Label(
                "This should stay on one line or to wrap to multiple lines? At leas it should display all the text?. "
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?"
                        + "A long label, longer than the combo box. Why doesn't it affect the width? And why is the gridlayout so high?");
        gl.addComponent(combo);
        gl.addComponent(labelLong);

        Button b = new Button("Add label text", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                labelLong.setValue(labelLong.getValue() + "-12345");
            }

        });
        getMainWindow().addComponent(b);
    }

}
