package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket6002 extends TestBase {

    @Override
    public void setup() {
        LegacyWindow main = getMainWindow();

        final VerticalLayout mainLayout = new VerticalLayout();
        main.setContent(mainLayout);

        mainLayout.addComponent(new Label(
                "Replace the floating-point values with an integer"));

        // ///////////////////////////////////////////////////
        // Better working case

        final ObjectProperty<Double> property1 = new ObjectProperty<Double>(
                new Double(42.0));

        // A text field that changes its caption
        final TextField tf1 = new TextField(
                "Changing this field modifies only the textfield", property1);
        tf1.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                // This value change event is called twice if the new
                // input value is an integer. The second time is during
                // paint() of AbstractOrderedLayout.

                System.out.println("Value 2 is: " + property1.getValue());

                tf1.setCaption("With caption " + property1.getValue());
            }
        });
        tf1.setImmediate(true);
        mainLayout.addComponent(tf1);

        // ///////////////////////////////////////////////////
        // Totally failing case

        final ObjectProperty<Double> property2 = new ObjectProperty<Double>(
                new Double(42.0));

        // A text field that adds new components
        final TextField tf2 = new TextField(
                "Changing this field modifies the layout - do it twice",
                property2);
        tf2.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                // This value change event is called twice if the new
                // input value is an integer. The second time is during
                // paint() of AbstractOrderedLayout.

                System.out.println("Value 1 is: " + property2.getValue());

                // When this listener is called the second time in paint(), the
                // add operation causes a ConcurrentModificationException
                mainLayout.addComponent(new Label(
                        "Added a component, value is " + property2.getValue()));
            }
        });
        tf2.setImmediate(true);
        mainLayout.addComponent(tf2);

        mainLayout.setSpacing(true);
    }

    @Override
    protected String getDescription() {
        return "Change the numbers to integer value or add 0 in the decimal representation. "
                + "This causes a secondary call during paint() to reformat the value, which causes ConcurrentModificationException in the second case.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6002;
    }
}
