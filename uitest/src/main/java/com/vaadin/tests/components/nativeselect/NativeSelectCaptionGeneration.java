package com.vaadin.tests.components.nativeselect;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

public class NativeSelectCaptionGeneration extends UI {

    @DesignRoot
    public static class TestComponent extends VerticalLayout {

        HorizontalLayout buttons;
        NativeSelect<String> nativeSelect;

        public TestComponent() {
            Design.read(this);

            // Store the declarative with to string fallback
            ItemCaptionGenerator<String> declarative = nativeSelect
                    .getItemCaptionGenerator();

            buttons.addComponents(
                    new Button("toString",
                            e -> nativeSelect
                                    .setItemCaptionGenerator(String::toString)),
                    new Button("Only number",
                            e -> nativeSelect.setItemCaptionGenerator(
                                    str -> str.substring(7))),
                    new Button("Declarative", e -> nativeSelect
                            .setItemCaptionGenerator(declarative)));
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        setContent(new TestComponent());
    }

}
