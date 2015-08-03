package com.vaadin.tests.components.grid;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@Push
@Theme("valo")
@SuppressWarnings("serial")
public class GridFastAsyncUpdate extends AbstractTestUI {

    private final Runnable addRowsTask = new Runnable() {
        @Override
        public void run() {
            System.out.println("Logging...");
            try {
                Random random = new Random();
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(random.nextInt(100));

                    GridFastAsyncUpdate.this.access(new Runnable() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void run() {

                            ++counter;
                            Item item = container.addItem(counter);
                            item.getItemProperty("sequenceNumber").setValue(
                                    String.valueOf(counter));
                            item.getItemProperty("millis").setValue(
                                    String.valueOf(Calendar.getInstance()
                                            .getTimeInMillis() - loggingStart));
                            item.getItemProperty("level").setValue(
                                    Level.INFO.toString());
                            item.getItemProperty("message").setValue("Message");
                            if (grid != null && !scrollLock) {
                                grid.scrollToEnd();
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
                System.out.println("logging thread interrupted");
            }
        }
    };

    private int counter;

    private Grid grid;
    private IndexedContainer container;
    private long loggingStart;
    private volatile boolean scrollLock = false;

    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        addComponent(layout);

        HorizontalLayout buttons = new HorizontalLayout();
        layout.addComponent(buttons);

        final ExecutorService logExecutor = Executors.newSingleThreadExecutor();

        final Button logButton = new Button("Start logging");
        logButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
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
            }
        });
        buttons.addComponent(logButton);

        final Button scrollButton = new Button("Stop scrolling");
        scrollButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (!scrollLock) {
                    System.out.println("Stop scrolling");
                    scrollButton.setCaption("Start scrolling");
                    scrollLock = true;
                } else {
                    System.out.println("Start scrolling");
                    scrollButton.setCaption("Stop scrolling");
                    scrollLock = false;
                }
            }
        });
        buttons.addComponent(scrollButton);

        container = new IndexedContainer();
        container.addContainerProperty("sequenceNumber", String.class, null);
        container.addContainerProperty("millis", String.class, null);
        container.addContainerProperty("level", String.class, null);
        container.addContainerProperty("message", String.class, null);

        grid = new Grid(container);
        grid.setWidth("100%");
        grid.setImmediate(true);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(final SelectionEvent event) {
                if (grid.getSelectedRow() != null) {
                    disableScroll();
                }
            }
        });

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1.0f);
    }

    protected void disableScroll() {
        scrollLock = true;
    }
}