package com.vaadin.tests.actions;

import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

/**
 * @author Vaadin Ltd
 */
public class ActionsOnDetachedComponents extends AbstractTestUIWithLog {

    private final AtomicInteger clickCounter = new AtomicInteger();
    private Panel mainLayout;

    @Override
    protected void setup(VaadinRequest request) {
        clickCounter.set(0);
        mainLayout = new Panel();
        mainLayout.setSizeFull();
        mainLayout.setContent(new ShortCutALayer());
        addComponent(mainLayout);

    }

    private Table tableWithActions(final LayerSwitcher switcher) {
        Table table = new Table();
        table.addContainerProperty("id", Integer.class, 0);
        table.addItems(1, 2, 3, 4);
        table.addActionHandler(new Action.Handler() {

            Action action = new Action("Table action");

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { action };
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                if (action == this.action) {
                    log("Fired action for tableAction");
                    switcher.switchLayers();
                }
            }
        });
        return table;
    }

    private interface LayerSwitcher {
        void switchLayers();
    }

    private class ShortCutALayer extends VerticalLayout
            implements LayerSwitcher {
        public ShortCutALayer() {
            setId("layer-A");
            Label l = new Label(getClass().getSimpleName());
            Button b = new Button("click here or press 'a'");
            b.setId("btn-A");
            b.setClickShortcut(ShortcutAction.KeyCode.A);
            b.addClickListener(event -> {
                log("Fired action for btn-A");
                switchLayers();
            });
            addComponents(l, b);
            Table table = tableWithActions(this);
            addComponent(table);
        }

        @Override
        public void switchLayers() {
            try {
                Thread.sleep(1000); // do something important

            } catch (InterruptedException e) {
            }
            mainLayout.setContent(new ShortCutBLayer());
        }
    }

    private class ShortCutBLayer extends VerticalLayout
            implements LayerSwitcher {
        public ShortCutBLayer() {
            setId("layer-B");
            Label l = new Label(getClass().getSimpleName());
            Button b = new Button("click here or press 'b'");
            b.setId("btn-B");
            b.setClickShortcut(ShortcutAction.KeyCode.B);
            b.addClickListener(event -> {
                log("Fired action for btn-B");
                switchLayers();
            });
            addComponents(l, b);
            Table table = tableWithActions(this);
            addComponent(table);
        }

        @Override
        public void switchLayers() {
            try {
                Thread.sleep(1000); // do something important
            } catch (InterruptedException e) {
            }
            mainLayout.setContent(new ShortCutALayer());
        }
    }
}
