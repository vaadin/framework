package com.vaadin.tests.push;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
public class MakeComponentVisibleWithPush extends AbstractTestUI {

    private VerticalLayout rootLayout;
    private Grid<Person> grid;
    private SearchThread searchThread;
    private List<Person> data;

    @Override
    protected void setup(VaadinRequest request) {
        data = new ArrayList<>();

        rootLayout = new VerticalLayout();
        setContent(rootLayout);

        grid = new Grid<Person>();
        grid.addColumn(Person::getName);
        grid.setVisible(false);
        rootLayout.addComponent(grid);

        Button doUpdateButton = new Button("Do Update", event -> {
            try {
                doUpdate();
            } catch (InterruptedException e) {
            }
        });

        rootLayout.addComponent(doUpdateButton);
    }

    private void doUpdate() throws InterruptedException {

        cancelSuggestThread();

        grid.setVisible(false);
        grid.setItems(data);

        UI ui = UI.getCurrent();
        searchThread = new SearchThread(ui);
        searchThread.start();

    }

    class SearchThread extends Thread {
        private UI ui;

        public SearchThread(UI ui) {
            this.ui = ui;
        }

        @Override
        public void run() {
            data = new ArrayList<Person>(data);
            data.add(new Person("Person " + (data.size() + 1)));

            if (!searchThread.isInterrupted()) {
                ui.access(() -> {
                    grid.setItems(data);
                    grid.setVisible(true);
                });
            }
        }

    }

    private void cancelSuggestThread() {

        if ((searchThread != null) && !searchThread.isInterrupted()) {
            searchThread.interrupt();
            searchThread = null;
        }
    }

    class Person {

        private String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
