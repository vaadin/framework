package com.vaadin.tests.components.nativeselect;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;

public class NativeSelectWidth extends AbstractTestUI {
    public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
            + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, "
            + "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor "
            + "in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat "
            + "cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<String> nativeSelect = new NativeSelect<>("Select:",
                Arrays.asList("Short item1", "Short item2", LOREM_IPSUM));
        nativeSelect.setValue(LOREM_IPSUM);
        nativeSelect.setWidth("200px");
        nativeSelect.setHeight("120px");

        NativeSelect<String> nativeSelect2 = new NativeSelect<>("Select:",
                Arrays.asList("Short 1", "Short 2", "A bit longer"));
        nativeSelect2.setSizeUndefined();

        TextArea placeholder = new TextArea("Placeholder",
                nativeSelect.getClass().getName());
        placeholder.setReadOnly(true);
        placeholder.setSizeFull();
        HorizontalLayout horizontalLayout = new HorizontalLayout(nativeSelect,
                nativeSelect2, placeholder);
        horizontalLayout.setWidth("500px");
        horizontalLayout.setHeight("500px");
        addComponent(horizontalLayout);
    }
}
