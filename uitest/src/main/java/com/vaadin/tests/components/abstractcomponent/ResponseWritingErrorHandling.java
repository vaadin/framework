package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

public class ResponseWritingErrorHandling extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        ErrorHandler errorHandler = event -> {
            String message = event.getThrowable().getMessage();
            log(message);
        };

        Button button = new Button("Throw in beforeClientResponse") {
            private boolean throwInBeforeClientResponse = false;
            {
                addClickListener(event -> {
                    throwInBeforeClientResponse = true;
                    // Make sure beforeClientResponse is called
                    markAsDirty();
                });
            }

            @Override
            public void beforeClientResponse(boolean initial) {
                if (throwInBeforeClientResponse) {
                    throwInBeforeClientResponse = false;
                    throw new RuntimeException("Button.beforeClientResponse");
                }
            }
        };
        button.setErrorHandler(errorHandler);

        addComponent(button);
    }

}
