package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.Select;

public class TestForWindowing extends CustomComponent {

    private Select s2;

    public TestForWindowing() {

        final VerticalLayout main = new VerticalLayout();

        main.addComponent(
                new Label("Click the button to create a new inline window."));

        final CheckBox asModal = new CheckBox("As modal");
        main.addComponent(asModal);

        final Button create = new Button("Create a new window", event -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            Window w = new Window("Testing Window", layout);

            if (asModal.getValue().booleanValue()) {
                w.setModal(true);
            }

            AbstractSelect s1 = new OptionGroup();
            s1.setCaption("1. Select output format");
            s1.addItem("Excel sheet");
            s1.addItem("CSV plain text");
            s1.setValue("Excel sheet");
            s1.setImmediate(true);

            s2 = new Select();
            s2.addItem("Separate by comma (,)");
            s2.addItem("Separate by colon (:)");
            s2.addItem("Separate by semicolon (;)");
            s2.setEnabled(false);

            s1.addValueChangeListener(valueChangeEvent -> {
                String v = (String) valueChangeEvent.getProperty().getValue();
                if (v.equals("CSV plain text")) {
                    s2.setEnabled(true);
                } else {
                    s2.setEnabled(false);
                }
            });

            layout.addComponent(s1);
            layout.addComponent(s2);

            Slider s = new Slider();
            s.setCaption("Volume");
            s.setMax(13);
            s.setMin(12);
            s.setResolution(2);
            // s.setOrientation(Slider.ORIENTATION_VERTICAL);
            // s.setArrows(false);

            layout.addComponent(s);

            UI.getCurrent().addWindow(w);
        });

        main.addComponent(create);

        setCompositionRoot(main);
    }

}
