package com.vaadin.tests.components;

import java.util.Arrays;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
public class ErrorLevels extends AbstractTestUI {

    private ComboBox errorLevels;
    private Button button;
    private Button borderlessButton;
    private Link link;
    private ComboBox comboBox;
    private TextField textField;
    private TextField textFieldBorderless;
    private TabSheet tabSheet;
    private Accordion accordion;
    private CheckBox checkBox;
    private NativeButton nativeButton;
    private FormLayout formLayout;
    private TextField formLayoutTextField;
    private Panel panel;
    private DateField dateField;

    @Override
    protected void setup(VaadinRequest request) {

        errorLevels = new ComboBox("Error level",
                Arrays.asList(ErrorMessage.ErrorLevel.values()));
        errorLevels.setNullSelectionAllowed(false);
        errorLevels.setValue(ErrorMessage.ErrorLevel.ERROR);
        errorLevels.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                setErrorMessages();
            }
        });
        addComponent(errorLevels);

        Label subtitle = new Label("Components");
        subtitle.setStyleName(ValoTheme.LABEL_H3);
        addComponent(subtitle);

        // Button
        button = new Button("Button");

        borderlessButton = new Button("Borderless button");
        borderlessButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);

        addComponent(new HorizontalLayout(button, borderlessButton));

        // Native button
        nativeButton = new NativeButton("Native button");
        addComponent(nativeButton);

        // Link
        link = new Link("Link", new ExternalResource("#"));
        addComponent(link);

        // Combo box
        comboBox = new ComboBox("Combo box");
        addComponent(comboBox);

        // Text field
        textField = new TextField("Text field");
        textField.setValue("text");

        textFieldBorderless = new TextField("Borderless text field");
        textFieldBorderless.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        textFieldBorderless.setValue("text");

        addComponent(new HorizontalLayout(textField, textFieldBorderless));

        // Date field
        dateField = new DateField("Date field");
        addComponent(dateField);

        // Check box
        checkBox = new CheckBox("Check box");
        addComponent(checkBox);

        // Tab sheet
        tabSheet = new TabSheet();
        tabSheet.addTab(new Label("Label1"), "Tab1");
        tabSheet.addTab(new Label("Label2"), "Tab2");
        tabSheet.setWidth("400px");
        addComponent(tabSheet);

        // Accordion
        accordion = new Accordion();
        accordion.addTab(new Label("Label1"), "Tab1");
        accordion.addTab(new Label("Label2"), "Tab2");
        accordion.setWidth("400px");
        addComponent(accordion);

        // Form layout
        formLayout = new FormLayout();
        formLayout.setWidth("400px");

        formLayoutTextField = new TextField("Form layout text field");
        formLayout.addComponent(formLayoutTextField);

        addComponent(formLayout);

        // Panel
        panel = new Panel();
        panel.setContent(new Label("Panel"));
        panel.setWidth("400px");
        addComponent(panel);

        setErrorMessages();

        getLayout().setSpacing(true);
    }

    private void setErrorMessages() {
        button.setComponentError(createErrorMessage("Button error"));
        borderlessButton.setComponentError(
                createErrorMessage("Borderless button error"));
        link.setComponentError(createErrorMessage("Link error"));
        comboBox.setComponentError(createErrorMessage("ComboBox error"));
        textField.setComponentError(createErrorMessage("Text field error"));
        textFieldBorderless.setComponentError(
                createErrorMessage("Borderless text field error"));
        tabSheet.setComponentError(createErrorMessage("Tab sheet error"));
        tabSheet.getTab(0).setComponentError(createErrorMessage("Tab error"));
        accordion.setComponentError(createErrorMessage("Accordion error"));
        accordion.getTab(0).setComponentError(createErrorMessage("Tab error"));
        checkBox.setComponentError(createErrorMessage("Check box error"));
        nativeButton
                .setComponentError(createErrorMessage("Native button error"));
        formLayout.setComponentError(createErrorMessage("Form layout error"));
        formLayoutTextField.setComponentError(
                createErrorMessage("Form layout text field error"));
        panel.setComponentError(createErrorMessage("Panel error"));
        dateField.setComponentError(createErrorMessage("Date field error"));
    }

    private ErrorMessage createErrorMessage(String text) {
        return new UserError(text, AbstractErrorMessage.ContentMode.TEXT,
                (ErrorMessage.ErrorLevel) errorLevels.getValue());
    }
}
