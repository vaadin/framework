package com.vaadin.tests.components.combobox;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.IntStream;

import com.vaadin.server.ClassResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.IconGenerator;

public class ComboBoxItemIcon extends TestBase {

    private IconGenerator<Integer> iconGenerator = i -> {
        switch (i % 3) {
        case 0:
            return new StreamResource(() -> getClass().getResourceAsStream(
                    "/com/vaadin/tests/integration/se.gif"), "se.gif");
        case 1:
            try {
                return new FileResource(Paths.get(getClass()
                        .getResource("/com/vaadin/tests/integration/fi.gif")
                        .toURI()).toFile());
            } catch (URISyntaxException e) {
                return null;
            }
        case 2:
            return new ClassResource("/com/vaadin/tests/m.gif");
        default:
            return null;
        }
    };

    @Override
    protected Integer getTicketNumber() {
        return 2455;
    }

    @Override
    protected String getDescription() {
        return "All items in the ComboBoxes should have icons.";
    }

    @Override
    protected void setup() {
        {
            ComboBox<String> cb = new ComboBox<>();
            cb.setItems("FI", "SE");
            cb.setItemIconGenerator(
                    item -> new ThemeResource("../tests-tickets/icons/"
                            + item.toLowerCase(Locale.ROOT) + ".gif"));

            addComponent(cb);
        }
        {
            ComboBox<String> cb = new ComboBox<>();
            cb.setItems("Finland", "Australia", "Hungary");
            cb.setItemIconGenerator(
                    item -> new ThemeResource("../tests-tickets/icons/"
                            + item.substring(0, 2).toLowerCase(Locale.ROOT)
                            + ".gif"));

            cb.setValue("Hungary");
            addComponent(cb);
        }
        {
            ComboBox<Integer> cb = new ComboBox<>();
            cb.setItems(IntStream.range(0, 3).boxed());
            cb.setItemIconGenerator(iconGenerator);

            // FIXME: Selecting ConnectorResource on init does not work.
            // cb.setValue(2);
            addComponent(cb);
        }
    }

}
