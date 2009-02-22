package com.itmill.toolkit.demo;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

// Calculator is created by extending Application-class. Application is
// deployed by adding ApplicationServlet to web.xml and this class as
// "application" parameter to the servlet.
public class Calc extends com.itmill.toolkit.Application {

    // Calculation data model is automatically stored in the user session
    private double current = 0.0;
    private double stored = 0.0;
    private char lastOperationRequested = 'C';

    // User interface components
    private final Label display = new Label("0.0");
    private final GridLayout layout = new GridLayout(4, 5);

    // Application initialization creates UI and connects it to business logic
    @Override
    public void init() {

        // Place the layout to the browser main window
        setMainWindow(new Window("Calculator Application", layout));

        // Create and add the components to the layout
        layout.addComponent(display, 0, 0, 3, 0);
        for (String caption : new String[] { "7", "8", "9", "/", "4", "5", "6",
                "*", "1", "2", "3", "-", "0", "=", "C", "+" }) {
            Button button = new Button(caption, new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    // On button click, calculate and show the result
                    display.setValue(calculate(event.getButton()));
                }
            });
            layout.addComponent(button);
        }
    }

    // Calculator "business logic" implemented here to keep the example minimal
    private double calculate(Button buttonClicked) {
        char requestedOperation = buttonClicked.getCaption().charAt(0);
        if ('0' <= requestedOperation && requestedOperation <= '9') {
            current = current * 10
                    + Double.parseDouble("" + requestedOperation);
            return current;
        }
        switch (lastOperationRequested) {
        case '+':
            stored += current;
            break;
        case '-':
            stored -= current;
            break;
        case '/':
            stored /= current;
            break;
        case '*':
            stored *= current;
            break;
        case 'C':
            stored = current;
            break;
        }
        lastOperationRequested = requestedOperation;
        current = 0.0;
        if (requestedOperation == 'C') {
            stored = 0.0;
        }
        return stored;
    }
}
