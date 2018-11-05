package com.vaadin.tests.components.nativeselect;

import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeSelect;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectEmptySelection extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<String> select = new NativeSelect<>();
        select.setItems(IntStream.range(1, 50)
                .mapToObj(index -> String.valueOf(index)));
        select.setEmptySelectionCaption("empty");
        addComponent(select);

        Button update = new Button("Update Empty Caption to 'updated'",
                event -> select.setEmptySelectionCaption("updated"));

        Button disallow = new Button("Disallow empty selection item",
                event -> select.setEmptySelectionAllowed(false));

        Button enable = new Button("Allow empty selection item",
                event -> select.setEmptySelectionAllowed(true));
        addComponents(update, disallow, enable);
    }

}
