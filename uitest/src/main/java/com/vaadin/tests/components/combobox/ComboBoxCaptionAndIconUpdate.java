package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ClassResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxCaptionAndIconUpdate extends AbstractTestUI {

    public static class Commit {
        private final long id;
        private String message;
        private ClassResource icon;

        Commit(long id, String message, ClassResource icon) {
            this.id = id;
            this.message = message;
            this.icon = icon;
        }

        public long getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ClassResource getIcon() {
            return icon;
        }

        public void setIcon(ClassResource icon) {
            this.icon = icon;
        }
    }

    List<Commit> backend = new ArrayList<>();

    private final ClassResource M_RESOURCE = new ClassResource(
            "/com/vaadin/tests/m.gif");
    private final ClassResource FI_RESOURCE = new ClassResource(
            "/com/vaadin/tests/integration/fi.gif");

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<Commit> comboBox = new ComboBox<Commit>();

        backend = Stream.of(1, 2)
                .map(id -> new Commit(id, "Commit ID " + id, M_RESOURCE))
                .collect(Collectors.toList());
        comboBox.setItems(backend);
        comboBox.setValue(backend.get(0));

        comboBox.setItemIconGenerator(i -> FI_RESOURCE);
        comboBox.setItemCaptionGenerator(i -> "Commit " + i.getId());
        comboBox.setWidth("300px");

        addComponent(comboBox);
        addComponent(createButton("Set Icon Generator", "icon",
                e -> comboBox.setItemIconGenerator(Commit::getIcon)));
        addComponent(createButton("Set Caption Generator", "caption",
                e -> comboBox.setItemCaptionGenerator(Commit::getMessage)));
        addComponent(createButton("Edit Message", "editMsg", e -> {
            Commit item = backend.get(0);
            item.setMessage("Edited message");
            comboBox.getDataProvider().refreshItem(item);
        }));
        addComponent(createButton("Edit Icon", "editIcon", e -> {
            Commit item = backend.get(0);
            item.setIcon(FI_RESOURCE);
            comboBox.getDataProvider().refreshItem(item);
        }));
        addComponent(createButton("Edit Message and Icon", "editAll", e -> {
            Commit item = backend.get(0);
            item.setMessage("Edited message and icon");
            item.setIcon(FI_RESOURCE);
            comboBox.getDataProvider().refreshItem(item);
        }));
    }

    private Button createButton(String caption, String id,
            ClickListener listener) {
        Button button = new Button(caption, listener);
        button.setId(id);
        return button;
    }

}
