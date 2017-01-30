package com.vaadin.tests.fonticon;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("tests-valo")
public class VaadinIconUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        TextField name = new TextField("Name");
        name.setIcon(VaadinIcons.USER);
        name.addStyleName("blueicon");
        layout.addComponent(name);

        // Button allows specifying icon resource in constructor
        Button ok = new Button("OK", VaadinIcons.CHECK);
        ok.addStyleName("blueicon");
        layout.addComponent(ok);

        setContent(layout);

        Label label = new Label("I " + VaadinIcons.HEART.getHtml() + " Vaadin",
                ContentMode.HTML);
        label.addStyleName("redicon");
        layout.addComponent(label);

        TextField amount = new TextField("Amount (in "
                + new String(
                        Character.toChars(VaadinIcons.DOLLAR.getCodepoint()))
                + ")");
        amount.addStyleName("amount");
        layout.addComponent(amount);
    }

}
