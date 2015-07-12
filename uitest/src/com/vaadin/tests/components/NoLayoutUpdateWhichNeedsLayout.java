package com.vaadin.tests.components;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
@Theme("valo")
public class NoLayoutUpdateWhichNeedsLayout extends UI {

    private ProgressBar progressBar;
    private Window w;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        setPollInterval(1000);

        Button button = new Button(
                "1. Execute scheduled task and show progress in a window");
        button.setId("openWindow");
        button.addClickListener(new Button.ClickListener() {

            private Window w2;

            @Override
            public void buttonClick(ClickEvent event) {
                GridLayout glo = new GridLayout();
                progressBar = new ProgressBar();
                progressBar.setIndeterminate(false);
                progressBar.setId("progress");
                glo.addComponent(progressBar);

                w2 = new Window("test");
                w2.setWidth("600px");
                w2.setHeight("200px");
                w2.setContent(glo);
                w2.center();
                Button closeB = new Button(
                        "2. Click to close after the progress is updated");
                closeB.setId("closeWindow");
                closeB.addClickListener(new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        w2.close();
                        w2 = null;
                    }
                });
                glo.addComponent(closeB);
                addWindow(w2);

                scheduleTask();
            }
        });

        Button openWin = new Button("3. Finally, Click to open a new Window");
        openWin.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                w = new Window("test");
                w.setWidth("300px");
                w.setHeight("300px");
                w.setContent(new VerticalLayout(new Label(
                        "simple test label component")));
                w.center();
                getUI().addWindow(w);
            }
        });

        Button stopPolling = new Button("Stop polling", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                setPollInterval(-1);
            }
        });
        layout.addComponents(button, openWin, stopPolling);
    }

    protected void scheduleTask() {
        Thread t = new Thread() {

            @Override
            public void run() {
                getUI().access(new Runnable() {
                    @Override
                    public void run() {
                        updateProgresBar(50);
                    }
                });
            }
        };
        ScheduledExecutorService worker = Executors
                .newSingleThreadScheduledExecutor();
        worker.schedule(t, 2, TimeUnit.SECONDS);
    }

    public void updateProgresBar(int pc) {
        progressBar.setValue((float) (pc / 100.0));
    }

}
