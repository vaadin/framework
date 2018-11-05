package com.vaadin.tests;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * This Component contains some simple test to see that component updates its
 * contents properly.
 *
 * @author Vaadin Ltd.
 */
public class OrderedLayoutSwapComponents extends CustomComponent {

    private final AbstractOrderedLayout main;

    List<MyComponent> order = new ArrayList<>();

    public OrderedLayoutSwapComponents() {

        main = new VerticalLayout();
        // main.setSizeFull();
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();

        for (int i = 0; i < 10; i++) {
            MyComponent c = new MyComponent("Component " + i);
            main.addComponent(c);
            order.add(c);
        }
        setCompositionRoot(main);

    }

    class MyComponent extends CustomComponent {

        private static final int FIRST = 0;
        private static final int LAST = 1;
        private Button up;
        private Button down;

        MyComponent(String name) {
            HorizontalLayout ol = new HorizontalLayout();
            ol.setId(name.replaceAll(" ", ""));
            ol.addComponent(new Label(name));
            up = new Button("up");
            up.addClickListener(event -> {
                int newIndex = order.indexOf(this) - 1;
                MyComponent old = order.get(newIndex);
                main.replaceComponent(old, this);
                order.remove(this);
                order.add(newIndex, this);
                if (newIndex == 0) {
                    setMode(FIRST);
                } else {
                    setMode(69);
                }
            });
            ol.addComponent(up);

            down = new Button("down");
            down.addClickListener(event -> {
                int newIndex = order.indexOf(MyComponent.this) + 1;
                MyComponent old = order.get(newIndex);
                main.replaceComponent(old, MyComponent.this);
                order.remove(MyComponent.this);
                order.add(newIndex, MyComponent.this);
                if (newIndex == order.size() - 1) {
                    MyComponent.this.setMode(LAST);
                } else {
                    MyComponent.this.setMode(69);
                }
            });
            ol.addComponent(down);

            setCompositionRoot(ol);

        }

        public void setMode(int mode) {
            up.setEnabled(true);
            down.setEnabled(true);
            if (mode == FIRST) {
                up.setEnabled(false);
            } else if (mode == LAST) {
                down.setEnabled(false);
            }
        }
    }

}
