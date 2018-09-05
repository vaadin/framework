package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxPopupWhenBodyScrolls extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles()
                .add("body.v-generated-body { overflow: auto;height:auto;}");
        getPage().getStyles().add(
                "body.v-generated-body .v-ui.v-scrollable{ overflow: visible;height:auto !important;}");
        ComboBox<String> cb = new ComboBox<>();
        cb.setDataProvider(new ItemDataProvider(10));

        Label spacer = new Label("foo");
        spacer.setHeight("2000px");
        addComponent(spacer);
        addComponent(cb);
        spacer = new Label("foo");
        spacer.setHeight("2000px");
        addComponent(spacer);
        // Chrome requires document.scrollTop (<body>)
        // Firefox + IE wants document.documentElement.scrollTop (<html>)
        getPage().getJavaScript().execute(
                "document.body.scrollTop=1800;document.documentElement.scrollTop=1800;");
    }
}
