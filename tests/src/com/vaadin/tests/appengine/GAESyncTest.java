package com.vaadin.tests.appengine;

import com.google.apphosting.api.DeadlineExceededException;
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class GAESyncTest extends Application {

    /**
     * 
     */
    private static final long serialVersionUID = -3724319151122707926l;

    @Override
    public void init() {
        setMainWindow(new IntrWindow(this));

    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        Throwable t = event.getThrowable();
        // Was this caused by a GAE timeout?
        while (t != null) {
            if (t instanceof DeadlineExceededException) {
                getMainWindow().showNotification("Bugger!",
                        "Deadline Exceeded", Notification.TYPE_ERROR_MESSAGE);
                return;
            }
            t = t.getCause();
        }

        super.terminalError(event);

    }

    private class IntrWindow extends Window {
        private int n = 0;
        private static final long serialVersionUID = -6521351715072191625l;
        TextField tf;
        Label l;
        Application app;
        GridLayout gl;

        private IntrWindow(Application app) {

            this.app = app;
            tf = new TextField("Echo thingie");
            tf.setImmediate(true);
            tf.addListener(new Property.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    IntrWindow.this.showNotification((String) event
                            .getProperty().getValue());

                }

            });
            addComponent(tf);

            l = new Label("" + n);
            addComponent(l);

            {
                Button b = new Button("Slow", new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                });
                addComponent(b);
            }

            {
                Button b = new Button("Add", new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        if (getWindow() == getApplication().getMainWindow()) {
                            getWindow().showNotification("main");
                            try {
                                Thread.sleep((5000));
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        addImage();
                    }

                });
                addComponent(b);
            }

            gl = new GridLayout(30, 50);
            addComponent(gl);

        }

        private void addImage() {
            ClassResource res = new ClassResource("img1.png", app) {

                private static final long serialVersionUID = 1L;

                @Override
                public DownloadStream getStream() {
                    try {
                        Thread.sleep((long) (Math.random() * 5000));
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return super.getStream();
                }

            };
            res.setCacheTime(0);
            Embedded emb = new Embedded("" + n, res);
            emb.setWidth("30px");
            emb.setHeight("5px");
            gl.addComponent(emb);
            l.setValue("" + n++);
        }

    }

    @Override
    public Window getWindow(String name) {
        Window w = super.getWindow(name);
        if (w == null) {
            w = new IntrWindow(this);
            addWindow(w);
        }
        return w;

    }

}
