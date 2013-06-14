package com.vaadin.tests.components.window;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.window.WindowState.WindowRole;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class ExtraWindowShownWaiAria extends TestBase {

    @Override
    protected void setup() {
        final CheckBox modal = new CheckBox("Modal dialog");
        final CheckBox additionalDescription = new CheckBox(
                "Additional Description");
        final TextField prefix = new TextField("Prefix: ");
        final TextField postfix = new TextField("Postfix: ");

        Button simple = new Button("Open Alert Dialog",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        CssLayout layout = new CssLayout();

                        final Window w = new Window("Sub window", layout);
                        w.center();
                        w.setModal(modal.getValue());
                        w.setAssistiveRole(WindowRole.ALERTDIALOG);
                        w.setAssistivePrefix(prefix.getValue());
                        w.setAssistivePostfix(postfix.getValue());

                        Label description1 = new Label("Simple alert dialog.");
                        layout.addComponent(description1);

                        if (!additionalDescription.getValue()) {
                            w.setAssistiveDescription(description1);
                        } else {
                            Label description2 = new Label(
                                    "Please select what to do!");
                            layout.addComponent(description2);

                            w.setAssistiveDescription(description1,
                                    description2);
                        }

                        layout.addComponent(new Button("Close",
                                new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(ClickEvent event) {
                                        w.close();
                                    }
                                }));
                        Button iconButton = new Button("A button with icon");
                        iconButton.setIcon(new ThemeResource(
                                "../runo/icons/16/ok.png"));
                        layout.addComponent(iconButton);

                        event.getButton().getUI().addWindow(w);
                        iconButton.focus();
                    }

                });
        getLayout().addComponent(simple);

        Button complex = new Button("Open Entry Dialog",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        FormLayout form = new FormLayout();

                        final Window w = new Window("Form Window", form);
                        w.center();
                        w.setModal(modal.getValue());
                        w.setAssistivePrefix(prefix.getValue());
                        w.setAssistivePostfix(postfix.getValue());

                        Label description1 = new Label(
                                "Please fill in your data");
                        form.addComponent(description1);

                        if (!additionalDescription.getValue()) {
                            w.setAssistiveDescription(description1);
                        } else {
                            Label description2 = new Label(
                                    "and press the button save.");
                            form.addComponent(description2);

                            w.setAssistiveDescription(description1,
                                    description2);
                        }

                        TextField name = new TextField("Name:");
                        form.addComponent(name);

                        form.addComponent(new TextField("Address"));

                        Button saveButton = new Button("Save",
                                new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(ClickEvent event) {
                                        w.close();
                                    }
                                });
                        form.addComponent(saveButton);

                        event.getButton().getUI().addWindow(w);
                        name.focus();
                    }
                });
        getLayout().addComponent(complex);

        getLayout().addComponent(modal);
        getLayout().addComponent(additionalDescription);

        getLayout().addComponent(prefix);
        getLayout().addComponent(postfix);

    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
