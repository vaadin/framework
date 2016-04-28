package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;

public class Ticket124 extends LegacyApplication {

    private TextField tf;
    private GridLayout gl;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(
                "#124: Insert & remove row for GridLayout");
        setMainWindow(w);
        setTheme("tests-tickets");
        // gl = new GridLayout(4, 4);
        gl = new GridLayout(2, 2);

        tf = new TextField("Row nr");
        Button insert = new Button("Insert row", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                insertRow();

            }
        });
        Button delete = new Button("Delete row", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                deleteRow();

            }
        });

        // gl.addComponent(new Label("0-0"), 0, 0);
        // gl.addComponent(new Label("0-1"), 1, 0);
        gl.addComponent(new Label("1-0"), 1, 0);
        gl.addComponent(new Label("1-1"), 1, 1);
        gl.addComponent(new Label("0,0-1,0"), 0, 0, 1, 0);
        gl.addComponent(new Label("2,0-3,0"), 2, 0, 3, 0);
        Label l = new Label("Large cell 0,1-2,2<br/>yadayada<br/>lorem ipsum");
        l.setContentMode(ContentMode.HTML);
        gl.addComponent(l, 0, 1, 2, 2);
        gl.addComponent(new Label("3-1"), 3, 1);
        gl.addComponent(new Label("3,2-3,3"), 3, 2, 3, 3);
        gl.addComponent(tf, 0, 3);
        gl.addComponent(insert, 1, 3);
        gl.addComponent(delete, 2, 3);

        gl.setStyleName("border");
        w.addComponent(gl);
    }

    protected void deleteRow() {
        int pos = Integer.parseInt(tf.getValue().toString());
        gl.removeRow(pos);

    }

    protected void clearRow() {
        int pos = Integer.parseInt(tf.getValue().toString());
        for (int col = 0; col < gl.getColumns(); col++) {
            try {
                gl.removeComponent(col, pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected void insertRow() {
        int pos = Integer.parseInt(tf.getValue().toString());
        gl.insertRow(pos);
        try {
            TextField t = new TextField("", "Newly added row");
            t.setWidth("100%");
            gl.addComponent(t, 0, pos, 3, pos);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
