package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxScrollToSelectedItem extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<Item> items = new ArrayList<>();

        LongStream.range(1, 100)
                .forEach(l -> items.add(new Item(l, "item:" + l)));
        Item selectedItem = new Item(50l, "SHOW ME");
        items.set(50, selectedItem);

        ComboBox<Item> box = new ComboBox<>("items", items);
        box.setItemCaptionGenerator(Item::getName);
        box.setScrollToSelectedItem(true);
        box.setValue(selectedItem);

        addComponent(box);
    }

    public class Item {
        private Long id;
        private String name;

        public Item(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
