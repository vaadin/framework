package com.vaadin.tests.components.grid;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@Push
public class GridFastAsyncUpdate extends AbstractTestUI {

    private final Runnable addRowsTask = new Runnable() {
        @Override
        public void run() {
            System.out.println("Logging...");
            try {
                Random random = new Random();
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(random.nextInt(100));

                    GridFastAsyncUpdate.this.access(() -> {
                        ++counter;
                        Item item = new Item(counter,
                                (Calendar.getInstance().getTimeInMillis()
                                        - loggingStart),
                                Level.INFO.toString(), "Message");
                        items.add(item);
                        grid.setItems(items);

                        if (grid != null && !scrollLock) {
                            grid.scrollToEnd();
                        }
                    });
                }
            } catch (InterruptedException e) {
                System.out.println("logging thread interrupted");
            }
        }
    };

    private int counter;
    private List<Item> items = new LinkedList<>();

    private Grid<Item> grid;
    private long loggingStart;
    private volatile boolean scrollLock = false;

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        addComponent(layout);

        HorizontalLayout buttons = new HorizontalLayout();
        layout.addComponent(buttons);

        final ExecutorService logExecutor = Executors.newSingleThreadExecutor();

        final Button logButton = new Button("Start logging");
        logButton.addClickListener(clickEvent -> {
            if ("Start logging".equals(logButton.getCaption())) {
                loggingStart = Calendar.getInstance().getTimeInMillis();
                logExecutor.submit(addRowsTask);
                logButton.setCaption("Stop logging");
            } else {
                System.out.println("Stop logging...");
                try {
                    logExecutor.shutdownNow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logButton.setCaption("Start logging");
            }
        });
        buttons.addComponent(logButton);

        final Button scrollButton = new Button("Stop scrolling");
        scrollButton.addClickListener(clickEvent -> {
            if (!scrollLock) {
                System.out.println("Stop scrolling");
                scrollButton.setCaption("Start scrolling");
                scrollLock = true;
            } else {
                System.out.println("Start scrolling");
                scrollButton.setCaption("Stop scrolling");
                scrollLock = false;
            }
        });
        buttons.addComponent(scrollButton);

        grid = new Grid<>();
        grid.addColumn(Item::getSequenceNumber).setCaption("");
        grid.addColumn(Item::getMillis).setCaption("");
        grid.addColumn(Item::getLevel).setCaption("");
        grid.addColumn(Item::getMessage).setCaption("");

        grid.setWidth("100%");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(selectionEvent -> {
            if (!selectionEvent.getAllSelectedItems().isEmpty()) {
                disableScroll();
            }
        });

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1.0f);
    }

    protected void disableScroll() {
        scrollLock = true;
    }

    protected class Item {
        Integer sequenceNumber;
        Long millis;
        String level, message;

        public Item(Integer sequanceNumber, Long millis, String level,
                String message) {
            this.sequenceNumber = sequanceNumber;
            this.millis = millis;
            this.level = level;
            this.message = message;
        }

        public Integer getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(Integer sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public Long getMillis() {
            return millis;
        }

        public void setMillis(Long millis) {
            this.millis = millis;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
