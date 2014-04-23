package com.vaadin.tests.components;

import java.util.Iterator;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class DisableEnableCascadeStyles extends TestBase {

    private Panel outerPanel;
    private TabSheet innerTabsheet;
    private Button button;
    private TextArea textArea;
    private TextField textField;
    private VerticalLayout layout;
    private NativeButton nativeButton;
    private Button enableDisablePanelButton;
    private Button enableDisableTabSheetButton;
    private Button enableDisableLayoutButton;
    private Button enableDisableComponentsButton;

    @Override
    protected void setup() {

        outerPanel = new Panel("Outer panel, enabled");
        innerTabsheet = new TabSheet();
        innerTabsheet.setCaption("Inner Tabsheet, enabled");

        button = new Button("Button, enabled");
        nativeButton = new NativeButton("NativeButton, enabled");
        textField = new TextField("TextField with caption and value, enabled");
        textField.setValue("Text");
        textArea = new TextArea("TextArea with caption and value, enabled");
        textArea.setValue("Text");
        layout = new VerticalLayout();
        layout.setCaption("VerticalLayout, enabled");
        layout.addComponent(button);
        layout.addComponent(nativeButton);
        layout.addComponent(textField);
        layout.addComponent(textArea);

        outerPanel.setContent(innerTabsheet);
        innerTabsheet.addTab(layout, "Tab containing layout");

        addComponent(outerPanel);

        enableDisablePanelButton = new Button("Disable panel",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        enableDisable(outerPanel, enableDisablePanelButton);

                    }
                });

        enableDisableTabSheetButton = new Button("Disable TabSheet",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        enableDisable(innerTabsheet,
                                enableDisableTabSheetButton);

                    }
                });

        enableDisableLayoutButton = new Button("Disable Tab content (Layout)",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        enableDisable(layout, enableDisableLayoutButton);

                    }
                });
        enableDisableComponentsButton = new Button("Disable Layout Components",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        for (Iterator<Component> i = layout
                                .getComponentIterator(); i.hasNext();) {
                            final Component c = i.next();
                            if (c.isEnabled()) {
                                c.setEnabled(false);
                                c.setCaption(c.getCaption().replace("enabled",
                                        "disabled"));
                            } else {
                                c.setEnabled(true);
                                c.setCaption(c.getCaption().replace("disabled",
                                        "enabled"));
                            }
                        }
                        if (layout.getComponent(0).isEnabled()) {
                            enableDisableComponentsButton
                                    .setCaption(enableDisableComponentsButton
                                            .getCaption().replace("Enable",
                                                    "Disable"));
                        } else {
                            enableDisableComponentsButton
                                    .setCaption(enableDisableComponentsButton
                                            .getCaption().replace("Disable",
                                                    "Enable"));
                        }
                    }
                });
        addComponent(enableDisablePanelButton);
        addComponent(enableDisableTabSheetButton);
        addComponent(enableDisableLayoutButton);
        addComponent(enableDisableComponentsButton);
    }

    protected void enableDisable(Component target, Button button) {
        if (target.isEnabled()) {
            target.setEnabled(false);
            button.setCaption(button.getCaption().replace("Disable", "Enable"));
            target.setCaption(target.getCaption()
                    .replace("enabled", "disabled"));
        } else {
            target.setEnabled(true);
            button.setCaption(button.getCaption().replace("Enable", "Disable"));
            target.setCaption(target.getCaption()
                    .replace("disabled", "enabled"));
        }
    }

    @Override
    protected String getDescription() {
        return "Tests the disable state is cascaded correctly to children components that can be disabled. The children and their captions must get the v-disabled style name.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8708;
    }

}
