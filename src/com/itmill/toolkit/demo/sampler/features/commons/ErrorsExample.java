package com.itmill.toolkit.demo.sampler.features.commons;

import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;

public class ErrorsExample extends VerticalLayout {

    public ErrorsExample() {
        setSpacing(true);

        Panel panel = new Panel("Configure this");
        panel.setComponentError(new UserError("This panel contains errors"));
        addComponent(panel);

        TextField input = new TextField("Input");
        input
                .setComponentError(new UserError(
                        "This field is never satisfied."));
        panel.addComponent(input);

    }
}
