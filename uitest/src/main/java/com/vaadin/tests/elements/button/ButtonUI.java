package com.vaadin.tests.elements.button;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 */
@SuppressWarnings("serial")
public class ButtonUI extends AbstractTestUI {

    public static final String TEXT_FIELD_ID = "testTextfield";
    public static final String LABEL_ID = "testLabel";
    public static String QUIET_BUTTON_ID = "quietButton";
    public static String QUIET_BUTTON_NO_CAPTION_ID = "quietButton2";
    public static String NORMAL_BUTTON_ID = "normalButton";

    final TextField testedField = new TextField();
    final Label testedLabel = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        testedField.setId(TEXT_FIELD_ID);
        addComponent(testedField);
        testedLabel.setId(LABEL_ID);
        addComponent(testedLabel);

        testedField.setValue("");

        Button quietButton = new Button("Quiet Button");
        quietButton.setId(QUIET_BUTTON_ID);
        quietButton.addStyleName(ValoTheme.BUTTON_QUIET);
        addListener(quietButton, "Clicked");

        Button quietButtonNoCaption = new Button("");
        quietButtonNoCaption.setId(QUIET_BUTTON_NO_CAPTION_ID);
        quietButtonNoCaption.addStyleName(ValoTheme.BUTTON_QUIET);
        quietButtonNoCaption.setIcon(FontAwesome.ANDROID);
        addListener(quietButtonNoCaption, "Clicked");

        addComponent(quietButton);
        addComponent(quietButtonNoCaption);
        addComponent(addButtonWithDelay());

    }

    private Button addButtonWithDelay() {
        Button btn = new Button();
        btn.setId(NORMAL_BUTTON_ID);
        btn.addClickListener(event -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            testedField.setValue("Clicked");
            testedLabel.setValue("Clicked");
        });
        return btn;
    }

    private void addListener(Button button, final String clickEventText) {
        button.addClickListener(event -> testedField.setValue(clickEventText));
    }

    @Override
    protected String getTestDescription() {
        return "Test button click, for button with ValoTheme.BUTTON_QUIET style";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16346;
    }

}
