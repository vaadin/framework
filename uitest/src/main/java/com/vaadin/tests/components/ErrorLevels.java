package com.vaadin.tests.components;

import java.util.Arrays;

import com.vaadin.annotations.Theme;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ErrorLevel;
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
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
public class ErrorLevels extends AbstractTestUI {

    private ComboBox<ErrorLevel> errorLevels;
    private Button button;
    private Button borderlessButton;
    private Link link;
    private ComboBox<String> comboBox;
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
    private TwinColSelect twinColSelect;

    private com.vaadin.v7.ui.ComboBox comboBoxCompat;
    private com.vaadin.v7.ui.TextField textFieldCompat;
    private com.vaadin.v7.ui.CheckBox checkBoxCompat;
    private com.vaadin.v7.ui.DateField dateFieldCompat;
    private com.vaadin.v7.ui.TwinColSelect twinColSelectCompat;

    @Override
    protected void setup(VaadinRequest request) {

        errorLevels = new ComboBox<>("Error level",
                Arrays.asList(ErrorLevel.values()));
        errorLevels.setEmptySelectionAllowed(false);
        errorLevels.setValue(ErrorLevel.ERROR);
        errorLevels.addValueChangeListener(event -> setErrorMessages());
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
        comboBox = new ComboBox<>("Combo box");
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

        // TwinColSelect
        twinColSelect = new TwinColSelect("Twin col select");
        addComponent(twinColSelect);

        Label subtitleCompat = new Label("Compatibility components");
        subtitleCompat.setStyleName(ValoTheme.LABEL_H3);
        addComponent(subtitleCompat);

        // Compatibility combo box
        comboBoxCompat = new com.vaadin.v7.ui.ComboBox(
                "Compatibility combo box");
        addComponent(comboBoxCompat);

        // Compatibility text field
        textFieldCompat = new com.vaadin.v7.ui.TextField(
                "Compatibility text field");
        textFieldCompat.setValue("text");

        // Compatibility check box
        checkBoxCompat = new com.vaadin.v7.ui.CheckBox("Check box");
        addComponent(checkBoxCompat);

        // Compatibility date field
        dateFieldCompat = new com.vaadin.v7.ui.DateField("Date field");
        addComponent(dateFieldCompat);

        // Compatibility twin col select
        twinColSelectCompat = new com.vaadin.v7.ui.TwinColSelect(
                "Twin col select");
        addComponent(twinColSelectCompat);

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
        twinColSelect
                .setComponentError(createErrorMessage("Twin col select error"));
        comboBoxCompat.setComponentError(
                createErrorMessage("Compatibility combo box error"));
        textFieldCompat.setComponentError(
                createErrorMessage("Compatibility text field error"));
        checkBoxCompat.setComponentError(
                createErrorMessage("Compatibility check box error"));
        dateFieldCompat.setComponentError(
                createErrorMessage("Compatibility date field error"));
        twinColSelectCompat.setComponentError(
                createErrorMessage("Compatibility twin col select error"));
    }

    private ErrorMessage createErrorMessage(String text) {
        return new UserError(text, AbstractErrorMessage.ContentMode.TEXT,
                errorLevels.getValue());
    }
}
