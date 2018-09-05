package com.vaadin.tests.components.textfield;

import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextField;

/**
 * To verify bug fix: Reproducing of bug Textfield value not updated when
 * InputPromt and ShortcutListener are used
 *
 * #13492
 *
 * @author Vaadin Ltd
 */
@Theme("reindeer")
public class InputPromptGetText extends AbstractReindeerTestUI {

    static final String FIELD = "field";
    static final String BUTTON = "button";
    static final String LABEL1 = "label1";
    static final String LABEL2 = "label2";

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        final TextField tf = new TextField();
        tf.setId(FIELD);
        tf.setInputPrompt("input text here");
        tf.setImmediate(true);
        tf.setNullRepresentation("");

        Button button = new Button("Click Me");
        button.setId(BUTTON);
        button.addClickListener(event -> {
            String input = tf.getValue();
            Label label = new Label("Your input was: " + input);
            label.setId(LABEL2);
            getLayout().addComponent(label);
        });
        tf.addShortcutListener(
                new ShortcutListener("Shortcut", KeyCode.ENTER, null) {

                    @Override
                    public void handleAction(Object sender, Object target) {
                        String input = tf.getValue();
                        Label label = new Label("Your input was: " + input);
                        label.setId(LABEL1);
                        getLayout().addComponent(label);
                    }
                });

        getLayout().addComponent(tf);
        getLayout().addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Reproducing of bug Textfield value not updated when InputPromt and ShortcutListener are used";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13492;
    }

}
