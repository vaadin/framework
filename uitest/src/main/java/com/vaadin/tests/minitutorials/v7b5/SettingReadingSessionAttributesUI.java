package com.vaadin.tests.minitutorials.v7b5;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

//Remove comment to preserve UI value when reloading
//@PreserveOnRefresh
public class SettingReadingSessionAttributesUI extends UI {

    private String value;

    private VerticalLayout statusHolder = new VerticalLayout();
    private TextField textField = new TextField();

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(statusHolder);
        layout.addComponent(textField);
        layout.addComponent(
                new Button("Set new values", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        String value = textField.getValue();

                        saveValue(SettingReadingSessionAttributesUI.this,
                                value);
                    }
                }));
        layout.addComponent(
                new Button("Reload page", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getPage().setLocation(getPage().getLocation());
                    }
                }));

        showValue(this);
    }

    private static void saveValue(SettingReadingSessionAttributesUI ui,
            String value) {
        // Save to UI instance
        ui.value = value;
        // Save to VaadinServiceSession
        ui.getSession().setAttribute("myValue", value);
        // Save to HttpSession
        VaadinService.getCurrentRequest().getWrappedSession()
                .setAttribute("myValue", value);

        // Show new values
        showValue(ui);
    }

    private static void showValue(SettingReadingSessionAttributesUI ui) {
        ui.statusHolder.removeAllComponents();
        ui.statusHolder.addComponent(new Label("Value in UI: " + ui.value));
        ui.statusHolder.addComponent(new Label("Value in VaadinServiceSession: "
                + ui.getSession().getAttribute("myValue")));
        ui.statusHolder.addComponent(new Label(
                "Value in HttpSession: " + VaadinService.getCurrentRequest()
                        .getWrappedSession().getAttribute("myValue")));
    }

}
