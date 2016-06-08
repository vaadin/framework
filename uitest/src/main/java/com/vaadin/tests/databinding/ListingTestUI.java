package com.vaadin.tests.databinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.data.typed.AbstractDataSource;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.components.nativeselect.NativeSelect;

@Theme("valo")
public class ListingTestUI extends AbstractTestUI {

    Random r = new Random();

    @Override
    protected void setup(VaadinRequest request) {
        final List<String> options = createOptions();
        NativeSelect<String> select = new NativeSelect<>(
                new AbstractDataSource<String>() {

                    @Override
                    public void save(String data) {
                    }

                    @Override
                    public void remove(String data) {
                    }

                    @Override
                    public Iterator<String> iterator() {
                        return options.iterator();
                    }
                });
        addComponent(select);

        addComponent(new Button("Notify", e -> select.getSelectionModel()
                .getSelected().forEach(s -> Notification.show(s))));
        addComponent(new Button("Random select", e -> {
            String value = options.get(r.nextInt(options.size()));
            select.getSelectionModel().select(value);
        }));
    }

    private List<String> createOptions() {
        List<String> options = new ArrayList<>();
        Stream.of(1, 2, 3, 4, 5).map(i -> "Option " + i).forEach(options::add);
        return options;
    }

}
